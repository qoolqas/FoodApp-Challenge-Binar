package com.raveendra.foodapp_challenge_binar.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "name")
    val title: String,
    @ColumnInfo(name = "price")
    val price: Double,
    @ColumnInfo(name = "address_description")
    val addressDescription: String,
    @ColumnInfo(name = "address_url")
    val addressUrl: String,
    @ColumnInfo(name = "rating")
    val rating: Double,
    @ColumnInfo(name = "desc")
    val desc: String,
    @ColumnInfo(name = "product_img_url")
    val productImgUrl: String
)
