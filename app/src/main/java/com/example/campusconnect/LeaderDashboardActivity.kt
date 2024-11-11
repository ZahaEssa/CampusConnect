package com.example.campusconnect

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class LeaderDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_leader_dashboard)

        val eventManagementButton: Button = findViewById(R.id.btnEventManagement)
        val clubManagementButton: Button = findViewById(R.id.btnClubManagement)
        val memberManagementButton: Button = findViewById(R.id.btnMemberManagement)

        // Initially, all the buttons are visible
        eventManagementButton.visibility = View.VISIBLE
        clubManagementButton.visibility = View.VISIBLE
        memberManagementButton.visibility = View.VISIBLE

        eventManagementButton.setOnClickListener {
            // Hide the buttons when the Event Management button is clicked
            hideButtons()

            // Replace the entire activity content with the event management layout (fragment)
            replaceWithFragment(EventManagementFragment())
            Toast.makeText(this, "Event Management clicked", Toast.LENGTH_SHORT).show()
        }

        clubManagementButton.setOnClickListener {
            // Hide the buttons when the Club Management button is clicked
            hideButtons()

            // Replace the entire activity content with the club management layout (fragment)
            replaceWithFragment(ClubManagementFragment())
            Toast.makeText(this, "Club Management clicked", Toast.LENGTH_SHORT).show()
        }

        memberManagementButton.setOnClickListener {
            // Hide the buttons when the Member Management button is clicked
            hideButtons()

            // Replace the entire activity content with the member management layout (fragment)
           // replaceWithFragment(MemberManagementFragment())
            Toast.makeText(this, "Member Management clicked", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to hide all buttons
    private fun hideButtons() {
        val eventManagementButton: Button = findViewById(R.id.btnEventManagement)
        val clubManagementButton: Button = findViewById(R.id.btnClubManagement)
        val memberManagementButton: Button = findViewById(R.id.btnMemberManagement)

        eventManagementButton.visibility = View.GONE
        clubManagementButton.visibility = View.GONE
        memberManagementButton.visibility = View.GONE
    }

    // Function to replace the fragment based on the clicked button
    private fun replaceWithFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(android.R.id.content, fragment)
        transaction.addToBackStack(null) // Optional: allows user to navigate back to the previous layout
        transaction.commit()
    }
}
