package com.raveendra.foodapp.presentation.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.raveendra.foodapp.data.repository.UserRepository
import com.raveendra.foodapp.tools.MainCoroutineRule
import com.raveendra.foodapp.util.ResultWrapper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class RegisterViewModelTest {
    @MockK
    lateinit var userRepository: UserRepository

    @get:Rule
    val testRule: TestRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule: TestRule = MainCoroutineRule(UnconfinedTestDispatcher())

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        viewModel = spyk(RegisterViewModel(userRepository))

        val loginResultMock = flow {
            emit(ResultWrapper.Success(true))
        }
        coEvery { userRepository.doRegister(any(), any(), any()) } returns loginResultMock
    }

    @Test
    fun doRegister() {
//        viewModel.registerResult.getOrAwaitValue()
        viewModel.doRegister("fullname", "password", "password")
        coVerify { userRepository.doRegister(any(), any(), any()) }
    }
}
