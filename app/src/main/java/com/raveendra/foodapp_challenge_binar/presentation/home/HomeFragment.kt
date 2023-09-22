package com.raveendra.foodapp_challenge_binar.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.raveendra.foodapp_challenge_binar.databinding.FragmentHomeBinding
import com.raveendra.foodapp_challenge_binar.model.FoodResponse
import com.raveendra.foodapp_challenge_binar.presentation.base.BaseFragment
import com.raveendra.foodapp_challenge_binar.presentation.home.HomeListAdapter.Companion.VH_MENU_GRID
import com.raveendra.foodapp_challenge_binar.presentation.home.HomeListAdapter.Companion.VH_MENU_LIST


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val homeViewModel by viewModels<HomeViewModel>()

    private val adapter: HomeListAdapter by lazy {
        HomeListAdapter(VH_MENU_LIST) {
            navigateToDetail(it)
        }
    }

    override val inflateLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.fetchFoodData()
        observeData()
    }

    private fun observeData() {
        homeViewModel.foodData.observe(requireActivity()) { foodList ->
            setupFoodRecyclerView(foodList)
        }
    }

    private fun setupFoodRecyclerView(foodList : List<FoodResponse>) : Unit = with(binding) {
        val span = if(adapter.viewType == VH_MENU_LIST) 1 else 2
        inclMenu.rvExploreMenu.layoutManager = GridLayoutManager(requireContext(),span)
        inclMenu.rvExploreMenu.adapter = adapter
        adapter.submitData(foodList)

        setupChangeAdapterLayout()
    }
    private fun setupChangeAdapterLayout() = with(binding){
        inclMenu.ivListController.setOnClickListener {
            if (adapter.viewType == VH_MENU_LIST){
                adapter.viewType = VH_MENU_GRID
                (inclMenu.rvExploreMenu.layoutManager as GridLayoutManager).spanCount = 2

            }else{
                adapter.viewType = VH_MENU_LIST
                (inclMenu.rvExploreMenu.layoutManager as GridLayoutManager).spanCount = 1
            }
            adapter.refreshList()
        }
    }

    private fun navigateToDetail(data: FoodResponse) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(data)
        findNavController().navigate(action)
    }

}