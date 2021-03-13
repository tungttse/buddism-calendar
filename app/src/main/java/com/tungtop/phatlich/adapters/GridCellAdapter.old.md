package com.tungtop.phatlich

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * TODO: Tung readme
 * Tham khao http://androidlearnsteps.blogspot.com/2014/09/custom-calendar-demo-in-android.html
 */

class GridCellAdapter// Days in Current Month
    (
    private val _context: Context,
//     private var textViewResourceId: Int,
//     private var month: Int,
    private var year: Int
) :
    BaseAdapter(), OnClickListener {
    private val list: MutableList<String>
    private val weekdays = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private val months = arrayOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )
    private val daysOfMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    private var daysInMonth: Int = 0
    private var prevMonthDays: Int = 0
    private val currentDayOfMonth: Int
    private var gridcell: TextView? = null

    private var month: Calendar? = null
    var pmonth: GregorianCalendar? = null
    var pmonthmaxset: GregorianCalendar? = null
    private val selectedDate: GregorianCalendar? = null

    internal var firstDay: Int = 0
    internal var maxWeeknumber: Int = 0
    internal var maxP: Int = 0
    internal var calMaxP: Int = 0
    internal var mnthlength: Int = 0
    internal var itemvalue: String = "";
    internal var curentDateString: String = "";
    internal var df: DateFormat? = null;


    init {
        this.list = ArrayList()
        Log.d(tag, "Month: $month Year: $year")
        val calendar = Calendar.getInstance()
        df = SimpleDateFormat("yyyy-MM-dd", Locale.US);

        currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        printCalendar()
    }

    override fun getItem(position: Int): String {
        return list[position]
    }

    override fun getCount(): Int {
        return list.size
    }

    

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var row: View? = convertView
        if (row == null) {
            val inflater =
                _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            row = inflater.inflate(R.layout.calendar_daycell, parent, false)
        }

        gridcell = row!!.findViewById(R.id.gridcell) as TextView

        val separatedTime = list.get(position).split("-")
        // taking last part of date. ie; 2 from 2012-12-02
        val gridvalue = separatedTime[2].replaceFirst("^0*".toRegex(), "")

        // checking whether the day is in current month or not.
        // Trang điểm
        if (Integer.parseInt(gridvalue) > 1 && position < firstDay) {
            // setting offdays to white color.
            gridcell!!.setTextColor(Color.parseColor("#CECECE"))
            gridcell!!.setClickable(false)
            gridcell!!.setFocusable(false)
        } else if (Integer.parseInt(gridvalue) < 7 && position > 28) {
            gridcell!!.setTextColor(Color.parseColor("#CECECE"))
            gridcell!!.setClickable(false)
            gridcell!!.setFocusable(false)
        } else {
            // setting curent month's days in blue color.
            if (list.get(position) == curentDateString) {
                gridcell!!.setTextColor(Color.parseColor("#CECECE"))
            } else {
                gridcell!!.setTextColor(Color.parseColor("#D41016"))
            }
        }

//        if (dayString.get(position) == curentDateString) {
//            setSelected(v)
//            previousView = v
//        } else {
//            v.setBackgroundResource(R.drawable.list_item_background)
//        }
        gridcell!!.setText(gridvalue)

        // create date string for comparison
        var date: String = list.get(position)

        if (date.length == 1) {
            date = "0$date"
        }

        var monthStr = "" + month!!.get(GregorianCalendar.MONTH)
        if (monthStr.length == 1) {
            monthStr = "0$monthStr"
        }

//        gridcell!!.setOnClickListener(this)

        // ACCOUNT FOR SPACING
//        val day_color =
//            list[position].split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//        gridcell!!.text = day_color[0]
//        gridcell!!.tag = day_color[0] + "-" + day_color[2] + "-" + day_color[3]
//
//        if (day_color[1] == "GREY") {
//            gridcell!!.setTextColor(Color.LTGRAY)
//        }
//        if (day_color[1] == "WHITE") {
//            gridcell!!.setTextColor(Color.GRAY)
//        }
//        if (day_color[1] == "NOW") {
//            gridcell!!.setTextColor(Color.MAGENTA)
//        }

//        gridcell!!.setTextColor(Color.GRAY)
        return row
    }

    override fun onClick(view: View) {
        val date_month_year = view.tag as String
        Log.i("tungtop", date_month_year.toString())
    }

    companion object {
        private val tag = "TungTop GridCellAdapter"
    }

    private fun getMaxP(): Int {
        val maxP: Int
        if (month!!.get(GregorianCalendar.MONTH) == month!!
                .getActualMinimum(GregorianCalendar.MONTH)
        ) {
            pmonth!!.set(
                month!!.get(GregorianCalendar.YEAR) - 1,
                month!!.getActualMaximum(GregorianCalendar.MONTH), 1
            )
        } else {
            pmonth!!.set(
                GregorianCalendar.MONTH,
                month!!.get(GregorianCalendar.MONTH) - 1
            )
        }
        maxP = pmonth!!.getActualMaximum(GregorianCalendar.DAY_OF_MONTH)

        return maxP
    }

//    private fun printMonth(mm: Int, yy: Int) {
//        Log.i(tag, "go to print month")
//        // The number of days to leave blank at
//        // the start of this month.
//        var trailingSpaces = 0
//        val leadSpaces = 0
//        var daysInPrevMonth = 0
//        var prevMonth = 0
//        var prevYear = 0
//        var nextMonth = 0
//        var nextYear = 0
//
//        val cal = GregorianCalendar(yy, mm, currentDayOfMonth)
//
//        // Days in Current Month
//        daysInMonth = daysOfMonth[mm]
//        val currentMonth = mm
//        if (currentMonth == 12) {
//            prevMonth = 11
//            daysInPrevMonth = daysOfMonth[prevMonth]
//            nextMonth = 1
//            prevYear = yy
//            nextYear = yy + 1
//        } else if (currentMonth == 1) {
//            prevMonth = 12
//            prevYear = yy - 1
//            nextYear = yy
//            daysInPrevMonth = daysOfMonth[prevMonth]
//            nextMonth = 2
//        } else {
//            prevMonth = currentMonth - 1
//            nextMonth = currentMonth + 1
//            nextYear = yy
//            prevYear = yy
//            daysInPrevMonth = daysOfMonth[prevMonth]
//        }
//
//        // Compute how much to leave before before the first day of the
//        // month.
//        // getDay() returns 0 for Sunday.
//        trailingSpaces = cal.get(Calendar.DAY_OF_WEEK) - 1
//
//        if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1) {
//            ++daysInMonth
//        }
//
//        // Trailing Month days
//        for (i in 0 until trailingSpaces) {
//            list.add((daysInPrevMonth - trailingSpaces + 1 + i).toString() + "-GREY" + "-" + months[prevMonth] + "-" + prevYear)
//        }
//
////        // Current Month Days
////        for (i in 1..daysInMonth) {
////            list.add(i.toString() + "-WHITE" + "-" + months[mm] + "-" + yy)
////        }
//
//        // Leading Month days
//        for (i in 0 until list.size % 7) {
//            Log.d(tag, "NEXT MONTH:= " + months[nextMonth])
//            list.add((i + 1).toString() + "-GREY" + "-" + months[nextMonth] + "-" + nextYear)
//        }
//
//        // Current Month Days
//        for (i in 1..daysInMonth) {
//
//            if (i == currentDayOfMonth) {
//                list.add(i.toString() + "-NOW" + "-" + months[currentMonth] + "-" + yy);
//            } else {
//                list.add(i.toString() + "-WHITE" + "-" + months[currentMonth] + "-" + yy);
//            }
//        }
//
//    }

}