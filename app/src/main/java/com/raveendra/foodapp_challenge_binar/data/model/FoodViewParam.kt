package com.raveendra.foodapp_challenge_binar.data.model

import android.os.Parcelable
import com.raveendra.foodapp_challenge_binar.data.network.api.model.FoodResponse
import kotlinx.parcelize.Parcelize


@Parcelize
data class FoodViewParam(
    val alamatResto: String?,
    val detail: String?,
    val harga: Double?,
    val hargaFormat: String?,
    val imageUrl: String?,
    val nama: String?
) : Parcelable

fun FoodResponse.toFoodViewParam() = FoodViewParam(
    alamatResto = this.alamatResto,
    detail = this.detail,
    harga = this.harga,
    hargaFormat = this.hargaFormat,
    imageUrl = this.imageUrl,
    nama = this.nama
)

fun Collection<FoodResponse>.toFoodViewParams() = this.map {
    it.toFoodViewParam()
}