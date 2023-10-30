package com.raveendra.foodapp.data.repository

import com.raveendra.foodapp.data.model.CategoryViewParam
import com.raveendra.foodapp.data.model.FoodViewParam
import com.raveendra.foodapp.data.model.toCategoryViewParams
import com.raveendra.foodapp.data.model.toFoodViewParams
import com.raveendra.foodapp.data.network.api.datasource.FoodDataSource
import com.raveendra.foodapp.data.network.api.model.OrderRequest
import com.raveendra.foodapp.util.ResultWrapper
import com.raveendra.foodapp.util.proceedFlow
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    suspend fun getFoods(category: String? = null): Flow<ResultWrapper<List<FoodViewParam>>>

    suspend fun getCategory(): Flow<ResultWrapper<List<CategoryViewParam>>>

    suspend fun postOrder(orderRequest: OrderRequest): Flow<ResultWrapper<Unit>>
}

class FoodRepositoryImpl(private val dataSource: FoodDataSource) : FoodRepository {
    override suspend fun getFoods(category: String?): Flow<ResultWrapper<List<FoodViewParam>>> {
        return proceedFlow { dataSource.getFoods(category).data?.toFoodViewParams().orEmpty() }
    }

    override suspend fun getCategory(): Flow<ResultWrapper<List<CategoryViewParam>>> {
        return proceedFlow { dataSource.getCategory().data?.toCategoryViewParams().orEmpty() }
    }

    override suspend fun postOrder(orderRequest: OrderRequest): Flow<ResultWrapper<Unit>> {
        return proceedFlow { dataSource.postOrder(orderRequest) }
    }
}
