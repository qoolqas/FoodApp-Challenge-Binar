package com.raveendra.foodapp_challenge_binar.data.local.database.datasource

import com.raveendra.foodapp_challenge_binar.data.local.database.dao.CartDao
import com.raveendra.foodapp_challenge_binar.data.local.database.entity.CartEntity
import kotlinx.coroutines.flow.Flow


interface CartDataSource {
    fun getAllCarts(): Flow<List<CartEntity>>
    fun getCartById(cartId: Int): Flow<CartEntity>
    suspend fun insertCart(cart: CartEntity) : Long
    suspend fun deleteCart(cart: CartEntity): Int
    suspend fun updateCart(cart: CartEntity): Int
    suspend fun deleteAllCart(): Int
}

class CartDatabaseDataSource(private val cartDao: CartDao) : CartDataSource {
    override fun getAllCarts(): Flow<List<CartEntity>> {
        return cartDao.getAllCarts()
    }

    override fun getCartById(cartId: Int): Flow<CartEntity> {
        return cartDao.getCartById(cartId)
    }

    override suspend fun insertCart(cart: CartEntity): Long {
        return cartDao.insertCart(cart)
    }

    override suspend fun deleteCart(cart: CartEntity): Int {
        return cartDao.deleteCart(cart)
    }
    override suspend fun deleteAllCart(): Int {
        return cartDao.deleteAllCart()
    }

    override suspend fun updateCart(cart: CartEntity): Int {
        return cartDao.updateCart(cart)
    }

}