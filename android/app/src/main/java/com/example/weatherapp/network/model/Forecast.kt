package com.example.weatherapp.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Forecast(
    private val time: String,
    val temperature: Double,
    val status: String,
) {

    private val mem = mutableMapOf<String, Int>()

    init {
        val validFormat = "yyyy-MM-dd\'T\'hh:mm"
        var i = 0
        var j = 0
        val N = validFormat.length
        var isValid = true
        while ((i < N) && (isValid)) {
            val fChar = validFormat[i]
            if (fChar == '\'') {
                i += 1
                while ((i < N) && (validFormat[i] != '\'') && isValid) {
                    isValid = validFormat[i] == time[j]
                    i++
                    j++
                }
                j -= 1

            } else if ((fChar in 'a'..'z') || (fChar in 'A'..'Z')) {
                isValid = time[j].isDigit()
                if (isValid) {
                    val key = fChar.toString()
                    this.mem[key] = (this.mem[key] ?: 0) * 10 + time[j].digitToInt()
                }
            }
            i += 1
            j += 1
        }
        if (!isValid) {
            this.mem.clear()
        }
    }

    val hours24
        get(): Int = this.mem["h"] ?: -1
}
