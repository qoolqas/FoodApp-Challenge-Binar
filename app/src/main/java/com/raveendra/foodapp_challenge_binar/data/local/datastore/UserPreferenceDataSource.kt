package com.raveendra.foodapp_challenge_binar.data.local.datastore

import androidx.datastore.preferences.core.intPreferencesKey
import com.raveendra.foodapp_challenge_binar.util.PreferenceDataStoreHelper
import kotlinx.coroutines.flow.Flow


interface UserPreferenceDataSource {
    fun getUserListMenuPrefFlow(): Flow<Int>
    suspend fun setUserLayoutMenuPref(layoutMenuType: Int)
}

class UserPreferenceDataSourceImpl(private val preferenceHelper: PreferenceDataStoreHelper) :
    UserPreferenceDataSource {

    override fun getUserListMenuPrefFlow(): Flow<Int> {
        return preferenceHelper.getPreference(PREF_USER_LIST_MENU, 1)
    }

    override suspend fun setUserLayoutMenuPref(layoutMenuType: Int) {
        return preferenceHelper.putPreference(PREF_USER_LIST_MENU, layoutMenuType)
    }

    companion object {
        val PREF_USER_LIST_MENU = intPreferencesKey("PREF_USER_LIST_MENU")
    }
}