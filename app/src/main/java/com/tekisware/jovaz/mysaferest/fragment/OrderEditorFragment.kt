package com.tekisware.jovaz.mysaferest.fragment


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tekisware.jovaz.mysaferest.R
import com.tekisware.jovaz.mysaferest.model.Meal
import com.tekisware.jovaz.mysaferest.model.Order
import com.tekisware.jovaz.mysaferest.model.OrderList
import com.tekisware.jovaz.mysaferest.model.Table

/**
 * OrderEditorFragment allows user to select the meals customers
 * order.
 */
class OrderEditorFragment : Fragment() {

    // Delegated Events Listener
    interface DelegatedEventsListener {
        fun onTableOrderListUpdated(tableId: Int, orderList: OrderList)
        fun onItemClicked(view: View, index: Int, item: Meal)
    }

    // Statics
    companion object {
        val ARG_ORDERLIST = "ARG_ORDERLIST"

        @JvmStatic
        fun newInstance(orderList: OrderList): OrderEditorFragment {
            val fragment = OrderEditorFragment().apply {
                arguments = Bundle()
                arguments?.putSerializable(ARG_ORDERLIST, orderList)
            }
            return(fragment)
        }
    }

    // Privates
    private val orderList by lazy<OrderList> {
        arguments?.getSerializable(ARG_ORDERLIST) as OrderList
    }

    private var delegatedEventsListener: OrderEditorFragment.DelegatedEventsListener? = null

    // Fragment Created Callback
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
    }

    // Fragment View Stuff
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val orderEditorView = inflater.inflate(R.layout.fragment_order_editor, container, false)
        return(orderEditorView)
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
        if (context is OrderEditorFragment.DelegatedEventsListener)
            this.delegatedEventsListener = context

        /* done */
        return
    }
    override fun onDetach() { super.onDetach()
        delegatedEventsListener = null
    }
}
