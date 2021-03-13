package com.tungtop.phatlich.models

import android.content.Context
import com.tungtop.phatlich.models.BaseModel.Companion.loadJsonFromAsset
import org.json.JSONArray
import org.json.JSONException

class Quotes(
    var quote: String,
    var source: String
) {
    companion object {
        fun getDataFromFile(
            filename: String,
            context: Context
        ): ArrayList<Quotes> {
            val quotes = ArrayList<Quotes>()
            try {
                // Load data
                val jsonString = loadJsonFromAsset(filename, context)
                val jsonData = JSONArray(jsonString)

                (0 until jsonData.length()).mapTo(quotes) {
                    var quote = jsonData.getJSONObject(it).getString("quote")
                    var source = jsonData.getJSONObject(it).getString("source")
                    Quotes(quote, source)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return quotes
        }
    }
}