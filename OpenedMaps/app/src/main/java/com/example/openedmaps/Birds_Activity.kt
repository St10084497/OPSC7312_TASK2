package com.example.openedmaps

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Birds_Activity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var databaseSave: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var birdList: ArrayList<TaskModel>
    private lateinit var adapter: BirdAdapter
    private var alertDialog : AlertDialog? = null //ui to place data on
    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_birds)

        database = FirebaseDatabase.getInstance().reference.child("items")
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        birdList = ArrayList()
        adapter = BirdAdapter(birdList)
        recyclerView.adapter = adapter

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_Birds
        bottomNavigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            val itemId = item.itemId
            if (itemId == R.id.bottom_home) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
                return@OnItemSelectedListener true
            } else if (itemId == R.id.bottom_Birds) {

                return@OnItemSelectedListener true
            } else if (itemId == R.id.bottom_setting) {
                startActivity(Intent(applicationContext, Settings_Activity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
                return@OnItemSelectedListener true
            }
            else if (itemId == R.id.bottom_about) {
                startActivity(Intent(applicationContext, aboutUs::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
                return@OnItemSelectedListener true
            }
            false
        })

        // Assuming you have fetched the entries into a list called "entries"
        // Fetch entries from Firebase or wherever you're getting the data
        val saveButton = findViewById<Button>(R.id.saveButton)
        val birdName = findViewById<EditText>(R.id.birdNameEditText)
        val locationName = findViewById<EditText>(R.id.locationNameEditText)
        val description = findViewById<EditText>(R.id.descriptionEditText)

        saveButton.setOnClickListener{
            saveToFirebase(birdName.text.toString(),locationName.text.toString(),description.text.toString())
            birdName.text=null
            locationName.text=null
            description.text=null
        }

        // Call showEntriesAlert to display the entries in card views

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                birdList.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val bird = postSnapshot.getValue(TaskModel::class.java)
                    bird?.let {
                        birdList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database errors if necessary
            }
        })




    }

    private fun saveToFirebase(birdName: String?, locationName: String?, description: String?) {
        val key = database.child("items").push().key
        if (key != null) {
            val task = TaskModel(birdName, locationName, description)
            databaseSave = FirebaseDatabase.getInstance().reference
            databaseSave.child("items").child(key).setValue(task)
                .addOnFailureListener {
                    Toast.makeText(this, "Data not saved to Firebase", Toast.LENGTH_SHORT).show()
                }
                .addOnSuccessListener {
                    Toast.makeText(this, "Data saved to Firebase", Toast.LENGTH_SHORT).show()


                }
        }
    }

    private fun fetchEntriesFromFirebase() {
        val query = database.child("items")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entries: MutableList<TaskModel> = mutableListOf()
                for (entrySnapshot in snapshot.children) {
                    val birdName = entrySnapshot.child("birdName").getValue(String::class.java)
                    val locationName = entrySnapshot.child("locationName").getValue(String::class.java)
                    val description = entrySnapshot.child("description").getValue(String::class.java)

                    if (birdName != null && locationName != null && description != null) {
                        val entry = TaskModel(birdName, locationName, description)
                        entries.add(entry)
                    }
                }

                // Add alert to display entries
                showEntriesAlert(entries)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Birds_Activity, "Failed to read entries", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEntriesAlert(entries: MutableList<TaskModel>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Bird Observation Entries")

        if (entries.isEmpty()) {
            builder.setMessage("No Entries Found")
        } else {
            val cardContainer = LinearLayout(this)
            cardContainer.orientation = LinearLayout.VERTICAL

            for (entry in entries) {
                val cardView = layoutInflater.inflate(R.layout.activity_birds, null) as CardView
                val cardLayout = cardView.findViewById<LinearLayout>(R.id.cardLayout)

                // Customize the card layout to display entry information
                // For example:
                val birdNameTextView = TextView(this)
                birdNameTextView.text = "Bird Name: ${entry.birdName}"

                val locationNameTextView = TextView(this)
                locationNameTextView.text = "Location: ${entry.locationName}"

                val descriptionTextView = TextView(this)
                descriptionTextView.text = "Description: ${entry.description}"

                // Add views to the card layout
                cardLayout.addView(birdNameTextView)
                cardLayout.addView(locationNameTextView)
                cardLayout.addView(descriptionTextView)

                cardContainer.addView(cardView)
            }

            builder.setView(cardContainer)
        }

        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        alertDialog = builder.create()
        alertDialog?.show()
    }
}

data class TaskModel(
    var birdName: String? = null,
    var locationName: String? = null,
    var description: String? = null
)