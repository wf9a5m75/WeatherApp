package com.example.weatherapp.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.Upsert
import com.example.weatherapp.network.model.City
import com.example.weatherapp.network.model.Prefecture
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PrefectureConverters {
    @OptIn(ExperimentalSerializationApi::class)
    @TypeConverter
    fun fromList(cities: List<City>): String {
        return Json.encodeToString(cities)
    }

    @TypeConverter
    fun toList(citiesJson: String): List<City> {
        return Json.decodeFromString(citiesJson)
    }
}

/**
 * Prefecture DAO
 */
@Dao
interface PrefectureDao {
    @Query("SELECT * FROM Prefecture")
    suspend fun getAll(): List<Prefecture>

    @Query("SELECT * FROM Prefecture where id = :keyId")
    suspend fun find(keyId: String): Prefecture?

    // vararg is similar to the arguments object of JS
    @Upsert
    suspend fun insertAll(vararg values: Prefecture)

    @Query("DELETE FROM Prefecture")
    suspend fun clear()

    @Query("SELECT count(*) FROM Prefecture")
    suspend fun count(): Int
}
