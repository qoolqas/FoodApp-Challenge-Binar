package com.raveendra.foodapp.data.repository

import app.cash.turbine.test
import com.raveendra.foodapp.core.ResponseWrapper
import com.raveendra.foodapp.data.local.database.datasource.CartDataSource
import com.raveendra.foodapp.data.local.database.entity.CartEntity
import com.raveendra.foodapp.data.model.FoodViewParam
import com.raveendra.foodapp.data.network.api.datasource.FoodDataSource
import com.raveendra.foodapp.model.Cart
import com.raveendra.foodapp.util.ResultWrapper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.IllegalStateException

class CartRepositoryImplTest {

    @MockK
    lateinit var localDataSource: CartDataSource

    @MockK
    lateinit var remoteDatasource: FoodDataSource

    private lateinit var repository: CartRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = CartRepositoryImpl(localDataSource, remoteDatasource)
    }

    @Test
    fun deleteAll() {
        coEvery { localDataSource.deleteAllCart() } returns Unit
        runTest {
            val result = repository.deleteAllCart()
            coVerify { localDataSource.deleteAllCart() }
            assertEquals(result, Unit)
        }
    }

    @Test
    fun `get user cart data, result success`() {
        val fakeCartList = listOf(
            CartEntity(
                id = 1,
                name = "Sate Cirebon",
                price = 12000.0,
                productImgUrl = "url",
                itemQuantity = 2,
                itemNotes = "notes"
            ),
            CartEntity(
                id = 2,
                name = "Sate Padang",
                price = 14000.0,
                productImgUrl = "url",
                itemQuantity = 2,
                itemNotes = "notes"
            )
        )
        every { localDataSource.getAllCarts() } returns flow {
            emit(fakeCartList)
        }
        runTest {
            repository.getUserCartData().map {
                delay(100)
                it
            }.test {
                delay(2201)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Success)
                assertEquals(data.payload?.first?.size, 2)
                assertEquals(data.payload?.second, 52000.0)
                verify { localDataSource.getAllCarts() }
            }
        }
    }

    @Test
    fun `get user cart data, result empty`() {
        every { localDataSource.getAllCarts() } returns flow {
            emit(listOf())
        }
        runTest {
            repository.getUserCartData().map {
                delay(100)
                it
            }.test {
                delay(2201)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Empty)
                verify { localDataSource.getAllCarts() }
            }
        }
    }

    @Test
    fun `get user cart data, result error`() {
        runTest {
            every { localDataSource.getAllCarts() } returns flow {
                throw IllegalStateException("mock error")
            }
            repository.getUserCartData().map {
                delay(100)
                it
            }.test {
                delay(2201)
                val data = expectMostRecentItem()
                println(data.exception)
                assertTrue(data is ResultWrapper.Error)
                verify { localDataSource.getAllCarts() }
            }
        }
    }

    @Test
    fun `get user cart data, result loading`() {
        val fakeCartList = emptyList<CartEntity>()
        every { localDataSource.getAllCarts() } returns flow {
            emit(fakeCartList)
        }
        runTest {
            repository.getUserCartData().map {
                delay(100)
                it
            }.test {
                delay(110)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Loading)
                verify { localDataSource.getAllCarts() }
            }
        }
    }

    @Test
    fun `create cart, result success, foodId not null`() {
        runTest {
            val mockFood = mockk<FoodViewParam>(relaxed = true)
            coEvery { localDataSource.insertCart(any()) } returns 1
            val createCart = repository.createCart(mockFood, 1)
            assertEquals(createCart, true)
            coVerify { localDataSource.insertCart(any()) }
        }
    }

    @Test
    fun `decrease cart, when item quantity less than or equal to 0`() {
        val mockCart = Cart(
            id = 0,
            name = "String",
            price = 15000.0,
            productImgUrl = "String",
            itemQuantity = 0,
            itemNotes = "String"
        )
        coEvery { localDataSource.deleteCart(any()) } returns 1
        coEvery { localDataSource.updateCart(any()) } returns 1
        runTest {
            repository.decreaseCart(mockCart).map {
                delay(100)
                it
            }.test {
                delay(210)
                val result = expectMostRecentItem()
                assertEquals(result.payload, true)
                coVerify(atLeast = 1) { localDataSource.deleteCart(any()) }
                coVerify(atLeast = 0) { localDataSource.updateCart(any()) }
            }
        }
    }

    @Test
    fun `decrease cart, when item quantity more than 0`() {
        val mockCart = Cart(
            id = 0,
            name = "String",
            price = 15000.0,
            productImgUrl = "String",
            itemQuantity = 2,
            itemNotes = "String"
        )
        coEvery { localDataSource.deleteCart(any()) } returns 1
        coEvery { localDataSource.updateCart(any()) } returns 1
        runTest {
            repository.decreaseCart(mockCart).map {
                delay(100)
                it
            }.test {
                delay(210)
                val result = expectMostRecentItem()
                assertEquals(result.payload, true)
                coVerify(atLeast = 0) { localDataSource.deleteCart(any()) }
                coVerify(atLeast = 1) { localDataSource.updateCart(any()) }
            }
        }
    }

    @Test
    fun `increase cart`() {
        val mockCart = Cart(
            id = 0,
            name = "String",
            price = 15000.0,
            productImgUrl = "String",
            itemQuantity = 2,
            itemNotes = "String"
        )
        coEvery { localDataSource.updateCart(any()) } returns 1
        runTest {
            repository.increaseCart(mockCart).map {
                delay(100)
                it
            }.test {
                delay(210)
                val result = expectMostRecentItem()
                assertEquals(result.payload, true)
                coVerify { localDataSource.updateCart(any()) }
            }
        }
    }

    @Test
    fun `set cart notes`() {
        val mockCart = Cart(
            id = 0,
            name = "String",
            price = 15000.0,
            productImgUrl = "String",
            itemQuantity = 2,
            itemNotes = "String"
        )
        coEvery { localDataSource.updateCart(any()) } returns 1
        runTest {
            repository.setCartNotes(mockCart).map {
                delay(100)
                it
            }.test {
                delay(210)
                val result = expectMostRecentItem()
                assertEquals(result.payload, true)
                coVerify { localDataSource.updateCart(any()) }
            }
        }
    }

    @Test
    fun `delete cart`() {
        val mockCart = Cart(
            id = 0,
            name = "String",
            price = 15000.0,
            productImgUrl = "String",
            itemQuantity = 2,
            itemNotes = "String"
        )
        coEvery { localDataSource.deleteCart(any()) } returns 1
        runTest {
            repository.deleteCart(mockCart).map {
                delay(100)
                it
            }.test {
                delay(210)
                val result = expectMostRecentItem()
                assertEquals(result.payload, true)
                coVerify { localDataSource.deleteCart(any()) }
            }
        }
    }

    @Test
    fun `order cart`() {
        val mockCarts = listOf(
            Cart(
                id = 0,
                name = "String",
                price = 15000.0,
                productImgUrl = "String",
                itemQuantity = 2,
                itemNotes = "String"
            )
        )
        coEvery { remoteDatasource.postOrder(any()) } returns ResponseWrapper(
            code = 200,
            data = listOf(true),
            message = "test",
            status = true
        )
        runTest {
            repository.orderCart(mockCarts, 10).map {
                delay(100)
                it
            }.test {
                delay(210)
                val result = expectMostRecentItem()
                assertTrue(result is ResultWrapper.Success)
            }
        }
    }
}
