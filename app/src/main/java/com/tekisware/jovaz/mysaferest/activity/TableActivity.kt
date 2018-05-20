package com.tekisware.jovaz.mysaferest.activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.ViewGroup
import com.tekisware.jovaz.mysaferest.R
import com.tekisware.jovaz.mysaferest.fragment.TableViewFragment
import com.tekisware.jovaz.mysaferest.model.OrderList
import com.tekisware.jovaz.mysaferest.model.Table
import java.util.*

class TableActivity : AppCompatActivity(), TableViewFragment.DelegatedEventsListener {

    // Privates

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
    }

    // Options Item Seletcted
    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    // Table OrderList Updated
    override fun onTableOrderListUpdated(tableId: Int, orderList: OrderList) {
        _curTable?.setOrderList(orderList, true)
    }
}
