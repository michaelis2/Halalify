package com.example.halalify

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.Query
import com.example.halalify.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.bumptech.glide.Glide

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {

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

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize firestore and current user
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Initialize search components
        val searchBar = binding.textInputEditText
        val searchButton = binding.searchbutton


        //Get data of current user from firestore under the users collection
        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("username")
                        binding.Usernamehome.text = username ?: "No Username Found"

                        val age = document.getLong("age")?.toInt() ?: 0
                        val height = document.getLong("height")?.toInt() ?: 0
                        val weight = document.getLong("weight")?.toInt() ?: 0
                        val gender = when (document.getString("gender")) {
                            "Men" -> "Men"
                            "Women" -> "Women"
                            else -> "Men"
                        }

                        val bmr = calculateBMR(gender, age, height, weight)
                        val intent = Intent(requireContext(), caloriehistory::class.java)
                        intent.putExtra("BMR_VALUE", bmr)

                        //display BMR
                        binding.basalmetabolicratedisplay.text = "BMR: %.2f kcal/day".format(bmr)

                    } else {
                        //display username
                        binding.Usernamehome.text = "Document not found"
                    }
                }
                .addOnFailureListener { exception ->
                    binding.Usernamehome.text = "Error: ${exception.message}"
                }

            fetchLastThreeFoods(db)
        } else {
            binding.Usernamehome.text = "No user logged in"
        }

        searchButton.setOnClickListener {
            // Get string from the search bar
            val searchText = searchBar.text.toString()
            if (searchText.isNotEmpty()) {
                val intent = Intent(requireContext(), Searchfoodsearchbar::class.java)
                intent.putExtra("FOOD_QUERY", searchText)
                startActivity(intent)
            }
        }
         // Button to go to calorie history page
        binding.button3.setOnClickListener {
            val intent = Intent(requireContext(), caloriehistory::class.java)
            startActivity(intent)
        }

        return view
    }


    // Get the last 3 Scanned foods from the firestore database
    private fun fetchLastThreeFoods(db: FirebaseFirestore) {
        val foodDataRef = db.collection("foodData")

        foodDataRef
            .orderBy("timestamp", Query.Direction.DESCENDING)
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

    // Ensure scanned foods history exists
    private fun updateFoodUI(foodList: List<Map<String, Any>?>) {
        if (foodList.isEmpty()) {
            hideFoodSection()
            return
        }

        if (foodList.size > 0) {
            updateFoodCard(foodList[0], binding.FoodImage1, binding.fooddetail1)
        } else {
            binding.FoodImage1.visibility = View.GONE
            binding.fooddetail1.visibility = View.GONE
        }

        if (foodList.size > 1) {
            updateFoodCard(foodList[1], binding.FoodImage2, binding.fooddetail2)
        } else {
            binding.FoodImage2.visibility = View.GONE
            binding.fooddetail2.visibility = View.GONE
        }

        if (foodList.size > 2) {
            updateFoodCard(foodList[2], binding.FoodImage3, binding.fooddetail3)
        } else {
            binding.FoodImage3.visibility = View.GONE
            binding.fooddetail3.visibility = View.GONE
        }
    }

    // Display the data of the previous scanned food
    private fun updateFoodCard(
        foodData: Map<String, Any>?,
        imageView: ImageView,
        detailView: TextView
    ) {
        if (foodData == null) return

        val name = foodData["name"] as? String ?: "Unknown Food"
        val calories = foodData["calories"] as? String ?: "Unknown Calories"
        val imageUrl = foodData["imageUrl"] as? String
        val halalstatus = foodData["halalStatus"] as? String

        detailView.text = "$name \n $calories kcal \n Status: $halalstatus"

        if (imageUrl != null && imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(imageView)
        } else {
            Glide.with(this)
                .load(R.drawable.placeholder)
                .into(imageView)
        }
    }

    // Hide image and detail if empty history
    private fun hideFoodSection() {
        binding.FoodImage1.visibility = View.GONE
        binding.fooddetail1.visibility = View.GONE
        binding.FoodImage2.visibility = View.GONE
        binding.fooddetail2.visibility = View.GONE
        binding.FoodImage3.visibility = View.GONE
        binding.fooddetail3.visibility = View.GONE
    }

    // to calculate the BMR
    private fun calculateBMR(gender: String, age: Int, height: Int, weight: Int): Double {
        return if (gender.equals("Women", ignoreCase = true)) {
            447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
        } else {
            88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
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