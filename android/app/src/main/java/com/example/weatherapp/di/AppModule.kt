package com.example.weatherapp.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import com.example.weatherapp.AppViewModel
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.database.AppDatabase
import com.example.weatherapp.database.KeyValueDao
import com.example.weatherapp.database.PrefectureDao
import com.example.weatherapp.network.IWeatherApi
import com.example.weatherapp.network.cache.CacheDB
import com.example.weatherapp.network.cache.CacheDao
import com.example.weatherapp.network.cache.ETagInspector
import com.example.weatherapp.utils.NetworkMonitor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideViewModel(
        networkMonitor: NetworkMonitor,
        weatherApi: IWeatherApi,
        prefectureDao: PrefectureDao,
        keyValueDao: KeyValueDao,
    ) = AppViewModel(
        dispatcher = Dispatchers.IO,
        networkMonitor = networkMonitor,
        weatherApi = weatherApi,
        prefectureDao = prefectureDao,
        keyValueDao = keyValueDao,
    )

    @Provides
    @Singleton
    fun providePrefectureDao(
        appDatabase: AppDatabase,
    ) = appDatabase.prefectureDao()

    @Provides
    @Singleton
    fun provideKeyValueDao(
        appDatabase: AppDatabase,
    ) = appDatabase.keyValueDao()

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        connectivityManager: ConnectivityManager,
    ) = NetworkMonitor(connectivityManager)

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideWeatherApi(
        apiEntryPoint: String,
        httpClient: OkHttpClient,
    ): IWeatherApi {
        return Retrofit.Builder().baseUrl(apiEntryPoint)
            .client(httpClient)
            .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
            .build()
            .create(IWeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideApiEntryPoint(): String = "https://weather-app-8a034.web.app"

    @Provides
    @Singleton
    fun provideHttpClient(
        interceptors: List<Interceptor>,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)

        for (it in interceptors) {
            builder.addInterceptor(it)
        }
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideInterceptors(
        eTagInspector: ETagInspector?,
        httpLoggerInspector: HttpLoggingInterceptor?,
    ): List<Interceptor> {
        var results = mutableListOf<Interceptor>()
        if (eTagInspector != null) {
            results.add(eTagInspector)
        }
        if (httpLoggerInspector != null) {
            results.add(httpLoggerInspector)
        }
        return results
    }

    @Provides
    @Singleton
    fun provideETagInspector(
        cacheDao: CacheDao,
    ) = ETagInspector(cacheDao)

    @Provides
    @Singleton
    fun provideHttpLoggerInspector(): HttpLoggingInterceptor? {
        return when (BuildConfig.DEBUG) {
            true -> HttpLoggingInterceptor().also { it.level = HttpLoggingInterceptor.Level.BASIC }
            else -> null
        }
    }

    @Provides
    @Singleton
    fun provideCacheDao(
        @ApplicationContext context: Context,
    ) = Room.databaseBuilder(
        context = context,
        klass = CacheDB::class.java,
        name = "http-cache.db",
    ).build().cacheDao()

    @Provides
    @Singleton
    fun provideAppDb(
        @ApplicationContext context: Context,
    ) = Room.databaseBuilder(
        context = context,
        klass = AppDatabase::class.java,
        name = "app-database.db",
    ).build()

    @Provides
    @Singleton
    fun provideConnectivityManager(
        @ApplicationContext context: Context,
    ) = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}
