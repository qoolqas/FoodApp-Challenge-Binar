package com.raveendra.foodapp.core

import com.google.gson.annotations.SerializedName

data class ResponseWrapper<T : Any>(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("data")
    val data: List<T>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
)
