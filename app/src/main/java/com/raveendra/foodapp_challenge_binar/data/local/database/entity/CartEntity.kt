package com.raveendra.foodapp_challenge_binar.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "carts")
data class CartEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "food_id")
    var foodId : Int = 0,
    @ColumnInfo(name = "item_quantity")
    var itemQuantity: Int = 0,
    @ColumnInfo(name = "item_notes")
    var itemNotes: String? = null,
)

/// 1 product > n cart