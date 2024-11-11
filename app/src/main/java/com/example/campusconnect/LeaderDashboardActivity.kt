package com.example.campusconnect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import android.widget.Button
import android.view.View

class LeaderDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_leader_dashboard)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val eventManagementButton: Button = findViewById(R.id.btnEventManagement)
        val clubManagementButton: Button = findViewById(R.id.btnClubManagement)
        val memberManagementButton: Button = findViewById(R.id.btnMemberManagement)

        // Button listeners for fragment replacements
        eventManagementButton.setOnClickListener {
            hideButtons()
            replaceWithFragment(EventManagementFragment())
        }

        clubManagementButton.setOnClickListener {
            hideButtons()
            replaceWithFragment(ClubManagementFragment())
        }

        memberManagementButton.setOnClickListener {
            hideButtons()
           // replaceWithFragment(MemberManagementFragment())
        }
    }

    // Create options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handle item selection from the options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_event_management -> {
                hideButtons()
                replaceWithFragment(EventManagementFragment())
                true
            }
            R.id.action_club_management -> {
                hideButtons()
                replaceWithFragment(ClubManagementFragment())
                true
            }
            R.id.action_member_management -> {
                hideButtons()
               // replaceWithFragment(MemberManagementFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Function to hide all buttons
    private fun hideButtons() {
        val eventManagementButton: Button = findViewById(R.id.btnEventManagement)
        val clubManagementButton: Button = findViewById(R.id.btnClubManagement)
        val memberManagementButton: Button = findViewById(R.id.btnMemberManagement)

        eventManagementButton.visibility = View.GONE
        clubManagementButton.visibility = View.GONE
        memberManagementButton.visibility = View.GONE
    }

    // Function to replace the fragment based on the clicked button
    private fun replaceWithFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null) // Optional: allows user to navigate back to the previous layout
        transaction.commit()
    }


}
