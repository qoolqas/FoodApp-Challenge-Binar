package com.raveendra.foodapp.presentation.profile

import androidx.lifecycle.ViewModel
import com.raveendra.foodapp.data.repository.UserRepository

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getCurrentUser() = userRepository.getCurrentUser()

    fun isUserLoggedIn() = userRepository.isLoggedIn()

    fun doLogout() {
        userRepository.doLogout()
    }
}
