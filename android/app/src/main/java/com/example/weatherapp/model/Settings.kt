package com.example.weatherapp.model

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.room.*
import com.google.gson.Gson

data class Settings(
    val city: MutableState<City>
) {
    private var _db: AppDatabase? = null

    /*
     * Save setting values to DB
     */
    fun save(context: Context) {
        val gson = Gson()
        val values = arrayOf(
            SettingsValue("city_json", gson.toJson(this.city.value))
        )
        this.getDB(context).settings().insertAll(*values)
    }

    fun load(context: Context) {
        val cityValue = this.getDB(context).settings().findByKey("city_json")
        if (cityValue == null) {
            return
        }

        val gson = Gson()
        val city = gson.fromJson(cityValue.value, City::class.java)
        this.city.value = city
    }

    private fun getDB(context: Context): AppDatabase {
        if (this._db != null) {
            return this._db!!
        }

        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app-db"
        ).build()
        this._db = db
        return db
    }
}

@Entity
data class SettingsValue(
    @PrimaryKey val keyId: String,
    @ColumnInfo val value: String?
)
@Dao
interface SettingDao {
    @Query("SELECT * FROM SettingsValue")
    fun getAll(): List<SettingsValue>

    @Query("SELECT * FROM SettingsValue where keyId = :keyId")
    fun findByKey(keyId: String): SettingsValue?

    // vararg is similar to the arguments object of JS
    @Insert
    fun insertAll(vararg values: SettingsValue)

    @Delete
    fun delete(dbValue: SettingsValue)

    @Query("DELETE FROM SettingsValue where keyId = :keyId")
    fun deleteById(keyId: String)
}

@Database(entities = [SettingsValue::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settings(): SettingDao
}