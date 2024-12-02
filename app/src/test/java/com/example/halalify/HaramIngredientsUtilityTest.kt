package com.example.halalify

import org.junit.jupiter.api.Assertions.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class HaramIngredientsUtilityTest {

    @Test
    fun testHaramIngredientFound() {
        // Test case where haram ingredients are present
        val ingredients = listOf("Gelatin", "Sugar", "Vegetable Oil")
        val result = HaramIngredientsUtility.checkHaramStatus(ingredients)

        // Assert that the result is true since "Gelatin" is in the haram ingredients list
        assertTrue(result, "The haram ingredient should be found")
    }

    @Test
    fun testNoHaramIngredient() {
        // Test case where no haram ingredients are present
        val ingredients = listOf("Sugar", "Salt", "Pepper")
        val result = HaramIngredientsUtility.checkHaramStatus(ingredients)

        // Assert that the result is false since no haram ingredients are in the list
        assertFalse(result, "No haram ingredients should be found")
    }

    @Test
    fun testCaseInsensitiveHaramIngredient() {
        // Test case where the haram ingredient is in a different case (case-insensitive test)
        val ingredients = listOf("gElAtIn", "Sugar")
        val result = HaramIngredientsUtility.checkHaramStatus(ingredients)

        // Assert that the result is true since "gElAtIn" should be considered as "Gelatin"
        assertTrue(result, "The haram ingredient should be found, ignoring case")
    }

    @Test
    fun testEmptyIngredientList() {
        // Test case where the ingredient list is empty
        val ingredients = listOf<String>()
        val result = HaramIngredientsUtility.checkHaramStatus(ingredients)

        // Assert that the result is false since the list is empty
        assertFalse(result, "An empty ingredient list should not have haram ingredients")
    }
}
