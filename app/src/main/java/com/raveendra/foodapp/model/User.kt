package com.raveendra.foodapp.model

import com.raveendra.foodapp.data.network.api.model.UserResponse

data class User(
    val fullName: String,
    val photoUrl: String,
    val email: String
)

fun UserResponse?.toUser(): User? = if (this != null) {
    User(
        fullName = this.fullName,
        photoUrl = this.photoUrl,
        email = this.email
    )
} else {
    null
}
