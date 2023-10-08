package com.raveendra.foodapp_challenge_binar

import android.app.Application
import com.raveendra.foodapp_challenge_binar.data.local.database.AppDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getInstance(this)
    }
}

