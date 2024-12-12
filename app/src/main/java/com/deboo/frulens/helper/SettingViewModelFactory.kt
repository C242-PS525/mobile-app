package com.deboo.frulens.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.deboo.frulens.data.SettingsPreferences
import com.deboo.frulens.ui.setting.SettingsViewModel

class SettingViewModelFactory(private val pref: SettingsPreferences) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}