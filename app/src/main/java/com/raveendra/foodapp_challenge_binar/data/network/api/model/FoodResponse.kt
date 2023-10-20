package com.raveendra.foodapp_challenge_binar.data.network.api.model


import com.google.gson.annotations.SerializedName

data class FoodResponse(
    @SerializedName("alamat_resto")
    val alamatResto: String?,
    @SerializedName("detail")
    val detail: String?,
    @SerializedName("harga")
    val harga: Double?,
    @SerializedName("harga_format")
    val hargaFormat: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("nama")
    val nama: String?
)