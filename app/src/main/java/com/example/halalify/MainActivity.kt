package com.example.halalify

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.halalify.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    companion object {
        val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }

    // Register the activity result launcher for permissions
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Check if all required permissions are granted
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false) {
                    permissionGranted = false
                }
            }

            if (permissionGranted) {
                // Start the camera if permissions are granted
                startCamera()
            } else {
                // Show a message if the permissions are denied
                Toast.makeText(baseContext, "Permission request denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check and request permissions if necessary
        if (arePermissionsGranted()) {
            startCamera() // If permissions are granted, start the camera
        } else {
            // Request camera permissions
            activityResultLauncher.launch(REQUIRED_PERMISSIONS)
        }


        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())  // Default fragment on app launch
        }

        // Set up BottomNavigationView listener to switch between fragments
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.Scan -> {
                    replaceFragment(ScanFragment())
                    true
                }
                R.id.Settings -> {
                    replaceFragment(SettingFragment())
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

    // Check if camera permissions are granted
    private fun arePermissionsGranted(): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Start the camera functionality
    private fun startCamera() {
        Toast.makeText(this, "Camera started", Toast.LENGTH_SHORT).show()
    }
}
