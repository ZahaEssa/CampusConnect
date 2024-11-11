package com.example.campusconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class EventAdapterForStudent(private val eventList: List<Event>) : RecyclerView.Adapter<EventAdapterForStudent.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.eventName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.eventDescription)
        val timeTextView: TextView = itemView.findViewById(R.id.eventTime)
        val venueTextView: TextView = itemView.findViewById(R.id.eventVenue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_student, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.nameTextView.text = event.name
        holder.descriptionTextView.text = event.description
        holder.timeTextView.text = event.time
        holder.venueTextView.text = event.venue
    }

    override fun getItemCount(): Int {
        return eventList.size
    }
}
