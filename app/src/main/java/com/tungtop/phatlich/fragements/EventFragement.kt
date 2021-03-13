package com.tungtop.phatlich.fragements

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.tungtop.phatlich.EventsAdapter
import com.tungtop.phatlich.R
import com.tungtop.phatlich.models.BuddhismEvents
import com.tungtop.phatlich.models.EventTransactionObject
import com.tungtop.phatlich.models.EventType
import java.util.*

class EventFragement : Fragment() {
    private var cacheView: View? = null
    private var listEventBuddhism: ArrayList<BuddhismEvents>? = null

    private var listView: ListView? = null
    private var adapter: EventsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (cacheView == null) {
            cacheView = inflater.inflate(R.layout.fragement_event, container, false)
        }

        listView = cacheView!!.findViewById<ListView>(R.id.list_event)
        adapter = EventsAdapter(context!!, R.layout.listview_item_event)

        handleView()
        return cacheView
    }

    private fun handleView() {


        listEventBuddhism = BuddhismEvents.getDataFromFile("events.json", context!!)

        for (i in 0..(listEventBuddhism!!.size - 1)) {
            adapter!!.addItem(
                EventTransactionObject(
                    EventType.HEADER,
                    listEventBuddhism!![i].month,
                    "",
                    "",
                    ""
                )
            )
            for (j in 0..(listEventBuddhism!![i].events.size - 1)) {
                adapter!!.addItem(
                    EventTransactionObject(
                        EventType.ITEM,
                        listEventBuddhism!![i].events[j].name,
                        listEventBuddhism!![i].events[j].day,
                        listEventBuddhism!![i].events[j].month,
                        listEventBuddhism!![i].events[j].description
                    )
                )
            }
        }

        listView!!.adapter = adapter
    }
}