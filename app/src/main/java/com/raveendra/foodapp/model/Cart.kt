package com.raveendra.foodapp.model

data class Cart(
    var id: Int = 0,
    val name: String,
    val price: Double,
    val productImgUrl: String,
    var itemQuantity: Int = 0,
    var itemNotes: String? = null
)
