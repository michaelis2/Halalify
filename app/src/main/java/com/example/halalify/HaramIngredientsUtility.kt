package com.example.halalify

object HaramIngredientsUtility {

    // Predefined list of haram ingredients
    private val haramIngredients = listOf(
        "Alcohol",
        "Gelatin (pork)",
        "Gelatin",
        "Lard",
        "Rennet",
        "Carnitine",
        "Vanillin (alcohol-based)",
        "Vanillin",
        "Pork",
        "Bacon",
        "Whey",
        "Pepsin",
        "Ethanol",
        "Wine",
        "Been",
        "Liquor",
        "Blood plasma",
        "Blood sausage",
        "Lecithin",
        "Animal Shortening",
        "Glycerol (Glycerine)",
        "Hormones",
        "Cochineal (Carmine)",
        "Rum",
        "Casein",
        "L-cysteine",
        "Cysteine",

    )

    /**
     * Function to cross-check scanned ingredients against the haram list
     * @param ingredients List of ingredients to check
     * @return true if any haram ingredient is found, false otherwise
     */
    fun checkHaramStatus(ingredients: List<String>): Boolean {
        return ingredients.any { ingredient ->
            haramIngredients.any { haram ->
                ingredient.contains(haram, ignoreCase = true)
            }
        }
    }
}
