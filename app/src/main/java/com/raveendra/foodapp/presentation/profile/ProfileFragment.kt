package com.raveendra.foodapp.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.raveendra.foodapp.R
import com.raveendra.foodapp.databinding.FragmentProfileBinding
import com.raveendra.foodapp.presentation.base.BaseFragment
import com.raveendra.foodapp.presentation.login.LoginActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override val inflateLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate

    private val viewModel: ProfileViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkIfUserLogin()
        setupViews()
        observeData()
    }

    private fun observeData() {
        viewModel.getCurrentUser()?.let {
            binding.etProfileName.setText(it.fullName)
            binding.etProfileEmail.setText(it.email)
        }
    }

    private fun setupViews() = with(binding) {
        binding.inclToolbar.ivLogout.isVisible = true
        inclToolbar.tvTitle.text = getString(R.string.title_profile)
        ivProfile.load(R.drawable.img_placeholder) {
            transformations(CircleCropTransformation())
            crossfade(true)
        }
        binding.inclToolbar.ivLogout.setOnClickListener {
            doLogout()
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
    private fun doLogout() {
        val dialog = AlertDialog.Builder(requireContext()).setMessage("Do you want to logout ?")
            .setPositiveButton(
                "Yes"
            ) { dialog, id ->
                viewModel.doLogout()
                navigateToLogin()
            }
            .setNegativeButton(
                "No"
            ) { dialog, id ->
                // no-op , do nothing
            }.create()
        dialog.show()
    }
}
