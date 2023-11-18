package com.raveendra.foodapp.presentation.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.raveendra.foodapp.data.local.datastore.UserPreferenceDataSource
import com.raveendra.foodapp.data.repository.FoodRepository
import com.raveendra.foodapp.tools.MainCoroutineRule
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class HomeViewModelTest {

    @MockK
    lateinit var foodRepository: FoodRepository

    @MockK
    lateinit var userPreferenceDataSource: UserPreferenceDataSource

    @get:Rule
    val testRule: TestRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule: TestRule = MainCoroutineRule(UnconfinedTestDispatcher())

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = spyk(
            HomeViewModel(foodRepository, userPreferenceDataSource),
            recordPrivateCalls = true
        )
//        val categoryResultMock = flow {
//            emit(ResultWrapper.Success(true))
//        }
//        coEvery { foodRepository.getCategory() } returns categoryResultMock
    }

    @Test
    fun getCategoryFlow() {
    }

    @Test
    fun getCombinedFlow() {
    }

    @Test
    fun setUserLayoutMenuPref() {
    }

    @Test
    fun getFoods() {
    }

    @Test
    fun getCategory() {
        viewModel.getCategory()
        coVerify { foodRepository.getCategory() }
    }
}
