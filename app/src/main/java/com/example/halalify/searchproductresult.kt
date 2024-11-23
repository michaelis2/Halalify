package com.example.halalify

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.bumptech.glide.Glide

class searchproductresult : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scannedproductresult)

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

        // Set the data to respective views
        productNameTextView.text = productName
        categoryTextView.text = "Category: $category"
        halalStatusTextView.text = "Halal Status: $halalStatus"

        // Load the image using Glide
        Glide.with(this).load(productImage).into(productImageView)

        // Handle the "Add to Calorie History" button click
        addCalorieButton.setOnClickListener {
            val foodData = hashMapOf(
                "product_name" to productName,
                "calories" to calories,
                "category" to category,
                "halal_status" to halalStatus,
                "image_url" to productImage
            )

            firestore.collection("foods")
                .add(foodData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Food data added to Firestore!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

}
