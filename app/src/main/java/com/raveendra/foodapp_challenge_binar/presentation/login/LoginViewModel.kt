package com.raveendra.foodapp_challenge_binar.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raveendra.foodapp_challenge_binar.data.repository.UserRepository
import com.raveendra.foodapp_challenge_binar.util.ResultWrapper
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _loginResult = MutableLiveData<ResultWrapper<Boolean>>()
    val loginResult : LiveData<ResultWrapper<Boolean>>
        get() = _loginResult

    fun doLogin(email: String, password: String){
        viewModelScope.launch {
            userRepository.doLogin(email,password).collect{result ->
                _loginResult.postValue(result)
            }
        }
    }

}