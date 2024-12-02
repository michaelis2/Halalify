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
import android.widget.ProgressBar


class caloriehistory : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var selectedDateText: TextView
    private lateinit var foodListText: TextView
    private lateinit var totalCaloriesText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var firestore: FirebaseFirestore
    private var bmrValue: Int = 2500 // Default value


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_caloriehistory)

        bmrValue = intent.getIntExtra("BMR_VALUE", 2500)

        // Initialize views
        calendarView = findViewById(R.id.calendarView)
        selectedDateText = findViewById(R.id.getselecteddate)
        foodListText = findViewById(R.id.foodlistanditscaloriesview)
        totalCaloriesText = findViewById(R.id.totalcaloriesthatdat)
        progressBar = findViewById(R.id.Prog)
        progressText = findViewById(R.id.txtper)

        // Initialize firestore
        firestore = FirebaseFirestore.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handle date selection from calendar
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
            selectedDateText.text = formattedDate
            fetchCalorieHistory(formattedDate)
        }
    }

    // Get the food data from firestore
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
                }
            }
            .addOnFailureListener { e ->
                foodListText.text = "Failed to fetch data: ${e.message}"
                totalCaloriesText.text = "Total calories: --"
                e.printStackTrace()
            }
    }


    // Display the data from firestore
    private fun displayCalorieHistory(querySnapshot: QuerySnapshot) {
        val foodList = mutableListOf<String>()
        var totalCalories = 0

        for (document in querySnapshot.documents) {
            val name = document.getString("name") ?: "Unknown Food"
            val calories = document.getLong("calories")?.toInt() ?: 0
            foodList.add("$name - $calories kcal")
            totalCalories += calories
        }

        foodListText.text = foodList.joinToString("\n")
        totalCaloriesText.text = "Total calories: $totalCalories"

        updateProgressBar(totalCalories)
    }

    // Update the percentage on the progress bar
    private fun updateProgressBar(calories: Int)  {
        // Calculate the progress percentage
        val percentage = (calories / bmrValue.toDouble()  * 100).toInt().coerceIn(0, 100)
        progressBar.progress = percentage
        progressText.text = "$percentage%"
    }
}

