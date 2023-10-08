package com.raveendra.foodapp_challenge_binar.data.local.database.mapper

import com.raveendra.foodapp_challenge_binar.data.local.database.entity.FoodEntity
import com.raveendra.foodapp_challenge_binar.model.Food



fun FoodEntity?.toFood() = Food(
    id = this?.id ?: 0,
    title = this?.title.orEmpty(),
    price = this?.price ?: 0.0,
    addressDescription = this?.addressDescription.orEmpty(),
    addressUrl = this?.addressUrl.orEmpty(),
    rating = this?.rating ?: 0.0,
    desc = this?.desc.orEmpty(),
    productImgUrl = this?.productImgUrl.orEmpty(),
)

fun Food?.toProductEntity() = FoodEntity(
    id = this?.id,
    title = this?.title.orEmpty(),
    price = this?.price ?: 0.0,
    addressDescription = this?.addressDescription.orEmpty(),
    addressUrl = this?.addressUrl.orEmpty(),
    rating = this?.rating ?: 0.0,
    desc = this?.desc.orEmpty(),
    productImgUrl = this?.productImgUrl.orEmpty(),
)

fun List<FoodEntity?>.toFoodList() = this.map { it.toFood() }
fun FoodEntity?.toFoodDetail() = this.toFood()
fun List<Food?>.toProductEntity() = this.map { it.toProductEntity() }
