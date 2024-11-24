package com.example.halalify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class getuserdata : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var RadioGroup: RadioGroup  // Declare the RadioGroup

    private val db = FirebaseFirestore.getInstance()  // Initialize Firestore
    private val auth = FirebaseAuth.getInstance()     // Get FirebaseAuth instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_getuserdata)

        // Initialize views
        usernameEditText = findViewById(R.id.getusername)
        ageEditText = findViewById(R.id.getage)
        weightEditText = findViewById(R.id.getweight)
        heightEditText = findViewById(R.id.getheight)
        saveButton = findViewById(R.id.save_button)
        RadioGroup = findViewById(R.id.RadioGroup)  // Initialize RadioGroup

        // Check if the user is logged in
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "You need to sign in first", Toast.LENGTH_SHORT).show()
            return
        }

        // Set click listener on save button
        saveButton.setOnClickListener {
            saveUserData(user.uid)  // Pass the current user's UID
        }
    }

    private fun saveUserData(userId: String) {
        val username = usernameEditText.text.toString().trim()
        val age = ageEditText.text.toString().trim()
        val weight = weightEditText.text.toString().trim()
        val height = heightEditText.text.toString().trim()

        // Check if all fields are filled
        if (username.isEmpty() || age.isEmpty() || weight.isEmpty() || height.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Get selected gender (Men or Women)
        val selectedGenderId = RadioGroup.checkedRadioButtonId
        val gender: String = when (selectedGenderId) {
            R.id.Men -> "Men"
            R.id.Women -> "Women"
            else -> {
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val userData = hashMapOf(
            "username" to username,
            "age" to age.toInt(),
            "weight" to weight.toDouble(),
            "height" to height.toDouble(),
            "gender" to gender  // Add gender to the data being saved
        )

        // Store user data in Firestore under a document named with userId
        db.collection("users").document(userId).set(userData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // If task is successful
                    Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
                    // Create an intent to navigate to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    // Optionally, close the current activity so the user can't return to it
                    finish()
                } else {
                    // If task fails, print the exception
                    val exception = task.exception
                    Log.e("getuserdata", "Failed to save data: ${exception?.message}")
                    Toast.makeText(this, "Failed to save data: ${exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
