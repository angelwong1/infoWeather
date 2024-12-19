package com.angelwong.infoweather.di

import android.content.Context
import androidx.room.Room
import com.angelwong.infoweather.data.source.local.DatabaseMigrations
import com.angelwong.infoweather.data.source.local.WeatherDatabase
import com.angelwong.infoweather.data.source.local.dao.LocationDao
import com.angelwong.infoweather.data.source.local.dao.SettingsDao
import com.angelwong.infoweather.data.source.local.dao.WeatherCacheDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            WeatherDatabase.DATABASE_NAME
        )
            .addMigrations(*DatabaseMigrations.ALL_MIGRATIONS)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideLocationDao(database: WeatherDatabase): LocationDao = database.locationDao()

    @Provides
    fun provideSettingsDao(database: WeatherDatabase): SettingsDao = database.settingsDao()

    @Provides
    fun provideWeatherCacheDao(database: WeatherDatabase): WeatherCacheDao = database.weatherCacheDao()
}
