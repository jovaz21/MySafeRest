package com.tekisware.jovaz.mysaferest.model

import java.io.Serializable

enum class OrderListStatus {
    IDLE,
    ORDERED,
    SERVING,
    SERVED
}

// Order List
data class OrderList(private var customersCount: Int): Serializable {

    // Init
    init {
        this.setCustomersCount(customersCount)
    }

    /** Status */
    var status: OrderListStatus = OrderListStatus.IDLE

    /** Meal Orders */
    private val list: MutableList<Order> = mutableListOf()
    val count
        get() = list.size
    val totalAmount
        get() = list.fold(0f, { value, order -> value + order.amount})

    /** Get Order At Index */
    fun getAt(index: Int): Order? = if ((index >= 0) && (index < count)) list[index] else null

    /** Add Order */
    fun add(order: Order) { this.list.add(order) }

    /** Index of */
    fun indexOf(order: Order): Int { return(this.list.indexOf(order)) }

    /** Remove Order At Index */
    fun removeAt(index: Int) { this.list.removeAt(index) }

    /** Find By Meal */
    fun findByMeal(mealId: Int): Order? {
        var res: Order? = null

        /* scan */
        this.list.forEach { if (it.meal.id == mealId) res = it }

        /* done */
        return(res)
    }

    // Get/Set CustomersCount
    fun getCustomersCount(): Int { return(this.customersCount) }
    fun setCustomersCount(value: Int) {

        /* check */
        if (customersCount <= 0) {
            throw(IllegalArgumentException("Invalid Customers Count"))
        }

        /* set */
        this.customersCount = value
    }
}