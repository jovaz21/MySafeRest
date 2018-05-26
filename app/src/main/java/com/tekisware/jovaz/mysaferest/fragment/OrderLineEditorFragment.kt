package com.tekisware.jovaz.mysaferest.fragment


import android.app.Activity
import android.content.Context
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

import com.tekisware.jovaz.mysaferest.R
import com.tekisware.jovaz.mysaferest.model.Meal
import com.tekisware.jovaz.mysaferest.model.Order
import com.tekisware.jovaz.mysaferest.model.Table
import kotlinx.android.synthetic.main.fragment_order_line_editor.*

/**
 * OrderLineEditorFragment allows user to fulfill an order
 * order.
 */
class OrderLineEditorFragment: Fragment() {

    // Delegated Events Listener
    interface DelegatedEventsListener: TableView.OnOrderListChangedListener { }

    // Statics
    companion object {
        val ARG_TABLE   = "ARG_TABLE"
        val ARG_MEAL    = "ARG_MEAL"
        val ARG_ORDER   = "ARG_ORDER"

        @JvmStatic
        fun newInstance(table: Table, meal: Meal, order: Order?): OrderLineEditorFragment {
            val fragment = OrderLineEditorFragment().apply {
                arguments = Bundle()
                arguments?.putSerializable(ARG_TABLE, table)
                arguments?.putSerializable(ARG_MEAL, meal)
                arguments?.putSerializable(ARG_ORDER, order)
            }
            return(fragment)
        }
    }

    // Privates
    private val table by lazy<Table> {
        arguments?.getSerializable(ARG_TABLE) as Table
    }
    private val meal by lazy<Meal> {
        arguments?.getSerializable(ARG_MEAL) as Meal
    }
    private var order: Order? = null

    private var delegatedEventsListener: OrderLineEditorFragment.DelegatedEventsListener? = null

    // Fragment Created Callback
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
        order = arguments?.getSerializable(ARG_ORDER) as Order?
    }

    // Fragment View Stuff
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val orderLineEditorView = inflater.inflate(R.layout.fragment_order_line_editor, container, false)

        /* set */
        setHasOptionsMenu(true)

        /* done */
        return(orderLineEditorView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)
        val mealPhoto   = view.findViewById(R.id.meal_photo) as ImageView
        val mealName    = view.findViewById(R.id.meal_name) as TextView
        val mealPrice   = view.findViewById(R.id.meal_price) as TextView
        val mealDesc    = view.findViewById(R.id.meal_desc) as TextView
        val itemCount   = view.findViewById(R.id.item_count) as TextView
        val count       = if (order != null) order!!.getCount() else 0
        val itemNotes   = view.findViewById(R.id.item_notes) as EditText
        val notes       = if (order != null) order!!.getNotes() else ""

        /* set */
        mealPhoto.setImageResource(meal.photo)
        mealName.text = meal.name
        mealPrice.text = context!!.getString(R.string.price_format, meal.price)
        mealDesc.text = meal.description

        /* Count */
        itemCount.text = count.toString()
        if (Build.VERSION.SDK_INT >= 23) {
            itemCount.setTextColor(
                    when(count) {
                        0       -> context!!.getColor(R.color.app_grey_300)
                        else    -> context!!.getColor(R.color.colorAccent)
                    }
            )
        }

        /* Item Notes */
        itemNotes.setText(notes, TextView.BufferType.EDITABLE)
        itemNotes.setEnabled(count > 0)

        /* Allergens */
        val allergenViews = arrayOf(
                view.findViewById(R.id.allergen_icon_0) as ImageView,
                view.findViewById(R.id.allergen_icon_1) as ImageView,
                view.findViewById(R.id.allergen_icon_2) as ImageView,
                view.findViewById(R.id.allergen_icon_3) as ImageView
        )
        allergenViews.forEach { it.visibility = View.GONE }

        val allergenIcons = meal.getAllergenIcons()
        var curIconViewIx = 0
        allergenIcons.forEach {
            allergenViews[curIconViewIx].setImageResource(it)
            allergenViews[curIconViewIx].visibility = View.VISIBLE
            curIconViewIx++
        }

        /* set */
        val minusButton = view.findViewById(R.id.item_minus_button) as ImageButton
        minusButton.setOnClickListener {
            var curCount = itemCount.text.toString().toInt()

            /* check */
            if (curCount <= 0)
                return@setOnClickListener

            /* set */
            curCount--
            itemCount.text = curCount.toString()
            if (Build.VERSION.SDK_INT >= 23) {
                itemCount.setTextColor(
                        when(curCount) {
                            0       -> context!!.getColor(R.color.app_grey_300)
                            else    -> context!!.getColor(R.color.colorAccent)
                        }
                )
            }

            /* set */
            itemNotes.setEnabled(curCount > 0)
        }
        val addButton = view.findViewById(R.id.item_add_button) as ImageButton
        addButton.setOnClickListener {
            var curCount = itemCount.text.toString().toInt()

            /* set */
            curCount++
            itemCount.text = curCount.toString()
            if (Build.VERSION.SDK_INT >= 23) {
                itemCount.setTextColor(
                        when(curCount) {
                            0       -> context!!.getColor(R.color.app_grey_300)
                            else    -> context!!.getColor(R.color.colorAccent)
                        }
                )
            }

            /* set */
            itemNotes.setEnabled(curCount > 0)
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
        if (context is OrderLineEditorFragment.DelegatedEventsListener)
            this.delegatedEventsListener = context

        /* done */
        return
    }
    override fun onDetach() { super.onDetach()
        delegatedEventsListener = null
    }

    // Fragment Options Menu Stuff
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) { super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_order_line_editor, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.ok_menu_item -> {
                val itemCount   = view!!.findViewById(R.id.item_count) as TextView
                val count       = itemCount.text.toString().toInt()
                val itemNotes   = view!!.findViewById(R.id.item_notes) as EditText
                val notes       = itemNotes.text.toString()

                /* check */
                if (count == 0) {

                    /* check */
                    if (order != null) {
                        table.orderList!!.removeAt(table.orderList!!.indexOf(order!!))
                        delegatedEventsListener?.onOrderListChanged(table.id, table.orderList!!)
                    }

                    /* done */
                    activity!!.supportFragmentManager.popBackStack()
                    return(true)
                }

                /* check */
                if (order == null) {
                    order = Order(meal, count, notes)
                    table.orderList!!.add(order!!)
                }

                /* set */
                order?.setCount(count)
                order?.setNotes(notes)
                delegatedEventsListener?.onOrderListChanged(table.id, table.orderList!!)

                /* done */
                activity!!.supportFragmentManager.popBackStack()
                return(true)
            }
        }
        return(super.onOptionsItemSelected(item))
    }
}
