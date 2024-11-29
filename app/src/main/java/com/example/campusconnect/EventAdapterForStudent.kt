package com.example.campusconnect

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class EventAdapterForStudent(
    private val eventList: MutableList<Event>,
    private val context: Context,
    private val isFavoritesScreen: Boolean = false
) : RecyclerView.Adapter<EventAdapterForStudent.EventViewHolder>() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
    private val gson = Gson() // For serializing and deserializing events

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.eventName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.eventDescription)
        val timeTextView: TextView = itemView.findViewById(R.id.eventTime)
        val venueTextView: TextView = itemView.findViewById(R.id.eventVenue)
        val linkTextView: TextView = itemView.findViewById(R.id.eventLink)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_event_student, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]

        // Set the event details
        holder.nameTextView.text = event.name
        holder.descriptionTextView.text = event.description
        holder.timeTextView.text = event.time
        holder.venueTextView.text = event.venue

        // Set the actual link if available
        if (event.link.isNotEmpty()) {
            holder.linkTextView.text = event.link
            holder.linkTextView.visibility = View.VISIBLE // Make it visible if link exists
        } else {
            holder.linkTextView.visibility = View.GONE // Hide if no link exists
        }

        // Set favorite icon based on whether the event is already favorited
        holder.favoriteButton.setImageResource(
            if (isEventFavorite(event)) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )

        // Handle the favorite button click
        holder.favoriteButton.setOnClickListener {
            if (isEventFavorite(event)) {
                // Remove from favorites
                removeEventFromFavorites(event)
                holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border)
                Toast.makeText(context, "${event.name} removed from favorites!", Toast.LENGTH_SHORT)
                    .show()

                if (isFavoritesScreen) {
                    // Remove from list in favorites screen
                    eventList.removeAt(position)
                    notifyItemRemoved(position)
                }
            } else {
                // Add to favorites
                addEventToFavorites(event)
                holder.favoriteButton.setImageResource(R.drawable.ic_favorite)
                Toast.makeText(context, "${event.name} added to favorites!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    // Check if an event is already in favorites
    private fun isEventFavorite(event: Event): Boolean {
        return sharedPreferences.contains(event.id)
    }

    // Add an event to favorites
    private fun addEventToFavorites(event: Event) {
        val eventJson = gson.toJson(event) // Serialize the event object
        val editor = sharedPreferences.edit()
        editor.putString(event.id, eventJson) // Save JSON string with Event ID as key
        editor.apply()

        // Debugging Log
        println("Event added to favorites: $eventJson")
    }

    // Remove an event from favorites
    private fun removeEventFromFavorites(event: Event) {
        val editor = sharedPreferences.edit()
        editor.remove(event.id)
        editor.apply()
    }
}
