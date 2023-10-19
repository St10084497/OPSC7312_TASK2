package com.example.openedmaps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class aboutUs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

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
                startActivity(Intent(applicationContext,Settings_Activity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
                return@OnItemSelectedListener true
            }
            else if (itemId == R.id.bottom_about) {

                return@OnItemSelectedListener true
            }
            false
        })
    }
}