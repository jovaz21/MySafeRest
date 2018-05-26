package com.tekisware.jovaz.mysaferest.fragment


import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.tekisware.jovaz.mysaferest.R
import com.tekisware.jovaz.mysaferest.model.*
import kotlinx.android.synthetic.main.fragment_meal_list.*

/**
 * MealListFragment is a list of Meals within 1 selected Category through which
 * user can easily manager orders
 */
class MealListFragment: Fragment() {

    // Delegated Events Listener
    interface DelegatedEventsListener: TableView.OnOrderListChangedListener {
        fun onMealOrderClicked(view: View, index: Int, meal: Meal, order: Order?)
    }

    // Statics
    companion object {
        val ARG_TABLE       = "ARG_TABLE"
        val ARG_CATEGORY    = "ARG_CATEGORY"

        @JvmStatic
        fun newInstance(table: Table, categoryName: String): MealListFragment {
            val fragment = MealListFragment().apply {
                arguments = Bundle()
                arguments?.putSerializable(ARG_TABLE, table)
                arguments?.putSerializable(ARG_CATEGORY, categoryName)
            }
            return(fragment)
        }
    }

    // Privates
    private val table by lazy<Table> {
        arguments?.getSerializable(OrderEditorFragment.ARG_TABLE) as Table
    }
    private val categoryName by lazy<String> {
        arguments?.getString(MealListFragment.ARG_CATEGORY)!!
    }

    // Privates
    private var delegatedEventsListener: DelegatedEventsListener? = null

    // Fragment Created Callback
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
    }

    // Fragment View Stuff
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meal_list, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)

        /* set */
        meal_list_view.adapter = MealListFragment.MealListItemAdapter(activity!!, table, delegatedEventsListener, DataManager.filterMealListByCategory(categoryName), table.orderList!!)

        /* set */
        meal_list_view.setOnItemClickListener { adapter: AdapterView<*>, thisView: View, index: Int, _: Long ->
            val meal = adapter.getItemAtPosition(index) as Meal
            delegatedEventsListener?.onMealOrderClicked(thisView, index, meal, table.orderList!!.findByMeal(meal.id))
        }
    }
    private class MealListItemAdapter(context: Context, table: Table, delegatedEventsListener: DelegatedEventsListener?, mealList: List<Meal>, orderList: OrderList): BaseAdapter() {
        private val context:                    Context
        private var table:                      Table? = null
        private var delegatedEventsListener:    DelegatedEventsListener? = null
        private var mealList:                   List<Meal> = listOf()
        private val orderList:                  OrderList

        // Setup
        init {
            this.context                    = context
            this.table                      = table
            this.delegatedEventsListener    = delegatedEventsListener
            this.mealList                   = mealList
            this.orderList                  = orderList
        }

        override fun isEnabled(position: Int): Boolean {
            return true
        }

        // Adapter Stuff
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(context)

            /* set */
            val meal = mealList.get(position)
            var order = orderList.findByMeal(meal.id)

            /* set */
            val name = meal.name.toUpperCase()
            val price = context.getString(R.string.price_format, meal.price)
            val count = if (order != null) order.getCount() else 0

            /* set */
            val listItem = layoutInflater.inflate(R.layout.meal_list_item, parent, false)
            listItem.findViewById<ImageView>(R.id.item_photo).setImageResource(meal.photo)
            listItem.findViewById<TextView>(R.id.item_name).text = name
            listItem.findViewById<TextView>(R.id.item_price).text = price

            /* Items Count */
            val countTextField = listItem.findViewById(R.id.item_count) as TextView
            countTextField.text = count.toString()
            if (Build.VERSION.SDK_INT >= 23) {
                countTextField.setTextColor(
                        when(count) {
                            0       -> context.getColor(R.color.app_grey_300)
                            else    -> context.getColor(R.color.colorAccent)
                        }
                )
            }

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

                    /* check */
                    if (order != null) {
                        orderList.removeAt(orderList.indexOf(order!!))
                        delegatedEventsListener?.onOrderListChanged(table!!.id, table!!.orderList!!)
                    }

                    /* done */
                    return@setOnClickListener
                }

                /* check */
                if (order == null) {
                    order = Order(meal, curCount, "")
                    orderList.add(order!!)
                }

                /* set */
                order?.setCount(curCount)
                delegatedEventsListener?.onOrderListChanged(table!!.id, table!!.orderList!!)
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

                /* check */
                if (order == null) {
                    order = Order(meal, curCount, "")
                    orderList.add(order!!)
                }

                /* set */
                order?.setCount(curCount)
                delegatedEventsListener?.onOrderListChanged(table!!.id, table!!.orderList!!)
            }

            /* done */
            return(listItem)
        }
        override fun getItem(position: Int): Any {
            return(mealList.get(position))
        }
        override fun getItemId(position: Int): Long {
            return(mealList.get(position).id.toLong())
        }
        override fun getCount(): Int {
            return(mealList.size)
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
        if (context is MealListFragment.DelegatedEventsListener)
            this.delegatedEventsListener = context

        /* done */
        return
    }
    override fun onDetach() { super.onDetach()
        delegatedEventsListener = null
    }
}
