package com.raveendra.foodapp.presentation.login

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

class LoginViewModelTest {

    @MockK
    lateinit var userRepository: UserRepository

    @get:Rule
    val testRule: TestRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule: TestRule = MainCoroutineRule(UnconfinedTestDispatcher())

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        viewModel = spyk(LoginViewModel(userRepository))

        val loginResultMock = flow {
            emit(ResultWrapper.Success(true))
        }
        coEvery { userRepository.doLogin(any(), any()) } returns loginResultMock
    }

    @Test
    fun doLogin() {
        viewModel.doLogin("password", "password")
        coVerify { userRepository.doLogin(any(), any()) }
    }
}
