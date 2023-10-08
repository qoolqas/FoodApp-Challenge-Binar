package com.raveendra.foodapp_challenge_binar.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.raveendra.foodapp_challenge_binar.data.local.database.entity.FoodEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface FoodDao {

    @Query("SELECT * FROM FOODS")
    fun getAllProducts(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM FOODS WHERE id == :id")
    fun getProductById(id: Int): Flow<FoodEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: List<FoodEntity>)

    @Delete
    suspend fun deleteProduct(product: FoodEntity): Int

    @Update
    suspend fun updateProduct(product: FoodEntity): Int
}