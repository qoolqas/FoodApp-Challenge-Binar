package com.raveendra.foodapp.data.network.api.model

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("nama")
    val nama: String?
)
