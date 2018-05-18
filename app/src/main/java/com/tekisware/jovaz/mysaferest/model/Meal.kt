package com.tekisware.jovaz.mysaferest.model

import android.graphics.Bitmap
import java.io.Serializable

enum class MealAllergen {
    CEREAL,
    CRUSTACEAN,
    EGG,
    FISH,
    PEANUT,
    MILK,
    NUTFRUIT
}

// Meal
data class Meal(val categoryName: String, val id: Int, val name: String, val price: Float, val photo: Int, val description: String, val allergens: List<MealAllergen>): Serializable {
}