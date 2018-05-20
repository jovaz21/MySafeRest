package com.tekisware.jovaz.mysaferest.model

import java.io.Serializable
import java.util.*

enum class TableStatus {
    AVAILABLE,
    BUSY,
    RESERVED
}

// Table
data class Table(val id: Int, val roomName: String, val name: String): Observable(), Serializable {
    var orderList: OrderList? = null

    // Init
    init {
        if (id <= 0) {
            throw(IllegalArgumentException("Invalid Table ID"))
        }
    }

    /** Status */
    var status: TableStatus = TableStatus.AVAILABLE

    // Set Reserved
    fun setReservedFor(customersCount: Int) {
        this.status = TableStatus.RESERVED
        this.orderList = OrderList(customersCount)
    }

    // Set Busy
    fun setBusyWith(customersCount: Int): OrderList {
        this.status = TableStatus.BUSY
        if (this.orderList == null)
            this.orderList = OrderList(customersCount)
        else {
            this.orderList!!.setCustomersCount(customersCount)
        }
        return(this.orderList!!)
    }

    // Set Available
    fun setAvailable() {
        this.status = TableStatus.AVAILABLE
        this.orderList = null
    }

    // Set OrderList
    fun setOrderList(value: OrderList, notifyObservers: Boolean) {

        /* set */
        this.orderList = value

        /* check */
        if (notifyObservers) {
            setChanged()
            notifyObservers(this)
        }
    }
}