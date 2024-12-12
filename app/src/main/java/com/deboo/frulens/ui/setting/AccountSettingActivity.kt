package com.deboo.frulens.ui.setting

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.deboo.frulens.databinding.ActivityAccountSettingsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSettingsBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set click listener for the update password button
        binding.btnUpdatePassword.setOnClickListener {
            val currentPassword = binding.etCurrentpassword.text.toString()
            val newPassword = binding.etNewpassword.text.toString()
            val confirmNewPassword = binding.etConfirmNewpassword.text.toString()

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmNewPassword) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                updatePassword(currentPassword, newPassword)
            }
        }
    }

    private fun updatePassword(currentPassword: String, newPassword: String) {
        showLoading(true)

        // Re-authenticate the user with the current password
        val user = auth.currentUser
        val credential = EmailAuthProvider.getCredential(user?.email!!, currentPassword)

        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Proceed to update the password
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            showLoading(false)
                            if (updateTask.isSuccessful) {
                                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                finish()  // Close the activity after successful update
                            } else {
                                Toast.makeText(this, "Password update failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    showLoading(false)
                    Toast.makeText(this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.cvProgress.visibility = View.VISIBLE
        } else {
            binding.cvProgress.visibility = View.GONE
        }
    }
}