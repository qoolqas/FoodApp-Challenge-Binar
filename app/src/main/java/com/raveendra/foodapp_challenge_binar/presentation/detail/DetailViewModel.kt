package com.raveendra.foodapp_challenge_binar.presentation.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raveendra.foodapp_challenge_binar.data.repository.CartRepository
import com.raveendra.foodapp_challenge_binar.data.repository.FoodRepository
import com.raveendra.foodapp_challenge_binar.model.Food
import com.raveendra.foodapp_challenge_binar.util.ResultWrapper
import kotlinx.coroutines.launch

class DetailViewModel(
    private val foodRepository: FoodRepository,
    private val cartRepository: CartRepository,
    foodId: Int
) : ViewModel() {

    private val _addToCartResult = MutableLiveData<ResultWrapper<Boolean>>()
    val addToCartResult: LiveData<ResultWrapper<Boolean>>
        get() = _addToCartResult

    private val _foodDetailLiveData = MutableLiveData<ResultWrapper<Food>>()
    val foodDetailLiveData: LiveData<ResultWrapper<Food>> get() = _foodDetailLiveData

    private val _productCountLiveData = MutableLiveData<Int>()
    val productCountLiveData: LiveData<Int> get() = _productCountLiveData

    private val _priceLiveData = MutableLiveData<Double>()
    val priceLiveData: LiveData<Double> get() = _priceLiveData

    private val _onSuccessAddToCart = MutableLiveData<Boolean>()
    val onSuccessAddToCart: LiveData<Boolean> get() = _onSuccessAddToCart

    init {
        _productCountLiveData.value = 1
        getDetailFood(foodId)
    }

    private fun getDetailFood(foodId: Int) {
        viewModelScope.launch {
            foodRepository.getDetailFood(foodId).collect {
                _foodDetailLiveData.value = it
            }
        }
    }

    fun increment() {
        val count = (_productCountLiveData.value ?: 0) + 1
        _productCountLiveData.postValue(count)
        _priceLiveData.postValue(_foodDetailLiveData.value?.payload?.price?.times(count) ?: 0.0)
    }

    fun decrement() {
        if ((_productCountLiveData.value ?: 0) > 1) {
            val count = (_productCountLiveData.value ?: 0) - 1
            _productCountLiveData.postValue(count)
            _priceLiveData.postValue(_foodDetailLiveData.value?.payload?.price?.times(count) ?: 0.0)
        }
    }

    fun addToCart() {
        viewModelScope.launch {
            val foodData = _foodDetailLiveData.value?.payload
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