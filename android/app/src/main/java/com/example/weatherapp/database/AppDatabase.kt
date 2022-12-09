package com.example.weatherapp.database

import androidx.room.*
import com.example.weatherapp.network.model.Prefecture
import kotlinx.coroutines.flow.*

@Database(entities = [Prefecture::class, KeyValuePair::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prefectureDao(): PrefectureDao

    abstract fun keyValueDao(): KeyValueDao
}
