package com.example.halalify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.halalify.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set initial fragment to be displayed
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())  // Default fragment on app launch
        }

        // Set up BottomNavigationView listener to switch between fragments
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Home -> {
                    replaceFragment(HomeFragment())  // Replace with Home fragment
                    true
                }
                R.id.Scan -> {
                    replaceFragment(ScanFragment())  // Replace with Search fragment
                    true
                }
                R.id.Settings -> {
                    replaceFragment(SettingFragment())  // Replace with Profile fragment
                    true
                }
                else -> false
            }
        }
    }

    // Function to replace the fragment
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout2, fragment)
            .commit()
    }
}
