package com.raveendra.foodapp_challenge_binar.model

import com.raveendra.foodapp_challenge_binar.data.network.api.model.UserResponse

data class User(
    val fullName: String,
    val photoUrl: String,
    val email: String
)

fun UserResponse?.toUser(): User? = if (this != null) User(
    fullName = this.fullName,
    photoUrl = this.photoUrl,
    email = this.email,
) else null