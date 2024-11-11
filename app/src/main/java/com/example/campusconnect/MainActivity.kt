package com.example.campusconnect

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Link buttons with their XML IDs
        val signUpButton: Button = findViewById(R.id.button_signup)
        val signInButton: Button = findViewById(R.id.button_signin)

        // Set click listeners
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
    }
}
