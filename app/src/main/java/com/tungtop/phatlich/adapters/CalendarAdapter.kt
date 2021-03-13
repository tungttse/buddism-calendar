package com.tungtop.phatlich

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.tungtop.phatlich.models.BuddhismEvents
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * TODO: Tung readme
 * Tham khao http://androidlearnsteps.blogspot.com/2014/09/custom-calendar-demo-in-android.html
 */

class CalendarAdapter// Days in Current Month
    (
    private val _context: Context,
    private var monthSelect: Calendar,
    private var listEventBuddhism: ArrayList<BuddhismEvents>
) :
    BaseAdapter()
//    View.OnClickListener
{
    private var list: MutableList<String>
    var itemsCurrentMonth: MutableList<String>
    private val weekdaysEN = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private val weekdaysVN = listOf("Hai", "Ba", "Tư", "Năm", "Sáu", "Bảy", "C.N")
    private var startWeekCfg: Int = Calendar.MONDAY // 1 is monday , 2 is sunday
    private var lanCfg: Int = 2 // 1 is VN , 2 is EN

    var previousMonth: GregorianCalendar? = null
    var displayMonth: GregorianCalendar? = null
    private var selectedDate: GregorianCalendar? = null

    var currentCalendar: GregorianCalendar? = null

    internal var firstDay: Int = 0
    internal var dayOfCurrentMonth: Int = 0
    internal var maximumDaysOfPreviousMonth: Int = 0
    internal var dayStartFromPreviousMonth: Int = 0
    internal var maxRowNumber: Int = 0
    internal var itemvalue: String = ""
    internal var curentDateString: String = ""
    internal var myDateFormat: DateFormat? = null


//    val Int.toDP: Int
//        get() = (this / Resources.getSystem().displayMetrics.density).toInt()
//
//    val Int.toPx: Int
//        get() = (this / Resources.getSystem().displayMetrics.density).toInt()

    var cellWidth = 0
    var MARGIN_DEFAULT = 100

    init {
        this.list = ArrayList()
        this.itemsCurrentMonth = ArrayList()
        myDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)

        val displayMetrics = _context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels - MARGIN_DEFAULT
        cellWidth = dpWidth / 7

        printCalendar()
    }

    override fun getItem(position: Int): String {
        return list[position]
    }

    override fun getCount(): Int {
        return list.size
    }

    fun updateCalendar(newCalendar: GregorianCalendar) {
        // reset some base values
        // update
        monthSelect = newCalendar.clone() as GregorianCalendar
        notifyDataSetChanged()
        printCalendar()
    }

    fun printCalendar() {
        list = ArrayList()
        itemsCurrentMonth = ArrayList()

        currentCalendar = monthSelect!! as GregorianCalendar
        selectedDate = currentCalendar!!.clone() as GregorianCalendar
        curentDateString = myDateFormat!!.format(selectedDate!!.getTime())
        dayOfCurrentMonth = currentCalendar!!.getActualMaximum(GregorianCalendar.DAY_OF_MONTH)

        //TODO: handle trường hợp tháng nhuận, tức là có 2 tháng âm như nhau, ví dụ năm 2020 có 2 tháng 4 âm lịch.
//        currentCalendar!!.firstDayOfWeek = startWeekCfg

        firstDay = currentCalendar!!.get(GregorianCalendar.DAY_OF_WEEK)

        //set currentCalendar to first day of current currentCalendar
        currentCalendar!!.set(Calendar.DAY_OF_MONTH, 1)

        displayMonth = currentCalendar!!.clone() as GregorianCalendar

        //get dayofweek of firstday of currentCalendar
        var dayOfWeek = currentCalendar!!.get(GregorianCalendar.DAY_OF_WEEK)

        var deltaDay: Int = 0 // số ngày để trừ đi
        if (startWeekCfg == Calendar.MONDAY) {
            if (dayOfWeek > Calendar.SUNDAY) {
                deltaDay = dayOfWeek - startWeekCfg
            } else {
                deltaDay = dayOfWeek - 1
            }
        } else {
            deltaDay = dayOfWeek - startWeekCfg
        }

        if (deltaDay == 0) {
            dayStartFromPreviousMonth = 1
            displayMonth!!.set(GregorianCalendar.DAY_OF_MONTH, dayStartFromPreviousMonth)
        } else {
            maximumDaysOfPreviousMonth = getPreviousMonthMaximumDay(currentCalendar!!)
            dayStartFromPreviousMonth = maximumDaysOfPreviousMonth - deltaDay + 1

            displayMonth!!.add(GregorianCalendar.MONTH, -1)
            displayMonth!!.set(GregorianCalendar.DAY_OF_MONTH, dayStartFromPreviousMonth)
        }

        // finding number of weeks in current currentCalendar.
        maxRowNumber = dayOfCurrentMonth + deltaDay
        if (maxRowNumber % 7 > 0) {
            maxRowNumber = (maxRowNumber / 7 + 1) * 7
        }

        // add title first
        for (title in getTitleWeekDays()) {
            list.add(title)
        }

        // add day
        for (n in 0 until maxRowNumber) {
            itemvalue = myDateFormat!!.format(displayMonth!!.time)

            var cDay = displayMonth!!.get(GregorianCalendar.DATE)
            var cMonth = displayMonth!!.get(GregorianCalendar.MONTH) + 1
            var cYear = displayMonth!!.get(GregorianCalendar.YEAR)

            // get lunar day here
            var lunarDay = VietCalendar.convertSolar2Lunar(cDay, cMonth, cYear, 7.0)

            var strHasEvent = "0"
            var events = listEventBuddhism!![lunarDay[1] - 1].events

            for (e in 0 until events.size) {
                if (events[e].day.toInt() == lunarDay[0]) {
                    strHasEvent = "1"
                    break
                }
            }

            list.add(itemvalue + "-" + lunarDay[0].toString() + "-" + lunarDay[1].toString() + "-" + lunarDay[2].toString() + "-" + lunarDay[3].toString() + "-" + strHasEvent)
            itemsCurrentMonth.add(lunarDay[0].toString() + "-" + lunarDay[1].toString() + "-" + lunarDay[2].toString() + "-" + lunarDay[3].toString() + "-" + strHasEvent)

            displayMonth!!.add(GregorianCalendar.DATE, 1)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row: View? = convertView
        var holder = CalendarViewHolder()

        if (row == null) {
            holder = CalendarViewHolder()

            val inflater =
                _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            row = inflater.inflate(R.layout.calendar_daycell, parent, false)

            holder.calCell = row!!.findViewById(R.id.calCell) as ConstraintLayout
            holder.gridcell = row!!.findViewById(R.id.gridcell) as TextView

            holder.lunarcell = row!!.findViewById(R.id.lunarcell) as TextView
            holder.eventView = row!!.findViewById(R.id.event) as ImageView

            row!!.tag = holder

        } else {
            holder = row!!.tag as CalendarViewHolder
        }


        val separatedTime = list.get(position).split("-")

        if (separatedTime.size > 1) {
            val gridvalue = separatedTime[0].replaceFirst("^0*".toRegex(), "")
            var textGridvalue = separatedTime[0].replaceFirst("^0*".toRegex(), "")

            if (Integer.parseInt(gridvalue) > 1 && position < firstDay) {
                // setting offdays to white color.
                holder.gridcell!!.setTextColor(Color.parseColor("#CECECE"))
            } else if (Integer.parseInt(gridvalue) < 7 && position > 28) {
                holder.gridcell!!.setTextColor(Color.parseColor("#CECECE"))
            } else {
                // setting curent currentCalendar's days in blue color.
                val cCal = GregorianCalendar.getInstance(Locale.getDefault()) as GregorianCalendar
                if (separatedTime[0].toInt() == cCal.get(Calendar.DAY_OF_MONTH)
                    && separatedTime[1].toInt() == (cCal.get(Calendar.MONTH) + 1)
                    && separatedTime[2].toInt() == cCal.get(Calendar.YEAR)
                ) {
                    holder.gridcell!!.setTextColor(Color.parseColor("#153131"))
                    holder.gridcell!!.setTypeface(Typeface.DEFAULT_BOLD)
                    holder.lunarcell!!.setTextColor(Color.parseColor("#153131"))
                    holder.lunarcell!!.setTypeface(Typeface.DEFAULT_BOLD)
                    holder.calCell!!.setBackgroundResource(R.drawable.border)

                } else {
                    holder.gridcell!!.setTextColor(Color.parseColor("#000000"))
                    holder.lunarcell!!.setTextColor(Color.parseColor("#6b6b6b"))
                    holder.lunarcell!!.setTypeface(Typeface.DEFAULT)
                    holder.gridcell!!.setTypeface(Typeface.DEFAULT)
                    holder.calCell!!.background = null
                }
            }

            holder.gridcell!!.setText(textGridvalue)
            holder.lunarcell!!.setText(separatedTime[3])

            if (separatedTime[3].toInt() == 1) {
                holder.lunarcell!!.setText(separatedTime[3] + "/" + separatedTime[4])
                holder.lunarcell!!.setTextColor(Color.parseColor("#b01740"))
            }

            // has event or not
            if (separatedTime[7] == "1") {
                holder.eventView!!.visibility = View.VISIBLE
            } else {
                holder.eventView!!.visibility = View.GONE
            }
//            holder.calCell!!.setOnClickListener(this)

            holder.gridcell!!.setTypeface(Typeface.DEFAULT)
        } else {
            holder.gridcell!!.setText(separatedTime[0])
            holder.gridcell!!.setTypeface(Typeface.DEFAULT_BOLD)
            holder.gridcell!!.setClickable(false)
            holder.gridcell!!.setFocusable(false)
        }

        holder.calCell!!.layoutParams.width = cellWidth

        return row
    }

    companion object {
        private val tag = "TTT"
    }

    private fun getPreviousMonthMaximumDay(curCalendar: Calendar): Int {
        previousMonth = curCalendar.clone() as GregorianCalendar
        val maxPrevious: Int
        if (curCalendar.get(GregorianCalendar.MONTH) == curCalendar.getActualMinimum(
                GregorianCalendar.MONTH
            )
        ) {
            previousMonth!!.set(
                currentCalendar!!.get(GregorianCalendar.YEAR) - 1,
                currentCalendar!!.getActualMaximum(GregorianCalendar.MONTH),
                1
            )
        } else {
            previousMonth!!.set(
                GregorianCalendar.MONTH,
                currentCalendar!!.get(GregorianCalendar.MONTH) - 1
            )
        }
        maxPrevious = previousMonth!!.getActualMaximum(GregorianCalendar.DAY_OF_MONTH)
        return maxPrevious
    }

    private fun getTitleWeekDays(): List<String> {
        var titleList: List<String>
        var finalList: List<String>

        if (lanCfg == 2) {
            titleList = weekdaysEN
        } else {
            titleList = weekdaysVN
        }
        finalList = titleList

        if (startWeekCfg == Calendar.SUNDAY) {
            val last: String = titleList.last()
            val linkedList = LinkedList(titleList)
            linkedList.push(last)
            linkedList.removeLast()
            finalList = linkedList.toList()
        }

        return finalList
    }

    //    override fun onClick(view: View) {
//        var duration: Long = 500
//
//        val colors = arrayOf(
//            ColorDrawable(Color.parseColor("#caf0f8")), // Animation starting color
//            ColorDrawable(Color.WHITE) // Animation ending color
//        )
//
//        // Initialize a new transition drawable instance
//        val transitionDrawable = TransitionDrawable(colors)
//
//        // Set the clicked item background
//        view.background = transitionDrawable
//        transitionDrawable.startTransition(duration.toInt()) // 600 Milliseconds
//
//        val runable = Runnable {
////            val separatedTime = view.tag.toString().split("-")
////            if (separatedTime[1].toInt() == 1) {
////                view.setBackgroundResource(R.drawable.border)
////            }
//        }
//        Handler().postDelayed(runable, duration)
//    }
}


class CalendarViewHolder {
    var calCell: ConstraintLayout? = null
    var gridcell: TextView? = null
    var lunarcell: TextView? = null
    var eventView: ImageView? = null
}