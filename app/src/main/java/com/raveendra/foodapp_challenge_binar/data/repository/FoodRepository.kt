package com.raveendra.foodapp_challenge_binar.data.repository

import com.raveendra.foodapp_challenge_binar.data.local.database.datasource.FoodDataSource
import com.raveendra.foodapp_challenge_binar.data.local.database.mapper.toFoodDetail
import com.raveendra.foodapp_challenge_binar.data.local.database.mapper.toFoodList
import com.raveendra.foodapp_challenge_binar.model.Food
import com.raveendra.foodapp_challenge_binar.util.ResultWrapper
import com.raveendra.foodapp_challenge_binar.util.proceed
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart


interface FoodRepository {
    fun getFoods(): Flow<ResultWrapper<List<Food>>>
    fun getDetailFood(foodId : Int): Flow<ResultWrapper<Food>>
}

class FoodRepositoryImpl(
    private val foodDataSource: FoodDataSource
) : FoodRepository {


    override fun getFoods(): Flow<ResultWrapper<List<Food>>> {
        return foodDataSource.getAllProducts().map {
            proceed { it.toFoodList() }
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(1000)
        }
    }

    override fun getDetailFood(foodId : Int): Flow<ResultWrapper<Food>> {
        return foodDataSource.getProductById(foodId).map {
            proceed { it.toFoodDetail() }
        }
    }
}