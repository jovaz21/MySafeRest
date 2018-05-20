package com.tekisware.jovaz.mysaferest.fragment


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.TextView

import com.tekisware.jovaz.mysaferest.R
import com.tekisware.jovaz.mysaferest.activity.TableActivity
import com.tekisware.jovaz.mysaferest.model.*
import kotlinx.android.synthetic.main.fragment_table_list.*

/**
 * TableViewFragment is the main view screen from which user can handle all the Orders Stuff
 * of a Table.
 */
class TableViewFragment : Fragment() {

    // Delegated Events Listener
    interface DelegatedEventsListener {
        fun onTableOrderListUpdated(tableId: Int, orderList: OrderList)
    }

    // Statics
    companion object {
        val ARG_TABLE = "ARG_TABLE"

        @JvmStatic
        fun newInstance(table: Table): TableViewFragment {
            val fragment = TableViewFragment().apply {
                arguments = Bundle()
                arguments?.putSerializable(ARG_TABLE, table)
            }
            return(fragment)
        }
    }

    // Privates
    private val table by lazy<Table> {
        arguments?.getSerializable(ARG_TABLE) as Table
    }

    private var delegatedEventsListener: DelegatedEventsListener? = null

    // Fragment Created Callback
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
    }

    // Fragment View Stuff
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val tableView = inflater.inflate(R.layout.fragment_table_view, container, false)
        return(tableView)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)

        /* Customers Count */
        val customersCountTextField = view.findViewById(R.id.customersCountTextField) as TextView
        customersCountTextField.text = table.orderList!!.getCustomersCount().toString()

        /* set */
        val minusButton = view.findViewById(R.id.minus_button) as ImageButton
        minusButton.setOnClickListener {
            var customersCount = customersCountTextField.text.toString().toInt()

            /* check */
            if (customersCount <= 1)
                return@setOnClickListener

            /* set */
            customersCount--
            customersCountTextField.text = customersCount.toString()

            /* set */
            if (table.status != TableStatus.AVAILABLE) {
                table.orderList!!.setCustomersCount(customersCount)
                delegatedEventsListener?.onTableOrderListUpdated(table.id, table.orderList!!)
            }
        }
        val addButton = view.findViewById(R.id.add_button) as ImageButton
        addButton.setOnClickListener {
            var customersCount = customersCountTextField.text.toString().toInt()

            /* set */
            customersCount++
            customersCountTextField.text = customersCount.toString()

            /* set */
            if (table.status != TableStatus.AVAILABLE) {
                table.orderList!!.setCustomersCount(customersCount)
                delegatedEventsListener?.onTableOrderListUpdated(table.id, table.orderList!!)
            }
        }

        /* Total Amount */
        val totalAmountLabel = view.findViewById(R.id.totalAmountLabel) as TextView
        val totalAmount = table.orderList!!.totalAmount
        totalAmountLabel.text = context?.getString(R.string.table_view_header_totalAmount_format, totalAmount)
    }

    // Fragment Attached/Detached Callback
    override fun onAttach(activity: Activity?) { super.onAttach(activity)
        onFragmentAttached(activity as Context)
    }
    override fun onAttach(context: Context) { super.onAttach(context)
        onFragmentAttached(context)
    }
    fun onFragmentAttached(context: Context) {

        /* check */
        if (context is TableViewFragment.DelegatedEventsListener)
            this.delegatedEventsListener = context

        /* done */
        return
    }
    override fun onDetach() { super.onDetach()
        delegatedEventsListener = null
    }
}
