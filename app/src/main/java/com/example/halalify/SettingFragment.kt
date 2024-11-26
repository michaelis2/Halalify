package com.example.halalify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import com.example.halalify.databinding.FragmentHomeBinding
import com.example.halalify.databinding.FragmentSettingBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {

    private lateinit var displayHeight: TextView
    private lateinit var displayWeight: TextView
    private lateinit var displayAge: TextView
    private lateinit var displayGender: TextView
    private lateinit var displayUsername: TextView

    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentSettingBinding? = null
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

        _binding = FragmentSettingBinding.inflate(inflater, container, false) // Initialize binding
        fetchUserData() // Ensure binding is initialized before calling this
        return binding.root // Use the root view from binding
    }

    private fun fetchUserData() {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid // Get the currently authenticated user's UID

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        try {
                            // Extract and handle fields correctly
                            val username = document.getString("username") ?: "Username not set"
                            val gender = document.getString("gender") ?: "Gender not set"
                            val age = document.getLong("age")?.toInt()?.toString() ?: "Age not set"
                            val height = document.getLong("height")?.toInt()?.toString() ?: "Height not set"
                            val weight = document.getLong("weight")?.toInt()?.toString() ?: "Weight not set"

                            // Safely update UI using binding
                            binding.apply {
                                textView2.text = username
                                displaygender.text = gender
                                displayage.text = age
                                displayheight.text = height
                                displayweight.text = weight
                            }
                        } catch (e: Exception) {
                            Log.e("SettingFragment", "Error parsing Firestore document", e)
                        }
                    } else {
                        Log.e("SettingFragment", "Document does not exist")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("SettingFragment", "Failed to fetch data from Firestore", exception)
                }
        } else {
            Log.e("SettingFragment", "No user is currently authenticated")
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}