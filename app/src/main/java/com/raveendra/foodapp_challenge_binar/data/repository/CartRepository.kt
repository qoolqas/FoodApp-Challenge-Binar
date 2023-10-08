package com.raveendra.foodapp_challenge_binar.data.repository

import com.raveendra.foodapp_challenge_binar.data.local.database.datasource.CartDataSource
import com.raveendra.foodapp_challenge_binar.data.local.database.entity.CartEntity
import com.raveendra.foodapp_challenge_binar.data.local.database.mapper.toCartEntity
import com.raveendra.foodapp_challenge_binar.data.local.database.mapper.toCartProductList
import com.raveendra.foodapp_challenge_binar.model.Cart
import com.raveendra.foodapp_challenge_binar.model.CartProduct
import com.raveendra.foodapp_challenge_binar.model.Food
import com.raveendra.foodapp_challenge_binar.util.ResultWrapper
import com.raveendra.foodapp_challenge_binar.util.proceed
import com.raveendra.foodapp_challenge_binar.util.proceedFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart


interface CartRepository {
    fun getUserCartData(): Flow<ResultWrapper<Pair<List<CartProduct>, Double>>>
    suspend fun createCart(food: Food, totalQuantity: Int): Boolean
    suspend fun decreaseCart(item: Cart): Flow<ResultWrapper<Boolean>>
    suspend fun increaseCart(item: Cart): Flow<ResultWrapper<Boolean>>
    suspend fun setCartNotes(item: Cart): Flow<ResultWrapper<Boolean>>
    suspend fun deleteCart(item: Cart): Flow<ResultWrapper<Boolean>>
    suspend fun deleteAllCart(): Flow<ResultWrapper<Boolean>>
}

class CartRepositoryImpl(
    private val dataSource: CartDataSource
) : CartRepository {

    override fun getUserCartData(): Flow<ResultWrapper<Pair<List<CartProduct>, Double>>> {
        return dataSource.getAllCarts().map {
            proceed {
                val cartList = it.toCartProductList()
                val totalPrice = cartList.sumOf {
                    val quantity = it.cart.itemQuantity
                    val pricePerItem = it.food.price
                    quantity * pricePerItem
                }
                Pair(cartList, totalPrice)
            }
        }.onStart {
            emit(ResultWrapper.Loading())
        }
    }
    //gunakan di detail
    override suspend fun createCart(
        food: Food,
        totalQuantity: Int
    ): Boolean{
        return food.id?.let {
            val affectedRow = dataSource.insertCart(
                CartEntity(foodId = it, itemQuantity = totalQuantity)
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
    override suspend fun deleteAllCart(): Flow<ResultWrapper<Boolean>> {
        return proceedFlow { dataSource.deleteAllCart() > 0 }
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