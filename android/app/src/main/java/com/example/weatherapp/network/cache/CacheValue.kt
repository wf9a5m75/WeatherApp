package com.example.weatherapp.network.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CacheValue(
    @PrimaryKey
    var url: String,
    @ColumnInfo
    var eTag: String,
    @ColumnInfo
    var lastModified: String,
)
