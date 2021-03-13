package com.tungtop.phatlich.models

import android.content.Context
import com.tungtop.phatlich.models.BaseModel.Companion.loadJsonFromAsset
import org.json.JSONArray
import org.json.JSONException


enum class EventType {
    ITEM,
    HEADER
}

data class EventTransactionObject(
    var type: EventType,
    var name: String,
    var day: String,
    var month: String,
    var description: String
)

data class Event(
    var day: String,
    var month: String,
    var name: String,
    var description: String
)

class BuddhismEvents(
    var month: String,
    var events: ArrayList<Event>
) {
    companion object {
        fun getDataFromFile(
            filename: String,
            context: Context
        ): ArrayList<BuddhismEvents> {
            val eventsByMonth = ArrayList<BuddhismEvents>()
            try {
                // Load data
                val jsonString = loadJsonFromAsset(filename, context)
                val jsonData = JSONArray(jsonString)

                (0 until jsonData.length()).mapTo(eventsByMonth) {
                    var month = jsonData.getJSONObject(it).getString("month")
                    var eventList = mapEvent(jsonData.getJSONObject(it).getString("events"), month)

                    BuddhismEvents(month, eventList)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return eventsByMonth
        }

        fun getEventByDate(buddhismEvents: ArrayList<BuddhismEvents>, lunarDay: IntArray): Event? {
            val month: Int = lunarDay[1]
            val day: Int = lunarDay[0]

            var eventsInMonth = buddhismEvents[month - 1].events

            for (i in 0..eventsInMonth.size - 1) {
                if (eventsInMonth[i].day.toInt() == day) {
                    return eventsInMonth[i]
                    break
                }
            }

            return null
        }

        private fun mapEvent(data: String, month: String): ArrayList<Event> {
            val myJson = JSONArray(data)
            val myArr = arrayListOf<Event>()

            for (i in 0..(myJson.length() - 1)) {
                var qItem = Event(
                    myJson.getJSONObject(i).getString("day").toString(),
                    month,
                    myJson.getJSONObject(i).getString("name").toString(),
                    myJson.getJSONObject(i).getString("description").toString()
                )

                myArr.add(qItem)
            }
            return myArr
        }
    }
}