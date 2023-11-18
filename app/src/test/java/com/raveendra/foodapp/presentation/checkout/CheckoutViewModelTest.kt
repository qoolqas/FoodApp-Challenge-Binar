package com.raveendra.foodapp.presentation.checkout

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.raveendra.foodapp.data.repository.CartRepository
import com.raveendra.foodapp.tools.MainCoroutineRule
import com.raveendra.foodapp.tools.getOrAwaitValue
import com.raveendra.foodapp.util.ResultWrapper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class CheckoutViewModelTest {
    @MockK
    lateinit var cartRepository: CartRepository

    @get:Rule
    val testRule: TestRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule: TestRule = MainCoroutineRule(UnconfinedTestDispatcher())

    private lateinit var viewModel: CheckoutViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        coEvery { cartRepository.getUserCartData() } returns flow {
            emit(
                ResultWrapper.Success(
                    Pair(
                        listOf(
                            mockk(relaxed = true),
                            mockk(relaxed = true)
                        ),
                        127000.0
                    )
                )
            )
        }

        viewModel = spyk(CheckoutViewModel(cartRepository))

        val updateResultMock = flow {
            emit(ResultWrapper.Success(true))
        }
        coEvery { cartRepository.deleteAllCart() } returns Unit
        coEvery { cartRepository.orderCart(any(), any()) } returns updateResultMock
    }

    @Test
    fun getCartList() {
        val result = viewModel.cartList.getOrAwaitValue()
        assertEquals(result.payload?.first?.size, 2)
        assertEquals(result.payload?.second, 127000.0)
    }

    @Test
    fun getCheckoutResult() {
    }

    @Test
    fun deleteAllCart() {
        viewModel.deleteAllCart()
        coVerify { cartRepository.deleteAllCart() }
    }

    @Test
    fun createOrder() {
        val result = viewModel.cartList.getOrAwaitValue()
        val cartsItem = result.payload?.first ?: return
        val totalPrice = result.payload?.second ?: return
        viewModel.createOrder()
        coVerify { cartRepository.orderCart(cartsItem, totalPrice.toInt()) }
    }
}
