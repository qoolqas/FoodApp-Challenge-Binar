package com.raveendra.foodapp.data.repository

import com.raveendra.foodapp.data.local.database.datasource.CartDataSource
import com.raveendra.foodapp.data.local.database.entity.CartEntity
import com.raveendra.foodapp.data.local.database.mapper.toCartEntity
import com.raveendra.foodapp.data.local.database.mapper.toCartList
import com.raveendra.foodapp.data.model.FoodViewParam
import com.raveendra.foodapp.data.network.api.datasource.FoodDataSource
import com.raveendra.foodapp.data.network.api.model.OrderItemRequest
import com.raveendra.foodapp.data.network.api.model.OrderRequest
import com.raveendra.foodapp.model.Cart
import com.raveendra.foodapp.util.ResultWrapper
import com.raveendra.foodapp.util.proceed
import com.raveendra.foodapp.util.proceedFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

interface CartRepository {
    fun getUserCartData(): Flow<ResultWrapper<Pair<List<Cart>, Double>>>
    suspend fun createCart(food: FoodViewParam, totalQuantity: Int): Boolean
    suspend fun decreaseCart(item: Cart): Flow<ResultWrapper<Boolean>>
    suspend fun increaseCart(item: Cart): Flow<ResultWrapper<Boolean>>
    suspend fun setCartNotes(item: Cart): Flow<ResultWrapper<Boolean>>
    suspend fun deleteCart(item: Cart): Flow<ResultWrapper<Boolean>>
    suspend fun deleteAllCart()
    suspend fun orderCart(items: List<Cart>, total: Int): Flow<ResultWrapper<Boolean>>
}

class CartRepositoryImpl(
    private val dataSource: CartDataSource,
    private val foodDataSource: FoodDataSource
) : CartRepository {

    override fun getUserCartData(): Flow<ResultWrapper<Pair<List<Cart>, Double>>> {
        return dataSource.getAllCarts().map {
            proceed {
                val result = it.toCartList()
                val totalPrice = result.sumOf {
                    val pricePerItem = it.price
                    val quantity = it.itemQuantity
                    pricePerItem * quantity
                }
                Pair(result, totalPrice)
            }
        }.map {
            if (it.payload?.first?.isEmpty() == true) {
                ResultWrapper.Empty(it.payload)
            } else {
                it
            }
        }.catch {
            emit(ResultWrapper.Error(exception = Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
        }
    }

    override suspend fun createCart(
        food: FoodViewParam,
        totalQuantity: Int
    ): Boolean {
        // masih belum bisa pake proceedFlow aneh
        return food.nama?.let {
            val affectedRow = dataSource.insertCart(
                CartEntity(
                    itemQuantity = totalQuantity,
                    productImgUrl = food.imageUrl.orEmpty(),
                    name = food.nama,
                    price = food.harga ?: 0.0
                )
            )
            affectedRow > 0
        } == true
    }

    override suspend fun decreaseCart(item: Cart): Flow<ResultWrapper<Boolean>> {
        val modifiedCart = item.copy().apply {
            itemQuantity -= 1
        }
        return if (modifiedCart.itemQuantity <= 0) {
            proceedFlow { dataSource.deleteCart(modifiedCart.toCartEntity()) > 0 }
        } else {
            proceedFlow { dataSource.updateCart(modifiedCart.toCartEntity()) > 0 }
        }
    }

    override suspend fun deleteAllCart() {
        return dataSource.deleteAllCart()
    }

    override suspend fun orderCart(items: List<Cart>, total: Int): Flow<ResultWrapper<Boolean>> {
        return proceedFlow {
            val orderItems = items.map {
                OrderItemRequest(
                    catatan = it.itemNotes,
                    harga = it.price,
                    nama = it.name,
                    qty = it.itemQuantity
                )
            }
            val orderRequest =
                OrderRequest(username = "Raveendra", total = total, orderItemRequests = orderItems)

            foodDataSource.postOrder(orderRequest).status == true
        }
    }

    override suspend fun increaseCart(item: Cart): Flow<ResultWrapper<Boolean>> {
        val modifiedCart = item.copy().apply {
            itemQuantity += 1
        }
        return proceedFlow { dataSource.updateCart(modifiedCart.toCartEntity()) > 0 }
    }

    override suspend fun setCartNotes(item: Cart): Flow<ResultWrapper<Boolean>> {
        return proceedFlow { dataSource.updateCart(item.toCartEntity()) > 0 }
    }

    override suspend fun deleteCart(item: Cart): Flow<ResultWrapper<Boolean>> {
        return proceedFlow { dataSource.deleteCart(item.toCartEntity()) > 0 }
    }
}
