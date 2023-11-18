package com.raveendra.foodapp.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.raveendra.foodapp.R
import com.raveendra.foodapp.data.model.CategoryViewParam
import com.raveendra.foodapp.data.model.FoodViewParam
import com.raveendra.foodapp.databinding.FragmentHomeBinding
import com.raveendra.foodapp.presentation.base.BaseFragment
import com.raveendra.foodapp.presentation.detail.DetailActivity
import com.raveendra.foodapp.presentation.home.HomeFoodListAdapter.Companion.VH_MENU_GRID
import com.raveendra.foodapp.presentation.home.HomeFoodListAdapter.Companion.VH_MENU_LIST
import com.raveendra.foodapp.util.ResultWrapper
import com.raveendra.foodapp.util.proceedWhen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val homeViewModel: HomeViewModel by viewModel()

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
        binding.inclToolbar.tvTitle.text = getString(R.string.title_home)
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

    private fun setupCategory(resultWrapper: ResultWrapper<List<CategoryViewParam>>) =
        with(binding) {
            resultWrapper.proceedWhen(
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
                    inclHeader.inclErrorState.tvError.text =
                        getString(R.string.label_something_wrong)
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
                    inclHeader.inclErrorState.tvError.text =
                        getString(R.string.label_something_wrong)
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
