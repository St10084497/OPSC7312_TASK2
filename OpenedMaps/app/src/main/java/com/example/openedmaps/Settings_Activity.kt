package com.example.openedmaps

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class Settings_Activity : AppCompatActivity() {
    private lateinit var unitsToggle: Switch
    private lateinit var maxDistanceSlider: SeekBar
    private lateinit var distanceTextView: TextView
    private lateinit var unitLabelTextView: TextView


    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_setting
        bottomNavigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            val itemId = item.itemId
            if (itemId == R.id.bottom_home) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
                return@OnItemSelectedListener true
            } else if (itemId == R.id.bottom_Birds) {
                startActivity(Intent(applicationContext, Birds_Activity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
                return@OnItemSelectedListener true
            } else if (itemId == R.id.bottom_setting) {

                return@OnItemSelectedListener true
            }
            else if (itemId == R.id.bottom_about) {
                startActivity(Intent(applicationContext,aboutUs::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
                return@OnItemSelectedListener true
            }
            false
        })




            unitsToggle = findViewById(R.id.unitsToggle)
            maxDistanceSlider = findViewById(R.id.maxDistanceSlider)
            distanceTextView = findViewById(R.id.distanceTextview)
            unitLabelTextView = findViewById(R.id.unitLabelTextView)

            // Load saved settings
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val isMetric = sharedPreferences.getBoolean("isMetric", true)
            val maxDistance = sharedPreferences.getInt("maxDistance", 50)

            // Set initial values for UI components based on saved settings and toggle state
            unitsToggle.isChecked = isMetric
            maxDistanceSlider.progress = maxDistance
            updateDistanceTextView(maxDistance, isMetric)
            updateUnitLabel(isMetric)

            unitsToggle.setOnCheckedChangeListener { _, isChecked ->
                // Update text when toggle state changes
                updateUnitLabel(isChecked)
                // Update distance text based on the new unit system
                updateDistanceTextView(maxDistanceSlider.progress, isChecked)
            }

            // Set up SeekBar change listener
            maxDistanceSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    // Update the distanceTextView based on the selected unit system
                    updateDistanceTextView(progress, unitsToggle.isChecked)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // Handle tracking touch if needed
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // Handle stopping touch if needed
                }
            })

            val saveButton = findViewById<Button>(R.id.button1)


            saveButton.setOnClickListener {
                // Save settings
                val editor = sharedPreferences.edit()
                editor.putBoolean("isMetric", unitsToggle.isChecked)
                editor.putInt("maxDistance", maxDistanceSlider.progress)
                editor.apply()

                Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()

            }


        }

        private fun updateDistanceTextView(progress: Int, isMetric: Boolean) {
            val distance = if (isMetric) {
                // Convert progress to kilometers if metric system is selected
                "$progress km"
            } else {
                // Convert progress to miles if imperial system is selected
                // 1 kilometer is approximately equal to 0.621371 miles
                val miles = (progress * 0.621371).toInt()
                "$miles miles"
            }
            // Update the distanceTextView based on the selected unit system
            distanceTextView.text = "Max Distance: $distance"
        }

        private fun updateUnitLabel(isMetric: Boolean) {
            val unitLabel = if (isMetric) {
                "Kilometers"
            } else {
                "Miles"
            }
            unitLabelTextView.text = "Selected Unit: $unitLabel"
        }
    }


