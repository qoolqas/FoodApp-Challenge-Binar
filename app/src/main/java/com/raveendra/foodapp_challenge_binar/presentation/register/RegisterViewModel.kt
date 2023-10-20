package com.raveendra.foodapp_challenge_binar.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raveendra.foodapp_challenge_binar.data.repository.UserRepository
import com.raveendra.foodapp_challenge_binar.util.ResultWrapper
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<ResultWrapper<Boolean>>()
    val registerResult : LiveData<ResultWrapper<Boolean>>
        get() = _registerResult

    fun doRegister(fullName: String, email : String, password : String){
        viewModelScope.launch {
            userRepository.doRegister(fullName,email,password).collect{result ->
                _registerResult.postValue(result)
            }
        }
    }
}