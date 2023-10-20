package com.raveendra.foodapp_challenge_binar.data.model

import com.raveendra.foodapp_challenge_binar.data.network.api.model.CategoryResponse


data class CategoryViewParam(
    val imageUrl: String?,
    val nama: String?
)

fun CategoryResponse.toCategoryViewParam() = CategoryViewParam(
    imageUrl = this.imageUrl,
    nama = this.nama
)

fun Collection<CategoryResponse>.toCategoryViewParams() = this.map {
    it.toCategoryViewParam()
}