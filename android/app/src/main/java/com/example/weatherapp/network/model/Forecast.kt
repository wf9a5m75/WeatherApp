package com.example.weatherapp.network.model

import kotlinx.serialization.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val ISO8601_FORMAT = "yyyy-MM-dd\'T\'hh:mm"

@Serializable
data class Forecast(
    private val time: String,
    val temperature: Double,
    val status: String,
) {

    private val mem = mutableMapOf<String, Int>()
    private var hour: Int = -1

    /*
     * timeにはISO8601形式で日時データが与えられる。
     * これをパースして、時間だけを取り出し、hour24で返す。
     */
    init {
        try {
            val date = SimpleDateFormat(ISO8601_FORMAT, Locale.getDefault()).parse(this.time)
            if (date != null) {
                val calendar = Calendar.getInstance().also {
                    it.time = date
                }
                this.hour = calendar.get(Calendar.HOUR_OF_DAY)
            }
        } catch (exception: ParseException) {
            // Do nothing
        }
    }

    val hours24
        get(): Int = this.hour
}
