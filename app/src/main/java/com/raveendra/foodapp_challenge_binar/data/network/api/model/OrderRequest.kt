package com.raveendra.foodapp_challenge_binar.data.network.api.model


import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("username")
    val username: String?,

    @SerializedName("total")
    val total: Int?,

    @SerializedName("orders")
    val orderItemRequests: List<OrderItemRequest>?,
)