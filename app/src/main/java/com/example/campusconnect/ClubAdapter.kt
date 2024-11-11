package com.example.campusconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClubAdapter(
    private val clubs: List<Club>,
    private val onClubClick: (Club) -> Unit // Lambda function to handle click events
) : RecyclerView.Adapter<ClubAdapter.ClubViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_club, parent, false)
        return ClubViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        val club = clubs[position]
        holder.bind(club)
    }

    override fun getItemCount(): Int = clubs.size

    // ViewHolder for holding individual club items
    inner class ClubViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val clubName: TextView = itemView.findViewById(R.id.clubNameText)
        private val clubDescription: TextView = itemView.findViewById(R.id.clubDescriptionText)
        private val clubLeader: TextView = itemView.findViewById(R.id.clubLeaderText)

        // Bind the club data to the view and set up the click listener
        fun bind(club: Club) {
            clubName.text = club.name
            clubDescription.text = club.description
            clubLeader.text = "Leader: ${club.leaderName}"

            // Set a click listener on the itemView to trigger the onClubClick lambda
            itemView.setOnClickListener {
                onClubClick(club)
            }
        }
    }
}
