package com.tungtop.phatlich.fragements

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import com.tungtop.phatlich.R
import com.tungtop.phatlich.VietCalendar
import com.tungtop.phatlich.libraries.OnSwipeTouchListener
import com.tungtop.phatlich.models.BuddhismEvents
import com.tungtop.phatlich.models.Quotes
import java.text.SimpleDateFormat
import java.util.*


class DayFragement : Fragment() {
    private var cacheView: View? = null
    private var listEventBuddhism: ArrayList<BuddhismEvents>? = null
    private var dynamicCal: GregorianCalendar? = null
    private var swipNextDayCount: Int = 0
    private var listQuotes: ArrayList<Quotes>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (cacheView == null) {
            cacheView =
                inflater.inflate(R.layout.fragement_day, container, false)
        }

        listEventBuddhism = BuddhismEvents.getDataFromFile("events.json", context!!)
        listQuotes = Quotes.getDataFromFile("quotes.json", context!!)

        handleView(cacheView!!)
        return cacheView
    }

    private fun showView(cal: Calendar) {
        var cDay = cal!!.get(GregorianCalendar.DATE)
        var cMonth = cal!!.get(GregorianCalendar.MONTH) + 1
        var cYear = cal!!.get(GregorianCalendar.YEAR)
        var lunarDay = VietCalendar.convertSolar2Lunar(cDay, cMonth, cYear, 7.0)
        var event = BuddhismEvents.getEventByDate(listEventBuddhism!!, lunarDay)

        val monthYear = cacheView!!.findViewById<TextView>(R.id.month_year)
        monthYear.text = "Tháng " + cMonth.toString() + " - " + cYear.toString()

        val day = cacheView!!.findViewById<TextView>(R.id.day_number)
        day.text = cDay.toString()

        //TODO: multiple lang here. Just Viet Nam for the first release
        val sdf = SimpleDateFormat("EEEE", Locale.forLanguageTag("vi"))
        val dayOfTheWeek = sdf.format(cal!!.time)
        val dayInWeek = cacheView!!.findViewById<TextView>(R.id.day_in_week)
        dayInWeek.text = dayOfTheWeek

        val quote = cacheView!!.findViewById<TextView>(R.id.quote)
        val quoteAuthor = cacheView!!.findViewById<TextView>(R.id.quote_author)
        if (event != null) {
            quote.text = event.name
            quoteAuthor.visibility = View.GONE
        } else {
            quote.text = "\"" + listQuotes!!.random().quote + "\""
            quoteAuthor.text = "-- " + listQuotes!![0].source
            quoteAuthor.visibility = View.VISIBLE
        }

        // lunar info
        val lunaDayText = cacheView!!.findViewById<TextView>(R.id.luna_day)
        val lunaMonthText = cacheView!!.findViewById<TextView>(R.id.luna_month)
        val lunaYearText = cacheView!!.findViewById<TextView>(R.id.luna_year)

        lunaDayText.text = lunarDay[0].toString()
        lunaMonthText.text = lunarDay[1].toString()
        lunaYearText.text = lunarDay[2].toString()
    }

    private fun handleView(cacheView: View) {
        var fixedCal = GregorianCalendar.getInstance(Locale.getDefault()) as GregorianCalendar
        dynamicCal = GregorianCalendar.getInstance(Locale.getDefault()) as GregorianCalendar

        showView(fixedCal)

        val dayView = cacheView!!.findViewById<LinearLayout>(R.id.day_view)
        dayView!!.setOnTouchListener(object : OnSwipeTouchListener(context!!) {
            override fun onSwipeRight() {
                // Go to previous day
                swipNextDayCount--
                dynamicCal!!.add(Calendar.DAY_OF_MONTH, -1)
                showView(dynamicCal!!)

                val ani = AnimationUtils.loadAnimation(context!!, R.anim.left_to_right)
                dayView!!.startAnimation(ani)
            }

            override fun onSwipeLeft() {
                // Go to next day
                swipNextDayCount++
                dynamicCal!!.add(Calendar.DAY_OF_MONTH, 1)
                showView(dynamicCal!!)

                val ani = AnimationUtils.loadAnimation(context!!, R.anim.right_to_left)
                dayView!!.startAnimation(ani)
            }

            override fun onSwipeBottom() {}
            override fun onSwipeTop() {}
        })

        dayView!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Phai co ham nay de tranh conflict giua swipe gridview va onClick cell
            }
        })
    }
}