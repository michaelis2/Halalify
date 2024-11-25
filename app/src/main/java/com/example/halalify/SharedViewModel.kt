package com.example.halalify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log

class SharedViewModel : ViewModel() {
    private val _totalCalories = MutableLiveData<Int>()
    val totalCalories: LiveData<Int> get() = totalCalories

    fun setTotalCalories(calories: Int) {
        _totalCalories.value = calories
      //  Log.d("SharedViewModel", "Total Calories Updated: $updatedCalories")
    }
}
