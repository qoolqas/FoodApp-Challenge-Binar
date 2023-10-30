package com.raveendra.foodapp.presentation.login

import android.content.Intent
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import com.raveendra.foodapp.R
import com.raveendra.foodapp.databinding.ActivityLoginBinding
import com.raveendra.foodapp.presentation.HomeActivity
import com.raveendra.foodapp.presentation.base.BaseViewModelActivity
import com.raveendra.foodapp.presentation.register.RegisterActivity
import com.raveendra.foodapp.util.highLightWord
import com.raveendra.foodapp.util.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseViewModelActivity<LoginViewModel, ActivityLoginBinding>() {
    override val viewModel: LoginViewModel by viewModel()

    override val bindingInflater: (LayoutInflater) -> ActivityLoginBinding
        get() = ActivityLoginBinding::inflate

    override fun setupViews(): Unit = with(binding) {
        setupForm()
        setClickListeners()
        layoutForm.apply {
            tilEmail.isVisible = true
            tilPassword.isVisible = true
        }
        tvNavToRegister.setOnClickListener {
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
        startActivity(
            Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
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
