package com.example.halalify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Log

class scannedproductresult : AppCompatActivity() {

    private lateinit var imageOfFood: ImageView
    private lateinit var foodName: TextView
    private lateinit var foodCategory: TextView
    private lateinit var halalStatus: TextView
    private lateinit var ingredientsList: TextView
    private lateinit var calorieDisplay: TextView
    private var fullIngredientsList: List<String> = emptyList()
    private val firestore = FirebaseFirestore.getInstance()
    private var shouldSaveCalories = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scannedproductresult)
        val goBackButton = findViewById<Button>(R.id.gobackbutton)

        // Initialize views
        imageOfFood = findViewById(R.id.imageoffood)
        foodName = findViewById(R.id.textView14)
        foodCategory = findViewById(R.id.textView18)
        halalStatus = findViewById(R.id.textView19)
        ingredientsList = findViewById(R.id.textView21)
        calorieDisplay = findViewById(R.id.calorydisplay)


        val calorieHistoryButton = findViewById<ImageButton>(R.id.addcalorieshistory)
        calorieHistoryButton.setOnClickListener {
            val intent = Intent(this, caloriehistory::class.java)
            shouldSaveCalories = false// Set flag when the button is clicked
            saveCalorieDataToFirestore() // Save calories to "calorie history"
            Toast.makeText(this, "Calories added to history", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }


        val seeIngredientsButton = findViewById<Button>(R.id.button7)
        seeIngredientsButton.setOnClickListener {
            val fullData = fullIngredientsList.joinToString("\n")
            val intent = Intent(this, ingredientlist::class.java)
            intent.putExtra("ingredients", fullData)
            startActivity(intent)
        }


        // Retrieve barcode from Intent extras
        val barcode = intent.getStringExtra("barcode")

        if (barcode != null) {
            fetchProductInfoFromAPI(barcode)
        } else {
            Toast.makeText(this, "No barcode found", Toast.LENGTH_SHORT).show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scannedproduct)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        goBackButton.setOnClickListener {
            finish()
        }
        // Retrieve and display the latest scanned barcode information
        // getLatestBarcodeAndFetchProductInfo()

    }


    private fun fetchProductInfoFromAPI(barcode: String) {
        val url = "https://world.openfoodfacts.org/api/v0/product/$barcode.json"
        val client = OkHttpClient()

        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@scannedproductresult,
                        "Failed to fetch product info",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val jsonData = responseBody.string()
                    val jsonObject = JSONObject(jsonData)

                    if (!jsonObject.has("product")) {
                        runOnUiThread {
                            AlertDialog.Builder(this@scannedproductresult)
                                .setTitle("Product Unavailable")
                                .setMessage("Food product unavailable")
                                .setPositiveButton("OK") { _, _ ->
                                    finish()
                                }
                                .setCancelable(false)
                                .show()
                        }
                        return
                    }

                    val product = jsonObject.getJSONObject("product")

                    val name = product.optString("product_name", "N/A")
                    val category = product.optJSONArray("categories_tags")?.optString(0) ?: "N/A"

                    // Ingredients parsing
                    val ingredientsArray = product.optJSONArray("ingredients")
                    val ingredients = mutableListOf<String>()
                    if (ingredientsArray != null && ingredientsArray.length() > 0) {
                        for (i in 0 until ingredientsArray.length()) {
                            try {
                                // Safely get each ingredient object
                                val ingredientObject = ingredientsArray.optJSONObject(i)

                                if (ingredientObject != null) {
                                    // Safely get the "text" key from the ingredient object
                                    val ingredientName = ingredientObject.optString("text", "Unknown Ingredient")
                                    ingredients.add(ingredientName)
                                } else {
                                    // Log or handle null ingredient objects
                                    Log.w("IngredientsParsing", "Null ingredient object at index $i")
                                }
                            } catch (e: Exception) {
                                // Log any exceptions to ensure parsing continues
                                Log.e("IngredientsParsing", "Error parsing ingredient at index $i: ${e.message}")
                            }
                        }
                    } else {
                        Log.w("IngredientsParsing", "No ingredients found")
                    }

                    // Check for haram ingredients
                    // val containsHaram = HaramIngredientsUtility.checkHaramStatus(ingredients)
                    val halalStatusText = when {
                        ingredients.isEmpty() -> "Unknown" // No ingredients available
                        HaramIngredientsUtility.checkHaramStatus(ingredients) -> "Non-Halal"
                        else -> "Halal"
                    }
                    // Nutritional information parsing
                    val nutriments = product.optJSONObject("nutriments")
                    val calorieInfo =
                        nutriments?.optString("energy-kcal_100g", "No calorie info available")
                            ?: "No calorie info available"

                    val nutritionInfo = mutableListOf<String>()
                    nutriments?.let {
                        val protein = it.optString("proteins_100g", "Unknown") + "g protein"
                        val carbs = it.optString("carbohydrates_100g", "Unknown") + "g carbs"
                        val fats = it.optString("fat_100g", "Unknown") + "g fat"
                        val energy = it.optString("energy-kcal_100g", "Unknown") + " kcal energy"

                        nutritionInfo.addAll(listOf(protein, carbs, fats, energy))


                    }


                    runOnUiThread {
                        foodName.text = name
                        foodCategory.text = category
                        halalStatus.text = halalStatusText
                        fullIngredientsList =
                            ingredients + nutritionInfo // Combine ingredients and nutritional info
                        calorieDisplay.text = "Calories: $calorieInfo"

                        val ingredient1TextView = findViewById<TextView>(R.id.textView22)
                        val ingredient2TextView = findViewById<TextView>(R.id.textView23)

                        if (fullIngredientsList.isEmpty()) {
                            ingredient1TextView.visibility = TextView.GONE
                            ingredient2TextView.visibility = TextView.GONE
                            ingredientsList.text = "No data found"
                        } else {
                            ingredientsList.text = "Ingredients:"
                            if (fullIngredientsList.size > 0) {
                                ingredient1TextView.text = fullIngredientsList[0]
                                ingredient1TextView.visibility = TextView.VISIBLE
                            } else {
                                ingredient1TextView.visibility = TextView.GONE
                            }
                            if (fullIngredientsList.size > 1) {
                                ingredient2TextView.text = fullIngredientsList[1]
                                ingredient2TextView.visibility = TextView.VISIBLE
                            } else {
                                ingredient2TextView.visibility = TextView.GONE
                            }
                        }

                        val imageUrl = product.optString("image_url", "")
                        if (imageUrl.isNotEmpty()) {
                            Glide.with(this@scannedproductresult)
                                .load(imageUrl)
                                .into(imageOfFood)
                        }
                        saveFoodDataToFirestore(
                            barcode,
                            name,
                            category,
                            ingredients,
                            halalStatusText,
                            calorieInfo,
                            imageUrl,
                        )


                    }
                }
            }

        })

    }

    private fun saveFoodDataToFirestore(
        barcode: String,
        name: String,
        category: String,
        ingredients: List<String>,
        halalStatus: String,
        calories: String,
        imageUrl : String

    ) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
            Date()
        )
        val foodData = hashMapOf(
            "barcode" to barcode,
            "name" to name,
            "category" to category,
            "ingredients" to ingredients,
            "halalStatus" to halalStatus,
            "calories" to calories,
            "timestamp" to timestamp,
            "imageUrl" to imageUrl
        )
        firestore.collection("foodData")
            .add(foodData)
            .addOnSuccessListener {
                Toast.makeText(this, "Food data saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveCalorieDataToFirestore() {
        if (calorieDisplay.text.isNotEmpty() && foodName.text.isNotEmpty()) {
            val calorieText = calorieDisplay.text.toString()

            // Extract the numeric calorie value from the string
            val numericCalories = calorieText.replace(Regex("[^\\d]"), "").toIntOrNull()

            if (numericCalories != null) {
                val timestamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                val calorieData = hashMapOf(
                    "name" to foodName.text.toString(),
                    "calories" to numericCalories, // Store as an integer
                    "timestamp" to timestamp
                )

                firestore.collection("calorieHistory")
                    .add(calorieData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Calorie data saved successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to save calorie data: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(this, "Invalid calorie data", Toast.LENGTH_SHORT).show()
            }
        }
    }


}

