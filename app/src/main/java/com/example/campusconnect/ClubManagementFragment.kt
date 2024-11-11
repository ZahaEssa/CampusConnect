

package com.example.campusconnect
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class ClubManagementFragment : Fragment() {

    private lateinit var etClubName: EditText
    private lateinit var etClubDescription: EditText
    private lateinit var etLeaderName: EditText
    private lateinit var btnCreateClub: Button
    private lateinit var btnEditClub: Button
    private val db = FirebaseFirestore.getInstance()
    private var clubId: String? = null // Store the club ID if a club already exists
    private val currentUserId: String? by lazy { FirebaseAuth.getInstance().currentUser?.uid }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_club_management, container, false)

        // Initialize views
        etClubName = view.findViewById(R.id.etClubName)
        etClubDescription = view.findViewById(R.id.etClubDescription)
        etLeaderName = view.findViewById(R.id.etLeaderName)
        btnCreateClub = view.findViewById(R.id.btnCreateClub)
        btnEditClub = view.findViewById(R.id.btnEditClub)

        // Load existing club data if it exists
        loadClubData()

        // Set up button click listeners
        btnCreateClub.setOnClickListener {
            createClub()
        }

        btnEditClub.setOnClickListener {
            editClub()
        }

        // Handle back button press to navigate back to LeaderDashboardActivity
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Start LeaderDashboardActivity when back is pressed
                val intent = Intent(requireContext(), LeaderDashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP  // Optional: Clears activity stack
                startActivity(intent)
                requireActivity().finish()  // Optionally finish the current activity
            }
        })

        return view
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
                            populateClubDetails(document)
                            btnCreateClub.visibility = View.GONE
                            btnEditClub.visibility = View.VISIBLE
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to load club data", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateClubDetails(document: QueryDocumentSnapshot) {
        etClubName.setText(document.getString("name"))
        etClubDescription.setText(document.getString("description"))
        etLeaderName.setText(document.getString("leaderName"))
    }

    private fun createClub() {
        val clubName = etClubName.text.toString().trim()
        val clubDescription = etClubDescription.text.toString().trim()
        val leaderName = etLeaderName.text.toString().trim()

        if (currentUserId != null && clubName.isNotEmpty() && clubDescription.isNotEmpty() && leaderName.isNotEmpty()) {
            val clubData = hashMapOf(
                "name" to clubName,
                "description" to clubDescription,
                "createdAt" to System.currentTimeMillis(),
                "createdBy" to currentUserId!!,
                "leaderName" to leaderName
            )

            db.collection("clubs").add(clubData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Club created successfully", Toast.LENGTH_SHORT).show()
                    btnCreateClub.visibility = View.GONE
                    btnEditClub.visibility = View.VISIBLE
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to create club", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Please enter all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editClub() {
        val clubName = etClubName.text.toString().trim()
        val clubDescription = etClubDescription.text.toString().trim()
        val leaderName = etLeaderName.text.toString().trim()

        if (clubId != null && clubName.isNotEmpty() && clubDescription.isNotEmpty() && leaderName.isNotEmpty()) {
            val updatedData = hashMapOf<String, Any>(
                "name" to clubName,
                "description" to clubDescription,
                "leaderName" to leaderName
            )

            db.collection("clubs").document(clubId!!)
                .update(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Club details updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to update club details", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Please enter all details", Toast.LENGTH_SHORT).show()
        }
    }
}
