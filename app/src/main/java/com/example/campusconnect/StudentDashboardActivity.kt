package com.example.campusconnect

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var viewClubsButton: Button
    private lateinit var leaderPanelButton: Button
    private lateinit var viewFavoritesButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        // Initialize buttons
        viewClubsButton = findViewById(R.id.viewClubsButton)
        leaderPanelButton = findViewById(R.id.leaderPanelButton)
        viewFavoritesButton = findViewById(R.id.viewFavoritesButton)

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

        // Set onClickListeners
        viewClubsButton.setOnClickListener {
            val intent = Intent(this, ClubsActivity::class.java)
            startActivity(intent)
        }

        leaderPanelButton.setOnClickListener {
            val intent = Intent(this, LeaderDashboardActivity::class.java)
            startActivity(intent)
        }

        viewFavoritesButton.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }

        // Set up Toolbar as ActionBar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable back arrow in ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back) // Ensure this icon exists in drawable
    }

    // Handle back arrow click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Navigate back to the previous activity
                onBackPressed() // This will navigate to the previous activity
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
