package com.raveendra.foodapp.presentation.checkout

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.raveendra.foodapp.R
import com.raveendra.foodapp.databinding.ActivityCheckoutBinding
import com.raveendra.foodapp.presentation.HomeActivity
import com.raveendra.foodapp.presentation.base.BaseViewModelActivity
import com.raveendra.foodapp.presentation.cart.CartListAdapter
import com.raveendra.foodapp.util.proceedWhen
import com.raveendra.foodapp.util.toIdrCurrency
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

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

    override val viewModel: CheckoutViewModel by viewModel()

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
                        showDialogCheckout()
                    },
                    doOnError = {
                        Toast.makeText(
                            this@CheckoutActivity,
                            getString(R.string.label_something_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
            .setCancelable(false)
            .setPositiveButton(getString(R.string.label_okay)) { _, _ ->
                viewModel.deleteAllCart()
                startActivity(
                    Intent(this, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            }.create().show()
    }
}
