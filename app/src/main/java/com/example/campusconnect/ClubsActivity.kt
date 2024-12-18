package com.example.campusconnect

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ClubsActivity : AppCompatActivity() {

    private lateinit var clubsRecyclerView: RecyclerView
    private lateinit var clubsAdapter: ClubAdapter
    private var clubsList: MutableList<Club> = mutableListOf()
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clubs)

        // Set up the Toolbar as ActionBar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set the title dynamically
        supportActionBar?.title = "Clubs"

        // Enable the back arrow in the ActionBar and set the custom back arrow icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)  // Use your custom drawable here

        db = FirebaseFirestore.getInstance()

        clubsRecyclerView = findViewById(R.id.clubsRecyclerView)
        clubsRecyclerView.layoutManager = LinearLayoutManager(this)

        clubsAdapter = ClubAdapter(clubsList) { club ->
            // Navigate to the EventsActivity when a club is clicked
            val intent = Intent(this, EventsActivity::class.java)
            intent.putExtra("clubId", club.id)
            intent.putExtra("clubName", club.name)
            startActivity(intent)
        }
        clubsRecyclerView.adapter = clubsAdapter

        // Load clubs data
        loadClubs()
    }

    // Inflate the menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.student_dashboard_menu, menu) // Make sure your menu file is named correctly
        return true
    }

    // Handle menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_view_clubs -> {
                // Navigate to Clubs Activity (current activity, so no change)
                true
            }
            R.id.action_view_favorites -> {
                // Navigate to View Favorites
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)
                true
            }
            android.R.id.home -> {
                // Handle the back button click
                onBackPressed()  // This will navigate back to the previous activity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadClubs() {
        db.collection("clubs")
            .get()
            .addOnSuccessListener { documents ->
                clubsList.clear()
                for (document in documents) {
                    val club = Club(
                        id = document.id,
                        name = document.getString("name") ?: "Unknown Club",
                        description = document.getString("description") ?: "No Description",
                        leaderName = document.getString("leaderName") ?: "Unknown Leader"
                    )
                    clubsList.add(club)
                }
                clubsAdapter.notifyDataSetChanged()
            }
    }
}
