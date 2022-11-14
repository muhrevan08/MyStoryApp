package com.revcoding.mystoryapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.revcoding.mystoryapp.R
import com.revcoding.mystoryapp.customview.MyButton
import com.revcoding.mystoryapp.databinding.ActivityRegisterBinding
import com.revcoding.mystoryapp.ui.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var myButton: MyButton
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
    }

    private fun setupAction() {
        myButton = binding.registerButton

        myButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            when {
                name.isEmpty() -> {
                    binding.edRegisterName.error = getString(R.string.enter_name)
                }
                email.isEmpty() -> {
                    binding.edRegisterEmail.error = getString(R.string.enter_email)
                }
                password.isEmpty() -> {
                    binding.edRegisterPassword.error = getString(R.string.enter_password)
                }
                else -> {
                    registerViewModel.registerUser(name, email, password)

                    registerViewModel.isRegisterSuccess.observe(this) {
                        it.getContentIfNotHandled()?.let { success ->
                            isRegisterSuccess(success)
                        }
                    }

                    registerViewModel.isLoading.observe(this) {
                        it.getContentIfNotHandled()?.let { state ->
                            showLoading(state)
                        }
                    }

                    registerViewModel.isFailed.observe(this) {
                        it.getContentIfNotHandled()?.let {
                            isFailed()
                        }
                    }
                }
            }
        }
        binding.textLinkToLogin.setOnClickListener {
            val moveToLogin = Intent(this, LoginActivity::class.java)
            startActivity(moveToLogin)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun isFailed() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.title_failure))
            setMessage(getString(R.string.message_there_is_problem))
            setPositiveButton(getString(R.string.next)) { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }

    private fun isRegisterSuccess(success: Boolean) {
        if (success) {
            AlertDialog.Builder(this).apply {
                setCancelable(false)
                setTitle(getString(R.string.title_success))
                setMessage(getString(R.string.message_account_sucessfully_registered))
                setPositiveButton(getString(R.string.next)) { _, _ ->
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.title_account_registered))
                setMessage(getString(R.string.message_registered_email_try_again))
                setPositiveButton(getString(R.string.next)) { dialog, _ -> dialog.dismiss() }
                create()
                show()
            }
        }
    }
}