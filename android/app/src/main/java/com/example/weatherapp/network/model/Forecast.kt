package com.example.weatherapp.network.model

import kotlinx.serialization.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val ISO8601_FORMAT = "yyyy-MM-dd\'T\'HH:mm"

@Serializable
data class Forecast(
    private val time: String,
    val temperature: Double,
    val status: String,
) {

    private var _hour: Int = -1
    private var _year: Int = -1
    private var _month: Int = -1
    private var _date: Int = -1

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
                this._year = calendar.get(Calendar.YEAR)
                this._month = calendar.get(Calendar.MONTH) + 1
                this._date = calendar.get(Calendar.DATE)
                this._hour = calendar.get(Calendar.HOUR_OF_DAY)
            }
        } catch (exception: ParseException) {
            // Do nothing
        }
    }

    val hours24
        get(): Int = this._hour

    val year
        get(): Int = this._year

    val month
        get(): Int = this._month

    val date
        get(): Int = this._date

}
