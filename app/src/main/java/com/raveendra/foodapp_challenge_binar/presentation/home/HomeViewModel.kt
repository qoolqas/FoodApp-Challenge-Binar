package com.raveendra.foodapp_challenge_binar.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raveendra.foodapp_challenge_binar.data.local.datastore.UserPreferenceDataSource
import com.raveendra.foodapp_challenge_binar.data.repository.FoodRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    repo: FoodRepository,
    private val userPreferenceDataSource: UserPreferenceDataSource
) : ViewModel() {
    val foodData = repo.getFoods()

    val userListMenuPref = userPreferenceDataSource.getUserListMenuPrefFlow()

    val combinedFlow = foodData.combine(userListMenuPref) { foodList, userLayoutPref ->
        Pair(foodList, userLayoutPref)
    }

    fun setUserLayoutMenuPref(layoutMenuType: Int) {
        viewModelScope.launch {
            userPreferenceDataSource.setUserLayoutMenuPref(layoutMenuType)
        }
    }

}