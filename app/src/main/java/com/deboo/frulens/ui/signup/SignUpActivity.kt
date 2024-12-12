package com.deboo.frulens.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.deboo.frulens.databinding.ActivitySignupBinding
import com.deboo.frulens.ui.signin.SignInActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var bind: ActivitySignupBinding
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setupView()
        playAnimation()
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        bind.btnLogin.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        bind.btnRegister.setOnClickListener {
            val email = bind.etEmail.text.toString()
            val password = bind.etPassword.text.toString()

            signUpViewModel.registerUser(email, password)
        }
    }

    private fun setupView() {
        bind.etEmail.setTextInputLayout(bind.tilEmail)
        bind.etPassword.setTextInputLayout(bind.tilPassword)
    }

    private fun playAnimation() {
        val email = ObjectAnimator.ofFloat(bind.tilEmail, View.ALPHA, 1f).setDuration(250)
        val password = ObjectAnimator.ofFloat(bind.tilPassword, View.ALPHA, 1f).setDuration(250)
        val register = ObjectAnimator.ofFloat(bind.btnRegister, View.ALPHA, 1f).setDuration(250)
        AnimatorSet().apply {
            playSequentially(email, password, register)
            startDelay = 250
        }.start()
    }

    private fun observeViewModel() {
        signUpViewModel.isLoading.observe(this) { isLoading ->
            bind.cvProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        signUpViewModel.isSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }

        signUpViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}