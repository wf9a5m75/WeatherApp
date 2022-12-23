package com.example.weatherapp.network.model

import kotlinx.serialization.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val DATETIME_FORMAT = "yyyy-MM-dd\'T\'hh:mm"

@Serializable
data class Forecast(
    private val time: String,
    val temperature: Double,
    val status: String,
) {

    companion object {

        private val TAG = Forecast::class.java.simpleName
    }

    private var hour: Int = -1

    /*
     * timeにはISO8601形式で日時データが与えられる。
     * これをパースして、時間だけを取り出し、hour24で返す。
     */
    init {
        try {
            val date = SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault()).parse(time)
            if (date != null) {
                val calendar = Calendar.getInstance().also {
                    it.time = date
                }
                hour = calendar.get(Calendar.HOUR_OF_DAY)
            }
        } catch (exception: ParseException) {
            // Do nothing
        }
    }

    val hours24
        get(): Int = hour
}
