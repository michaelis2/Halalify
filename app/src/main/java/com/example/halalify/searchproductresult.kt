package com.example.halalify

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class searchproductresult : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchproductresult)

        // Retrieve data passed through Intent
        val productName = intent.getStringExtra("product_name") ?: "Unknown"
        val productImage = intent.getStringExtra("product_image") ?: ""
        val calories = intent.getStringExtra("calories") ?: "0"
        val category = intent.getStringExtra("category") ?: "Unknown"
        val halalStatus = intent.getStringExtra("halal_status") ?: "Unknown"

        // Bind views
        val productNameTextView = findViewById<TextView>(R.id.textView14)
        val categoryTextView = findViewById<TextView>(R.id.textView18)
        val halalStatusTextView = findViewById<TextView>(R.id.textView19)
        val productImageView = findViewById<ImageView>(R.id.imageoffood)
        val addCalorieButton = findViewById<ImageButton>(R.id.addcalorieshistory)
        val calorieDisplay = findViewById<TextView>(R.id.calorydisplay)


        // Set the data to respective views
        productNameTextView.text = productName
        categoryTextView.text = "Category: $category"
        halalStatusTextView.text = "Halal Status: $halalStatus"
        calorieDisplay.text="Calorie: $calories"

        // Load the image using Glide
        Glide.with(this).load(productImage).into(productImageView)

        // Handle the "Add to Calorie History" button click
        addCalorieButton.setOnClickListener {
            // Ensure calorieDisplay is not empty
            if (calorieDisplay.text.isNotEmpty() && productNameTextView.text.isNotEmpty()) {
                val calorieText = calorieDisplay.text.toString()

                // Extract the numeric calorie value from the string
                val numericCalories = calorieText.replace(Regex("[^\\d]"), "").toIntOrNull()

                if (numericCalories != null) {
                    val timestamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    // Prepare the data to be saved
                    val calorieData = hashMapOf(
                        "name" to productNameTextView.text.toString(),
                        "calories" to numericCalories,  // Store as an integer
                        "timestamp" to timestamp
                    )

                    // Save to Firestore
                    firestore.collection("calorieHistory")
                        .add(calorieData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Calorie data saved successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to save calorie data: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Invalid calorie data", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please provide calorie information", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
