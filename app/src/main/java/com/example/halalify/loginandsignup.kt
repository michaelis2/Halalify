package com.example.halalify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class loginandsignup : AppCompatActivity() {


    // Connect to firebase Auth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize the auth
        auth = FirebaseAuth.getInstance()

        // Initialize views
        val emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword)
        val loginButton = findViewById<Button>(R.id.button2)
        val signupButton = findViewById<Button>(R.id.button10)

        loginButton.setOnClickListener {
            // Get user credentials
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Login successful, navigate to MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            // Login failed, show error message
                            Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show()
            }
        }
        signupButton.setOnClickListener {
            val intent = Intent(this, signup::class.java) 
            startActivity(intent)
            finish() // Finish this activity to prevent going back to signup
        }
    }


}
