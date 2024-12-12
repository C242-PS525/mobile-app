package com.deboo.frulens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.deboo.frulens.helper.SettingViewModelFactory
import com.deboo.frulens.data.SettingsPreferences
import com.deboo.frulens.ui.setting.SettingsViewModel
import com.deboo.frulens.data.dataStore
import com.deboo.frulens.ui.signin.SignInActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        // Remove the action bar
        supportActionBar?.hide()

        val settingsViewModel = ViewModelProvider(
            this,
            SettingViewModelFactory(SettingsPreferences.getInstance(dataStore))
        )[SettingsViewModel::class.java]

        settingsViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                settingsViewModel.saveThemeSetting(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                settingsViewModel.saveThemeSetting(false)
            }
        }

        val logoImage: ImageView = findViewById(R.id.logoImage)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        logoImage.startAnimation(fadeIn)

        // Delay for 3 seconds (3000ms)
        Handler(Looper.getMainLooper()).postDelayed({
            // Navigate to MainActivity
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }, 3000)
    }
}