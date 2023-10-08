package com.raveendra.foodapp_challenge_binar.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import coil.transform.CircleCropTransformation
import com.raveendra.foodapp_challenge_binar.R
import com.raveendra.foodapp_challenge_binar.databinding.FragmentProfileBinding
import com.raveendra.foodapp_challenge_binar.presentation.base.BaseFragment

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override val inflateLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() = with(binding) {
        inclToolbar.tvTitle.text = getString(R.string.title_profile)
        ivProfile.load(R.drawable.img_dimsum){
            transformations(CircleCropTransformation())
            crossfade(true)
        }
    }
}