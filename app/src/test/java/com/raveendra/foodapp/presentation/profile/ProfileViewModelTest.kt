package com.raveendra.foodapp.presentation.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.raveendra.foodapp.data.repository.UserRepository
import com.raveendra.foodapp.model.User
import com.raveendra.foodapp.tools.MainCoroutineRule
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class ProfileViewModelTest {
    @MockK
    lateinit var userRepository: UserRepository

    @get:Rule
    val testRule: TestRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule: TestRule = MainCoroutineRule(UnconfinedTestDispatcher())

    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        viewModel = spyk(ProfileViewModel(userRepository))

        val loginResultMock = mockk<User>(relaxed = true)
        every { userRepository.getCurrentUser() } returns loginResultMock
        every { userRepository.isLoggedIn() } returns true
        every { userRepository.doLogout() } returns true
    }

    @Test
    fun getCurrentUser() {
        viewModel.getCurrentUser()
        coVerify { userRepository.getCurrentUser() }
    }

    @Test
    fun isUserLoggedIn() {
        viewModel.isUserLoggedIn()
        verify { userRepository.isLoggedIn() }
    }

    @Test
    fun doLogout() {
        viewModel.doLogout()
        verify { userRepository.doLogout() }
    }
}
