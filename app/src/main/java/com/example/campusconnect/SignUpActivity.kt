package com.example.campusconnect

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.RadioButton
import android.widget.Button
import android.widget.Toast
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
    private lateinit var radioGroupRole: RadioGroup
    private lateinit var radioLeader: RadioButton
    private lateinit var radioStudent: RadioButton

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

        radioGroupRole = findViewById(R.id.radioGroupRole)
        radioLeader = findViewById(R.id.radioLeader)
        radioStudent = findViewById(R.id.radioStudent)

        val btnSignup = findViewById<Button>(R.id.btnSignup)
        btnSignup.setOnClickListener {
            val firstName = edtFirstName.text.toString().trim()
            val lastName = edtLastName.text.toString().trim()
            val admissionNumber = edtAdmissionNumber.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            // Get the selected role
            val selectedRole = when (radioGroupRole.checkedRadioButtonId) {
                R.id.radioLeader -> "leader"
                R.id.radioStudent -> "student"
                else -> "student" // Default role (if nothing is selected)
            }

            // Validate input
            if (firstName.isNotEmpty() && lastName.isNotEmpty() && admissionNumber.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                signUp(firstName, lastName, admissionNumber, email, password, selectedRole)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUp(firstName: String, lastName: String, admissionNumber: String, email: String, password: String, role: String) {
        // Create the user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // After successful sign-up, store the user's details in Firestore
                    val userId = FirebaseAuth.getInstance().currentUser?.uid

                    if (userId != null) {
                        val userData = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "admissionNumber" to admissionNumber,
                            "email" to email,
                            "role" to role
                        )

                        // Save user data to Firestore
                        FirebaseFirestore.getInstance().collection("users").document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "User signed up successfully.", Toast.LENGTH_SHORT).show()

                                // Redirect to SignInActivity after successful sign-up
                                val intent = Intent(this, SignInActivity::class.java)
                                startActivity(intent)
                                finish()  // Close SignUpActivity to prevent going back to it
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error saving user data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // If sign-up failed, show an error message
                    Toast.makeText(this, "Sign Up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
