package com.tungtop.phatlich

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.tungtop.phatlich.models.EventTransactionObject
import com.tungtop.phatlich.models.EventType
import java.util.*
import kotlin.collections.ArrayList


class EventsAdapter(
    private val context: Context,
    private val intLayout: Int
) : BaseAdapter() {
    private val TYPE_ITEM = 0
    private val TYPE_HEADER = 1
    private val TYPES_COUNT = 2

    private val mData = ArrayList<EventTransactionObject>()


    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    fun addItem(item: EventTransactionObject) {
        mData.add(item)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): EventTransactionObject {
        return mData[position]
    }

    override fun getViewTypeCount(): Int {
        return TYPES_COUNT
    }

    override fun getItemViewType(position: Int): Int {
        return if (mData[position].type == EventType.HEADER) TYPE_HEADER else TYPE_ITEM
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val type = getItemViewType(position)

        var view = convertView
        var holder = ViewHolder()

        if (view == null) {
            if (type == TYPE_ITEM) {
                view = inflater.inflate(
                    intLayout,
                    parent,
                    false
                )
                holder.name = view!!.findViewById(R.id.name)
                holder.day = view!!.findViewById(R.id.day)
                holder.description =
                    view!!.findViewById(R.id.description)
                holder.remaining_time =
                    view!!.findViewById(R.id.remaining_time)
            } else {
                view = inflater.inflate(
                    R.layout.listview_item_header_event,
                    parent,
                    false
                )
                holder.name = view!!.findViewById(R.id.name)
            }

            view!!.tag = holder

        } else {
            holder = view!!.tag as ViewHolder
        }

        if (type == TYPE_ITEM) {
            holder.name!!.text = getItem(position).name
            holder.day!!.text = getItem(position).day + "/" + getItem(position).month
            holder.description!!.text = getItem(position).description
            holder.remaining_time!!.text = ""
            holder.remaining_time!!.visibility = View.GONE

            // if today
            val cCal = GregorianCalendar.getInstance(Locale.getDefault()) as GregorianCalendar
            var cDay = cCal!!.get(GregorianCalendar.DATE)
            var cMonth = cCal!!.get(GregorianCalendar.MONTH) + 1
            var cYear = cCal!!.get(GregorianCalendar.YEAR)

            // get lunar day here
            var lunarDay = VietCalendar.convertSolar2Lunar(cDay, cMonth, cYear, 7.0)

            val myParent = holder.name!!.parent as LinearLayout

            if (getItem(position).day.toInt() == lunarDay[0]
                && getItem(position).month.toInt() == lunarDay[1]
            ) {
                myParent.setBackgroundColor(Color.parseColor("#bfd7ff"))
                holder.day!!.typeface = Typeface.DEFAULT_BOLD
                holder.description!!.typeface = Typeface.DEFAULT_BOLD
                holder.remaining_time!!.typeface = Typeface.DEFAULT_BOLD
            } else {
                holder.day!!.typeface = Typeface.DEFAULT
                holder.description!!.typeface = Typeface.DEFAULT
                holder.remaining_time!!.typeface = Typeface.DEFAULT

                if (getItem(position).month.toInt() < lunarDay[1]) {
                    // if pass
                    myParent.setBackgroundColor(Color.parseColor("#f2f2f2"))
                } else if(getItem(position).month.toInt() == lunarDay[1] && getItem(position).day.toInt() < lunarDay[0]) {
                    // if pass
                    myParent.setBackgroundColor(Color.parseColor("#f2f2f2"))
                } else {
                    // if future
                    myParent.setBackgroundColor(Color.parseColor("#ffffff"))

                    //TODO: count remaing day
//                    holder.remaining_time!!.text = ""
//                    holder.remaining_time!!.visibility = View.GONE
                }
            }
        } else {
            holder.name!!.text = "Tháng " + getItem(position).name
        }

        return view!!
    }
}

class ViewHolder {
    var name: TextView? = null
    var day: TextView? = null
    var month: TextView? = null
    var description: TextView? = null
    var remaining_time: TextView? = null
}