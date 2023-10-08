package com.raveendra.foodapp_challenge_binar.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Food(
    val id: Int? = null,
    val title: String,
    val price: Double,
    val addressDescription: String,
    val addressUrl: String,
    val rating: Double,
    val desc: String,
    val productImgUrl: String
): Parcelable
