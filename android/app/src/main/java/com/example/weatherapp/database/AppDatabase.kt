package com.example.weatherapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherapp.network.model.Prefecture
import com.example.weatherapp.network.model.WeeklyForecast

@Database(
    entities = [
        Prefecture::class,
        KeyValuePair::class,
        WeeklyForecast::class
    ],
    version = 2
)
@TypeConverters(
    PrefectureConverters::class,
    WeeklyForecastConverters::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prefectureDao(): PrefectureDao

    abstract fun keyValueDao(): KeyValueDao

    abstract fun weeklyForecastDao(): WeeklyForecastDao
}
