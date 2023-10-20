package com.raveendra.foodapp_challenge_binar.presentation.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import coil.load
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.raveendra.foodapp_challenge_binar.R
import com.raveendra.foodapp_challenge_binar.data.local.database.AppDatabase
import com.raveendra.foodapp_challenge_binar.data.local.database.datasource.CartDataSource
import com.raveendra.foodapp_challenge_binar.data.local.database.datasource.CartDatabaseDataSource
import com.raveendra.foodapp_challenge_binar.data.model.FoodViewParam
import com.raveendra.foodapp_challenge_binar.data.network.api.datasource.FoodDataSource
import com.raveendra.foodapp_challenge_binar.data.network.api.datasource.FoodDataSourceImpl
import com.raveendra.foodapp_challenge_binar.data.network.api.service.FoodService
import com.raveendra.foodapp_challenge_binar.data.repository.CartRepository
import com.raveendra.foodapp_challenge_binar.data.repository.CartRepositoryImpl
import com.raveendra.foodapp_challenge_binar.databinding.ActivityDetailBinding
import com.raveendra.foodapp_challenge_binar.presentation.base.BaseViewModelActivity
import com.raveendra.foodapp_challenge_binar.util.GenericViewModelFactory
import com.raveendra.foodapp_challenge_binar.util.toIdrCurrency

class DetailActivity : BaseViewModelActivity<DetailViewModel, ActivityDetailBinding>() {
    companion object{
        const val EXTRA_FOOD = "EXTRA_FOOD"
        fun navigate(context: Context, foodViewParam: FoodViewParam) = with(context) {
            startActivity(
                Intent(
                    this,
                    DetailActivity::class.java
                ).putExtra(EXTRA_FOOD, foodViewParam)
            )
        }
    }

    override val viewModel: DetailViewModel by viewModels {
        val database = AppDatabase.getInstance(this)
        val cartDao = database.cartDao()
        val cartDataSource: CartDataSource = CartDatabaseDataSource(cartDao)
        val service = FoodService.invoke(ChuckerInterceptor(this.applicationContext))
        val foodDataSource: FoodDataSource = FoodDataSourceImpl(service)
        val repoCart: CartRepository = CartRepositoryImpl(cartDataSource,foodDataSource)
        GenericViewModelFactory.create(
            DetailViewModel(repoCart,intent?.extras)
        )
    }

    override val bindingInflater: (LayoutInflater) -> ActivityDetailBinding
        get() = ActivityDetailBinding::inflate

    override fun setupObservers() {
        setupDetail(viewModel.food)
        viewModel.productCountLiveData.observe(this){ count ->
            binding.inclButtonDetail.tvTotalQty.text = count.toString()
            viewModel.priceLiveData.observe(this){
                val totalPrice = it
                binding.inclButtonDetail.tvTotalQty.text = count.toString()
                binding.inclButtonDetail.btAddToCart.text = getString(R.string.text_add_to_cart, totalPrice.toIdrCurrency())
            }
        }
        viewModel.onSuccessAddToCart.observe(this){
            if (it) Toast.makeText(this, getString(R.string.text_success_add_to_cart), Toast.LENGTH_SHORT).show()
            else Toast.makeText(this, getString(R.string.label_something_wrong), Toast.LENGTH_SHORT).show()
        }

    }

    override fun setupViews() = with(binding) {

        ivBackDetail.setOnClickListener {
            finish()
        }
    }

    private fun setupDetail(foodData : FoodViewParam?) = with(binding){
        foodData.let { data ->
            ivImageDetail.load(data?.imageUrl) {
                crossfade(true)
            }
            tvTitleDetail.text = data?.nama
            tvDescriptionDetail.text = data?.detail
            tvPriceDetail.text = data?.harga?.toIdrCurrency()
            tvLocationDescDetail.text = getString(R.string.text_location_detail, data?.alamatResto)
            tvLocationDescDetail.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.app.goo.gl/h4wQKqaBuXzftGK77"))
                startActivity(intent)
            }
        }
        setupButton(foodData)
    }

    private fun setupButton(foodData : FoodViewParam?) = with(binding) {
        foodData.let { data ->
            inclButtonDetail.btAddToCart.text =
                getString(R.string.text_add_to_cart, data?.harga?.toIdrCurrency())
            inclButtonDetail.btAddToCart.setOnClickListener {
                viewModel.addToCart()
            }
        }
        inclButtonDetail.apply {

            clPlusQty.setOnClickListener {
                viewModel.increment()
            }
            clMinusQty.setOnClickListener {
                viewModel.decrement()
            }
        }
    }
}