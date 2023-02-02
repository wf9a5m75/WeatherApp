package com.example.weatherapp.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.Upsert
import com.example.weatherapp.network.model.DailyForecast
import com.example.weatherapp.network.model.WeeklyForecast
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WeeklyForecastConverters {
    @OptIn(ExperimentalSerializationApi::class)
    @TypeConverter
    fun fromResponse(response: WeeklyForecast): String {
        return Json.encodeToString(response)
    }

    @TypeConverter
    fun toResponse(responseJson: String): WeeklyForecast {
        return Json.decodeFromString(responseJson)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @TypeConverter
    fun fromListOfDailyForecast(forecasts: List<DailyForecast>): String {
        return Json.encodeToString(forecasts)
    }

    @TypeConverter
    fun toListOfDailyForecast(jsonStr: String): List<DailyForecast> {
        return Json.decodeFromString(jsonStr)
    }
}
/**
 * WeeklyForecast DAO
 */
@Dao
interface WeeklyForecastDao {

    @Query("SELECT * FROM WeeklyForecast where cityId = :cityId")
    suspend fun find(cityId: String): WeeklyForecast?

    @Upsert
    suspend fun put(forecast: WeeklyForecast)

    @Query("DELETE FROM WeeklyForecast where cityId = :cityId")
    suspend fun remove(cityId: String)

}
