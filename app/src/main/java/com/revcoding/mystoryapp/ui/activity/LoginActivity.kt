package com.revcoding.mystoryapp.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.revcoding.mystoryapp.R
import com.revcoding.mystoryapp.customview.MyButton
import com.revcoding.mystoryapp.data.model.UserPreference
import com.revcoding.mystoryapp.databinding.ActivityLoginBinding
import com.revcoding.mystoryapp.ViewModelFactory
import com.revcoding.mystoryapp.ui.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var binding: ActivityLoginBinding
    private lateinit var myButton: MyButton
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }

    private fun setupAction() {
        myButton = binding.loginButton

        myButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = getString(R.string.enter_email)
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = getString(R.string.enter_password)
                }
                else -> {
                    loginViewModel.getUser(email, password)

                    loginViewModel.isLoading.observe(this) {
                        it.getContentIfNotHandled()?.let {  state ->
                            showLoading(state)
                        }
                    }

                    loginViewModel.isFailed.observe(this) {
                        it.getContentIfNotHandled()?.let {
                            isFailed()
                        }
                    }

                    loginViewModel.isAuthSuccess.observe(this) {
                        it.getContentIfNotHandled()?.let { success ->
                            isLoginSuccess(success)
                        }
                    }
                }
            }
        }
        binding.textLinkToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun isFailed() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.title_failure))
            setMessage(getString(R.string.message_there_is_problem))
            setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }

    private fun isLoginSuccess(success: Boolean) {
        if (success) {
            AlertDialog.Builder(this).apply {
                setCancelable(false)
                setTitle(getString(R.string.title_success))
                setMessage(getString(R.string.message_login_successfully))
                setPositiveButton(getString(R.string.next)) { _, _ ->
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.title_failure))
                setMessage("Email/Password yang kamu masukkan salah, harap cek lagi ya..")
                setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
                create()
                show()
            }
        }
    }
}