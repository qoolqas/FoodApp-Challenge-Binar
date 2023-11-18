package com.raveendra.foodapp.data.repository

import app.cash.turbine.test
import com.raveendra.foodapp.core.ResponseWrapper
import com.raveendra.foodapp.data.network.api.datasource.FoodDataSource
import com.raveendra.foodapp.data.network.api.model.CategoryResponse
import com.raveendra.foodapp.data.network.api.model.FoodResponse
import com.raveendra.foodapp.util.ResultWrapper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.IllegalStateException

class FoodRepositoryImplTest {

    @MockK
    lateinit var datasource: FoodDataSource

    private lateinit var repository: FoodRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = FoodRepositoryImpl(datasource)
    }

    @Test
    fun `get categories, with result loading`() {
        val mockCategoryResponse = mockk<ResponseWrapper<CategoryResponse>>()
        runTest {
            coEvery { datasource.getCategory() } returns mockCategoryResponse
            repository.getCategory().map {
                delay(100)
                it
            }.test {
                delay(110)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.getCategory() }
            }
        }
    }

    @Test
    fun `get categories, with result success`() {
        val fakeCategoryResponse = CategoryResponse(
            imageUrl = "imageUrl",
            nama = "nama"
        )
        val fakeResponseWrapper = ResponseWrapper(
            code = 200,
            data = listOf(fakeCategoryResponse),
            message = "success",
            status = true
        )
        runTest {
            coEvery { datasource.getCategory() } returns fakeResponseWrapper
            repository.getCategory().map {
                delay(100)
                it
            }.test {
                delay(220)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Success)
                assertEquals(data.payload?.get(0)?.nama, "nama")
                coVerify { datasource.getCategory() }
            }
        }
    }

    @Test
    fun `get categories, with result empty`() {
        val fakeResponseWrapper = ResponseWrapper(
            code = 200,
            data = emptyList<CategoryResponse>(),
            message = "success",
            status = true
        )
        runTest {
            coEvery { datasource.getCategory() } returns fakeResponseWrapper
            repository.getCategory().map {
                delay(100)
                it
            }.test {
                delay(220)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Empty)
                coVerify { datasource.getCategory() }
            }
        }
    }

    @Test
    fun `get categories, with result error`() {
        runTest {
            coEvery { datasource.getCategory() } throws IllegalStateException("mock error")
            repository.getCategory().map {
                delay(100)
                it
            }.test {
                delay(220)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.getCategory() }
            }
        }
    }

    @Test
    fun `get foods, with result loading`() {
        val mockFoodsResponse = mockk<ResponseWrapper<FoodResponse>>()
        runTest {
            coEvery { datasource.getFoods(any()) } returns mockFoodsResponse
            repository.getFoods("mie").map {
                delay(100)
                it
            }.test {
                delay(110)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.getFoods(any()) }
            }
        }
    }

    @Test
    fun `get foods, with result success`() {
        val fakeFoodsResponse = FoodResponse(
            alamatResto = "alamat",
            detail = "detail",
            harga = 1000.0,
            hargaFormat = "hargaFormat",
            imageUrl = "imageUrl",
            nama = "nama"
        )
        val fakeResponseWrapper = ResponseWrapper(
            code = 200,
            data = listOf(fakeFoodsResponse),
            message = "success",
            status = true
        )
        runTest {
            coEvery { datasource.getFoods(any()) } returns fakeResponseWrapper
            repository.getFoods("mie").map {
                delay(100)
                it
            }.test {
                delay(220)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Success)
                assertEquals(data.payload?.get(0)?.nama, "nama")
                coVerify { datasource.getFoods(any()) }
            }
        }
    }

    @Test
    fun `get foods, with result empty`() {
        val fakeResponseWrapper = ResponseWrapper(
            code = 200,
            data = emptyList<FoodResponse>(),
            message = "success",
            status = true
        )
        runTest {
            coEvery { datasource.getFoods(any()) } returns fakeResponseWrapper
            repository.getFoods("mie").map {
                delay(100)
                it
            }.test {
                delay(220)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Empty)
                coVerify { datasource.getFoods(any()) }
            }
        }
    }

    @Test
    fun `get foods, with result error`() {
        runTest {
            coEvery { datasource.getFoods(any()) } throws IllegalStateException("mock error")
            repository.getFoods("mie").map {
                delay(100)
                it
            }.test {
                delay(220)
                val data = expectMostRecentItem()
                assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.getFoods(any()) }
            }
        }
    }
}
