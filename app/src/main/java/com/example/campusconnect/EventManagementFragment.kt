package com.example.campusconnect

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.widget.Button
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import android.content.Intent

class EventManagementFragment : Fragment() {

    private lateinit var etEventName: EditText
    private lateinit var etEventDescription: EditText
    private lateinit var etEventTime: EditText
    private lateinit var etEventVenue: EditText
    private lateinit var etEventLink: EditText  // Add the EditText for event link
    private lateinit var btnCreateEvent: Button
    private lateinit var recyclerViewEvents: RecyclerView
    private val eventsList = mutableListOf<Event>()
    private lateinit var eventAdapter: EventAdapter
    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private var clubId: String? = null
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate to LeaderDashboardActivity on back press
                val intent = Intent(requireContext(), LeaderDashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP  // Clears the current activity stack
                startActivity(intent)
                requireActivity().finish()  // Optionally finish the current activity
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_management, container, false)

        // Initialize views
        etEventName = view.findViewById(R.id.etEventName)
        etEventDescription = view.findViewById(R.id.etEventDescription)
        etEventTime = view.findViewById(R.id.etEventTime)
        etEventVenue = view.findViewById(R.id.etEventVenue)
        etEventLink = view.findViewById(R.id.etEventLink)  // Initialize the event link EditText
        btnCreateEvent = view.findViewById(R.id.btnCreateEvent)
        recyclerViewEvents = view.findViewById(R.id.recyclerViewEvents)

        // Setup RecyclerView
        recyclerViewEvents.layoutManager = LinearLayoutManager(context)
        eventAdapter = EventAdapter(eventsList,
            object : EventAdapter.OnEventClickListener {
                override fun onEventClick(event: Event) {
                    showEditEventDialog(event)
                }
            },
            object : EventAdapter.OnEventDeleteListener {
                override fun onEventDelete(event: Event) {
                    deleteEvent(event)
                }
            },
            object : EventAdapter.OnEventUpdateListener {
                override fun onEventUpdate(event: Event) {
                    showEditEventDialog(event)
                }
            }
        )
        recyclerViewEvents.adapter = eventAdapter

        btnCreateEvent.setOnClickListener {
            if (clubId != null) {
                createEvent()
            } else {
                Toast.makeText(requireContext(), "Club ID not found. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        loadClubData()

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }

    private fun loadClubData() {
        currentUserId?.let { userId ->
            db.collection("clubs")
                .whereEqualTo("createdBy", userId)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            clubId = document.id
                            loadEvents() // Load events for the specific club
                        }
                    } else {
                        Toast.makeText(requireContext(), "No club found for the current user.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to load club data.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadEvents() {
        if (clubId != null) {
            db.collection("events")
                .whereEqualTo("clubId", clubId)
                .get()
                .addOnSuccessListener { documents ->
                    eventsList.clear()
                    for (document in documents) {
                        val event = Event(
                            name = document.getString("name") ?: "",
                            description = document.getString("description") ?: "",
                            time = document.getString("time") ?: "",
                            venue = document.getString("venue") ?: "",
                            link = document.getString("link") ?: "", // Add event link
                            id = document.id
                        )
                        eventsList.add(event)
                    }
                    eventAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to load events.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Club ID is null. Cannot load events.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createEvent() {
        val eventName = etEventName.text.toString().trim()
        val eventDescription = etEventDescription.text.toString().trim()
        val eventTime = etEventTime.text.toString().trim()
        val eventVenue = etEventVenue.text.toString().trim()
        val eventLink = etEventLink.text.toString().trim()  // Get event link

        if (eventName.isNotEmpty() && eventDescription.isNotEmpty() && eventTime.isNotEmpty() && eventVenue.isNotEmpty()) {
            val eventData = hashMapOf(
                "name" to eventName,
                "description" to eventDescription,
                "time" to eventTime,
                "venue" to eventVenue,
                "link" to eventLink,  // Add link to the event data
                "createdBy" to currentUserId!!,
                "clubId" to clubId!!
            )

            db.collection("events").add(eventData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Event created", Toast.LENGTH_SHORT).show()
                    loadEvents()

                    // Clear input fields
                    etEventName.text.clear()
                    etEventDescription.text.clear()
                    etEventTime.text.clear()
                    etEventVenue.text.clear()
                    etEventLink.text.clear()  // Clear the event link field
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to create event", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Please enter event details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteEvent(event: Event) {
        db.collection("events").document(event.id).delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Event deleted", Toast.LENGTH_SHORT).show()
                loadEvents()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to delete event", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEditEventDialog(event: Event) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_event, null)

        val etDialogEventName: EditText = dialogView.findViewById(R.id.etDialogEventName)
        val etDialogEventDescription: EditText = dialogView.findViewById(R.id.etDialogEventDescription)
        val etDialogEventTime: EditText = dialogView.findViewById(R.id.etDialogEventTime)
        val etDialogEventVenue: EditText = dialogView.findViewById(R.id.etDialogEventVenue)
        val etDialogEventLink: EditText = dialogView.findViewById(R.id.etDialogEventLink)  // Add link field

        etDialogEventName.setText(event.name)
        etDialogEventDescription.setText(event.description)
        etDialogEventTime.setText(event.time)
        etDialogEventVenue.setText(event.venue)
        etDialogEventLink.setText(event.link)  // Set the event link

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Edit Event")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val updatedEventName = etDialogEventName.text.toString().trim()
                val updatedEventDescription = etDialogEventDescription.text.toString().trim()
                val updatedEventTime = etDialogEventTime.text.toString().trim()
                val updatedEventVenue = etDialogEventVenue.text.toString().trim()
                val updatedEventLink = etDialogEventLink.text.toString().trim()  // Get updated link

                if (updatedEventName.isNotEmpty() && updatedEventDescription.isNotEmpty() && updatedEventTime.isNotEmpty() && updatedEventVenue.isNotEmpty()) {
                    val updatedEventData: MutableMap<String, Any> = mutableMapOf(
                        "name" to updatedEventName,
                        "description" to updatedEventDescription,
                        "time" to updatedEventTime,
                        "venue" to updatedEventVenue,
                        "link" to updatedEventLink  // Update link
                    )

                    db.collection("events").document(event.id).update(updatedEventData)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Event updated", Toast.LENGTH_SHORT).show()
                            loadEvents()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to update event", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Please enter all event details", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        builder.show()
    }
}
