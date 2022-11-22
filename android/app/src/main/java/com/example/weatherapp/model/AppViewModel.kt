package com.example.weatherapp.model

import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.HashMap

class AppViewModel : ViewModel() {
    var city: City = City("", "")

    val locations: MutableList<Prefecture> = mutableListOf()

    private val timers: HashMap<String, Timer> = hashMapOf()

    fun fixedRateTimer(
        name: String,
        daemon: Boolean,
        startAt: Date,
        period: Long,
        action: TimerTask.() -> Unit
    ): Timer {
        if (timers.containsKey(name)) {
            return timers.get(name)!!
        }

        val timer = kotlin.concurrent.fixedRateTimer(name, daemon, startAt, period, action)
        timers.put(name, timer)
        return timer
    }
}
