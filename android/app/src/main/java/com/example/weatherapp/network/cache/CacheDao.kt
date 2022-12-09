package com.example.weatherapp.network.cache

import androidx.room.Dao
import androidx.room.Upsert

@Dao
interface CacheDao {

    @androidx.room.Query("SELECT * FROM CacheValue where url = :url")
    fun get(url: String): CacheValue?

    // vararg is similar to the arguments object of JS
    @Upsert
    fun put(value: CacheValue)

    @androidx.room.Query("DELETE FROM CacheValue where url = :url")
    fun remove(url: String)
}
