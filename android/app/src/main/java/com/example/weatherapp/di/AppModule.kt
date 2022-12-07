package com.example.weatherapp.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import com.example.weatherapp.model.AppDatabase
import com.example.weatherapp.model.CacheDB
import com.example.weatherapp.model.IWeatherApi
import com.example.weatherapp.model.RetrofitHelper
import com.example.weatherapp.model.WeatherApi
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
    fun provideNetworkMonitor(
        connectivityManager: ConnectivityManager
    ): NetworkMonitor {
        return NetworkMonitor(connectivityManager)
    }

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: IWeatherApi): WeatherApi {
        return WeatherApi(retrofit)
    }

    @Provides
    @Singleton
    fun provideRetrofit(cacheDB: CacheDB): IWeatherApi {
        return RetrofitHelper.getInstance(cacheDB).create(IWeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCacheDB(
        @ApplicationContext context: Context
    ): CacheDB {
        return Room.databaseBuilder(
            context = context,
            klass = CacheDB::class.java,
            name = "etag-database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAppDb(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "app-database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(
        @ApplicationContext context: Context
    ): ConnectivityManager {
        return context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
    }
}