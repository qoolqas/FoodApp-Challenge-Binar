package com.raveendra.foodapp.presentation.cart

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.raveendra.foodapp.data.repository.CartRepository
import com.raveendra.foodapp.data.repository.UserRepository
import com.raveendra.foodapp.tools.MainCoroutineRule
import com.raveendra.foodapp.tools.getOrAwaitValue
import com.raveendra.foodapp.util.ResultWrapper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class CartViewModelTest {
    @MockK
    lateinit var cartRepository: CartRepository

    @MockK
    lateinit var userRepository: UserRepository

    @get:Rule
    val testRule: TestRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule: TestRule = MainCoroutineRule(UnconfinedTestDispatcher())

    private lateinit var viewModel: CartViewModel

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

        viewModel = spyk(CartViewModel(cartRepository, userRepository))

        val updateResultMock = flow {
            emit(ResultWrapper.Success(true))
        }
        coEvery { cartRepository.decreaseCart(any()) } returns updateResultMock
        coEvery { cartRepository.increaseCart(any()) } returns updateResultMock
        coEvery { cartRepository.setCartNotes(any()) } returns updateResultMock
        every { userRepository.isLoggedIn() } returns true
    }

    @Test
    fun `test cart list`() {
        val result = viewModel.cartList.getOrAwaitValue()
        assertEquals(result.payload?.first?.size, 2)
        assertEquals(result.payload?.second, 127000.0)
    }

    @Test
    fun `test decrease cart`() {
        viewModel.decreaseCart(mockk())
        coVerify { cartRepository.decreaseCart(any()) }
    }

    @Test
    fun `test increase cart`() {
        viewModel.increaseCart(mockk())
        coVerify { cartRepository.increaseCart(any()) }
    }

    @Test
    fun `test set cart notes`() {
        viewModel.setCartNotes(mockk())
        coVerify { cartRepository.setCartNotes(any()) }
    }

    @Test
    fun `test is user login`() {
        viewModel.isUserLoggedIn()
        verify { userRepository.isLoggedIn() }
    }
}
