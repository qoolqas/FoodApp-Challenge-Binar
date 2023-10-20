package com.raveendra.foodapp_challenge_binar.model



data class Cart(
    var id: Int = 0,
    val name: String,
    val price: Double,
    val productImgUrl: String,
    var itemQuantity: Int = 0,
    var itemNotes: String? = null,
)
