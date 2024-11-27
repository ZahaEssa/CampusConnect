package com.example.campusconnect

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class EventsActivity : AppCompatActivity() {

    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapterForStudent
    private var eventList: MutableList<Event> = mutableListOf()
    private lateinit var db: FirebaseFirestore
    private lateinit var clubNameTextView: TextView

    // Declare clubId
    private lateinit var clubId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        // Set up the Toolbar as ActionBar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set the title dynamically
        supportActionBar?.title = "Events"

        // Enable the back arrow in the ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)  // Use your custom drawable here

        // Get clubId passed from ClubsActivity
        clubId = intent.getStringExtra("clubId") ?: ""
        clubNameTextView = findViewById(R.id.clubNameText)

        db = FirebaseFirestore.getInstance()

        // Initialize RecyclerView for displaying events
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView)
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)

        eventAdapter = EventAdapterForStudent(eventList)
        eventsRecyclerView.adapter = eventAdapter

        // Load events data for the specific club
        loadClubNameAndEvents()
    }

    // Handle back arrow click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle the back button click
                onBackPressed()  // This will navigate back to the previous activity
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Fetch club name and events from Firestore
    private fun loadClubNameAndEvents() {
        if (clubId.isNotEmpty()) {
            // Fetch the club name from Firestore using clubId
            db.collection("clubs").document(clubId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val clubName = document.getString("name") ?: "Unknown Club"
                        clubNameTextView.text = clubName
                    }
                }

            // Fetch events for the specific club
            db.collection("events")
                .whereEqualTo("clubId", clubId)
                .get()
                .addOnSuccessListener { documents ->
                    eventList.clear()
                    for (document in documents) {
                        val event = document.toObject(Event::class.java)
                        eventList.add(event)
                    }
                    eventAdapter.notifyDataSetChanged()
                }
        }
    }
}
