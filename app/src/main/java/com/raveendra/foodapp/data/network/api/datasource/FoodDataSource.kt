package com.raveendra.foodapp.data.network.api.datasource

import com.raveendra.foodapp.core.ResponseWrapper
import com.raveendra.foodapp.data.network.api.model.CategoryResponse
import com.raveendra.foodapp.data.network.api.model.FoodResponse
import com.raveendra.foodapp.data.network.api.model.OrderRequest
import com.raveendra.foodapp.data.network.api.service.FoodService

interface FoodDataSource {
    suspend fun getFoods(category: String?): ResponseWrapper<FoodResponse>

    suspend fun getCategory(): ResponseWrapper<CategoryResponse>

    suspend fun postOrder(orderRequest: OrderRequest): ResponseWrapper<Boolean>
}

class FoodDataSourceImpl(
    private val service: FoodService
) : FoodDataSource {
    override suspend fun getFoods(category: String?): ResponseWrapper<FoodResponse> {
        return service.getFoods(category)
    }

    override suspend fun getCategory(): ResponseWrapper<CategoryResponse> {
        return service.getCategory()
    }

    override suspend fun postOrder(orderRequest: OrderRequest): ResponseWrapper<Boolean> {
        return service.postOrder(orderRequest)
    }
}
