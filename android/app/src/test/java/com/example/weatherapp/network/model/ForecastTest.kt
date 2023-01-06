package com.example.weatherapp.network.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ForecastTest {

    @Test
    fun `Should parse the valid ISO8601 date correctly`() {
        val data = Forecast(
            time = "2022-11-15T22:33",
            temperature = 3.0,
            status = "something",
        )
        assertEquals(22, data.hours24)
    }

    @Test
    fun `Should return -1 if given date is invalid`() {
        val data = Forecast(
            time = "2022-11-15 22:33",
            temperature = 3.0,
            status = "something",
        )
        assertEquals(-1, data.hours24)
    }
}
