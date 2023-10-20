package com.raveendra.foodapp_challenge_binar.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.raveendra.foodapp_challenge_binar.R
import com.raveendra.foodapp_challenge_binar.data.local.datastore.UserPreferenceDataSourceImpl
import com.raveendra.foodapp_challenge_binar.data.local.datastore.appDataStore
import com.raveendra.foodapp_challenge_binar.data.model.CategoryViewParam
import com.raveendra.foodapp_challenge_binar.data.model.FoodViewParam
import com.raveendra.foodapp_challenge_binar.data.network.api.datasource.FoodDataSource
import com.raveendra.foodapp_challenge_binar.data.network.api.datasource.FoodDataSourceImpl
import com.raveendra.foodapp_challenge_binar.data.network.api.service.FoodService
import com.raveendra.foodapp_challenge_binar.data.repository.FoodRepository
import com.raveendra.foodapp_challenge_binar.data.repository.FoodRepositoryImpl
import com.raveendra.foodapp_challenge_binar.databinding.FragmentHomeBinding
import com.raveendra.foodapp_challenge_binar.presentation.base.BaseFragment
import com.raveendra.foodapp_challenge_binar.presentation.detail.DetailActivity
import com.raveendra.foodapp_challenge_binar.presentation.home.HomeFoodListAdapter.Companion.VH_MENU_GRID
import com.raveendra.foodapp_challenge_binar.presentation.home.HomeFoodListAdapter.Companion.VH_MENU_LIST
import com.raveendra.foodapp_challenge_binar.util.GenericViewModelFactory
import com.raveendra.foodapp_challenge_binar.util.PreferenceDataStoreHelperImpl
import com.raveendra.foodapp_challenge_binar.util.ResultWrapper
import com.raveendra.foodapp_challenge_binar.util.proceedWhen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val homeViewModel: HomeViewModel by viewModels {

        val dataStore = requireActivity().appDataStore
        val dataStoreHelper = PreferenceDataStoreHelperImpl(dataStore)
        val userPreferenceDataSource = UserPreferenceDataSourceImpl(dataStoreHelper)
        val service = FoodService.invoke(ChuckerInterceptor(requireContext().applicationContext))
        val foodDataSource: FoodDataSource = FoodDataSourceImpl(service)
        val repo: FoodRepository = FoodRepositoryImpl(foodDataSource)
        GenericViewModelFactory.create(HomeViewModel(repo, userPreferenceDataSource))
    }

    private val foodAdapter: HomeFoodListAdapter by lazy {
        HomeFoodListAdapter(VH_MENU_LIST) {
            navigateToDetail(it)
        }
    }
    private val categoryAdapter: HomeCategoryListAdapter by lazy {
        HomeCategoryListAdapter {
            homeViewModel.getFoods(category = it.nama?.lowercase())
        }
    }

    override val inflateLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getCategory()
        homeViewModel.getFoods()
        observeData()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.combinedFlow
                .flowOn(Dispatchers.IO)
                .collect { (foodList, userLayoutPref) ->
                    setupFoodRecyclerView(foodList, userLayoutPref)
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.categoryFlow.collect {
                setupCategory(it)
            }
        }

    }

    private fun setupCategory(resultWrapper: ResultWrapper<List<CategoryViewParam>>) = with(binding) {
        resultWrapper.proceedWhen (
            doOnSuccess = {
                inclHeader.pbFoodRv.isVisible = false
                inclHeader.rvCategory.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                inclHeader.rvCategory.adapter = categoryAdapter
                resultWrapper.payload?.let { categoryAdapter.submitData(it) }
            },
            doOnLoading = {
                inclHeader.pbFoodRv.isVisible = true
            },
            doOnError = {
                inclHeader.pbFoodRv.isVisible = false
                inclHeader.inclErrorState.root.isVisible = true
                inclHeader.inclErrorState.tvError.text = getString(R.string.label_something_wrong)

            },
            doOnEmpty = {
                inclHeader.pbFoodRv.isVisible = false
                inclHeader.inclErrorState.root.isVisible = true
                inclHeader.inclErrorState.tvError.text = getString(R.string.label_empty_state)
            }
        )
    }

    private fun setupFoodRecyclerView(
        foodList: ResultWrapper<List<FoodViewParam>>,
        layoutMenuType: Int
    ): Unit =
        with(binding) {
            foodList.proceedWhen(
                doOnSuccess = {
                    inclMenu.rvExploreMenu.isVisible = true
                    inclMenu.pbFoodRv.isVisible = false
                    val span = if (layoutMenuType == VH_MENU_LIST) 1 else 2
                    foodAdapter.viewType = layoutMenuType
                    inclMenu.rvExploreMenu.layoutManager = GridLayoutManager(requireContext(), span)
                    inclMenu.rvExploreMenu.adapter = foodAdapter
                    foodList.payload?.let { foodAdapter.submitData(it) }
                    foodAdapter.refreshList()

                    setupChangeAdapterLayout(layoutMenuType)
                },
                doOnLoading = {
                    inclMenu.rvExploreMenu.isVisible = false
                    inclMenu.pbFoodRv.isVisible = true
                },
                doOnError = {
                    inclMenu.pbFoodRv.isVisible = false
                    inclHeader.inclErrorState.root.isVisible = true
                    inclHeader.inclErrorState.tvError.text = getString(R.string.label_something_wrong)
                },
                doOnEmpty = {
                    inclMenu.pbFoodRv.isVisible = false
                    inclHeader.inclErrorState.root.isVisible = true
                    inclHeader.inclErrorState.tvError.text = getString(R.string.label_empty_state)
                }
            )

        }

    private fun setupChangeAdapterLayout(layoutMenuType: Int) = with(binding) {
        inclMenu.ivListController.load(if (layoutMenuType == VH_MENU_LIST) R.drawable.ic_grid else R.drawable.ic_list)

        inclMenu.ivListController.setOnClickListener {
            if (foodAdapter.viewType == VH_MENU_LIST) {
                foodAdapter.viewType = VH_MENU_GRID
                inclMenu.ivListController.load(R.drawable.ic_list)
                homeViewModel.setUserLayoutMenuPref(VH_MENU_GRID)
                (inclMenu.rvExploreMenu.layoutManager as GridLayoutManager).spanCount = 2

            } else {
                foodAdapter.viewType = VH_MENU_LIST
                inclMenu.ivListController.load(R.drawable.ic_grid)
                homeViewModel.setUserLayoutMenuPref(VH_MENU_LIST)
                (inclMenu.rvExploreMenu.layoutManager as GridLayoutManager).spanCount = 1
            }
            foodAdapter.refreshList()
        }
    }

    private fun navigateToDetail(food: FoodViewParam) {
        DetailActivity.navigate(requireContext(), food)
    }

}