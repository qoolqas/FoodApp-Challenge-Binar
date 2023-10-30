package com.raveendra.foodapp.presentation.detail

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raveendra.foodapp.data.model.FoodViewParam
import com.raveendra.foodapp.data.repository.CartRepository
import com.raveendra.foodapp.presentation.detail.DetailActivity.Companion.EXTRA_FOOD
import kotlinx.coroutines.launch

class DetailViewModel(
    private val cartRepository: CartRepository,
    extras: Bundle?
) : ViewModel() {

    val food = extras?.getParcelable<FoodViewParam>(EXTRA_FOOD)

    private val _productCountLiveData = MutableLiveData<Int>()
    val productCountLiveData: LiveData<Int> get() = _productCountLiveData

    private val _priceLiveData = MutableLiveData<Double>()
    val priceLiveData: LiveData<Double> get() = _priceLiveData

    private val _onSuccessAddToCart = MutableLiveData<Boolean>()
    val onSuccessAddToCart: LiveData<Boolean> get() = _onSuccessAddToCart

    init {
        _productCountLiveData.value = 1
    }

    fun increment() {
        val count = (_productCountLiveData.value ?: 0) + 1
        _productCountLiveData.postValue(count)
        _priceLiveData.postValue(food?.harga?.times(count) ?: 0.0)
    }

    fun decrement() {
        if ((_productCountLiveData.value ?: 0) > 1) {
            val count = (_productCountLiveData.value ?: 0) - 1
            _productCountLiveData.postValue(count)
            _priceLiveData.postValue(food?.harga?.times(count) ?: 0.0)
        }
    }

    fun addToCart() {
        viewModelScope.launch {
            val foodData = food
            val total = productCountLiveData.value
            if (foodData != null && total != null) {
                try {
                    cartRepository.createCart(foodData, total)
                    _onSuccessAddToCart.value = true
                } catch (e: Exception) {
                    Log.d("Error add to cart", e.toString())
                    _onSuccessAddToCart.value = false
                }
            }
        }
    }
}
