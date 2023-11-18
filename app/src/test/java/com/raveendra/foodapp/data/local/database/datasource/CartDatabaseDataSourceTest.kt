package com.raveendra.foodapp.data.local.database.datasource

import app.cash.turbine.test
import com.raveendra.foodapp.data.local.database.dao.CartDao
import com.raveendra.foodapp.data.local.database.entity.CartEntity
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CartDatabaseDataSourceTest {

    @MockK
    lateinit var cartDao: CartDao

    private lateinit var dataSource: CartDataSource

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dataSource = CartDatabaseDataSource(cartDao)
    }

    @Test
    fun getAllCarts() {
        val itemEntityMock1 = mockk<CartEntity>()
        val itemEntityMock2 = mockk<CartEntity>()
        val listEntityMock = listOf(itemEntityMock1, itemEntityMock2)
        val flowMock = flow {
            emit(listEntityMock)
        }
        coEvery { cartDao.getAllCarts() } returns flowMock
        runTest {
            dataSource.getAllCarts().test {
                val result = awaitItem()
                assertEquals(listEntityMock, result)
                assertEquals(itemEntityMock1, result[0])
                assertEquals(itemEntityMock2, result[1])
                awaitComplete()
            }
        }
    }

    @Test
    fun getCartById() {
        val cartEntityMock = mockk<CartEntity>()
        val flowMock = flow {
            emit(cartEntityMock)
        }
        coEvery { cartDao.getCartById(any()) } returns flowMock
        runTest {
            dataSource.getCartById(1).test {
                val result = awaitItem()
                assertEquals(cartEntityMock, result)
                awaitComplete()
            }
        }
    }

    @Test
    fun insertCart() {
    }

    @Test
    fun deleteCart() {
    }

    @Test
    fun deleteAllCart() {
    }

    @Test
    fun updateCart() {
    }
}
