package com.raveendra.foodapp_challenge_binar.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raveendra.foodapp_challenge_binar.data.local.datastore.UserPreferenceDataSource
import com.raveendra.foodapp_challenge_binar.data.model.CategoryViewParam
import com.raveendra.foodapp_challenge_binar.data.model.FoodViewParam
import com.raveendra.foodapp_challenge_binar.data.repository.FoodRepository
import com.raveendra.foodapp_challenge_binar.util.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val foodRepository: FoodRepository,
    private val userPreferenceDataSource: UserPreferenceDataSource
) : ViewModel() {

    private val _foodFlow = MutableSharedFlow<ResultWrapper<List<FoodViewParam>>>()

    private val _categoryFlow = MutableSharedFlow<ResultWrapper<List<CategoryViewParam>>>()
    val categoryFlow: MutableSharedFlow<ResultWrapper<List<CategoryViewParam>>>
        get() = _categoryFlow

    private val userListMenuPref = userPreferenceDataSource.getUserListMenuPrefFlow()

    val combinedFlow = _foodFlow.combine(userListMenuPref) { foodList, userLayoutPref ->
        Pair(foodList, userLayoutPref)
    }

    fun setUserLayoutMenuPref(layoutMenuType: Int) {
        viewModelScope.launch {
            userPreferenceDataSource.setUserLayoutMenuPref(layoutMenuType)
        }
    }


    fun getFoods(category : String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.getFoods(category).collect {
                _foodFlow.emit(it)
            }
        }
    }
    fun getCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.getCategory().collect {
                _categoryFlow.emit(it)
            }
        }
    }

}