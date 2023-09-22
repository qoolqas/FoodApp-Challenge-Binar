package com.raveendra.foodapp_challenge_binar.presentation.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.raveendra.foodapp_challenge_binar.R
import com.raveendra.foodapp_challenge_binar.databinding.FragmentDetailBinding
import com.raveendra.foodapp_challenge_binar.presentation.base.BaseFragment
import com.raveendra.foodapp_challenge_binar.util.toIdrCurrency


class DetailFragment : BaseFragment<FragmentDetailBinding>() {

    private val viewModel by viewModels<DetailViewModel>()

    private val args by navArgs<DetailFragmentArgs>()

    override val inflateLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDetailBinding
        get() = FragmentDetailBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeData()
    }

    private fun observeData() {
        viewModel.countItem.observe(requireActivity()) { count ->
            setupTotal(count)
        }
    }

    private fun setupViews(): Unit = with(binding) {
        args.foodData.let { data ->
            ivBackDetail.setOnClickListener {
                findNavController().navigateUp()
            }
            ivImageDetail.load(data.imageUrl) {
                crossfade(true)
            }
            tvTitleDetail.text = data.title
            tvDescriptionDetail.text = data.description
            tvPriceDetail.text = data.price.toIdrCurrency()
            tvLocationDescDetail.text = getString(R.string.text_location_detail, data.address)
            tvLocationDescDetail.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.addressUrl))
                startActivity(intent)
            }
        }
        setupButton()
    }

    private fun setupButton() = with(binding) {
        args.foodData.let { data ->
            inclButtonDetail.btAddToCart.text =
                getString(R.string.text_add_to_cart, data.price.toIdrCurrency())
            inclButtonDetail.btAddToCart.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.text_success_add_to_cart, data.title),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        inclButtonDetail.apply {
            tvTotalQty.text = viewModel.getCount().toString()
            clPlusQty.setOnClickListener {
                viewModel.increment()
            }
            clMinusQty.setOnClickListener {
                viewModel.decrement()
            }

        }
    }

    private fun setupTotal(count : Int) = with(binding) {
        val totalPrice = args.foodData.price * count
        inclButtonDetail.tvTotalQty.text = count.toString()
        inclButtonDetail.btAddToCart.text =
            getString(R.string.text_add_to_cart, totalPrice.toIdrCurrency())
    }
}