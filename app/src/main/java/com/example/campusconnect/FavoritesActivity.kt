package com.example.campusconnect

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class FavoritesActivity : AppCompatActivity() {
    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var favoriteEventAdapter: EventAdapterForStudent
    private val favoriteEventList = mutableListOf<Event>()
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        // Set the title of the activity
        title = "Favorites"

        // Enable the back button in ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize RecyclerView
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView)
        favoritesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Adapter
        favoriteEventAdapter = EventAdapterForStudent(favoriteEventList, this, isFavoritesScreen = true)
        favoritesRecyclerView.adapter = favoriteEventAdapter

        // Load favorite events
        loadFavoriteEvents()
    }

    // Handle the ActionBar back button
    override fun onSupportNavigateUp(): Boolean {
        navigateToStudentDashboard()
        return true
    }

    // Handle the physical back button (hardware back)
    override fun onBackPressed() {
        navigateToStudentDashboard()  // Navigate to Student Dashboard
        super.onBackPressed()  // Call super to preserve the default back button behavior
    }

    // Method to navigate to StudentDashboardActivity
    private fun navigateToStudentDashboard() {
        val intent = Intent(this, StudentDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish() // Ensure the current activity is removed from the back stack
    }

    private fun loadFavoriteEvents() {
        val sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE)
        val favoriteEventIds = sharedPreferences.all.keys.toList()

        // Debugging Log
        println("Favorite Event IDs: $favoriteEventIds")

        if (favoriteEventIds.isEmpty()) {
            Toast.makeText(this, "No favorite events yet!", Toast.LENGTH_SHORT).show()
            return
        }

        favoriteEventList.clear()

        for (eventId in favoriteEventIds) {
            val eventJson = sharedPreferences.getString(eventId, null)
            println("Retrieved Event JSON: $eventJson") // Debugging Log

            if (eventJson != null) {
                try {
                    val event = gson.fromJson(eventJson, Event::class.java)
                    favoriteEventList.add(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        if (favoriteEventList.isEmpty()) {
            Toast.makeText(this, "No favorite events found!", Toast.LENGTH_SHORT).show()
        } else {
            println("Loaded Favorites: $favoriteEventList") // Debugging Log
        }

        favoriteEventAdapter.notifyDataSetChanged()
    }

    // Handle back arrow click in the ActionBar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navigateToStudentDashboard() // Navigate back to the student dashboard
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
