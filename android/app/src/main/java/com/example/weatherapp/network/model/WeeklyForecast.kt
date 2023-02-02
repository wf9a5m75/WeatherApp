package com.example.weatherapp.network.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class WeeklyForecast(
    @PrimaryKey
    var cityId: String = "",

    @ColumnInfo
    val last_update: String,

    @ColumnInfo
    val overall: String,

    @ColumnInfo
    val forecasts: List<DailyForecast>,
)
