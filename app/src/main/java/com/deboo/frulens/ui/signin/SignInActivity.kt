package com.deboo.frulens.ui.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.deboo.frulens.MainActivity
import com.deboo.frulens.databinding.ActivitySigninBinding
import com.deboo.frulens.ui.signup.SignUpActivity

class SignInActivity : AppCompatActivity() {

    private lateinit var bind: ActivitySigninBinding
    private val loginViewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // Check if the user is already logged in
        loginViewModel.checkLoginStatus()

        // Observe the login status
        loginViewModel.loginStatus.observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                // Navigate to MainActivity if logged in
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                setupView()
                playAnimation()
                setupListener()
                observeViewModel()
            }
        }
    }

    private fun setupView() {
        bind.etEmail.setTextInputLayout(bind.tilEmail)
        bind.etPassword.setTextInputLayout(bind.tilPassword)
    }

    private fun playAnimation() {
        val email = ObjectAnimator
            .ofFloat(bind.tilEmail, View.ALPHA, 1f)
            .setDuration(250)
        val password = ObjectAnimator
            .ofFloat(bind.tilPassword, View.ALPHA, 1f)
            .setDuration(250)
        val signin = ObjectAnimator
            .ofFloat(bind.btnLogin, View.ALPHA, 1f)
            .setDuration(250)

        AnimatorSet().apply {
            playSequentially(email, password, signin)
            startDelay = 250
        }.start()
    }

    private fun setupListener() {
        bind.btnRegister.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        bind.btnLogin.setOnClickListener {
            val email = bind.etEmail.text.toString()
            val password = bind.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            } else {
                loginViewModel.loginUser(email, password)
            }
        }
    }
    private fun observeViewModel() {
        // Observe loading state to show/hide loading indicator
        loginViewModel.isLoading.observe(this) { isLoading ->
            bind.cvProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error messages
        loginViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        loginViewModel.isSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Welcome to FruLens!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }
    }
}
