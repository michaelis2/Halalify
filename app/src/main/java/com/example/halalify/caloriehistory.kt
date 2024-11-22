package com.example.halalify

import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*

class caloriehistory : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var selectedDateText: TextView
    private lateinit var foodListText: TextView
    private lateinit var totalCaloriesText: TextView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_caloriehistory)

        // Initialize views
        calendarView = findViewById(R.id.calendarView)
        selectedDateText = findViewById(R.id.getselecteddate)
        foodListText = findViewById(R.id.foodlistanditscaloriesview)
        totalCaloriesText = findViewById(R.id.totalcaloriesthatdat)

        firestore = FirebaseFirestore.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handle date selection
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
            selectedDateText.text = formattedDate
            fetchCalorieHistory(formattedDate)
        }
    }

    private fun fetchCalorieHistory(date: String) {
        firestore.collection("calorieHistory")
            .whereEqualTo("timestamp", date)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    foodListText.text = "No data found for $date."
                    totalCaloriesText.text = "Total calories: 0"
                } else {
                    displayCalorieHistory(querySnapshot)
                    extractCalorieData(querySnapshot)
                }
            }
            .addOnFailureListener { e ->
                foodListText.text = "Failed to fetch data: ${e.message}"
                totalCaloriesText.text = "Total calories: --"
                e.printStackTrace()
            }
    }



    private fun displayCalorieHistory(querySnapshot: QuerySnapshot) {
        if (querySnapshot.isEmpty) {
            foodListText.text = "No data for selected date"
            totalCaloriesText.text = "Total calories: 0"
            return
        }

        val foodList = mutableListOf<String>()
        var totalCalories = 0

        for (document in querySnapshot.documents) {
            val name = document.getString("name") ?: "Unknown Food"
            val calories = document.getLong("calories")?.toInt() ?: 0 // Use getLong for numeric fields

            foodList.add("$name - $calories kcal")
            totalCalories += calories
        }

        foodListText.text = foodList.joinToString("\n")
        totalCaloriesText.text = "Total calories: $totalCalories"
    }

    private fun extractCalorieData(querySnapshot: QuerySnapshot): List<Pair<String, Int>> {
        if (querySnapshot.isEmpty) {
            return emptyList()
        }

        val extractedData = mutableListOf<Pair<String, Int>>()

        for (document in querySnapshot.documents) {
            val name = document.getString("name") ?: "Unknown Food"
            val calories = document.getLong("calories")?.toInt() ?: 0 // Use getLong for numeric fields
            extractedData.add(name to calories) // Pair of food name and calorie count
        }

        // Optionally log or return the data for external processing
        extractedData.forEach { (name, calories) ->
            println("Food: $name, Calories: $calories") // Debugging
        }

        return extractedData
    }
}
