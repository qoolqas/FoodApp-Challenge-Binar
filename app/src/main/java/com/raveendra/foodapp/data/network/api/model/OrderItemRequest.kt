package com.raveendra.foodapp.data.network.api.model

import com.google.gson.annotations.SerializedName

data class OrderItemRequest(
    @SerializedName("catatan")
    val catatan: String?,
    @SerializedName("harga")
    val harga: Double?,
    @SerializedName("nama")
    val nama: String?,
    @SerializedName("qty")
    val qty: Int?
)
