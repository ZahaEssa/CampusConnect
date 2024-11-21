package com.example.campusconnect

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.widget.Button
import android.content.Intent
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

        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        mAuth = FirebaseAuth.getInstance()

        val btnSignin = findViewById<Button>(R.id.btnSignin)
        btnSignin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in was successful, now check the role
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        checkUserRole(userId)
                    }
                } else {
                    Toast.makeText(this, "Sign In failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
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

                    // Redirect based on isAdmin and role
                    when {
                        isAdmin -> {
                            val intent = Intent(this, LeaderDashboardActivity::class.java)
                            startActivity(intent)
                            finish() // Close the SignInActivity
                        }
                        role == "student" -> {
                            val intent = Intent(this, StudentDashboardActivity::class.java)
                            startActivity(intent)
                            finish() // Close the SignInActivity
                        }
                        else -> {
                            Toast.makeText(this, "Access restricted. Contact admin.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
