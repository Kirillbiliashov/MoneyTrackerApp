package com.example.moneytrackerapp

/**
 * This is mock data for testing the look of UI. It will be removed when database support is added
 */

object Datasource {

    val categories = listOf("Groceries", "Clothes", "Education")

    private val groceriesExpenses = listOf("Fruits" to 4.99, "Dairy" to 3.99, "Meat" to 6.99)
    private val clothesExpenses = listOf("T-Shirt" to 11.99, "Cap" to 6.99, "Tracksuit" to 64.99)
    private val educationExpenses =
        listOf("Book" to 15.99, "Udemy course" to 9.99, "Podcast subscription" to 2.99)

    fun getExpenses(category: String) = when (category) {
        "Groceries" -> groceriesExpenses
        "Clothes" -> clothesExpenses
        "Education" -> educationExpenses
        else -> listOf()
    }

}