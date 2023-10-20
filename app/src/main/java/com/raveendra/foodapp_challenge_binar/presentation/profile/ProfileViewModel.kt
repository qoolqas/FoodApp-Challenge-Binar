package com.raveendra.foodapp_challenge_binar.presentation.profile

import androidx.lifecycle.ViewModel
import com.raveendra.foodapp_challenge_binar.data.repository.UserRepository

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getCurrentUser() = userRepository.getCurrentUser()

    fun isUserLoggedIn() =  userRepository.isLoggedIn()

    fun doLogout() {
        userRepository.doLogout()
    }
}