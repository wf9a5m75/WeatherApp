package com.example.weatherapp.network.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [CacheValue::class], version = 1)
@TypeConverters(DateToLongConverter::class)
abstract class CacheDB : RoomDatabase() {

    abstract fun cacheDao(): CacheDao
}
