package com.example.campusconnect

import android.content.Intent
import android.os.Bundle
import android.view.Menu
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

        // Set up the Toolbar and set the title to "Favorites"
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Favorites"  // Set the title to "Favorites"

        // Enable the back button in ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back) // Ensure you have this icon in drawable

        // Initialize RecyclerView
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView)
        favoritesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Adapter
        favoriteEventAdapter = EventAdapterForStudent(favoriteEventList, this, isFavoritesScreen = true)
        favoritesRecyclerView.adapter = favoriteEventAdapter

        // Load favorite events
        loadFavoriteEvents()
    }

    // Inflate the menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.student_dashboard_menu, menu) // Inflate the menu with your XML file
        return true
    }

    // Handle menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navigateToStudentDashboard()  // Handle the back action
                true
            }
            R.id.action_view_clubs -> {
                // Navigate to Clubs Activity
                val intent = Intent(this, ClubsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_view_favorites -> {
                // Navigate to View Favorites (currently already in favorites screen)
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

        if (favoriteEventIds.isEmpty()) {
            Toast.makeText(this, "No favorite events yet!", Toast.LENGTH_SHORT).show()
            return
        }

        favoriteEventList.clear()

        for (eventId in favoriteEventIds) {
            val eventJson = sharedPreferences.getString(eventId, null)
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
        }

        favoriteEventAdapter.notifyDataSetChanged()
    }
}
