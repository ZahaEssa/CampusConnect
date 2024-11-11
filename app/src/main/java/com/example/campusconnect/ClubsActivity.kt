package com.example.campusconnect

import android.content.Intent
import android.os.Bundle
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
                        leaderName = document.getString("leaderName") ?: "Unknown Leader" // Add this line to fetch leader's name
                    )
                    clubsList.add(club)
                }
                clubsAdapter.notifyDataSetChanged()
            }
    }

}
