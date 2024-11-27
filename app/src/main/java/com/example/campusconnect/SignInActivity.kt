package com.example.campusconnect

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        // Initialize views
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        mAuth = FirebaseAuth.getInstance()

        // Enable back arrow functionality in the action bar
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)  // Ensure this line for setting the back icon

        // Sign-in button action
        val btnSignin = findViewById<Button>(R.id.btnSignin)
        btnSignin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }

        // Redirect to Sign Up if the user doesn't have an account
        val txtSignUp = findViewById<TextView>(R.id.txtSignUp)
        txtSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in successful, check the user's role
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        checkUserRole(userId)
                    } else {
                        Toast.makeText(this, "Authentication failed. Try again.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Sign-In failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserRole(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val isAdmin = document.getBoolean("isAdmin") ?: false
                    val role = document.getString("role") ?: "student"

                    // Redirect user based on their role
                    when {
                        isAdmin -> navigateToDashboard(LeaderDashboardActivity::class.java)
                        role == "student" -> navigateToDashboard(StudentDashboardActivity::class.java)
                        else -> Toast.makeText(this, "Access restricted. Contact admin.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User data not found. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToDashboard(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
        finish() // Close SignInActivity to prevent going back
    }

    // Handle back arrow click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Navigate to the homepage (MainActivity)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Optionally, call finish to ensure this activity is not kept in the stack
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
