package com.tekisware.jovaz.mysaferest.fragment


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager

import com.tekisware.jovaz.mysaferest.R
import com.tekisware.jovaz.mysaferest.model.*
import kotlinx.android.synthetic.main.fragment_order_editor.*
import android.support.v4.view.ViewCompat.animate
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.opengl.ETC1.getWidth
import android.view.*
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.TextView
import com.tekisware.jovaz.mysaferest.activity.TableActivity

/**
 * OrderEditorFragment allows user to select the meals customers
 * order.
 */
class OrderEditorFragment: Fragment() {

    // Delegated Events Listener
    interface DelegatedEventsListener: TableView.OnOrderListChangedListener { }

    // Statics
    companion object {
        val ARG_TABLE = "ARG_TABLE"

        @JvmStatic
        fun newInstance(table: Table): OrderEditorFragment {
            val fragment = OrderEditorFragment().apply {
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

    private var delegatedEventsListener: OrderEditorFragment.DelegatedEventsListener? = null

    // Fragment Created Callback
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
    }

    // Fragment View Stuff
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val orderEditorView = inflater.inflate(R.layout.fragment_order_editor, container, false)

        // Setting ViewPager for each Tabs
        val viewPager = orderEditorView.findViewById(R.id.viewpager) as ViewPager
        val tabLayout = activity?.findViewById(R.id.tab_layout) as TabLayout
        tabLayout.setupWithViewPager(viewPager)

        /* setup */
        viewPager.adapter = object: FragmentPagerAdapter(activity?.supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return(MealListFragment.newInstance(table, DataManager.mealCategoryList[position]))
            }
            override fun getCount(): Int = DataManager.mealCategoryList.size
            override fun getPageTitle(position: Int): CharSequence {
                return(DataManager.mealCategoryList[position])
            }
        }

        /* Show Tabs */
        tabLayout.visibility = View.VISIBLE

        /* done */
        return(orderEditorView)
    }
    override fun onDestroyView() { super.onDestroyView()

        /* release */
        val fragmentManagerTx = activity!!.supportFragmentManager.beginTransaction()
        activity?.supportFragmentManager?.fragments?.forEach {
            if (it is MealListFragment)
                fragmentManagerTx.remove(it)
        }
        fragmentManagerTx.commit()

        /* Hide Tabs */
        val tabLayout = activity?.findViewById(R.id.tab_layout) as TabLayout
        tabLayout.visibility = View.GONE
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
