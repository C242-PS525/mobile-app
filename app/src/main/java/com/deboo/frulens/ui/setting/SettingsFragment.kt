package com.deboo.frulens.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.deboo.frulens.R
import com.deboo.frulens.data.LoginPreferences
import com.deboo.frulens.data.SettingsPreferences
import com.deboo.frulens.data.dataStore
import com.deboo.frulens.data.loginDataStore
import com.deboo.frulens.helper.SettingViewModelFactory
import com.deboo.frulens.ui.signin.SignInActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private lateinit var switchTheme: SwitchMaterial
    private val settingViewModel: SettingsViewModel by viewModels {
        SettingViewModelFactory(SettingsPreferences.getInstance(requireContext().dataStore))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        switchTheme = view.findViewById(R.id.switch_theme)

        // Observe theme settings and update switch
        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }

        // Handle switch toggle
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveThemeSetting(isChecked)
        }

        val setAcc: Button = view.findViewById(R.id.SetAcBtn)
        setAcc.setOnClickListener {
            val intent = Intent(requireContext(), AccountSettingsActivity::class.java)
            startActivity(intent)
        }

        val out: Button = view.findViewById(R.id.btn_out)
        out.setOnClickListener {
            // Handle sign-out button click
            signOut()
        }
        return view
    }
    private fun signOut() {
        // Clear login status from DataStore
        val loginPreferences = LoginPreferences.getInstance(requireContext().loginDataStore)
        lifecycleScope.launch {
            loginPreferences.saveStatus(false) // Set login status to false
        }

        // Show a Toast message indicating that the user has been signed out
        Toast.makeText(requireContext(), "You have been signed out!", Toast.LENGTH_SHORT).show()

        // Redirect to the SignInActivity (login screen)
        val intent = Intent(requireContext(), SignInActivity::class.java)
        startActivity(intent)

        // Optional: Finish the current activity (if needed)
        requireActivity().finish()
    }
}