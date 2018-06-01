package com.tekisware.jovaz.mysaferest.activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tekisware.jovaz.mysaferest.R
import com.tekisware.jovaz.mysaferest.controller.TableController
import com.tekisware.jovaz.mysaferest.fragment.*
import com.tekisware.jovaz.mysaferest.model.*
import java.util.*

class TableActivity : AppCompatActivity(), TableView.OnOrderListChangedListener, TableViewFragment.DelegatedEventsListener, OrderEditorFragment.DelegatedEventsListener, MealListFragment.DelegatedEventsListener {

    // Statics
    companion object {
        val EXTRA_TABLE = "EXTRA_TABLE"

        // Intent
        fun intent(context: Context, table: Table): Intent {
            val intent = Intent(context, TableActivity::class.java)
            intent.putExtra(EXTRA_TABLE, table)
            return(intent)
        }

        // Show TableView
        fun showTableView(parent: AppCompatActivity, table: Table) {

            /* setup */
            TableController.setup(parent, table)

            /* check */
            if (parent.findViewById<ViewGroup>(R.id.table_activity_fragment) == null) {
                val intent = TableActivity.intent(parent, table)
                parent.startActivity(intent)
            }
            else {
                var fragment = parent.supportFragmentManager.findFragmentById(R.id.table_activity_fragment)

                /* check */
                if (fragment != null) {

                    /* check */
                    while (!(fragment is TableViewFragment)) {
                        parent.supportFragmentManager.popBackStackImmediate()
                        fragment = parent.supportFragmentManager.findFragmentById(R.id.table_activity_fragment)
                    }

                    /* done */
                    fragment.bindTable(table, true)
                    return
                }

                /* setup */
                val tableViewFragment = TableViewFragment.newInstance(table, true)
                parent.supportFragmentManager.beginTransaction()
                        .add(R.id.table_activity_fragment, tableViewFragment)
                        .commit()
            }

            /* done */
            return
        }
    }

    // Privates
    private var tableController: TableController? = null
    private val table by lazy<Table> {
        intent.getSerializableExtra(EXTRA_TABLE) as Table
    }

    // Activity Created Callback
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)

        /* start */
        tableController = TableController.start(this, table)

        // Adding Toolbar to Main screen
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = this.table.name
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /* check */
        if (findViewById<ViewGroup>(R.id.table_activity_fragment) == null)
            return
        if (findViewById<ViewGroup>(R.id.table_activity_fragment).visibility == View.INVISIBLE) {
            finish()
            return
        }

        /* check */
        var fragment = supportFragmentManager.findFragmentById(R.id.table_activity_fragment) as? TableViewFragment
        if (fragment != null) {
            fragment.bindTable(table, false)
            return
        }

        /* setup */
        fragment = TableViewFragment.newInstance(this.table, false)
        supportFragmentManager.beginTransaction()
                .add(R.id.table_activity_fragment, fragment)
                .commit()
    }

    // Options Item Seletcted
    override fun onOptionsItemSelected(item: MenuItem?) = when (tableController!!.onOptionsItemSelected(item)) {
        true -> true
        else -> super.onOptionsItemSelected(item)
    }

    // Add Order Button Clicked
    override fun onAddOrderClicked() {
        tableController?.onAddOrderClicked()
    }

    // Order Selected from TableViewFragment
    override fun onOrderClicked(view: View, index: Int, order: Order) {
        tableController?.onOrderClicked(view, index, order)
    }

    // Meal+Order Selected from MealListFragment
    override fun onMealOrderClicked(view: View, index: Int, meal: Meal, order: Order?) {
        tableController?.onMealOrderClicked(view, index, meal, order)
    }

    // Table OrderList Changed
    override fun onOrderListChanged(tableId: Int, orderList: OrderList) {
        tableController?.onOrderListChanged(tableId, orderList)
    }

    // Table Closed
    override fun onCloseTable(tableId: Int) {
        tableController?.onCloseTable(tableId)
    }
}
