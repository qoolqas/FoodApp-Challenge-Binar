package com.raveendra.foodapp_challenge_binar.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class FoodResponse(
    val id : String = UUID.randomUUID().toString(),
    val title : String,
    val description : String,
    val price : Double,
    val address : String,
    val addressUrl : String,
    val imageUrl : Int
) : Parcelable
