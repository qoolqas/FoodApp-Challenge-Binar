package com.raveendra.foodapp.data.model

import com.raveendra.foodapp.data.network.api.model.CategoryResponse

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
