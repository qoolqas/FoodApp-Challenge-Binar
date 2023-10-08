package com.raveendra.foodapp_challenge_binar.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore


val Context.appDataStore by preferencesDataStore(
    name = "FoodApp"
)