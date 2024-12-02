package com.example.halalify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        setContentView(R.layout.activity_signup)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set up views
        val emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress2)
        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword2)
        val confirmPasswordEditText = findViewById<EditText>(R.id.editTextTextPassword3)
        val signUpButton = findViewById<Button>(R.id.button6)
        val loginButton = findViewById<Button>(R.id.button9)

        // Set up listener for the sign-up button
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            // Validate input
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Register user with Firebase
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()

                        // Launch MainActivity and finish Signup Activity
                        val intent = Intent(this, getuserdata::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Set up listener for the login button to navigate to the login activity
        loginButton.setOnClickListener {
            val intent = Intent(this, loginandsignup::class.java) // Replace with your login activity class
            startActivity(intent)
            finish() // Finish this activity to prevent going back to signup
        }
    }
}
