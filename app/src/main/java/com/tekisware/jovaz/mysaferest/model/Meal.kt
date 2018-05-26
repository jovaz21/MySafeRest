package com.tekisware.jovaz.mysaferest.model

import android.graphics.Bitmap
import com.tekisware.jovaz.mysaferest.R
import java.io.Serializable

enum class MealAllergen {
    GLUTINE,
    CROSTACEI,
    UOVA,
    ARACHIDI,
    LATTE,
    FRUTTA
}

// Meal
data class Meal(val categoryName: String, val id: Int, val name: String, val price: Float, val photo: Int, val description: String, val allergens: List<MealAllergen>): Serializable {

    // Get AllergenIcons
    fun getAllergenIcons(): MutableList<Int> {
        val res = mutableListOf<Int>()

        /* scan */
        allergens.forEach {
            when(it) {
                MealAllergen.GLUTINE -> res.add(R.drawable.glutine)
                MealAllergen.CROSTACEI -> res.add(R.drawable.crostacei)
                MealAllergen.UOVA -> res.add(R.drawable.uova)
                MealAllergen.ARACHIDI -> res.add(R.drawable.arachidi)
                MealAllergen.LATTE -> res.add(R.drawable.latte)
                MealAllergen.FRUTTA -> res.add(R.drawable.frutta)}
        }

        /* done */
        return(res)
    }
}