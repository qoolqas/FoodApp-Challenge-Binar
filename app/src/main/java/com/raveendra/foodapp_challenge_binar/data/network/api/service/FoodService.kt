package com.raveendra.foodapp_challenge_binar.data.network.api.service

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.raveendra.foodapp_challenge_binar.BuildConfig
import com.raveendra.foodapp_challenge_binar.core.ResponseWrapper
import com.raveendra.foodapp_challenge_binar.data.network.api.model.CategoryResponse
import com.raveendra.foodapp_challenge_binar.data.network.api.model.FoodResponse
import com.raveendra.foodapp_challenge_binar.data.network.api.model.OrderRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface FoodService {
    @GET("listmenu")
    suspend fun getFoods(@Query("c") category: String? = null): ResponseWrapper<FoodResponse>

    @GET("category")
    suspend fun getCategory(): ResponseWrapper<CategoryResponse>

    @POST("order")
    suspend fun postOrder(@Body orderRequest: OrderRequest): ResponseWrapper<Boolean>

    companion object {
        @JvmStatic
        operator fun invoke(chucker :ChuckerInterceptor): FoodService {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(chucker)
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
            return retrofit.create(FoodService::class.java)
        }
    }
}