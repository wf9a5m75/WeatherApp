package com.example.weatherapp.model

import androidx.room.*
import kotlinx.coroutines.flow.*

/**
 * Location DAO
 */
@Entity
data class LocationValue(
    @PrimaryKey val id: String,
    @ColumnInfo val sortOrder: Int,
    @ColumnInfo val kind: String,
    @ColumnInfo val value: String
)

@Dao
interface LocationsDao {
    @Query("SELECT * FROM LocationValue order by sortOrder")
    fun getAll(): List<LocationValue>

    @Query("SELECT * FROM LocationValue where id = :keyId")
    fun findByKey(keyId: String): LocationValue?

    // vararg is similar to the arguments object of JS
    @Upsert
    fun insertAll(vararg values: LocationValue)

    @Query("DELETE FROM LocationValue")
    fun clear()

    @Query("SELECT count(*) FROM LocationValue")
    fun count(): Int
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
    fun get(keyId: String): KeyValuePair?

    @Upsert
    fun put(keyValue: KeyValuePair)

    @Upsert
    fun putAll(vararg keyValue: KeyValuePair)
}

@Database(entities = [LocationValue::class, KeyValuePair::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locaitonDao(): LocationsDao

    abstract fun keyValueDao(): KeyValueDao
}
