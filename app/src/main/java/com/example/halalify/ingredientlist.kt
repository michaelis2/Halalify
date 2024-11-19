package com.example.halalify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ingredientlist : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredientlist)

        // Retrieve the ingredients string from the intent
        val ingredientsData = intent.getStringExtra("ingredients") ?: "No ingredients available"

        // Split the ingredients string into a list (assuming ingredients are comma-separated)
        val ingredientsList = ingredientsData.split(",").map { it.trim() }

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.ingredientRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = IngredientsAdapter(ingredientsList)
    }
}
