package com.raveendra.foodapp_challenge_binar.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.raveendra.foodapp_challenge_binar.R
import com.raveendra.foodapp_challenge_binar.data.network.firebase.auth.FirebaseAuthDataSource
import com.raveendra.foodapp_challenge_binar.data.network.firebase.auth.FirebaseAuthDataSourceImpl
import com.raveendra.foodapp_challenge_binar.data.repository.UserRepository
import com.raveendra.foodapp_challenge_binar.data.repository.UserRepositoryImpl
import com.raveendra.foodapp_challenge_binar.databinding.FragmentProfileBinding
import com.raveendra.foodapp_challenge_binar.presentation.base.BaseFragment
import com.raveendra.foodapp_challenge_binar.presentation.login.LoginActivity
import com.raveendra.foodapp_challenge_binar.util.GenericViewModelFactory
import kotlinx.coroutines.launch

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override val inflateLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate


    private val viewModel: ProfileViewModel by viewModels {
        val firebaseAuth = FirebaseAuth.getInstance()
        val dataSource: FirebaseAuthDataSource = FirebaseAuthDataSourceImpl(firebaseAuth)
        val userRepository: UserRepository = UserRepositoryImpl(dataSource)
        GenericViewModelFactory.create(ProfileViewModel(userRepository))
    }

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
        ivProfile.load(R.drawable.img_placeholder){
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
                //no-op , do nothing
            }.create()
        dialog.show()
    }
}