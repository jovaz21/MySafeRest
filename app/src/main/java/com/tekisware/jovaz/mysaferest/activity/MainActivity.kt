package com.tekisware.jovaz.mysaferest.activity

import android.content.DialogInterface
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.tekisware.jovaz.mysaferest.R
import com.tekisware.jovaz.mysaferest.fragment.TableListFragment
import com.tekisware.jovaz.mysaferest.model.Table
import com.tekisware.jovaz.mysaferest.model.TableStatus
import kotlinx.android.synthetic.main.table_list_actionsheet_menu.*

class MainActivity : AppCompatActivity(), TableListFragment.DelegatedEventsListener {

    // Privates
    private var tableListFragment: TableListFragment? = null

    // Activity Created Callback
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Adding Toolbar to Main screen
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = "My Safe Rest'"
        setSupportActionBar(toolbar)

        /* check */
        if (findViewById<ViewGroup>(R.id.table_list_fragment) == null)
            return

        /* check */
        tableListFragment = supportFragmentManager.findFragmentById(R.id.table_list_fragment) as? TableListFragment
        if (tableListFragment != null)
            return

        /* set */
        tableListFragment = TableListFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .add(R.id.table_list_fragment, tableListFragment)
                .commit()
    }

    // TableList Fragment EventsListener
    override fun onItemClicked(view: View, index: Int, item: Table) {

        /* check */
        if (item.status == TableStatus.BUSY)
            return

        /* set */
        val sheetDialog = BottomSheetDialog(this)
        val sheetView = getLayoutInflater().inflate(R.layout.table_list_actionsheet_menu, null)
        sheetDialog.setContentView(sheetView)

        /* Table Name */
        val tableNameLabel = sheetView.findViewById(R.id.tableNameLabel) as TextView
        tableNameLabel.text = item.name.toUpperCase()

        /* Customers Count */
        val customersCountTextField = sheetView.findViewById(R.id.customersCountTextField) as TextView
        if (item.status == TableStatus.AVAILABLE)
            customersCountTextField.text = "1"
        else
            customersCountTextField.text = item.orderList!!.getCustomersCount().toString()

        /* set */
        val minusButton = sheetView.findViewById(R.id.minus_button) as ImageButton
        minusButton.setOnClickListener {
            var customersCount = customersCountTextField.text.toString().toInt()

            /* check */
            if (customersCount <= 1)
                return@setOnClickListener

            /* set */
            customersCount--
            customersCountTextField.text = customersCount.toString()

            /* set */
            if (item.status != TableStatus.AVAILABLE)
                item.orderList!!.setCustomersCount(customersCount)
        }
        val addButton = sheetView.findViewById(R.id.add_button) as ImageButton
        addButton.setOnClickListener {
            var customersCount = customersCountTextField.text.toString().toInt()

            /* set */
            customersCount++
            customersCountTextField.text = customersCount.toString()

            /* set */
            if (item.status != TableStatus.AVAILABLE)
                item.orderList!!.setCustomersCount(customersCount)

        }

        /* set */
        val setReservedButton = sheetView.findViewById(R.id.set_reserved_button) as Button
        val setBusyButton = sheetView.findViewById(R.id.set_busy_button) as Button
        val setAvailableButton = sheetView.findViewById(R.id.set_available_button) as Button

        /* set */
        setReservedButton.setOnClickListener {
            val customersCount = customersCountTextField.text.toString().toInt()
            item.setReservedFor(customersCount)
            sheetDialog.dismiss()
        }
        setBusyButton.setOnClickListener {
            val customersCount = customersCountTextField.text.toString().toInt()
            item.setBusyWith(customersCount)
            sheetDialog.dismiss()
        }
        setAvailableButton.setOnClickListener {
            item.setAvailable()
            sheetDialog.dismiss()
        }

        /* set */
        setReservedButton.visibility  = if (item.status == TableStatus.RESERVED) View.GONE else View.VISIBLE
        setAvailableButton.visibility = if (item.status == TableStatus.RESERVED) View.VISIBLE else View.GONE

        /* show */
        sheetDialog.setOnDismissListener(object: DialogInterface.OnDismissListener {
            override fun onDismiss(dialog: DialogInterface?) {
                tableListFragment?.onListItemUpdated(index, item)
                //Snackbar.make(view, item.name, Snackbar.LENGTH_LONG).show()
            }

        })
        sheetDialog.show()
    }
}