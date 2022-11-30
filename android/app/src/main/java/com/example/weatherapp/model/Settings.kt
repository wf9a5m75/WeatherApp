package com.example.weatherapp.model

import androidx.room.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @OptIn(ExperimentalSerializationApi::class)
    @TypeConverter
    public fun fromList(cities: List<City>): String {
        return Json.encodeToString(cities)
    }

    @TypeConverter
    public fun toList(citiesJson: String): List<City> {
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
    suspend fun findByKey(keyId: String): Prefecture?

    // vararg is similar to the arguments object of JS
    @Upsert
    suspend fun insertAll(vararg values: Prefecture)

    @Query("DELETE FROM Prefecture")
    suspend fun clear()

    @Query("SELECT count(*) FROM Prefecture")
    suspend fun count(): Int
}

/**
 * General values
 */
@Entity
data class KeyValuePair(
    @PrimaryKey val id: String,
    @ColumnInfo val value: String
)

@Dao
interface KeyValueDao {

    @Query("SELECT * FROM KeyValuePair where id = :keyId limit 1")
    suspend fun get(keyId: String): KeyValuePair?

    @Upsert
    suspend fun put(keyValue: KeyValuePair)

    @Upsert
    suspend fun putAll(vararg keyValue: KeyValuePair)
}

@Database(entities = [Prefecture::class, KeyValuePair::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prefectureDao(): PrefectureDao

    abstract fun keyValueDao(): KeyValueDao
}
