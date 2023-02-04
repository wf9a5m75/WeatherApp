package com.example.weatherapp.network.cache

import androidx.room.TypeConverter
import java.util.Date

class DateToLongConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}
