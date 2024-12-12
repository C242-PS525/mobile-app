package com.deboo.frulens.ui.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SignUpViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean>
        get() = _isSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun registerUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _errorMessage.postValue("Please fill in all fields")
            return
        }

        showLoading(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    // Registration successful
                    _isSuccess.postValue(true)
                    Log.d("SignUp", "Account created successfully for: $email")
                } else {
                    // Registration failed
                    _isSuccess.postValue(false)
                    _errorMessage.postValue("Registration failed: ${task.exception?.message}")
                    Log.e("SignUp", "Registration failed", task.exception)
                }
            }
    }

    private fun showLoading(state: Boolean) {
        _isLoading.postValue(state)
    }
}
