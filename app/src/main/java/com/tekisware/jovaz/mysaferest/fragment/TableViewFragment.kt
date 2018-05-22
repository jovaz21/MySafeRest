package com.tekisware.jovaz.mysaferest.fragment


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView

import com.tekisware.jovaz.mysaferest.R
import com.tekisware.jovaz.mysaferest.model.*
import kotlinx.android.synthetic.main.fragment_table_view.*

/**
 * TableViewFragment is the main view screen from which user can handle all the Orders Stuff
 * of a Table.
 */
class TableViewFragment : Fragment() {

    // Delegated Events Listener
    interface DelegatedEventsListener {
        fun onTableOrderListUpdated(tableId: Int, orderList: OrderList)
        fun onItemClicked(view: View, index: Int, item: Order)
        fun onAddOrderClicked()
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

        /* set */
        order_list_view.adapter = TableViewFragment.OrderListItemAdapter(activity!!, table.orderList!!)

        /* set */
        order_list_view.setOnItemClickListener { adapter: AdapterView<*>, thisView: View, index: Int, _: Long ->
            delegatedEventsListener?.onItemClicked(thisView, index, adapter.getItemAtPosition(index) as Order)
        }

        /* Add Button */
        val addOrderButton = view.findViewById(R.id.add_order_button) as FloatingActionButton
        addOrderButton.setOnClickListener {
            delegatedEventsListener?.onAddOrderClicked()
        }
    }
    private class OrderListItemAdapter(context: Context, orderList: OrderList): BaseAdapter() {
        private val context: Context
        private val orderList: OrderList

        // Setup
        init {
            this.context    = context
            this.orderList  = orderList
        }

        // Adapter Stuff
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(context)

            /* check */
            val order = orderList.getAt(position)
            val name = order?.meal?.name?.toUpperCase()
            val count = order?.getCount()

            /* set */
            val listItem = layoutInflater.inflate(R.layout.order_list_item, parent, false)
            listItem.findViewById<TextView>(R.id.item_name).text = name
            listItem.findViewById<TextView>(R.id.orderLineCountTextField).text = count.toString()

            /* done */
            return(listItem)
        }
        override fun getItem(position: Int): Any {
            return(orderList.getAt(position)!!)
        }
        override fun getItemId(position: Int): Long {
            return(orderList.getAt(position)!!.meal?.id.toLong())
        }
        override fun getCount(): Int {
            return(orderList.count)
        }

        // Update Item
        fun notifyItemChanged(item: Order) {
            context.run {
                notifyDataSetChanged()
            }
        }
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

    // On ListItem Updated
    fun onListItemUpdated(item: Order) {
        (order_list_view.adapter as TableViewFragment.OrderListItemAdapter).notifyItemChanged(item)
    }
}
