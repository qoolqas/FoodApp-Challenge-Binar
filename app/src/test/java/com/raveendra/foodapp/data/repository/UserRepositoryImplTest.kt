package com.raveendra.foodapp.data.repository

import com.raveendra.foodapp.data.network.firebase.auth.FirebaseAuthDataSource
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class UserRepositoryImplTest {

    @MockK
    lateinit var datasource: FirebaseAuthDataSource

    private lateinit var repository: UserRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = UserRepositoryImpl(datasource)
    }

    @Test
    fun doLogin() {
    }

    @Test
    fun doRegister() {
    }

    @Test
    fun doLogout() {
    }

    @Test
    fun isLoggedIn() {
    }

    @Test
    fun getCurrentUser() {
    }

    @Test
    fun updateProfile() {
    }

    @Test
    fun updatePassword() {
    }

    @Test
    fun updateEmail() {
    }

    @Test
    fun sendChangePasswordRequestByEmail() {
    }
}
