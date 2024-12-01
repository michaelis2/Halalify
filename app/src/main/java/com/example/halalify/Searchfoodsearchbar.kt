package com.example.halalify

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class Searchfoodsearchbar : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchfoodsearchbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize search bar and the button
        val searchBar = findViewById<EditText>(R.id.editTextText7)
        val searchButton = findViewById<ImageButton>(R.id.imageButton6)

        // Get initial keyword from the home fragment
        val initialSearchQuery = intent.getStringExtra("FOOD_QUERY") ?: ""

        // Set the initial keyword in the search bar and fetch suggestions
        if (initialSearchQuery.isNotEmpty()) {
            searchBar.setText(initialSearchQuery)
            fetchFoodSuggestions(initialSearchQuery)
        }

        // When the search button is clicked, fetch suggestions for the entered query
        searchButton.setOnClickListener {
            val newQuery = searchBar.text.toString().trim()
            if (newQuery.isNotEmpty()) {
                fetchFoodSuggestions(newQuery)
            } else {
                Toast.makeText(this, "Please enter a search keyword", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Fetches food suggestions based on the search query
    private fun fetchFoodSuggestions(query: String) {
        val apiUrl = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=$query&search_simple=1&action=process&json=1"


        // Use Coroutine to perform network operation in the background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                // Check if the request was successful
                if (connection.responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val suggestions = parseSearchResponse(response)

                    // Switch back to the main thread to update the UI
                    withContext(Dispatchers.Main) {
                        if (suggestions.isNotEmpty()) {
                            displaySuggestions(suggestions) // Display suggestions in the UI
                        } else {
                            Toast.makeText(this@Searchfoodsearchbar, "No matching products found", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val errorResponse = connection.errorStream?.bufferedReader()?.readText() ?: "Unknown error"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Searchfoodsearchbar, "Error: $errorResponse", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Show error message if an exception occurs
                    Toast.makeText(this@Searchfoodsearchbar, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    // Displays food suggestions in the UI
    private fun displaySuggestions(suggestions: List<Map<String, String>>) {
        val resultContainer = findViewById<LinearLayout>(R.id.resultContainer)
        resultContainer.removeAllViews() // Clear any existing views


        // Iterate through each suggestion and create a view for it
        for (suggestion in suggestions) {
            val productName = suggestion["productName"] ?: "Unknown Product"
            val calories = suggestion["calories"] ?: "Unknown"
            val productImage = suggestion["productImage"] ?: ""
            val category = suggestion["category"] ?: "N/A"
            val halalStatus = suggestion["halalStatus"] ?: "Unknown"


            // Create a layout for each suggestion
            val suggestionView = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 16, 16, 16)
            }

            // Initialize an ImageView for the product image and load it using Glide
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(150, 150)
            }
            Glide.with(this).load(productImage).into(imageView)

            val textView = TextView(this).apply {
                text = productName  // Only display product name, no calories
                textSize = 16f
                setPadding(16, 0, 0, 0)
                setOnClickListener {
                    // On click, pass the product details to the result activity
                    val intent = Intent(this@Searchfoodsearchbar, searchproductresult::class.java)
                    intent.putExtra("product_name", productName)
                    intent.putExtra("calories", calories)
                    intent.putExtra("product_image", productImage)
                    intent.putExtra("category", category)
                    intent.putExtra("halal_status", halalStatus)

                    startActivity(intent)
                }
            }

            suggestionView.addView(imageView)
            suggestionView.addView(textView)
            resultContainer.addView(suggestionView)
        }
    }



    // Parses the response from the OpenFoodFacts API and returns a list of product suggestions
    private fun parseSearchResponse(response: String): List<Map<String, String>> {
        val jsonObject = JSONObject(response)
        val productsArray = jsonObject.optJSONArray("products") ?: return emptyList()

        val suggestions = mutableListOf<Map<String, String>>()
        for (i in 0 until productsArray.length()) {
            val product = productsArray.getJSONObject(i)

            // Parsing product name, ID, and image URL
            val productName = product.optString("product_name", "Unknown Product")
            val productId = product.optString("_id", "")
            val productImage = product.optString("image_front_url", "")

            // Parsing nutritional information, including calories
            val nutriments = product.optJSONObject("nutriments")
            val calories = nutriments?.optString("energy-kcal_100g", "Unknown") ?: "Unknown"
            val category = product.optJSONArray("categories_tags")?.optString(0) ?: "N/A"

            // Parsing ingredients (if available)
            val ingredientsArray = product.optJSONArray("ingredients")
            val ingredients = if (ingredientsArray != null) {
                val ingredientsList = mutableListOf<String>()
                for (j in 0 until ingredientsArray.length()) {
                    ingredientsList.add(ingredientsArray.getString(j))
                }
                ingredientsList // Return the list directly instead of a concatenated string
            } else {
                emptyList<String>() // Return an empty list if no ingredients available
            }

            val halalStatusText = when {
                ingredients.isEmpty() -> "Unknown" // No ingredients available
                HaramIngredientsUtility.checkHaramStatus(ingredients) -> "Non-Halal"
                else -> "Halal"
            }

            // Add the product information to the suggestions list
            if (productId.isNotEmpty()) {
                val productInfo = mapOf(
                    "productName" to productName,
                    "calories" to calories,
                    "productImage" to productImage,
                    "category" to category,
                    "halalStatus" to halalStatusText
                )
                suggestions.add(productInfo)
            }
        }
        return suggestions
    }


}
