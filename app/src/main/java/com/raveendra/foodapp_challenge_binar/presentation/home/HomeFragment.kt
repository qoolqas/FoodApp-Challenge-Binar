package com.raveendra.foodapp_challenge_binar.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import com.raveendra.foodapp_challenge_binar.R
import com.raveendra.foodapp_challenge_binar.data.local.database.AppDatabase
import com.raveendra.foodapp_challenge_binar.data.local.database.datasource.FoodDataSource
import com.raveendra.foodapp_challenge_binar.data.local.database.datasource.FoodDatabaseDataSource
import com.raveendra.foodapp_challenge_binar.data.local.datastore.UserPreferenceDataSourceImpl
import com.raveendra.foodapp_challenge_binar.data.local.datastore.appDataStore
import com.raveendra.foodapp_challenge_binar.data.repository.FoodRepository
import com.raveendra.foodapp_challenge_binar.data.repository.FoodRepositoryImpl
import com.raveendra.foodapp_challenge_binar.databinding.FragmentHomeBinding
import com.raveendra.foodapp_challenge_binar.model.Food
import com.raveendra.foodapp_challenge_binar.presentation.base.BaseFragment
import com.raveendra.foodapp_challenge_binar.presentation.detail.DetailActivity
import com.raveendra.foodapp_challenge_binar.presentation.home.HomeListAdapter.Companion.VH_MENU_GRID
import com.raveendra.foodapp_challenge_binar.presentation.home.HomeListAdapter.Companion.VH_MENU_LIST
import com.raveendra.foodapp_challenge_binar.util.GenericViewModelFactory
import com.raveendra.foodapp_challenge_binar.util.PreferenceDataStoreHelperImpl
import com.raveendra.foodapp_challenge_binar.util.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val homeViewModel: HomeViewModel by viewModels {

        val dataStore = requireActivity().appDataStore
        val dataStoreHelper = PreferenceDataStoreHelperImpl(dataStore)
        val userPreferenceDataSource = UserPreferenceDataSourceImpl(dataStoreHelper)

        val database = AppDatabase.getInstance(requireContext())
        val foodDao = database.foodDao()
        val foodDataSource: FoodDataSource = FoodDatabaseDataSource(foodDao)
        val repo: FoodRepository = FoodRepositoryImpl(foodDataSource)
        GenericViewModelFactory.create(HomeViewModel(repo, userPreferenceDataSource))
    }

    private val adapter: HomeListAdapter by lazy {
        HomeListAdapter(VH_MENU_LIST) {
            it.id?.let { id -> navigateToDetail(id) }
        }
    }

    override val inflateLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    }

    private fun setupFoodRecyclerView(
        foodList: ResultWrapper<List<Food>>,
        layoutMenuType: Int
    ): Unit =
        with(binding) {
            val span = if (layoutMenuType == VH_MENU_LIST) 1 else 2
            adapter.viewType = layoutMenuType
            inclMenu.rvExploreMenu.layoutManager = GridLayoutManager(requireContext(), span)
            inclMenu.rvExploreMenu.adapter = adapter
            foodList.payload?.let { adapter.submitData(it) }

            setupChangeAdapterLayout(layoutMenuType)
        }

    private fun setupChangeAdapterLayout(layoutMenuType: Int) = with(binding) {
        inclMenu.ivListController.load(if (layoutMenuType == VH_MENU_LIST) R.drawable.ic_grid else R.drawable.ic_list)

        inclMenu.ivListController.setOnClickListener {
            if (adapter.viewType == VH_MENU_LIST) {
                adapter.viewType = VH_MENU_GRID
                inclMenu.ivListController.load(R.drawable.ic_list)
                homeViewModel.setUserLayoutMenuPref(VH_MENU_GRID)
                (inclMenu.rvExploreMenu.layoutManager as GridLayoutManager).spanCount = 2

            } else {
                adapter.viewType = VH_MENU_LIST
                inclMenu.ivListController.load(R.drawable.ic_grid)
                homeViewModel.setUserLayoutMenuPref(VH_MENU_LIST)
                (inclMenu.rvExploreMenu.layoutManager as GridLayoutManager).spanCount = 1
            }
            adapter.refreshList()
        }
    }

    private fun navigateToDetail(foodId: Int) {
        DetailActivity.navigate(requireContext(), foodId)
    }

}