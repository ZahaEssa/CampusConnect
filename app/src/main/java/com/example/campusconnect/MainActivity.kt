package com.example.campusconnect

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request notification permissions for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }

        // Link buttons with their XML IDs
        val signUpButton: Button = findViewById(R.id.button_signup)
        val signInButton: Button = findViewById(R.id.button_signin)

        signUpButton.setOnClickListener {
            // Navigate to the Sign Up activity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        signInButton.setOnClickListener {
            // Navigate to the Sign In activity
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        // Retrieve and log the Firebase Messaging token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM Token", "Token: ${task.result}")
            } else {
                Log.e("FCM Token", "Token generation failed", task.exception)
            }
        }
    }

    // Handling the result of the notification permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permissions", "Notification permission granted")
            } else {
                Log.e("Permissions", "Notification permission denied")
            }
        }
    }
}

