package com.example.campusconnect

import android.os.Bundle
import android.util.Log
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

        // Retrieve clubId from Intent
        clubId = intent.getStringExtra("clubId") ?: ""

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.eventsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Adapter
        eventAdapter = EventAdapterForStudent(eventList)
        recyclerView.adapter = eventAdapter

        // Load events specific to the club from Firestore
        loadEventsForClub()
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
                    Log.d("EventsActivity", "Loaded ${eventList.size} events for club $clubId")
                }
                .addOnFailureListener { e ->
                    Log.e("EventsActivity", "Error loading events", e)
                }
        } else {
            Log.e("EventsActivity", "Club ID is missing.")
        }
    }
}
