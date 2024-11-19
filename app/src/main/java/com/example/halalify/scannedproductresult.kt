package com.example.halalify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

class scannedproductresult : AppCompatActivity() {

    private lateinit var imageOfFood: ImageView
    private lateinit var foodName: TextView
    private lateinit var foodCategory: TextView
    private lateinit var halalStatus: TextView
    private lateinit var ingredientsList: TextView
    private var fullIngredientsList: List<String> = emptyList()


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
        val seeIngredientsButton = findViewById<Button>(R.id.button7)
        seeIngredientsButton.setOnClickListener {
            val fullIngredientsText = fullIngredientsList.joinToString("\n")

            // Create an intent to open the ingredient list activity
            val intent = Intent(this, ingredientlist::class.java)

            // Pass the ingredients data to the new activity
            intent.putExtra("ingredients", fullIngredientsText)
            startActivity(intent)}

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
                    Toast.makeText(this@scannedproductresult, "Failed to fetch product info", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val jsonData = responseBody.string()
                    val jsonObject = JSONObject(jsonData)

                    // Check if product exists
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

                    // Parse product data
                    val name = product.optString("product_name", "N/A")
                    val category = product.optJSONArray("categories_tags")?.optString(0) ?: "N/A"

                    // Handle ingredients array
                    val ingredientsArray = product.optJSONArray("ingredients")
                    val ingredients = if (ingredientsArray != null && ingredientsArray.length() > 0) {
                        val list = mutableListOf<String>()
                        for (i in 0 until ingredientsArray.length()) {
                            val ingredientObject = ingredientsArray.getJSONObject(i)
                            val ingredientName = ingredientObject.optString("text", "Unknown Ingredient")
                            list.add(ingredientName)
                        }
                        list
                    } else {
                        emptyList()
                    }

                    // Determine halal status
                    val halalStatusText = if (ingredients.any { it.contains("halal", true) }) "Halal" else "Not Halal"

                    // Update UI on main thread
                    runOnUiThread {
                        foodName.text = name
                        foodCategory.text = category
                        halalStatus.text = halalStatusText

                        val ingredient1TextView = findViewById<TextView>(R.id.textView22)
                        val ingredient2TextView = findViewById<TextView>(R.id.textView23)

                        if (ingredients.isEmpty()) {
                            // Hide ingredient TextViews if no ingredients found
                            ingredient1TextView.visibility = TextView.GONE
                            ingredient2TextView.visibility = TextView.GONE
                            ingredientsList.text = "No ingredients found"
                        } else {
                            // Display "Ingredients:" in its own TextView
                            ingredientsList.text = "Ingredients:"
                            fullIngredientsList = ingredients

                            // Display first ingredient or hide if not available
                            if (ingredients.size > 0) {
                                ingredient1TextView.text = ingredients[0]
                                ingredient1TextView.visibility = TextView.VISIBLE
                            } else {
                                ingredient1TextView.visibility = TextView.GONE
                            }

                            // Display second ingredient or hide if not available
                            if (ingredients.size > 1) {
                                ingredient2TextView.text = ingredients[1]
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
                    }
                }
            }
        })
    }



}
