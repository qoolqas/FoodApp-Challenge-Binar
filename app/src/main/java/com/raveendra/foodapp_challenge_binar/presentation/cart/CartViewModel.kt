package com.raveendra.foodapp_challenge_binar.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.raveendra.foodapp_challenge_binar.model.Cart
import com.raveendra.foodapp_challenge_binar.data.repository.CartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CartViewModel(private val repo: CartRepository) : ViewModel() {

    val cartList = repo.getUserCartData().asLiveData(Dispatchers.IO)

    fun decreaseCart(item: Cart) {
        viewModelScope.launch { repo.decreaseCart(item).collect() }
    }
    fun increaseCart(item: Cart) {
        viewModelScope.launch { repo.increaseCart(item).collect() }
    }
    fun setCartNotes(item: Cart) {
        viewModelScope.launch { repo.setCartNotes(item).collect() }
    }

}