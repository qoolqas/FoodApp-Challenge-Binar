package com.raveendra.foodapp_challenge_binar.presentation.cart

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.firebase.auth.FirebaseAuth
import com.raveendra.foodapp_challenge_binar.R
import com.raveendra.foodapp_challenge_binar.data.local.database.AppDatabase
import com.raveendra.foodapp_challenge_binar.data.local.database.datasource.CartDataSource
import com.raveendra.foodapp_challenge_binar.data.local.database.datasource.CartDatabaseDataSource
import com.raveendra.foodapp_challenge_binar.data.network.api.datasource.FoodDataSource
import com.raveendra.foodapp_challenge_binar.data.network.api.datasource.FoodDataSourceImpl
import com.raveendra.foodapp_challenge_binar.data.network.api.service.FoodService
import com.raveendra.foodapp_challenge_binar.data.network.firebase.auth.FirebaseAuthDataSource
import com.raveendra.foodapp_challenge_binar.data.network.firebase.auth.FirebaseAuthDataSourceImpl
import com.raveendra.foodapp_challenge_binar.data.repository.CartRepository
import com.raveendra.foodapp_challenge_binar.data.repository.CartRepositoryImpl
import com.raveendra.foodapp_challenge_binar.data.repository.UserRepository
import com.raveendra.foodapp_challenge_binar.data.repository.UserRepositoryImpl
import com.raveendra.foodapp_challenge_binar.databinding.FragmentCartBinding
import com.raveendra.foodapp_challenge_binar.model.Cart
import com.raveendra.foodapp_challenge_binar.presentation.base.BaseFragment
import com.raveendra.foodapp_challenge_binar.presentation.checkout.CheckoutActivity
import com.raveendra.foodapp_challenge_binar.presentation.login.LoginActivity
import com.raveendra.foodapp_challenge_binar.util.GenericViewModelFactory
import com.raveendra.foodapp_challenge_binar.util.hideKeyboard
import com.raveendra.foodapp_challenge_binar.util.proceedWhen
import com.raveendra.foodapp_challenge_binar.util.toIdrCurrency
import kotlinx.coroutines.launch

class CartFragment : BaseFragment<FragmentCartBinding>() {
    override val inflateLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCartBinding
        get() = FragmentCartBinding::inflate

    private val viewModel: CartViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val cartDao = database.cartDao()
        val cartDataSource: CartDataSource = CartDatabaseDataSource(cartDao)
        val service = FoodService.invoke(ChuckerInterceptor(requireContext().applicationContext))
        val foodDataSource: FoodDataSource = FoodDataSourceImpl(service)
        val cartRepository: CartRepository = CartRepositoryImpl(cartDataSource,foodDataSource)
        val firebaseAuth = FirebaseAuth.getInstance()
        val dataSource: FirebaseAuthDataSource = FirebaseAuthDataSourceImpl(firebaseAuth)
        val userRepository: UserRepository = UserRepositoryImpl(dataSource)
        GenericViewModelFactory.create(CartViewModel(cartRepository,userRepository))
    }

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
                    result.payload?.let { (carts,totalPrice) ->
                        adapter.submitData(carts)
                        binding.tvTotalPrice.text = totalPrice.toIdrCurrency()
                        if (totalPrice != 0.0) btnCheckout.isEnabled = true
                    }
                },
                doOnError = { err ->
                    cartState.root.isVisible = true
                    cartState.tvError.isVisible = true
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
                    cartState.ivError.isVisible = false
                    cartState.tvError.text = getString(R.string.label_empty_state)
                    pbLoading.isVisible = false
                }

            )
            if(result.payload?.first?.isEmpty() == true){
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
        Toast.makeText(requireContext(), getString(R.string.label_not_login), Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

}