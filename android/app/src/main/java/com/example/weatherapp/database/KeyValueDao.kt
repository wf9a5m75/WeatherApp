package com.example.weatherapp.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert

/**
 * General values
 */
@Entity
data class KeyValuePair(
    @PrimaryKey
    val id: String,
    @ColumnInfo
    val value: String
)

@Dao
interface KeyValueDao {

    @Query("SELECT * FROM KeyValuePair where id = :keyId limit 1")
    suspend fun get(keyId: String): KeyValuePair?

    @Upsert
    suspend fun put(keyValue: KeyValuePair)

//    @Upsert
//    suspend fun putAll(vararg keyValue: KeyValuePair)
}
