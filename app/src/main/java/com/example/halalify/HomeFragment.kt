package com.example.halalify

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.Query
import com.google.android.material.textfield.TextInputEditText
import com.example.halalify.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.bumptech.glide.Glide


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null




    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize ViewBinding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Adding logic for the search bar and search button
        val searchBar = binding.textInputEditText
        val searchButton = binding.searchbutton

        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("username")
                        // Display username in the UI
                        binding.Usernamehome.text = username ?: "No Username Found"

                        // Fetch user details for BMR calculation
                        val age = document.getLong("age")?.toInt() ?: 0
                        val height = document.getLong("height")?.toInt() ?: 0
                        val weight = document.getLong("weight")?.toInt() ?: 0
                        val gender = when (document.getString("gender")) {
                            "Men" -> "Men"
                            "Women" -> "Women"
                            else -> "Men"  // Default to "Men" if the gender is not set or doesn't match
                        }

                        // Calculate and display BMR
                        val bmr = calculateBMR(gender, age, height, weight)
                        binding.basalmetabolicratedisplay.text = "BMR: ${bmr.toInt()} kcal/day"
                    } else {
                        binding.Usernamehome.text = "Document not found"
                    }
                }
                .addOnFailureListener { exception ->
                    binding.Usernamehome.text = "Error: ${exception.message}"
                }

            // Fetch and display last three scanned foods
            fetchLastThreeFoods(db)
        } else {
            // User is not authenticated
            binding.Usernamehome.text = "No user logged in"
        }

        searchButton.setOnClickListener {
            val searchText = searchBar.text.toString()
            if (searchText.isNotEmpty()) {
                // Navigate to Searchfoodsearchbar activity with the search text
                val intent = Intent(requireContext(), Searchfoodsearchbar::class.java)
                intent.putExtra("FOOD_QUERY", searchText) // Pass the user input
                startActivity(intent)
            }
        }

        return view
    }

    private fun fetchLastThreeFoods(db: FirebaseFirestore) {
        val foodDataRef = db.collection("foodData")

        // Query for the last three added foods (global, not user-specific)
        foodDataRef
            .orderBy("timestamp", Query.Direction.DESCENDING) // Ensure timestamp is indexed
            .limit(3)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val foodList = task.result!!.documents.map { it.data }
                    updateFoodUI(foodList)
                } else {
                    hideFoodSection()
                }
            }
    }


    private fun updateFoodUI(foodList: List<Map<String, Any>?>) {
        if (foodList.isEmpty()) {
            hideFoodSection()
            return
        }

        // Update the UI with the food data
        if (foodList.size > 0) {
            val food1 = foodList[0]
            updateFoodCard(
                food1,
                binding.FoodImage1,
                binding.fooddetail1
            )
        } else {
            binding.FoodImage1.visibility = View.GONE
            binding.fooddetail1.visibility = View.GONE
        }

        if (foodList.size > 1) {
            val food2 = foodList[1]
            updateFoodCard(
                food2,
                binding.FoodImage2,
                binding.fooddetail2
            )
        } else {
            binding.FoodImage2.visibility = View.GONE
            binding.fooddetail2.visibility = View.GONE
        }

        if (foodList.size > 2) {
            val food3 = foodList[2]
            updateFoodCard(
                food3,
                binding.FoodImage3,
                binding.fooddetail3
            )
        } else {
            binding.FoodImage3.visibility = View.GONE
            binding.fooddetail3.visibility = View.GONE
        }
    }

    private fun updateFoodCard(
        foodData: Map<String, Any>?,
        imageView: ImageView,
        detailView: TextView
    ) {
        if (foodData == null) return

        val name = foodData["name"] as? String ?: "Unknown Food"
        val calories = foodData["calories"] as? String ?: "Unknown Calories"
        val imageUrl = foodData["imageUrl"] as? String
        val halalstatus= foodData["halalStatus"] as? String

        // Update food details
        detailView.text = "$name - $calories kcal, Status: $halalstatus"

        // Load the image using Glide if imageUrl is not null
        if (imageUrl != null && imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl) // Glide will load the image from the URL
                .placeholder(R.drawable.placeholder) // Add a placeholder image to resources
                .error(R.drawable.placeholder) // Optional: handle the error case with a fallback image
                .into(imageView)
        } else {
            // If no imageUrl, use the placeholder image or hide the ImageView
            Glide.with(this)
                .load(R.drawable.placeholder) // Use the placeholder image if no image URL
                .into(imageView)
        }
    }

    private fun hideFoodSection() {
        binding.FoodImage1.visibility = View.GONE
        binding.fooddetail1.visibility = View.GONE
        binding.FoodImage2.visibility = View.GONE
        binding.fooddetail2.visibility = View.GONE
        binding.FoodImage3.visibility = View.GONE
        binding.fooddetail3.visibility = View.GONE
    }
    // Function to calculate BMR
    private fun calculateBMR(gender: String, age: Int, height: Int, weight: Int): Double {
        return if (gender.equals("Women", ignoreCase = true)) {
            // BMR formula for women
            447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
        } else {
            // BMR formula for men
            88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

