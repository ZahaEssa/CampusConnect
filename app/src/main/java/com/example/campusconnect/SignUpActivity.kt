package com.example.campusconnect

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var edtFirstName: EditText
    private lateinit var edtLastName: EditText
    private lateinit var edtAdmissionNumber: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize views
        edtFirstName = findViewById(R.id.edtFirstName)
        edtLastName = findViewById(R.id.edtLastName)
        edtAdmissionNumber = findViewById(R.id.edtAdmissionNumber)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)

        mAuth = FirebaseAuth.getInstance()

        val btnSignup = findViewById<Button>(R.id.btnSignup)
        btnSignup.setOnClickListener {
            val firstName = edtFirstName.text.toString().trim()
            val lastName = edtLastName.text.toString().trim()
            val admissionNumber = edtAdmissionNumber.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            // Call the signUp function
            signUp(firstName, lastName, admissionNumber, email, password)
        }

        // Enable back arrow functionality in the action bar
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    private fun signUp(firstName: String, lastName: String, admissionNumber: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid

                    if (userId != null) {
                        val userData = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "admissionNumber" to admissionNumber,
                            "email" to email,
                            "isAdmin" to false,
                            "role" to "student"
                        )

                        FirebaseFirestore.getInstance().collection("users").document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "User signed up successfully.", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this, SignInActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error saving user data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Sign Up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
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

    // Sign-in link click function
    fun goToSignIn(view: View) {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }
}


