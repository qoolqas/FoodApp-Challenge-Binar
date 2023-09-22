package com.raveendra.foodapp_challenge_binar.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raveendra.foodapp_challenge_binar.data.FoodDataSource
import com.raveendra.foodapp_challenge_binar.data.FoodDataSourceImpl
import com.raveendra.foodapp_challenge_binar.model.FoodResponse

class HomeViewModel : ViewModel() {

    private val foodDataSource: FoodDataSource = FoodDataSourceImpl()

    private val _foodData = MutableLiveData<List<FoodResponse>>()
    val foodData: LiveData<List<FoodResponse>> get() = _foodData

    fun fetchFoodData() {
        val data = foodDataSource.getFoodData()
        _foodData.postValue(data)
    }
}