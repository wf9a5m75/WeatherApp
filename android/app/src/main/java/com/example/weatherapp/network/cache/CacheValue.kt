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
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var body: ByteArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CacheValue

        if (url != other.url) return false
        if (eTag != other.eTag) return false
        if (lastModified != other.lastModified) return false
        if (!body.contentEquals(other.body)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + eTag.hashCode()
        result = 31 * result + lastModified.hashCode()
        result = 31 * result + body.contentHashCode()
        return result
    }
}
