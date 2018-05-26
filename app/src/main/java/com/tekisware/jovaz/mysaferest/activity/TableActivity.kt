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
import com.tekisware.jovaz.mysaferest.fragment.*
import com.tekisware.jovaz.mysaferest.model.Meal
import com.tekisware.jovaz.mysaferest.model.Order
import com.tekisware.jovaz.mysaferest.model.OrderList
import com.tekisware.jovaz.mysaferest.model.Table
import java.util.*

class TableActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener, TableView.OnOrderListChangedListener, TableViewFragment.DelegatedEventsListener, OrderEditorFragment.DelegatedEventsListener, MealListFragment.DelegatedEventsListener {

    // Statics
    companion object {
        val EXTRA_TABLE = "EXTRA_TABLE"

        /** Last Observed Table */
        private var _curTable: Table? = null

        // Intent
        fun intent(context: Context, table: Table): Intent {
            val intent = Intent(context, TableActivity::class.java)
            intent.putExtra(EXTRA_TABLE, table)
            return(intent)
        }

        // Show TableView
        fun showTableView(parent: AppCompatActivity, table: Table) {
            val fragment = parent.supportFragmentManager.findFragmentById(R.id.table_activity_fragment)

            /* check */
            if (parent is Observer) {
                val observer = parent as Observer

                /* set */
                if (_curTable != null) {
                    _curTable!!.deleteObserver(observer)
                }
                _curTable = table
                _curTable?.addObserver(observer)
            }

            /* check */
            if (fragment == null) {
                val intent = TableActivity.intent(parent, table)
                parent.startActivity(intent)
            }
            else {
                // TODO: Update Current Fragment Content
            }

            /* done */
            return
        }
    }

    // Privates
    private val table by lazy<Table> {
        intent.getSerializableExtra(EXTRA_TABLE) as Table
    }

    // Activity Created Callback
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)

        // Adding Toolbar to Main screen
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = this.table.name
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /* check */
        if (findViewById<ViewGroup>(R.id.table_activity_fragment) == null)
            return

        /* check */
        var fragment = supportFragmentManager.findFragmentById(R.id.table_activity_fragment) as? TableViewFragment
        if (fragment != null)
            return

        /* setup */
        fragment = TableViewFragment.newInstance(this.table)
        supportFragmentManager.beginTransaction()
                .add(R.id.table_activity_fragment, fragment)
                .commit()

        /* set */
        supportFragmentManager.addOnBackStackChangedListener(this)
    }

    // Options Item Seletcted
    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        android.R.id.home -> {
            if (supportFragmentManager.getBackStackEntryCount() == 0){
                finish()
            }
            else {
                supportFragmentManager.popBackStack()
            }
            //finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    // Add Order Button Clicked
    override fun onAddOrderClicked() {
        val orderEditorFragment = OrderEditorFragment.newInstance(this.table)
        supportFragmentManager.beginTransaction()
                .replace(R.id.table_activity_fragment, orderEditorFragment)
                .addToBackStack(null)
                .commit()
    }

    // Order Selected from TableViewFragment
    override fun onOrderClicked(view: View, index: Int, order: Order) {
        //Snackbar.make(view, order.meal.name, Snackbar.LENGTH_LONG).show()
        val orderLineEditorFragment = OrderLineEditorFragment.newInstance(this.table, order.meal, order)
        supportFragmentManager.beginTransaction()
                .replace(R.id.table_activity_fragment, orderLineEditorFragment)
                .addToBackStack(null)
                .commit()
    }

    // Meal+Order Selected from MealListFragment
    override fun onMealOrderClicked(view: View, index: Int, meal: Meal, order: Order?) {
        //Snackbar.make(view, meal.name, Snackbar.LENGTH_LONG).show()
        val orderLineEditorFragment = OrderLineEditorFragment.newInstance(this.table, meal, order)
        supportFragmentManager.beginTransaction()
                .replace(R.id.table_activity_fragment, orderLineEditorFragment)
                .addToBackStack(null)
                .commit()
    }

    // Table OrderList Changed
    override fun onOrderListChanged(tableId: Int, orderList: OrderList) {
        _curTable?.setOrderList(orderList, true)
    }

    // Table Activity BackStack Changed
    override fun onBackStackChanged() {

        /* check */
        /*var fragment = supportFragmentManager.findFragmentById(R.id.table_activity_fragment) as? TableViewFragment
        if (fragment != null) {
            val orderList = _curTable?.orderList
            return
        }*/
    }
}
