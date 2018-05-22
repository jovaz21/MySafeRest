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
import com.tekisware.jovaz.mysaferest.model.OrderList
import com.tekisware.jovaz.mysaferest.model.Table
import kotlinx.android.synthetic.main.fragment_meal_list.*

/**
 * MealListFragment is a list of Meals within 1 selected Category through which
 * user can easily manager orders
 */
class MealListFragment: Fragment() {

    // Delegated Events Listener
    interface DelegatedEventsListener {
        fun onOrderListUpdated(orderList: OrderList)
    }

    // Statics
    companion object {
        val ARG_CATEGORY = "ARG_CATEGORY"

        @JvmStatic
        fun newInstance(categoryName: String): MealListFragment {
            val fragment = MealListFragment().apply {
                arguments = Bundle()
                arguments?.putSerializable(ARG_CATEGORY, categoryName)
            }
            return(fragment)
        }
    }

    // Privates
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
        meal_category.text = this.categoryName
    }

    // Fragment is Started
    override fun onStart() { super.onStart()
        meal_category.text = this.categoryName
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
