package com.deboo.frulens.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deboo.frulens.api.ArticlesClient
import com.deboo.frulens.data.Recipes
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class HomeViewModel : ViewModel() {

    private val _loading = MutableLiveData<Boolean>().apply { value = false }
    val loading: LiveData<Boolean> get() = _loading

    private val _recipes = MutableLiveData<List<Recipes>>()
    val recipes: LiveData<List<Recipes>> get() = _recipes

    fun fetchRecipes() {
        viewModelScope.launch {
            _loading.value = true // Set loading to true when starting the fetch
            try {
                val response = ArticlesClient.getApiService().getRecipes() // Assuming this is a suspend function
                if (response.isNotEmpty()) {
                    Log.d(TAG, "EventsResponse: $response") // Log the entire response
                    _recipes.value = response // Assign the list to LiveData
                } else {
                    Log.e(TAG, "Error in response: $response")
                }
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP Exception: ${e.message}")
            } catch (e: IOException) {
                Log.e(TAG, "Network Error: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected Error: ${e.message}")
            } finally {
                _loading.value = false // Set loading to false after completion
            }
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}
