package com.example.weatherapp.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
    val result =
        context.dataStore.data
//            .filter {
//                pref -> pref[PREF_ID] == "pref_city"
//            }
            .map { pref ->
                when (pref[PREF_ID]) {
                    "pref_city" -> Json.decodeFromString<City>(pref[PREF_VALUE].toString())
                    else -> null
                }
            }

    return result.first() ?: City("", "")
}

@Entity
data class LocationValue(
    @PrimaryKey val id: String,
    @ColumnInfo val value: String?
)

@Dao
interface LocationsDao {
    @Query("SELECT * FROM SettingsValue")
    fun getAll(): List<LocationValue>

    @Query("SELECT * FROM SettingsValue where keyId = :keyId")
    fun findByKey(keyId: String): LocationValue?

    // vararg is similar to the arguments object of JS
    @Upsert
    fun insertAll(vararg values: LocationValue)

    @Query("DELETE FROM Settings")
    fun clear()
}

@Database(entities = [LocationValue::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locaitonDao(): LocationsDao
}

suspend fun saveLocations(context: Context, locations: List<Prefecture>) {
    val db: AppDatabase = Room.databaseBuilder(
        context = context,
        klass = AppDatabase::class.java,
        name = "app-database"
    ).build()

    val locationDao = db.locaitonDao()

    locationDao.clear()
    locationDao.insertAll()

    db.close()
}
