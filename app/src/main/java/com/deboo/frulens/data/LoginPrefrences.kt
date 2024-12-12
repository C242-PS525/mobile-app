package com.deboo.frulens.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to create a DataStore for login preferences
val Context.loginDataStore: DataStore<Preferences> by preferencesDataStore(name = "login_preferences")

class LoginPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val LOGIN_STATUS = booleanPreferencesKey("login_status")

    // Function to get the current login status
    fun getStatus(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[LOGIN_STATUS] ?: false // Default to false if not set
        }
    }

    // Function to save the login status
    suspend fun saveStatus(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[LOGIN_STATUS] = isLoggedIn
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LoginPreferences? = null

        // Function to get the instance of LoginPreferences
        fun getInstance(dataStore: DataStore<Preferences>): LoginPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = LoginPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
