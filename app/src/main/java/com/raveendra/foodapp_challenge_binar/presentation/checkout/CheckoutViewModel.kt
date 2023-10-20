package com.raveendra.foodapp_challenge_binar.presentation.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.raveendra.foodapp_challenge_binar.data.repository.CartRepository
import com.raveendra.foodapp_challenge_binar.util.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CheckoutViewModel(private val repo: CartRepository) : ViewModel() {

    val cartList = repo.getUserCartData().asLiveData(Dispatchers.IO)

    private val _checkoutResult = MutableSharedFlow<ResultWrapper<Boolean>>()
    val checkoutResult: MutableSharedFlow<ResultWrapper<Boolean>>
        get() = _checkoutResult

    fun deleteAllCart() {
        viewModelScope.launch(Dispatchers.IO)  { repo.deleteAllCart().collect() }
    }

    fun createOrder(){
        viewModelScope.launch(Dispatchers.IO) {
            val cartsItem = cartList.value?.payload?.first ?: return@launch
            val totalPrice = cartList.value?.payload?.second ?: return@launch
            repo.orderCart(cartsItem, totalPrice.toInt()).collect{
                _checkoutResult.emit(it)
            }
        }
    }
}