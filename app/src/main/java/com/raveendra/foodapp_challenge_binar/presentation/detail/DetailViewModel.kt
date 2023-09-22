package com.raveendra.foodapp_challenge_binar.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DetailViewModel : ViewModel() {
    
    private val _countItem = MutableLiveData<Int>()
    val countItem: LiveData<Int> get() = _countItem

    init {
        _countItem.value = 1
    }
    fun getCount() = countItem

    fun increment() {
        _countItem.value = (_countItem.value ?: 1) + 1
    }


    fun decrement() {
        if ((_countItem.value ?: 1) > 1) {
            _countItem.value = (_countItem.value ?: 1) - 1
        }
    }
}