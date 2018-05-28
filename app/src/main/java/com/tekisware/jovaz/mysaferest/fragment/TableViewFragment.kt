package com.tekisware.jovaz.mysaferest.fragment


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView

import com.tekisware.jovaz.mysaferest.R
import com.tekisware.jovaz.mysaferest.model.*
import kotlinx.android.synthetic.main.fragment_table_view.*

/**
 * TableView.OnOrderListChangedListener
 */
class TableView {
    interface OnOrderListChangedListener {
        fun onOrderListChanged(tableId: Int, orderList: OrderList)
    }
}

/**
 * TableViewFragment is the main view screen from which user can handle all the Orders Stuff
 * of a Table.
 */
class TableViewFragment : Fragment() {

    // Delegated Events Listener
    interface DelegatedEventsListener: TableView.OnOrderListChangedListener {
        fun onOrderClicked(view: View, index: Int, order: Order)
        fun onAddOrderClicked()
        fun onCloseTable(tableId: Int)
    }

    // Statics
    companion object {
        val ARG_TABLE = "ARG_TABLE"
        val ARG_SPLIT = "ARG_SPLIT"

        @JvmStatic
        fun newInstance(table: Table, splitMode: Boolean): TableViewFragment {
            val fragment = TableViewFragment().apply {
                arguments = Bundle()
                arguments?.putSerializable(ARG_TABLE, table)
                arguments?.putSerializable(ARG_SPLIT, splitMode)
            }
            return(fragment)
        }
    }

    // Privates
    private var table: Table? = null
    private var splitMode: Boolean? = false

    private var delegatedEventsListener: DelegatedEventsListener? = null

    // Fragment Created Callback
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
        val argTable = arguments?.getSerializable(ARG_TABLE)
        val argSplit = arguments?.getBoolean(ARG_SPLIT)

        /* check */
        if (argTable != null)
            table = argTable as Table
        if (argSplit != null)
            splitMode = argSplit

        /* */
        arguments?.clear()
    }

    // Fragment View Stuff
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val tableView = inflater.inflate(R.layout.fragment_table_view, container, false)

        /* done */
        return(tableView)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)

        /* update */
        updateView()

        /* set */
        val minusButton = view.findViewById(R.id.minus_button) as ImageButton
        minusButton.setOnClickListener {
            var customersCount = customersCountTextField.text.toString().toInt()

            /* check */
            if ((table?.status != TableStatus.BUSY) || (table?.orderList == null) || (customersCount <= 1))
                return@setOnClickListener

            /* set */
            customersCount--
            customersCountTextField.text = customersCount.toString()

            /* set */
            if (table!!.status != TableStatus.AVAILABLE) {
                table!!.orderList!!.setCustomersCount(customersCount)
                delegatedEventsListener?.onOrderListChanged(table!!.id, table!!.orderList!!)
            }
        }
        val addButton = view.findViewById(R.id.add_button) as ImageButton
        addButton.setOnClickListener {
            var customersCount = customersCountTextField.text.toString().toInt()

            /* check */
            if ((table?.status != TableStatus.BUSY) || (table?.orderList == null))
                return@setOnClickListener

            /* set */
            customersCount++
            customersCountTextField.text = customersCount.toString()

            /* set */
            if (table!!.status != TableStatus.AVAILABLE) {
                table!!.orderList!!.setCustomersCount(customersCount)
                delegatedEventsListener?.onOrderListChanged(table!!.id, table!!.orderList!!)
            }
        }

        /* set */
        order_list_view.setOnItemClickListener { adapter: AdapterView<*>, thisView: View, index: Int, _: Long ->
            delegatedEventsListener?.onOrderClicked(thisView, index, adapter.getItemAtPosition(index) as Order)
        }

        /* Add Button */
        val addOrderButton = view.findViewById(R.id.add_order_button) as FloatingActionButton
        addOrderButton.setOnClickListener {
            delegatedEventsListener?.onAddOrderClicked()
        }
    }
    fun updateView() {

        /* check */
        if (view == null)
            return

        /* set */
        if (table != null)
            (this.activity as AppCompatActivity).supportActionBar?.title = table?.name?.toUpperCase()
        setHasOptionsMenu(table?.orderList != null)

        /* Customers Count */
        val customersCountTextField = view?.findViewById(R.id.customersCountTextField) as TextView
        if (table?.status == TableStatus.BUSY)
            customersCountTextField.text = table?.orderList!!.getCustomersCount().toString()
        else
            customersCountTextField.text = "0"

        /* Total Amount */
        this.updateTotalAmount()

        /* set */
        order_list_view.adapter = TableViewFragment.OrderListItemAdapter(activity!!, this, table?.orderList)

        /* Add Button */
        val addOrderButton = view?.findViewById(R.id.add_order_button) as FloatingActionButton
        addOrderButton.visibility = when(table?.status) {
            TableStatus.BUSY -> View.VISIBLE
            else -> View.INVISIBLE
        }
    }
    private fun updateTotalAmount() {
        val totalAmountLabel = view?.findViewById(R.id.totalAmountLabel) as TextView

        /* check */
        var totalAmount = 0f
        if (table?.orderList != null) {
            totalAmount = table!!.orderList!!.totalAmount
        }

        /* set */
        totalAmountLabel.text = context?.getString(R.string.table_view_header_totalAmount_format, totalAmount)
    }
    private class OrderListItemAdapter(context: Context, fragment: TableViewFragment, orderList: OrderList?): BaseAdapter() {
        private val context: Context
        private val fragment: TableViewFragment
        private val orderList: OrderList?

        // Setup
        init {
            this.context    = context
            this.fragment   = fragment
            this.orderList  = orderList
        }

        // Adapter Stuff
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(context)

            /* check */
            val order = orderList?.getAt(position)!!
            val name = order.meal.name.toUpperCase()
            val count = order.getCount()

            /* set */
            val listItem = layoutInflater.inflate(R.layout.order_list_item, parent, false)
            listItem.findViewById<TextView>(R.id.item_name).text = name

            /* Customers Count */
            val countTextField = listItem.findViewById(R.id.item_count) as TextView
            countTextField.text = count.toString()

            /* set */
            val minusButton = listItem.findViewById(R.id.item_minus_button) as ImageButton
            minusButton.setOnClickListener {
                var curCount = countTextField.text.toString().toInt()

                /* check */
                if (curCount <= 0)
                    return@setOnClickListener

                /* set */
                curCount--
                countTextField.text = curCount.toString()
                if (Build.VERSION.SDK_INT >= 23) {
                    countTextField.setTextColor(
                            when(curCount) {
                                0       -> context.getColor(R.color.app_grey_300)
                                else    -> context.getColor(R.color.colorAccent)
                            }
                    )
                }

                /* check */
                if (curCount <= 0) {

                    /* remove */
                    orderList.removeAt(orderList.indexOf(order))
                    fragment.onListItemUpdated(order)

                    /* done */
                    return@setOnClickListener
                }

                /* set */
                order.setCount(curCount)
                fragment.onListItemUpdated(order)
            }
            val addButton = listItem.findViewById(R.id.item_add_button) as ImageButton
            addButton.setOnClickListener {
                var curCount = countTextField.text.toString().toInt()

                /* set */
                curCount++
                countTextField.text = curCount.toString()
                if (Build.VERSION.SDK_INT >= 23) {
                    countTextField.setTextColor(
                            when(curCount) {
                                0       -> context.getColor(R.color.app_grey_300)
                                else    -> context.getColor(R.color.colorAccent)
                            }
                    )
                }

                /* set */
                order.setCount(curCount)
                fragment.onListItemUpdated(order)
            }

            /* done */
            return(listItem)
        }
        override fun getItem(position: Int): Any {
            return(orderList?.getAt(position)!!)
        }
        override fun getItemId(position: Int): Long {
            return(orderList?.getAt(position)!!.meal.id.toLong())
        }
        override fun getCount(): Int {
            if (orderList == null)
                return(0)
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
        table = null
        splitMode = false
    }

    // Fragment Options Menu Stuff
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) { super.onCreateOptionsMenu(menu, inflater)
        if (table?.orderList != null)
            inflater?.inflate(R.menu.menu_order_editor, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        //if (splitMode && (table?.orderList == null))
        //    menu?.clear()
        super.onPrepareOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.closeTable_menu_item -> {

                /* confirm */
                val builder = AlertDialog.Builder(activity)
                builder.setTitle(context?.getString(R.string.table_view_closeTable_confirm_title))
                builder.setMessage(context?.getString(R.string.table_view_closeTable_confirm_message))
                builder.setPositiveButton(context?.getString(R.string.table_view_closeTable_confirm_yes)) { dialog, which ->
                    delegatedEventsListener?.onCloseTable(table!!.id)
                    if (!splitMode!!)
                        activity?.finish()
                }
                builder.setNegativeButton(context?.getString(R.string.table_view_closeTable_confirm_no)) { dialog, which ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()

                /* done */
                return(true)
            }
        }
        return(super.onOptionsItemSelected(item))
    }

    // Bind Table
    fun bindTable(table: Table, splitMode: Boolean) {

        /* set */
        this.table      = table
        this.splitMode  = splitMode

        /* update */
        updateView()
    }

    // Notify OrderList Changed
    fun notifyOrderListChanged() {
        updateTotalAmount()
        delegatedEventsListener?.onOrderListChanged(table!!.id, table!!.orderList!!)
    }

    // On ListItem Updated
    fun onListItemUpdated(item: Order) {
        (order_list_view.adapter as TableViewFragment.OrderListItemAdapter).notifyItemChanged(item)
        notifyOrderListChanged()
    }
}
