package com.raveendra.foodapp.data.local.database.mapper

import com.raveendra.foodapp.data.local.database.entity.CartEntity
import com.raveendra.foodapp.model.Cart

// Entity > View Object
fun CartEntity?.toCart() = Cart(
    id = this?.id ?: 0,
    name = this?.name.orEmpty(),
    price = this?.price ?: 0.0,
    productImgUrl = this?.productImgUrl.orEmpty(),
    itemQuantity = this?.itemQuantity ?: 0,
    itemNotes = this?.itemNotes.orEmpty()
)

// View Object > Entity
fun Cart?.toCartEntity() = CartEntity(
    id = this?.id,
    name = this?.name.orEmpty(),
    price = this?.price ?: 0.0,
    productImgUrl = this?.productImgUrl.orEmpty(),
    itemQuantity = this?.itemQuantity ?: 0,
    itemNotes = this?.itemNotes.orEmpty()
)

// list of entity > list of view object
fun List<CartEntity?>.toCartList() = this.map { it.toCart() }
