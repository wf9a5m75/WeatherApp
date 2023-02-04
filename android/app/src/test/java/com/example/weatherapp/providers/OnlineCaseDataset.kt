package com.example.weatherapp.providers

import com.example.weatherapp.database.KeyValueDao
import com.example.weatherapp.database.PrefectureDao
import com.example.weatherapp.database.WeeklyForecastDao
import com.example.weatherapp.network.IWeatherApi
import com.example.weatherapp.network.model.City
import com.example.weatherapp.network.model.DailyForecast
import com.example.weatherapp.network.model.Forecast
import com.example.weatherapp.network.model.LocationResponse
import com.example.weatherapp.network.model.Prefecture
import com.example.weatherapp.network.model.WeeklyForecast
import com.example.weatherapp.utils.INetworkMonitor
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.spy
import retrofit2.Response

object OnlineCaseDataset : ITestCaseDataset {

    private val dummyLocations = listOf(
        Prefecture(
            id = "pref_a",
            name = "PREF_A",
            cities = listOf(
                City(
                    "city1_in_pref_a",
                    "City1",
                ),
                City(
                    "city2_in_pref_a",
                    "City2",
                ),
            ),
        ),
        Prefecture(
            id = "pref_b",
            name = "PREF_B",
            cities = listOf(
                City(
                    "city3_in_pref_b",
                    "City3",
                ),
                City(
                    "city4_in_pref_b",
                    "City4",
                ),
            ),
        ),
    )

    override fun provideNetworkMonitor() = spy<INetworkMonitor> {
        on { this.isOnline } doReturn true
    }

    override fun provideWeatherApi() = spy<IWeatherApi> {
        onBlocking { this.getWeeklyForecast(any()) } doReturn Response.success(
            WeeklyForecast(
                last_update = "2023-01-30T12:01",
                overall = "sunny",
                forecasts = listOf(
                    DailyForecast(
                        date = "2023-01-30",
                        forecasts = listOf(
                            Forecast(
                                time = "00:00",
                                temperature = 8.3,
                                status = "clear",
                            ),
                            Forecast(
                                time = "01:00",
                                temperature = 8.0,
                                status = "clear",
                            ),
                        ),
                    ),

                    DailyForecast(
                        date = "2023-01-31",
                        forecasts = listOf(
                            Forecast(
                                time = "00:00",
                                temperature = 8.1,
                                status = "clear",
                            ),
                            Forecast(
                                time = "01:00",
                                temperature = 7.8,
                                status = "clear",
                            ),
                        ),
                    ),
                ),
            ),
        )

        onBlocking { this.getLocations() } doReturn Response.success(
            LocationResponse(
                last_update = "2023-02-02T00:00",
                prefectures = dummyLocations,
            ),
        )
    }

    override fun providePrefectureDao() = spy<PrefectureDao> {
        onBlocking { this.find(any()) } doReturn null

        onBlocking { this.count() } doReturn 0

        onBlocking { this.getAll() } doReturn dummyLocations

        onBlocking { this.clear() } doAnswer { }

        onBlocking { this.insertAll(any()) } doAnswer { }
    }

    override fun provideKeyValueDao() = spy<KeyValueDao> {
        onBlocking { this.get(any()) } doReturn null

        onBlocking { this.put(any()) } doAnswer {}
    }

    override fun provideWeeklyForecastDao() = spy<WeeklyForecastDao> {
        onBlocking { this.find(any()) } doReturn null

        onBlocking { this.put(any()) } doAnswer {}

        onBlocking { this.remove(any()) } doAnswer {}
    }
}
