package com.raveendra.foodapp_challenge_binar.presentation.checkout

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.raveendra.foodapp_challenge_binar.R
import com.raveendra.foodapp_challenge_binar.data.local.database.AppDatabase
import com.raveendra.foodapp_challenge_binar.data.local.database.datasource.CartDataSource
import com.raveendra.foodapp_challenge_binar.data.local.database.datasource.CartDatabaseDataSource
import com.raveendra.foodapp_challenge_binar.data.network.api.datasource.FoodDataSource
import com.raveendra.foodapp_challenge_binar.data.network.api.datasource.FoodDataSourceImpl
import com.raveendra.foodapp_challenge_binar.data.network.api.service.FoodService
import com.raveendra.foodapp_challenge_binar.data.repository.CartRepository
import com.raveendra.foodapp_challenge_binar.data.repository.CartRepositoryImpl
import com.raveendra.foodapp_challenge_binar.databinding.ActivityCheckoutBinding
import com.raveendra.foodapp_challenge_binar.presentation.base.BaseViewModelActivity
import com.raveendra.foodapp_challenge_binar.presentation.cart.CartListAdapter
import com.raveendra.foodapp_challenge_binar.util.GenericViewModelFactory
import com.raveendra.foodapp_challenge_binar.util.proceedWhen
import com.raveendra.foodapp_challenge_binar.util.toIdrCurrency
import kotlinx.coroutines.launch

class CheckoutActivity : BaseViewModelActivity<CheckoutViewModel, ActivityCheckoutBinding>() {
    companion object {
        fun navigate(context: Context) = with(context) {
            startActivity(
                Intent(
                    this,
                    CheckoutActivity::class.java
                )
            )
        }
    }

    private val adapter: CartListAdapter by lazy {
        CartListAdapter()
    }

    override val viewModel: CheckoutViewModel by viewModels {
        val database = AppDatabase.getInstance(this)
        val cartDao = database.cartDao()
        val cartDataSource: CartDataSource = CartDatabaseDataSource(cartDao)
        val service = FoodService.invoke(ChuckerInterceptor(this.applicationContext))
        val foodDataSource: FoodDataSource = FoodDataSourceImpl(service)
        val repo: CartRepository = CartRepositoryImpl(cartDataSource, foodDataSource)
        GenericViewModelFactory.create(CheckoutViewModel(repo))
    }

    override val bindingInflater: (LayoutInflater) -> ActivityCheckoutBinding
        get() = ActivityCheckoutBinding::inflate

    override fun setupViews() = with(binding) {
        inclToolbar.tvTitle.text = getString(R.string.title_checkout)
        btnCheckout.setOnClickListener {

            viewModel.createOrder()
        }
        setupList()
    }

    private fun setupList() {
        binding.rvCart.itemAnimator = null
        binding.rvCart.adapter = adapter
    }

    override fun setupObservers() {
        lifecycleScope.launch {
            viewModel.checkoutResult.collect {
                it.proceedWhen(
                    doOnSuccess = {
                        Toast.makeText(
                            this@CheckoutActivity,
                            getString(R.string.label_checkout_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.deleteAllCart()
                        showDialogCheckout()
                    },
                    doOnError = {
                        Toast.makeText(this@CheckoutActivity, getString(R.string.label_something_wrong), Toast.LENGTH_SHORT).show()
                    },
                )
            }
        }
        viewModel.cartList.observe(this) { result ->
            result.proceedWhen(
                doOnSuccess = {
                    binding.rvCart.isVisible = true
                    binding.cartState.root.isVisible = false
                    binding.pbLoading.isVisible = false
                    binding.cartState.tvError.isVisible = false
                    result.payload?.let { (carts, totalPrice) ->
                        adapter.submitData(carts)
                        binding.tvTotalPrice.text = totalPrice.toIdrCurrency()
                    }
                },
                doOnError = { err ->
                    binding.cartState.root.isVisible = true
                    binding.cartState.tvError.isVisible = true
                    binding.cartState.tvError.text = err.exception?.message.orEmpty()
                    binding.pbLoading.isVisible = false
                },
                doOnLoading = {
                    binding.cartState.root.isVisible = true
                    binding.cartState.tvError.isVisible = false
                    binding.pbLoading.isVisible = true
                    binding.rvCart.isVisible = false
                },
                doOnEmpty = {
                    binding.cartState.root.isVisible = true
                    binding.cartState.tvError.isVisible = true
                    binding.cartState.tvError.text = getString(R.string.label_empty_state)
                    binding.pbLoading.isVisible = false
                }
            )
        }
    }

    private fun showDialogCheckout() {
        AlertDialog.Builder(this)
            .setMessage("Checkout Success")
            .setPositiveButton(getString(R.string.label_okay)) { _, _ ->
                finish()
            }
    }


}