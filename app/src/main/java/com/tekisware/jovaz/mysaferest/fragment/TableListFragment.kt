package com.tekisware.jovaz.mysaferest.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.tekisware.jovaz.mysaferest.R
import com.tekisware.jovaz.mysaferest.model.DataManager
import com.tekisware.jovaz.mysaferest.model.Table
import com.tekisware.jovaz.mysaferest.model.TableStatus
import kotlinx.android.synthetic.main.fragment_table_list.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TableListFragment.DelegatedEventsListener] interface
 * to handle events.
 * Use the [TableListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TableListFragment : Fragment() {

    // Delegated Events Listener
    interface DelegatedEventsListener {
        fun onItemClicked(view: View, index: Int, item: Table)
    }

    // Statics
    companion object {
        @JvmStatic
        fun newInstance() = TableListFragment()
    }

    // Privates
    private var delegatedEventsListener: DelegatedEventsListener? = null

    // Fragment Created Callback
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
    }

    // Fragment View Stuff
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val tableListView = inflater.inflate(R.layout.fragment_table_list, container, false)

        /* done */
        return(tableListView)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)

        /* set */
        table_list_view.adapter = TableListItemAdapter(activity!!, DataManager.tableList)

        /* set */
        table_list_view.setOnItemClickListener { adapter: AdapterView<*>, thisView: View, index: Int, _: Long ->
            delegatedEventsListener?.onItemClicked(thisView, index, adapter.getItemAtPosition(index) as Table)
        }
    }
    private class TableListItemAdapter(context: Context, tableList: List<Table>): BaseAdapter() {
        private val context: Context
        private val tableList: List<Table>

        // Setup
        init {
            this.context    = context
            this.tableList  = tableList
        }

        // Adapter Stuff
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(context)

            /* check */
            val table = tableList.get(position)
            val customersCount = table.orderList?.getCustomersCount()
            val totalAmount = table.orderList?.totalAmount
            val tableStatusDesc = when(table.status) {
                TableStatus.AVAILABLE -> "-"
                TableStatus.RESERVED -> if (customersCount!! > 1) context.getString(R.string.table_reserved_many_desc_format, customersCount) else context.getString(R.string.table_reserved_one_desc_format, customersCount)
                TableStatus.BUSY -> if (customersCount!! > 1) context.getString(R.string.table_busy_many_desc_format, customersCount, totalAmount) else context.getString(R.string.table_busy_one_desc_format, customersCount, totalAmount)
            }

            /* set */
            val listItem = layoutInflater.inflate(R.layout.table_list_item, parent, false)
            listItem.findViewById<TextView>(R.id.list_title).text = table.name.toUpperCase()
            listItem.findViewById<TextView>(R.id.list_desc).text = tableStatusDesc

            /* done */
            return(listItem)
        }
        override fun getItem(position: Int): Any {
            return(tableList.get(position))
        }
        override fun getItemId(position: Int): Long {
            return(tableList.get(position).id.toLong())
        }
        override fun getCount(): Int {
            return(tableList.size)
        }

        // Update Item
        fun notifyItemChanged(item: Table) {
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
        if (context is DelegatedEventsListener)
            this.delegatedEventsListener = context

        /* done */
        return
    }
    override fun onDetach() { super.onDetach()
        delegatedEventsListener = null
    }

    // On ListItem Updated
    fun onListItemUpdated(item: Table) {
        (table_list_view.adapter as TableListItemAdapter).notifyItemChanged(item)
    }
}
