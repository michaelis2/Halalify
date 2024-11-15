package com.example.halalify

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
                        // Product not found, show a dialog
                        runOnUiThread {
                            AlertDialog.Builder(this@scannedproductresult)
                                .setTitle("Product Unavailable")
                                .setMessage("Food product unavailable")
                                .setPositiveButton("OK") { _, _ ->
                                    // Close this activity to go back to the previous screen
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
                    val ingredients = product.optString("ingredients_text", "No ingredients")
                    val imageUrl = product.optString("image_url", "")
                    val halalStatusText = if (ingredients.contains("halal", true)) "Halal" else "Not Halal"

                    // Update UI on main thread
                    runOnUiThread {
                        foodName.text = name
                        foodCategory.text = category
                        halalStatus.text = halalStatusText
                        ingredientsList.text = "Ingredients: $ingredients"
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
