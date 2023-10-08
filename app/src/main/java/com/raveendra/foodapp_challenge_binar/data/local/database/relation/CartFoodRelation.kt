package com.raveendra.foodapp_challenge_binar.data.local.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.raveendra.foodapp_challenge_binar.data.local.database.entity.CartEntity
import com.raveendra.foodapp_challenge_binar.data.local.database.entity.FoodEntity


data class CartFoodRelation(
    @Embedded
    val cart: CartEntity,
    @Relation(parentColumn = "food_id", entityColumn = "id")
    val food: FoodEntity
)