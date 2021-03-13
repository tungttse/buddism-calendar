package com.tungtop.phatlich.fragements

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ListView
import android.widget.TextView
import com.tungtop.phatlich.CalendarAdapter
import com.tungtop.phatlich.EventsAdapter
import com.tungtop.phatlich.libraries.OnSwipeTouchListener
import com.tungtop.phatlich.models.BuddhismEvents
import com.tungtop.phatlich.models.EventTransactionObject
import com.tungtop.phatlich.models.EventType
import java.util.*

/**
 * Lấy mốc số 544 cộng với năm dương lịch hiện tại. (Ví dụ: năm 2013 + 544 = 2557. Năm 2013 thì Phật lịch sẽ là 2557).
 * Và do Phật lịch được bắt đầu tính từ năm Phật nhập niết bàn (tức năm 544 trước TL),
 * nên thời điểm đánh dấu Phật lịch mới của một năm là vào ngày Vía Phật Thích Ca Nhập Diệt – 15 tháng 2 âm lịch.
 */

class CalendarFragement : Fragment() {
    private var cacheView: View? = null
    private var TAG: String = "tungtop"
    private var calendarGridView: GridView? = null

    private var adapter: CalendarAdapter? = null
    private var month: Calendar? = null

    private var swipNextMonthCount = 0
    private var dynamicCal: GregorianCalendar? = null

    private var monthText: TextView? = null
    private var yearText: TextView? = null

    private var listEventBuddhism: ArrayList<BuddhismEvents>? = null

    private var adapterEvent: EventsAdapter? = null

    private var eventsElement: ListView? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        (activity as MainNavigationActivity).setTitleText("医療相談")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (cacheView == null) {
            cacheView =
                inflater.inflate(com.tungtop.phatlich.R.layout.fragement_calendar, container, false)
        }

        eventsElement =
            cacheView!!.findViewById<ListView>(com.tungtop.phatlich.R.id.list_view_events)

        handleView(cacheView!!)

        return cacheView
    }

    private fun handleView(cacheView: View) {
        calendarGridView = cacheView!!.findViewById(com.tungtop.phatlich.R.id.calendar) as GridView
        month = GregorianCalendar.getInstance(Locale.getDefault()) as GregorianCalendar
        dynamicCal = GregorianCalendar.getInstance(Locale.getDefault()) as GregorianCalendar

        listEventBuddhism = BuddhismEvents.getDataFromFile("events.json", context!!)

        updateMonthYearInfo()

        adapter = CalendarAdapter(activity!!.applicationContext, month!!, listEventBuddhism!!)
        adapter!!.notifyDataSetChanged()
        calendarGridView!!.setAdapter(adapter)

        setDynamicHeightGridView(calendarGridView!!, 7)

        calendarGridView!!.setOnTouchListener(object : OnSwipeTouchListener(context!!) {
            override fun onSwipeRight() {
                // go to previous month
                swipNextMonthCount--
                dynamicCal!!.add(Calendar.MONTH, -1)
                adapter!!.updateCalendar(dynamicCal!!)
                setDynamicHeightGridView(calendarGridView!!, 7)
                updateMonthYearInfo()

                showListEvent(adapter!!, dynamicCal!!)

                val ani = AnimationUtils.loadAnimation(
                    context!!,
                    com.tungtop.phatlich.R.anim.left_to_right
                )
                calendarGridView!!.startAnimation(ani)
            }

            override fun onSwipeLeft() {
                // go to next month
                swipNextMonthCount++
                dynamicCal!!.add(Calendar.MONTH, 1)
                adapter!!.updateCalendar(dynamicCal!!)
                setDynamicHeightGridView(calendarGridView!!, 7)
                updateMonthYearInfo()
                showListEvent(adapter!!, dynamicCal!!)

                val ani = AnimationUtils.loadAnimation(
                    context!!,
                    com.tungtop.phatlich.R.anim.right_to_left
                )
                calendarGridView!!.startAnimation(ani)
            }

            override fun onSwipeBottom() {}
            override fun onSwipeTop() {}
        })

        calendarGridView!!.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                var duration: Long = 200

                val colors = arrayOf(
                    ColorDrawable(Color.parseColor("#caf0f8")), // Animation starting color
                    ColorDrawable(Color.WHITE) // Animation ending color
                )

                // Initialize a new transition drawable instance
                val transitionDrawable = TransitionDrawable(colors)

                // Set the clicked item background
                view.background = transitionDrawable
                transitionDrawable.startTransition(duration.toInt()) // 600 Milliseconds

                val runable = Runnable {
                    val separatedTime = selectedItem.split("-")
                    if (separatedTime.size > 1) {
                        val cCal =
                            GregorianCalendar.getInstance(Locale.getDefault()) as GregorianCalendar
                        if (separatedTime[0].toInt() == cCal.get(Calendar.DAY_OF_MONTH)
                            && separatedTime[1].toInt() == (cCal.get(Calendar.MONTH) + 1)
                            && separatedTime[2].toInt() == cCal.get(Calendar.YEAR)
                        ) {
                            view.setBackgroundResource(com.tungtop.phatlich.R.drawable.border)
                        }
                    }
                }
                Handler().postDelayed(runable, duration)
            }
        }
        showListEvent(adapter!!, dynamicCal!!)
    }

    private fun updateMonthYearInfo() {
        monthText = cacheView!!.findViewById<TextView>(com.tungtop.phatlich.R.id.month)
        val currentMonth = dynamicCal!!.get(Calendar.MONTH) + 1
        monthText!!.text = "Tháng " + currentMonth.toString()

        yearText = cacheView!!.findViewById<TextView>(com.tungtop.phatlich.R.id.year)
        val currentYear = dynamicCal!!.get(Calendar.YEAR)
        val currentBuddhismYear = currentYear + 544

        //TODO: check if this is 15/2, add new Buddhism Year.
        yearText!!.text = currentBuddhismYear.toString() + "|" + currentYear.toString()
    }

    private fun showListEvent(adapter: CalendarAdapter, cal: GregorianCalendar) {
        adapterEvent =
            EventsAdapter(context!!, com.tungtop.phatlich.R.layout.listview_item_event_for_month)
        for (i in 0..(adapter.itemsCurrentMonth.size - 1)) {
            val lunarArr = adapter.itemsCurrentMonth[i].split("-")
            if (lunarArr[4].toInt() == 1) {
                // had event this day

                var intLunaArr = lunarArr.map { it.toInt() }
                var event =
                    BuddhismEvents.getEventByDate(listEventBuddhism!!, intLunaArr.toIntArray())
                adapterEvent!!.addItem(
                    EventTransactionObject(
                        EventType.ITEM,
                        event!!.name,
                        event!!.day,
                        event!!.month,
                        event!!.description
                    )
                )
            }
        }
        eventsElement!!.adapter = adapterEvent

        setDynamicHeightListView(eventsElement!!)
        eventsElement!!.setOnTouchListener(object : OnSwipeTouchListener
            (context!!) {
            override fun onSwipeRight() {
                // go to previous month
                swipNextMonthCount--
                dynamicCal!!.add(Calendar.MONTH, -1)
                adapter!!.updateCalendar(dynamicCal!!)
                setDynamicHeightGridView(calendarGridView!!, 7)
                updateMonthYearInfo()

                showListEvent(adapter!!, dynamicCal!!)

                val ani = AnimationUtils.loadAnimation(
                    context!!,
                    com.tungtop.phatlich.R.anim.left_to_right
                )
                calendarGridView!!.startAnimation(ani)
            }

            override fun onSwipeLeft() {
                // go to next month
                swipNextMonthCount++
                dynamicCal!!.add(Calendar.MONTH, 1)
                adapter!!.updateCalendar(dynamicCal!!)
                setDynamicHeightGridView(calendarGridView!!, 7)
                updateMonthYearInfo()
                showListEvent(adapter!!, dynamicCal!!)

                val ani = AnimationUtils.loadAnimation(
                    context!!,
                    com.tungtop.phatlich.R.anim.right_to_left
                )
                calendarGridView!!.startAnimation(ani)
            }

            override fun onSwipeBottom() {}
            override fun onSwipeTop() {}
        })
    }

    private fun setDynamicHeightGridView(gridView: GridView, columnCount: Int) {
        val gridViewAdapter = gridView.adapter
            ?: // pre-condition
            return

        var rows = gridViewAdapter.count / columnCount
        val dip = 60f // heigh of a cell calendar
        // convert to px
        val px = dip * resources.getDisplayMetrics().density;

        val params = gridView.layoutParams
        params.height = px.toInt() * rows
        gridView.layoutParams = params
        gridView.requestLayout()
    }

    fun setDynamicHeightListView(listView: ListView) {

        val mAdapter = listView.adapter

        val initHeight = 50 * resources.getDisplayMetrics().density // init height
        var totalHeight = initHeight.toInt()

        for (i in 0 until mAdapter.count) {
            val mView = mAdapter.getView(i, null, listView)

            mView.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )

            totalHeight += mView.measuredHeight
        }

        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (mAdapter.count - 1)
        listView.layoutParams = params
        listView.requestLayout()
    }

//    private fun setGridViewHeightBasedOnChildren(gridView: GridView, noOfColumns: Int) {
//        val gridViewAdapter = gridView.adapter
//            ?: // adapter is not set yet
//            return
//
//        var totalHeight: Int //total height to set on grid view
//        val items = gridViewAdapter.count //no. of items in the grid
//        val rows: Int //no. of rows in grid
//
//        val listItem = gridViewAdapter.getView(0, null, gridView)
//        listItem.measure(0, 0)
//        totalHeight = listItem.measuredHeight
//
//        val x: Float
//        if (items > noOfColumns) {
//            x = (items / noOfColumns).toFloat()
//
//            //Check if exact no. of rows of rows are available, if not adding 1 extra row
//            if (items % noOfColumns != 0) {
//                rows = (x + 1).toInt()
//            } else {
//                rows = x.toInt()
//            }
//            totalHeight *= rows
//
//        }
//
//        //Setting height on grid view
//        val params = gridView.layoutParams
//        params.height = totalHeight
//        gridView.layoutParams = params
//    }


}