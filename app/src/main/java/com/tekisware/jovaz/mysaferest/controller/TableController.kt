package com.tekisware.jovaz.mysaferest.controller

import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.tekisware.jovaz.mysaferest.R
import com.tekisware.jovaz.mysaferest.activity.TableActivity
import com.tekisware.jovaz.mysaferest.fragment.*
import com.tekisware.jovaz.mysaferest.model.Meal
import com.tekisware.jovaz.mysaferest.model.Order
import com.tekisware.jovaz.mysaferest.model.OrderList
import com.tekisware.jovaz.mysaferest.model.Table
import java.util.*

// Table Controller
class TableController(activity: AppCompatActivity, table: Table): TableView.OnOrderListChangedListener, TableViewFragment.DelegatedEventsListener, OrderEditorFragment.DelegatedEventsListener, MealListFragment.DelegatedEventsListener {

    // Statics
    companion object {

        /** Current Observed Table */
        private var _curTable: Table? = null

        // Setup
        fun setup(parent: AppCompatActivity, table: Table) {

            /* check */
            if (parent is Observer) {
                val observer = parent as Observer

                /* set */
                if (_curTable != null) {
                    _curTable!!.deleteObservers()
                }
                _curTable = table
                _curTable?.addObserver(observer)
            }
        }

        // Start
        fun start(activity: AppCompatActivity, table: Table): TableController {
            return(TableController(activity, table))
        }
    }

    /** Privates */
    private val activity:   AppCompatActivity
    private val table:      Table

    // Setup
    init {
        this.activity   = activity
        this.table      = table
    }

    override fun onOrderListChanged(tableId: Int, orderList: OrderList) {
        _curTable?.setOrderList(orderList, true)
    }

    override fun onOrderClicked(view: View, index: Int, order: Order) {
        val orderLineEditorFragment = OrderLineEditorFragment.newInstance(this.table, order.meal, order)
        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.table_activity_fragment, orderLineEditorFragment)
                .addToBackStack(null)
                .commit()
    }

    override fun onAddOrderClicked() {
        val orderEditorFragment = OrderEditorFragment.newInstance(this.table)
        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.table_activity_fragment, orderEditorFragment)
                .addToBackStack(null)
                .commit()
    }

    override fun onCloseTable(tableId: Int) {
        _curTable?.setAvailable(true)
    }

    override fun onMealOrderClicked(view: View, index: Int, meal: Meal, order: Order?) {
        val orderLineEditorFragment = OrderLineEditorFragment.newInstance(this.table, meal, order)
        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.table_activity_fragment, orderLineEditorFragment)
                .addToBackStack(null)
                .commit()
    }

    // Options Item Seletcted
    fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        android.R.id.home -> {
            if (activity.supportFragmentManager.getBackStackEntryCount() == 0){
                activity.finish()
            }
            else {
                activity.supportFragmentManager.popBackStack()
            }
            //finish()
            true
        }
        else -> false
    }
}