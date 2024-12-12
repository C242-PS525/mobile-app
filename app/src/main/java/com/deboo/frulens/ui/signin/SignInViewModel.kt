package com.deboo.frulens.ui.signin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.deboo.frulens.data.LoginPreferences
import com.deboo.frulens.data.loginDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val loginPreferences = LoginPreferences.getInstance(application.loginDataStore)
    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> get() = _loginStatus
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

    // Function to perform login with backoff and retry logic
    fun loginUser(email: String, password: String, retryCount: Int = 3) {
        if (retryCount <= 0) {
            _errorMessage.postValue("Too many attempts. Please try again later.")
            return
        }

        viewModelScope.launch {
            try {
                showLoading(true)
                val result = auth.signInWithEmailAndPassword(email, password).await()
                if (result.user != null) {
                    // Login successful, save status and update UI
                    loginPreferences.saveStatus(true)
                    _loginStatus.postValue(true)
                    _isSuccess.postValue(true)
                } else {
                    _loginStatus.postValue(false)
                    _isSuccess.postValue(false)
                    _errorMessage.postValue("Login failed. Please check your credentials.")
                }
            } catch (e: FirebaseAuthException) {
                if (e.message?.contains("blocked all requests") == true) {
                    // Handle blocked requests with retry logic
                    delay(5000) // Add delay before retrying
                    loginUser(email, password, retryCount - 1)
                } else {
                    // Handle other authentication errors
                    _loginStatus.postValue(false)
                    _errorMessage.postValue("Authentication failed: ${e.message}")
                }
            } catch (e: Exception) {
                _loginStatus.postValue(false)
                _errorMessage.postValue("An unexpected error occurred: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    // Function to check login status (can be used for checking if the user is already logged in)
    fun checkLoginStatus() {
        viewModelScope.launch {
            loginPreferences.getStatus().collect { isLoggedIn ->
                _loginStatus.postValue(isLoggedIn)
            }
        }
    }

    private fun showLoading(state: Boolean) {
        _isLoading.postValue(state)
    }
}
