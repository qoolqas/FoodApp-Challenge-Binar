package com.raveendra.foodapp.presentation.cart

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.raveendra.foodapp.R
import com.raveendra.foodapp.databinding.FragmentCartBinding
import com.raveendra.foodapp.model.Cart
import com.raveendra.foodapp.presentation.base.BaseFragment
import com.raveendra.foodapp.presentation.checkout.CheckoutActivity
import com.raveendra.foodapp.presentation.login.LoginActivity
import com.raveendra.foodapp.util.hideKeyboard
import com.raveendra.foodapp.util.proceedWhen
import com.raveendra.foodapp.util.toIdrCurrency
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CartFragment : BaseFragment<FragmentCartBinding>() {
    override val inflateLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCartBinding
        get() = FragmentCartBinding::inflate

    private val viewModel: CartViewModel by viewModel()

    private val adapter: CartListAdapter by lazy {
        CartListAdapter(object : CartListener {
            override fun onPlusTotalItemCartClicked(cart: Cart) {
                viewModel.increaseCart(cart)
            }

            override fun onMinusTotalItemCartClicked(cart: Cart) {
                viewModel.decreaseCart(cart)
            }

            override fun onUserDoneEditingNotes(cart: Cart) {
                viewModel.setCartNotes(cart)
                hideKeyboard()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkIfUserLogin()
        setupViews()
        setupList()
        observeData()
        setClickListener()
    }

    private fun setupViews() = with(binding) {
        inclToolbar.tvTitle.text = getString(R.string.title_cart)
        btnCheckout.setOnClickListener {
            CheckoutActivity.navigate(requireContext())
        }
    }

    private fun setClickListener() {
        binding.btnCheckout.setOnClickListener {
            context?.startActivity(Intent(requireContext(), CheckoutActivity::class.java))
        }
    }

    private fun setupList() {
        binding.rvCart.itemAnimator = null
        binding.rvCart.adapter = adapter
    }

    private fun observeData() = with(binding) {
        viewModel.cartList.observe(viewLifecycleOwner) { result ->
            result.proceedWhen(
                doOnSuccess = {
                    rvCart.isVisible = true
                    cartState.root.isVisible = false
                    pbLoading.isVisible = false
                    cartState.tvError.isVisible = false
                    result.payload?.let { (carts, totalPrice) ->
                        adapter.submitData(carts)
                        binding.tvTotalPrice.text = totalPrice.toIdrCurrency()
                        if (totalPrice != 0.0) btnCheckout.isEnabled = true
                    }
                },
                doOnError = { err ->
                    cartState.root.isVisible = true
                    cartState.tvError.isVisible = true
                    cartState.ivError.isVisible = true
                    cartState.tvError.text = err.exception?.message.orEmpty()
                    pbLoading.isVisible = false
                },
                doOnLoading = {
                    pbLoading.isVisible = true
                    rvCart.isVisible = false
                },
                doOnEmpty = {
                    cartState.root.isVisible = true
                    cartState.tvError.isVisible = true
                    cartState.ivError.isVisible = true
                    cartState.tvError.text = getString(R.string.label_empty_state)
                    pbLoading.isVisible = false
                }

            )
            if (result.payload?.first?.isEmpty() == true) {
                cartState.root.isVisible = true
                cartState.tvError.isVisible = true
                cartState.tvError.text = getString(R.string.label_empty_state)
                pbLoading.isVisible = false
            }
        }
    }

    private fun checkIfUserLogin() {
        lifecycleScope.launch {
            if (!viewModel.isUserLoggedIn()) {
                navigateToLogin()
            }
        }
    }

    private fun navigateToLogin() {
        Toast.makeText(requireContext(), getString(R.string.label_not_login), Toast.LENGTH_SHORT)
            .show()
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
}
