package com.example.halalify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IngredientsAdapter(private val ingredients: List<String>) :
    RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    // ViewHolder class to hold each ingredient item
    class IngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ingredientTextView: TextView = view.findViewById(R.id.ingredientText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ingredient_item, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.ingredientTextView.text = ingredients[position]
    }

    override fun getItemCount(): Int = ingredients.size
}
