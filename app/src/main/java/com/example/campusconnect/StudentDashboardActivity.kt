package com.example.campusconnect

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class StudentDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        // Initialize the button to view clubs
        val viewClubsButton = findViewById<Button>(R.id.viewClubsButton)

        // Set an onClickListener to navigate to the ClubsActivity
        viewClubsButton.setOnClickListener {
            val intent = Intent(this, ClubsActivity::class.java)
            startActivity(intent)
        }
    }
}
