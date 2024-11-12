package com.example.campusconnect

import android.widget.Button
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Define the adapter class for the RecyclerView
class EventAdapter(
    private val eventsList: List<Event>,
    private val eventClickListener: OnEventClickListener,
    private val eventDeleteListener: OnEventDeleteListener,
    private val eventUpdateListener: OnEventUpdateListener // Add update listener
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    interface OnEventClickListener {
        fun onEventClick(event: Event)
    }

    interface OnEventDeleteListener {
        fun onEventDelete(event: Event)
    }

    interface OnEventUpdateListener { // Interface for event update
        fun onEventUpdate(event: Event)
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.eventName)
        val eventDescription: TextView = itemView.findViewById(R.id.eventDescription)
        val eventTime: TextView = itemView.findViewById(R.id.eventTime)
        val eventVenue: TextView = itemView.findViewById(R.id.eventVenue)
        val eventLink: TextView = itemView.findViewById(R.id.eventLink)  // Add TextView for link
        val deleteButton: Button = itemView.findViewById(R.id.btnDelete)
        val updateButton: Button = itemView.findViewById(R.id.btnUpdate)

        fun bind(event: Event) {
            eventName.text = event.name
            eventDescription.text = event.description
            eventTime.text = event.time
            eventVenue.text = event.venue
            eventLink.text = event.link  // Set event link text

            itemView.setOnClickListener {
                eventClickListener.onEventClick(event)
            }

            deleteButton.setOnClickListener {
                eventDeleteListener.onEventDelete(event)
            }

            updateButton.setOnClickListener {
                eventUpdateListener.onEventUpdate(event)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(eventsList[position])
    }

    override fun getItemCount(): Int = eventsList.size
}

