package com.raveendra.foodapp_challenge_binar.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.raveendra.foodapp_challenge_binar.data.dummy.DummyProductDataSourceImpl
import com.raveendra.foodapp_challenge_binar.data.local.database.AppDatabase.Companion.getInstance
import com.raveendra.foodapp_challenge_binar.data.local.database.dao.CartDao
import com.raveendra.foodapp_challenge_binar.data.local.database.dao.FoodDao
import com.raveendra.foodapp_challenge_binar.data.local.database.entity.CartEntity
import com.raveendra.foodapp_challenge_binar.data.local.database.entity.FoodEntity
import com.raveendra.foodapp_challenge_binar.data.local.database.mapper.toProductEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@Database(
    entities = [CartEntity::class, FoodEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun foodDao(): FoodDao

    companion object {
        private const val DB_NAME = "FoodApp.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(DatabaseSeederCallback(context))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

class DatabaseSeederCallback(private val context: Context) : RoomDatabase.Callback() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        scope.launch {
            getInstance(context).foodDao().insertProduct(prepopulateProducts())
            getInstance(context).cartDao().insertCarts(prepopulateCarts())
            getInstance(context).cartDao().insertCart(prepopulateCart())
        }
    }

    private fun prepopulateProducts(): List<FoodEntity> {
        return DummyProductDataSourceImpl().getProductList().toProductEntity()
    }

    private fun prepopulateCarts(): List<CartEntity> {
        return mutableListOf(
            CartEntity(
                id = 1,
                foodId = 1,
                itemNotes = "Barang yang fresh ya",
                itemQuantity = 3
            ),
            CartEntity(
                id = 2,
                foodId = 2,
                itemNotes = "Barang yang fresh yaaaaaa",
                itemQuantity = 6
            ),
        )
    }
    private fun prepopulateCart(): CartEntity {
        return CartEntity(
            id = 3,
            foodId = 3,
            itemNotes = "Barang yang fresh ya",
            itemQuantity = 3
        )
    }
}