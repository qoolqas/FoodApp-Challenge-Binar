package com.raveendra.foodapp_challenge_binar.data.network.api.model

import com.google.firebase.auth.FirebaseUser

data class UserResponse(
    val fullName: String,
    val photoUrl: String,
    val email: String
)

fun FirebaseUser?.toUserResponse(): UserResponse? = if (this != null) UserResponse(
    fullName = this.displayName.orEmpty(),
    photoUrl = this.photoUrl.toString(),
    email = this.email.orEmpty(),
) else null