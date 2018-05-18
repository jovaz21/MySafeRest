package com.tekisware.jovaz.mysaferest.model

import java.io.Serializable

enum class OrderStatus {
    IDLE,
    ORDERED,
    SERVED
}

// Order
data class Order(val meal: Meal, private var count: Int, private var notes: String): Serializable {

    // Init
    init {
        if (count <= 0) {
            throw(IllegalArgumentException("Invalid Meal Count for Order"))
        }
    }

    /** Status */
    var status: OrderStatus = OrderStatus.IDLE

    /** Order Amount */
    val amount
        get() = meal.price * count

    // Get/Set Count
    fun getCount(): Int { return(this.count) }
    fun setCount(value: Int) { this.count = value }

    // Get/Set Notes
    fun getNotes(): String { return(this.notes) }
    fun setNotes(value: String) { this.notes = value }
}