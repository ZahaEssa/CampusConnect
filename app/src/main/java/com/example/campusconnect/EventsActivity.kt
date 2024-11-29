package com.example.campusconnect

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EventsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapterForStudent
    private val eventList = mutableListOf<Event>()
    private val db: FirebaseFirestore = Firebase.firestore

    // Declare clubId variable to store the club ID passed from the previous activity
    private lateinit var clubId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        // Set up the Toolbar as ActionBar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set the title of the toolbar dynamically (this will override the XML title)
        supportActionBar?.title = "Events"

        // Enable the back button in ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)  // Use your custom drawable for the back arrow

        // Retrieve clubId from Intent
        clubId = intent.getStringExtra("clubId") ?: ""

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.eventsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Adapter and pass the context to it
        eventAdapter = EventAdapterForStudent(eventList, this)
        recyclerView.adapter = eventAdapter

        // Load events specific to the club from Firestore
        loadEventsForClub()
    }

    // Inflate the menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.student_dashboard_menu, menu) // Inflate the menu with your XML file
        return true
    }

    // Handle menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_view_clubs -> {
                // Navigate to Clubs Activity
                val intent = Intent(this, ClubsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_view_favorites -> {
                // Navigate to View Favorites
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)
                true
            }
            android.R.id.home -> {
                // Navigate back to the ClubsActivity
                navigateToClubsActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToClubsActivity() {
        // Intent to navigate back to the ClubsActivity
        val intent = Intent(this, ClubsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish() // Finish this activity so that it is removed from the back stack
    }

    private fun loadEventsForClub() {
        if (clubId.isNotEmpty()) {
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
                .addOnFailureListener { e ->
                    // Notify user of the error
                    Toast.makeText(this, "Failed to load events: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Invalid Club ID", Toast.LENGTH_SHORT).show()
        }
    }

    // Add an event to favorites
    fun addEventToFavorites(event: Event) {
        // Ensure the event ID is valid
        if (event.id.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid event data", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Save the event ID
        editor.putBoolean(event.id, true) // Storing event ID as key and marking it as true
        editor.apply()

        Toast.makeText(this, "${event.name} added to favorites!", Toast.LENGTH_SHORT).show()
    }
}
