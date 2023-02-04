package com.example.weatherapp.providers

import com.example.weatherapp.database.KeyValueDao
import com.example.weatherapp.database.PrefectureDao
import com.example.weatherapp.database.WeeklyForecastDao
import com.example.weatherapp.network.IWeatherApi
import com.example.weatherapp.utils.INetworkMonitor

interface ITestCaseDataset {
    fun provideNetworkMonitor(): INetworkMonitor

    fun provideWeatherApi(): IWeatherApi

    fun providePrefectureDao(): PrefectureDao

    fun provideKeyValueDao(): KeyValueDao

    fun provideWeeklyForecastDao(): WeeklyForecastDao
}
