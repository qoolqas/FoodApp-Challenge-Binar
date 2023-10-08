package com.raveendra.foodapp_challenge_binar.data.local.database.datasource

import com.raveendra.foodapp_challenge_binar.data.local.database.dao.FoodDao
import com.raveendra.foodapp_challenge_binar.data.local.database.entity.FoodEntity
import kotlinx.coroutines.flow.Flow


interface FoodDataSource {
    fun getAllProducts(): Flow<List<FoodEntity>>
    fun getProductById(id: Int): Flow<FoodEntity>
    suspend fun insertProducts(product: List<FoodEntity>)
    suspend fun deleteProduct(product: FoodEntity): Int
    suspend fun updateProduct(product: FoodEntity): Int

}

class FoodDatabaseDataSource(private val dao : FoodDao) : FoodDataSource {
    override fun getAllProducts(): Flow<List<FoodEntity>> {
        return dao.getAllProducts()
    }

    override fun getProductById(id: Int): Flow<FoodEntity> {
        return dao.getProductById(id)
    }

    override suspend fun insertProducts(product: List<FoodEntity>) {
        return dao.insertProduct(product)
    }

    override suspend fun deleteProduct(product: FoodEntity): Int {
        return dao.deleteProduct(product)
    }

    override suspend fun updateProduct(product: FoodEntity): Int {
        return dao.updateProduct(product)
    }

}