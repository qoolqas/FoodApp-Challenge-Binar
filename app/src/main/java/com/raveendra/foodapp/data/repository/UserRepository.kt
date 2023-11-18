package com.raveendra.foodapp.data.repository

import com.raveendra.foodapp.data.network.api.model.toUserResponse
import com.raveendra.foodapp.data.network.firebase.auth.FirebaseAuthDataSource
import com.raveendra.foodapp.model.User
import com.raveendra.foodapp.model.toUser
import com.raveendra.foodapp.util.ResultWrapper
import com.raveendra.foodapp.util.proceedFlow
import kotlinx.coroutines.flow.Flow

/**
Written with love by Muhammad Hermas Yuda Pamungkas
Github : https://github.com/hermasyp
 **/
interface UserRepository {
    suspend fun doLogin(email: String, password: String): Flow<ResultWrapper<Boolean>>

    suspend fun doRegister(
        fullName: String,
        email: String,
        password: String
    ): Flow<ResultWrapper<Boolean>>

    fun doLogout(): Boolean

    fun isLoggedIn(): Boolean

    fun getCurrentUser(): User?
}

class UserRepositoryImpl(private val dataSource: FirebaseAuthDataSource) : UserRepository {
    override suspend fun doLogin(email: String, password: String): Flow<ResultWrapper<Boolean>> {
        return proceedFlow { dataSource.doLogin(email, password) }
    }

    override suspend fun doRegister(
        fullName: String,
        email: String,
        password: String
    ): Flow<ResultWrapper<Boolean>> {
        return proceedFlow { dataSource.doRegister(fullName, email, password) }
    }

    override fun doLogout(): Boolean {
        return dataSource.doLogout()
    }

    override fun isLoggedIn(): Boolean {
        return dataSource.isLoggedIn()
    }

    override fun getCurrentUser(): User? {
        return dataSource.getCurrentUser().toUserResponse().toUser()
    }
}
