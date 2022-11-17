package com.example.weatherapp.model

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val PREF_ID = stringPreferencesKey("id")
val PREF_TYPE = stringPreferencesKey("type")
val PREF_VALUE = stringPreferencesKey("value")

suspend fun savePrefCity(context: Context, city: City) {



    context.dataStore.edit { pref ->
        pref[PREF_ID] = "pref_city"
        pref[PREF_TYPE] = "city"
        pref[PREF_VALUE] = Json.encodeToString(city)
    }
}

suspend fun loadPrefCity(context: Context): City {
    val result = runBlocking {
        context.dataStore.data
//            .filter {
//                pref -> pref[PREF_ID] == "pref_city"
//            }
            .map { pref ->
                when(pref[PREF_ID]) {
                    "pref_city" -> Json.decodeFromString<City>(pref[PREF_VALUE].toString())
                    else -> null
                }
        }
    }
    return result.first() ?: City("", "")
}

//@Entity
//data class SettingsValue(
//    @PrimaryKey val keyId: String,
//    @ColumnInfo val value: String?
//)
//@Dao
//interface SettingDao {
//    @Query("SELECT * FROM SettingsValue")
//    fun getAll(): List<SettingsValue>
//
//    @Query("SELECT * FROM SettingsValue where keyId = :keyId")
//    fun findByKey(keyId: String): SettingsValue?
//
//    // vararg is similar to the arguments object of JS
//    @Upsert
//    fun insertAll(vararg values: SettingsValue)
//
//    @Delete
//    fun delete(dbValue: SettingsValue)
//
//    @Query("DELETE FROM SettingsValue where keyId = :keyId")
//    fun deleteById(keyId: String)
//}
//
//@Database(entities = [SettingsValue::class], version = 1)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun settings(): SettingDao
//}