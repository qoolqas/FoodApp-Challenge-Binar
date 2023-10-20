package com.raveendra.foodapp_challenge_binar.presentation.login

import android.content.Intent
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.raveendra.foodapp_challenge_binar.R
import com.raveendra.foodapp_challenge_binar.data.network.firebase.auth.FirebaseAuthDataSource
import com.raveendra.foodapp_challenge_binar.data.network.firebase.auth.FirebaseAuthDataSourceImpl
import com.raveendra.foodapp_challenge_binar.data.repository.UserRepository
import com.raveendra.foodapp_challenge_binar.data.repository.UserRepositoryImpl
import com.raveendra.foodapp_challenge_binar.databinding.ActivityLoginBinding
import com.raveendra.foodapp_challenge_binar.presentation.HomeActivity
import com.raveendra.foodapp_challenge_binar.presentation.base.BaseViewModelActivity
import com.raveendra.foodapp_challenge_binar.presentation.register.RegisterActivity
import com.raveendra.foodapp_challenge_binar.util.GenericViewModelFactory
import com.raveendra.foodapp_challenge_binar.util.highLightWord
import com.raveendra.foodapp_challenge_binar.util.proceedWhen

class LoginActivity : BaseViewModelActivity<LoginViewModel, ActivityLoginBinding>() {
    override val viewModel: LoginViewModel by viewModels {
        val firebaseAuth = FirebaseAuth.getInstance()
        val dataSource: FirebaseAuthDataSource = FirebaseAuthDataSourceImpl(firebaseAuth)
        val userRepository: UserRepository = UserRepositoryImpl(dataSource)
        GenericViewModelFactory.create(LoginViewModel(userRepository))
    }

    override val bindingInflater: (LayoutInflater) -> ActivityLoginBinding
        get() = ActivityLoginBinding::inflate

    override fun setupViews(): Unit = with(binding) {
        setupForm()
        setClickListeners()
        layoutForm.apply {
            tilEmail.isVisible = true
            tilPassword.isVisible = true
        }
        tvNavToRegister.setOnClickListener{
            navigateToRegister()
        }
    }
    private fun setupForm() {
        with(binding.layoutForm) {
            tilEmail.isVisible = true
            tilPassword.isVisible = true
        }
    }


    private fun navigateToMain() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun setClickListeners() {
        binding.btLogin.setOnClickListener {
            doLogin()
        }
        binding.tvNavToRegister.highLightWord(getString(R.string.text_highlight_register)) {
            navigateToRegister()
        }
    }

    private fun doLogin() {
        if (isFormValid()) {
            val email = binding.layoutForm.etEmail.text.toString().trim()
            val password = binding.layoutForm.etPassword.text.toString().trim()
            viewModel.doLogin(email, password)
        }
    }

    private fun isFormValid(): Boolean {
        val email = binding.layoutForm.etEmail.text.toString().trim()
        val password = binding.layoutForm.etPassword.text.toString().trim()

        return checkEmailValidation(email) &&
                checkPasswordValidation(password, binding.layoutForm.tilPassword)
    }

    private fun checkEmailValidation(email: String): Boolean {
        return if (email.isEmpty()) {
            binding.layoutForm.tilEmail.isErrorEnabled = true
            binding.layoutForm.tilEmail.error = getString(R.string.text_error_email_empty)
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.layoutForm.tilEmail.isErrorEnabled = true
            binding.layoutForm.tilEmail.error = getString(R.string.text_error_email_invalid)
            false
        } else {
            binding.layoutForm.tilEmail.isErrorEnabled = false
            true
        }
    }

    private fun checkPasswordValidation(
        confirmPassword: String,
        textInputLayout: TextInputLayout
    ): Boolean {
        return if (confirmPassword.isEmpty()) {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error =
                getString(R.string.text_error_password_empty)
            false
        } else if (confirmPassword.length < 8) {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error =
                getString(R.string.text_error_password_less_than_8_char)
            false
        } else {
            textInputLayout.isErrorEnabled = false
            true
        }
    }
    override fun setupObservers() {
        viewModel.loginResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.pbLoading.isVisible = false
                    binding.btLogin.isVisible = true
                    navigateToMain()
                },
                doOnError = {
                    binding.pbLoading.isVisible = false
                    binding.btLogin.isVisible = true
                    Toast.makeText(
                        this,
                        "Login Failed : ${it.exception?.message.orEmpty()}",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                doOnLoading = {
                    binding.pbLoading.isVisible = true
                    binding.btLogin.isVisible = false
                }
            )
        }
    }

    private fun navigateToRegister() {
        RegisterActivity.navigate(this@LoginActivity)
    }

}