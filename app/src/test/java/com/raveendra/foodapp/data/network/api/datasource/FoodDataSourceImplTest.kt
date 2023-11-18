package com.raveendra.foodapp.data.network.api.datasource

import com.raveendra.foodapp.core.ResponseWrapper
import com.raveendra.foodapp.data.network.api.model.CategoryResponse
import com.raveendra.foodapp.data.network.api.model.FoodResponse
import com.raveendra.foodapp.data.network.api.model.OrderRequest
import com.raveendra.foodapp.data.network.api.service.FoodService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FoodDataSourceImplTest {

    @MockK
    lateinit var service: FoodService

    private lateinit var dataSource: FoodDataSource

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dataSource = FoodDataSourceImpl(service)
    }

    @Test
    fun getFoods() {
        runTest {
            val mockResponse = mockk<ResponseWrapper<FoodResponse>>(relaxed = true)
            coEvery { service.getFoods(any()) } returns mockResponse
            val response = dataSource.getFoods("makanan")
            coVerify { service.getFoods(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun getCategory() {
        runTest {
            val mockResponse = mockk<ResponseWrapper<CategoryResponse>>(relaxed = true)
            coEvery { service.getCategory() } returns mockResponse
            val response = dataSource.getCategory()
            coVerify { service.getCategory() }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun postOrder() {
        runTest {
            val mockRequest = mockk<OrderRequest>(relaxed = true)
            val mockResponse = mockk<ResponseWrapper<Boolean>>(relaxed = true)
            coEvery { service.postOrder(any()) } returns mockResponse
            val response = dataSource.postOrder(mockRequest)
            coVerify { service.postOrder(any()) }
            assertEquals(response, mockResponse)
        }
    }
}
