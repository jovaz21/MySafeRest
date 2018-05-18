package com.tekisware.jovaz.mysaferest.model

import com.tekisware.jovaz.mysaferest.R

// Data Manager
object DataManager {

    /** Tables */
    val tableList by lazy {
        listOf<Table>(
                Table(1, "Sala nº1", "Mesa nº1"),
                Table(2, "Sala nº2", "Mesa nº2"),
                Table(3, "Sala nº3", "Mesa nº3"),
                Table(4, "Sala nº4", "Mesa nº4"),
                Table(5, "Sala nº5", "Mesa nº5"),
                Table(6, "Sala nº6", "Mesa nº6"),
                Table(7, "Sala nº7", "Mesa nº7"),
                Table(8, "Sala nº8", "Mesa nº8")
        )
    }
    fun getTable(index: Int) = if ((index >= 0) && (index < tableList.size)) tableList[index] else null

    /** Meals */
    val mealList by lazy {
        listOf<Meal>(
                Meal("BURGUERS", 0, "THE ITALIAN (2 CARNES)", 5.99f, R.drawable.the_italian, "Guardada entre dos panes de patata estilo brioche, jugosa carne a la parrilla acompañada de fresca lechuga, suave cebolla, exquisita salsa de tomate y deliciosas sabanitas de mozzarella.", listOf<MealAllergen>()),
                Meal("BURGUERS", 1, "THE BACON (2 CARNES)", 5.65f, R.drawable.the_bacon, "Dos deliciosas carnes a la parrilla acompañadas de crujientes lonchas de bacon con queso, tomate y mayonesa de bacon.", listOf<MealAllergen>()),
                Meal("BURGUERS", 2, "THE HUEVO (2 CARNES)", 5.65f, R.drawable.the_huevo, "Steakhouse, en su versión simple o doble, con carne a la parrilla, mayonesa, tomate, bacon, cebolla crujiente, kétchup y queso americano. Todo ello coronado por un delicioso huevo frito entre pan steakhouse.", listOf<MealAllergen>()),
                Meal("POSTRES", 3, "HAAGEN DAZS BELGIAN CHOCOLATE 500ML", 7.0f, R.drawable.belgian_chocolate, "Chocolate belga con delicadas láminas de chocolate belga negro", listOf<MealAllergen>()),
                Meal("POSTRES", 4, "GOFRE CALIENTE CON SIROPE", 2.75f, R.drawable.gofre, "Delicioso gofre caliente con sirope a elegir: Chocolate, Fresa o Caramelo", listOf<MealAllergen>()),
                Meal("POSTRES", 5, "TARTA OREO", 1.99f, R.drawable.tarta_oreo, "Tarta Oreo", listOf<MealAllergen>()),
                Meal("BEBIDAS", 6, "COCA COLA ZERO", 2.50f, R.drawable.cocacola_zero, "CocaCola Zero", listOf<MealAllergen>()),
                Meal("BEBIDAS", 7, "COCA COLA", 2.50f, R.drawable.cocacola, "CocaCola", listOf<MealAllergen>()),
                Meal("BEBIDAS", 8, "COCA COLA LIGHT", 2.50f, R.drawable.cocacola_light, "CocaCola Light", listOf<MealAllergen>()),
                Meal("BEBIDAS", 9, "FANTA NARANJA", 2.50f, R.drawable.fanta_naranja, "Fanta Naranja", listOf<MealAllergen>()),
                Meal("BEBIDAS", 10, "FANTA LIMÓN", 2.50f, R.drawable.fanta_limon, "Fanta Limón", listOf<MealAllergen>())
        )
    }
    fun getMeal(index: Int) = if ((index >= 0) && (index < mealList.size)) mealList[index] else null
}