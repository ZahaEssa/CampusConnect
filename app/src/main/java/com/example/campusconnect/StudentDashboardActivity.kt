package com.example.campusconnect

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var viewClubsButton: Button
    private lateinit var leaderPanelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        // Initialize buttons
        viewClubsButton = findViewById(R.id.viewClubsButton)
        leaderPanelButton = findViewById(R.id.leaderPanelButton) // Ensure this button is in your layout XML
        leaderPanelButton.visibility = View.GONE // Hide the leader panel button by default

        // Check if the current user is a leader
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            FirebaseFirestore.getInstance().collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val role = document.getString("role") ?: "student"

                        // Adjust UI based on the user's role
                        if (role == "leader") {
                            leaderPanelButton.visibility = View.VISIBLE // Show leader panel for leaders
                        }
                    } else {
                        Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Set onClickListener for viewClubsButton
        viewClubsButton.setOnClickListener {
            val intent = Intent(this, ClubsActivity::class.java)
            startActivity(intent)
        }

        // Set onClickListener for leaderPanelButton (if it becomes visible)
        leaderPanelButton.setOnClickListener {
            val intent = Intent(this, LeaderDashboardActivity::class.java) // Redirect to leader dashboard
            startActivity(intent)
        }
    }
}
