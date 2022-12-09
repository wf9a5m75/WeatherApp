package com.example.weatherapp.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import com.example.weatherapp.database.AppDatabase
import com.example.weatherapp.AppViewModel
import com.example.weatherapp.database.KeyValueDao
import com.example.weatherapp.database.PrefectureDao
import com.example.weatherapp.network.IWeatherApi
import com.example.weatherapp.network.RetrofitHelper
import com.example.weatherapp.network.cache.CacheDB
import com.example.weatherapp.network.cache.CacheDao
import com.example.weatherapp.utils.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
        keyValueDao: KeyValueDao
    ) = AppViewModel(
        networkMonitor = networkMonitor,
        weatherApi = weatherApi,
        prefectureDao = prefectureDao,
        keyValueDao = keyValueDao
    )
    @Provides
    @Singleton
    fun providePrefectureDao(
        appDatabase: AppDatabase
    ) = appDatabase.prefectureDao()

    @Provides
    @Singleton
    fun provideKeyValueDao(
        appDatabase: AppDatabase
    ) = appDatabase.keyValueDao()

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        connectivityManager: ConnectivityManager
    ) = NetworkMonitor(connectivityManager)

    @Provides
    @Singleton
    fun provideWeatherApi(cacheDao: CacheDao): IWeatherApi = RetrofitHelper
        .getInstance(cacheDao)
        .create(IWeatherApi::class.java)

    @Provides
    @Singleton
    fun provideCacheDao(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context = context,
        klass = CacheDB::class.java,
        name = "etag-database"
    ).build().cacheDao()

    @Provides
    @Singleton
    fun provideAppDb(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context = context,
        klass = AppDatabase::class.java,
        name = "app-database"
    ).build()

    @Provides
    @Singleton
    fun provideConnectivityManager(
        @ApplicationContext context: Context
    ) = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

}
