package com.example.weatherapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherapp.network.model.Prefecture

@Database(entities = [Prefecture::class, KeyValuePair::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prefectureDao(): PrefectureDao

    abstract fun keyValueDao(): KeyValueDao
}
