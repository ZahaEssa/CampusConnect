package com.example.campusconnect

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LeaderDashboardActivity : AppCompatActivity() {

    private lateinit var eventManagementButton: Button
    private lateinit var clubManagementButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_leader_dashboard)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable back button in the toolbar
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)  // This enables the back button
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)  // Custom back arrow (optional)

        // Initialize buttons
        eventManagementButton = findViewById(R.id.btnEventManagement)
        clubManagementButton = findViewById(R.id.btnClubManagement)

        // Hide buttons by default
        eventManagementButton.visibility = View.GONE
        clubManagementButton.visibility = View.GONE

        // Check user role and isAdmin from Firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            FirebaseFirestore.getInstance().collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val isAdmin = document.getBoolean("isAdmin") ?: false
                        val role = document.getString("role") ?: "student"

                        // Adjust UI based on role and isAdmin
                        if (isAdmin || role == "leader") {
                            eventManagementButton.visibility = View.VISIBLE
                            clubManagementButton.visibility = View.VISIBLE
                        } else {
                            Toast.makeText(this, "Access restricted.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Button listeners for fragment replacements
        eventManagementButton.setOnClickListener {
            replaceWithFragment(EventManagementFragment())
        }

        clubManagementButton.setOnClickListener {
            replaceWithFragment(ClubManagementFragment())
        }
    }

    // Create options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handle item selection from the options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_event_management -> {
                replaceWithFragment(EventManagementFragment())
                true
            }
            R.id.action_club_management -> {
                replaceWithFragment(ClubManagementFragment())
                true
            }
            android.R.id.home -> {
                // This handles the back arrow click in the toolbar
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Function to replace the fragment based on the clicked button
    private fun replaceWithFragment(fragment: Fragment) {
        // Hide the buttons
        eventManagementButton.visibility = View.GONE
        clubManagementButton.visibility = View.GONE

        // Replace the fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null) // This ensures the fragment is added to the back stack
        transaction.commit()
    }

}
