package com.example.weatherapp.providers

import com.example.weatherapp.database.KeyValueDao
import com.example.weatherapp.database.PrefectureDao
import com.example.weatherapp.database.WeeklyForecastDao
import com.example.weatherapp.network.IWeatherApi
import com.example.weatherapp.network.model.DailyForecast
import com.example.weatherapp.network.model.Forecast
import com.example.weatherapp.network.model.Prefecture
import com.example.weatherapp.network.model.WeeklyForecast
import com.example.weatherapp.utils.INetworkMonitor
import okhttp3.ResponseBody.Companion.toResponseBody
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import retrofit2.Response

object OfflineCaseDataset: ITestCaseDataset {

    override fun provideNetworkMonitor() = spy<INetworkMonitor> {
        on { this.isOnline } doReturn false
    }

    override fun provideWeatherApi() = spy<IWeatherApi> {
        onBlocking { this.getWeeklyForecast(any()) } doReturn
                Response.error(500, "".toResponseBody(null))

        onBlocking { this.getLocations() } doReturn
                Response.error(500, "".toResponseBody(null))
    }

    override fun providePrefectureDao() = spy<PrefectureDao> {
        onBlocking { this.find(any()) } doReturn null

        onBlocking { this.count() } doReturn 0

        onBlocking { this.getAll() } doReturn listOf<Prefecture>()

        onBlocking { this.clear() } doAnswer { }

        onBlocking { this.insertAll(any()) } doAnswer { }
    }

    override fun provideKeyValueDao() = spy<KeyValueDao> {
        onBlocking { this.get(any()) } doReturn null

        onBlocking { this.put(any()) } doAnswer {}
    }


    override fun provideWeeklyForecastDao() = spy<WeeklyForecastDao> {
        onBlocking { this.find(any()) } doAnswer {
            val cityId = it.getArgument(0, String::class.java)
            if (cityId != "city_offline") {
                null
            } else {
                WeeklyForecast(
                    cityId = "city_offline",
                    last_update = "2023-02-02T11:54",
                    overall = "rain",
                    forecasts = listOf(
                        DailyForecast(
                            date = "2023-02-02",
                            forecasts = listOf(
                                Forecast(
                                    time = "00:00",
                                    temperature = 6.3,
                                    status = "rain"
                                ),
                                Forecast(
                                    time = "01:00",
                                    temperature = 6.0,
                                    status = "rain"
                                ),
                            ),
                        ),

                        DailyForecast(
                            date = "2023-02-03",
                            forecasts = listOf(
                                Forecast(
                                    time = "00:00",
                                    temperature = 8.1,
                                    status = "clear"
                                ),
                                Forecast(
                                    time = "01:00",
                                    temperature = 7.8,
                                    status = "clear"
                                ),
                            ),
                        ),
                    ),
                )
            }
        }

        onBlocking { this.put(any()) } doAnswer {}

        onBlocking { this.remove(any()) } doAnswer {}
    }
}
