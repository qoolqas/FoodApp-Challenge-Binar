package com.raveendra.foodapp_challenge_binar.presentation.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.raveendra.foodapp_challenge_binar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}