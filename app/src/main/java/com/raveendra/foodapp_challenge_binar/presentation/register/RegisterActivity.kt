package com.raveendra.foodapp_challenge_binar.presentation.register

import android.content.Context
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
import com.raveendra.foodapp_challenge_binar.databinding.ActivityRegisterBinding
import com.raveendra.foodapp_challenge_binar.presentation.HomeActivity
import com.raveendra.foodapp_challenge_binar.presentation.base.BaseViewModelActivity
import com.raveendra.foodapp_challenge_binar.util.GenericViewModelFactory
import com.raveendra.foodapp_challenge_binar.util.highLightWord
import com.raveendra.foodapp_challenge_binar.util.proceedWhen

class RegisterActivity : BaseViewModelActivity<RegisterViewModel, ActivityRegisterBinding>() {
    companion object {
        fun navigate(context: Context) = with(context) {
            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }
    }

    override val viewModel: RegisterViewModel by viewModels {
        val firebaseAuth = FirebaseAuth.getInstance()
        val dataSource: FirebaseAuthDataSource = FirebaseAuthDataSourceImpl(firebaseAuth)
        val userRepository: UserRepository = UserRepositoryImpl(dataSource)
        GenericViewModelFactory.create(RegisterViewModel(userRepository))
    }

    override val bindingInflater: (LayoutInflater) -> ActivityRegisterBinding
        get() = ActivityRegisterBinding::inflate

    override fun setupViews(): Unit = with(binding) {
        setupForm()
        setClickListeners()
        layoutForm.apply {
            tilName.isVisible = true
            tilEmail.isVisible = true
            tilPassword.isVisible = true
            tilConfirmPassword.isVisible = true
        }
        tvNavToLogin.setOnClickListener {
            finish()
        }
        btnRegister.setOnClickListener {
            viewModel.doRegister(
                layoutForm.etName.text.toString(),
                layoutForm.etEmail.text.toString(),
                layoutForm.etPassword.text.toString()
            )
        }
    }

    override fun setupObservers() {
        viewModel.registerResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.pbLoading.isVisible = false
                    binding.btnRegister.isVisible = true
                    navigateToMain()
                },
                doOnError = {
                    binding.pbLoading.isVisible = false
                    binding.btnRegister.isVisible = true
                    Toast.makeText(
                        this,
                        "Login Failed : ${it.exception?.message.orEmpty()}",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                doOnLoading = {
                    binding.pbLoading.isVisible = true
                    binding.btnRegister.isVisible = false
                }
            )
        }
    }

    private fun setClickListeners() {
        binding.btnRegister.setOnClickListener {
            doRegister()
        }
        binding.tvNavToLogin.highLightWord(getString(R.string.text_highlight_login_here)) {
            navigateToLogin()
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun navigateToLogin() {
        finish()
    }

    private fun doRegister() {
        if (isFormValid()) {
            val email = binding.layoutForm.etEmail.text.toString().trim()
            val password = binding.layoutForm.etPassword.text.toString().trim()
            val fullName = binding.layoutForm.etName.text.toString().trim()
            viewModel.doRegister(fullName, email, password)
        }
    }

    private fun setupForm() {
        with(binding.layoutForm) {
            tilEmail.isVisible = true
            tilPassword.isVisible = true
            tilName.isVisible = true
            tilConfirmPassword.isVisible = true
        }
    }

    private fun isFormValid(): Boolean {
        val password = binding.layoutForm.etPassword.text.toString().trim()
        val confirmPassword = binding.layoutForm.etConfirmPassword.text.toString().trim()
        val fullName = binding.layoutForm.etName.text.toString().trim()
        val email = binding.layoutForm.etEmail.text.toString().trim()

        return checkNameValidation(fullName) && checkEmailValidation(email) &&
                checkPasswordValidation(password, binding.layoutForm.tilPassword) &&
                checkPasswordValidation(confirmPassword, binding.layoutForm.tilConfirmPassword) &&
                checkPwdAndConfirmPwd(password, confirmPassword)

    }

    private fun checkNameValidation(fullName: String): Boolean {
        return if (fullName.isEmpty()) {
            binding.layoutForm.tilName.isErrorEnabled = true
            binding.layoutForm.tilName.error = getString(R.string.text_error_name_cannot_empty)
            false
        } else {
            binding.layoutForm.tilName.isErrorEnabled = false
            true
        }
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

    private fun checkPwdAndConfirmPwd(password: String, confirmPassword: String): Boolean {
        return if (password != confirmPassword) {
            binding.layoutForm.tilPassword.isErrorEnabled = true
            binding.layoutForm.tilPassword.error =
                getString(R.string.text_password_does_not_match)
            binding.layoutForm.tilConfirmPassword.isErrorEnabled = true
            binding.layoutForm.tilConfirmPassword.error =
                getString(R.string.text_password_does_not_match)
            false
        } else {
            binding.layoutForm.tilPassword.isErrorEnabled = false
            binding.layoutForm.tilConfirmPassword.isErrorEnabled = false
            true
        }
    }

}