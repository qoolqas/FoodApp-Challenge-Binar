package com.raveendra.foodapp_challenge_binar.data.local.database.mapper

import com.raveendra.foodapp_challenge_binar.data.local.database.entity.CartEntity
import com.raveendra.foodapp_challenge_binar.data.local.database.relation.CartFoodRelation
import com.raveendra.foodapp_challenge_binar.model.Cart
import com.raveendra.foodapp_challenge_binar.model.CartProduct


// Entity > View Object
fun CartEntity?.toCart() = Cart(
    id = this?.id ?: 0,
    foodId = this?.foodId ?: 0,
    itemQuantity = this?.itemQuantity ?: 0,
    itemNotes = this?.itemNotes.orEmpty()
)
// View Object > Entity
fun Cart?.toCartEntity() = CartEntity(
    id = this?.id,
    foodId = this?.foodId ?: 0,
    itemQuantity = this?.itemQuantity ?: 0,
    itemNotes = this?.itemNotes.orEmpty()
)
// list of entity > list of view object
fun List<CartEntity?>.toCartList() = this.map { it.toCart() }

// Entity > View Object
fun CartFoodRelation.toCartProduct() = CartProduct(
    cart = this.cart.toCart(),
    food = this.food.toFood()
)
// list of entity > list of view object
fun List<CartFoodRelation>.toCartProductList() = this.map { it.toCartProduct() }