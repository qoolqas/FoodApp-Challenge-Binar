package com.raveendra.foodapp_challenge_binar.presentation.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import coil.load
import com.raveendra.foodapp_challenge_binar.R
import com.raveendra.foodapp_challenge_binar.data.local.database.AppDatabase
import com.raveendra.foodapp_challenge_binar.data.local.database.datasource.CartDataSource
import com.raveendra.foodapp_challenge_binar.data.local.database.datasource.CartDatabaseDataSource
import com.raveendra.foodapp_challenge_binar.data.local.database.datasource.FoodDataSource
import com.raveendra.foodapp_challenge_binar.data.local.database.datasource.FoodDatabaseDataSource
import com.raveendra.foodapp_challenge_binar.data.repository.CartRepository
import com.raveendra.foodapp_challenge_binar.data.repository.CartRepositoryImpl
import com.raveendra.foodapp_challenge_binar.data.repository.FoodRepository
import com.raveendra.foodapp_challenge_binar.data.repository.FoodRepositoryImpl
import com.raveendra.foodapp_challenge_binar.databinding.ActivityDetailBinding
import com.raveendra.foodapp_challenge_binar.model.Food
import com.raveendra.foodapp_challenge_binar.presentation.base.BaseViewModelActivity
import com.raveendra.foodapp_challenge_binar.util.GenericViewModelFactory
import com.raveendra.foodapp_challenge_binar.util.toIdrCurrency

class DetailActivity : BaseViewModelActivity<DetailViewModel, ActivityDetailBinding>() {
    companion object{
        private const val EXTRA_ID = "EXTRA_ID"
        fun navigate(context: Context, productId: Int) = with(context) {
            startActivity(
                Intent(
                    this,
                    DetailActivity::class.java
                ).putExtra(EXTRA_ID, productId)
            )
        }
    }
    private val productIdExtra: Int by lazy { intent.getIntExtra(EXTRA_ID, 0) }

    override val viewModel: DetailViewModel by viewModels {
        val database = AppDatabase.getInstance(this)
        val cartDao = database.cartDao()
        val foodDao = database.foodDao()
        val cartDataSource: CartDataSource = CartDatabaseDataSource(cartDao)
        val foodDataSource: FoodDataSource = FoodDatabaseDataSource(foodDao)
        val repoCart: CartRepository = CartRepositoryImpl(cartDataSource)
        val repoFood: FoodRepository = FoodRepositoryImpl(foodDataSource)
        GenericViewModelFactory.create(
            DetailViewModel(repoFood,repoCart,productIdExtra)
        )
    }

    override val bindingInflater: (LayoutInflater) -> ActivityDetailBinding
        get() = ActivityDetailBinding::inflate

    override fun setupObservers() {
        viewModel.foodDetailLiveData.observe(this){foodDetail ->
            setupDetail(foodDetail.payload)
        }
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

    private fun setupDetail(foodData : Food?) = with(binding){
        foodData.let { data ->
            ivImageDetail.load(data?.productImgUrl) {
                crossfade(true)
            }
            tvTitleDetail.text = data?.title
            tvDescriptionDetail.text = data?.desc
            tvPriceDetail.text = data?.price?.toIdrCurrency()
            tvLocationDescDetail.text = getString(R.string.text_location_detail, data?.addressDescription)
            tvLocationDescDetail.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data?.addressUrl))
                startActivity(intent)
            }
        }
        setupButton(foodData)
    }

    private fun setupButton(foodData : Food?) = with(binding) {
        foodData.let { data ->
            inclButtonDetail.btAddToCart.text =
                getString(R.string.text_add_to_cart, data?.price?.toIdrCurrency())
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