package com.example.campusconnect

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    val id: String = "",              // Unique ID for the event
    val name: String = "",            // Name of the event
    val description: String = "",     // Description of the event
    val time: String = "",            // Time of the event
    val venue: String = "",           // Venue of the event
    val link: String = ""             // Optional link related to the event
) : Parcelable
