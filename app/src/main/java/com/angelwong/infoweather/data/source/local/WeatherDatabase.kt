package com.angelwong.infoweather.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.angelwong.infoweather.data.source.local.converter.Converters
import com.angelwong.infoweather.data.source.local.dao.LocationDao
import com.angelwong.infoweather.data.source.local.dao.SettingsDao
import com.angelwong.infoweather.data.source.local.dao.WeatherCacheDao
import com.angelwong.infoweather.data.source.local.entity.LocationEntity
import com.angelwong.infoweather.data.source.local.entity.SettingsEntity
import com.angelwong.infoweather.data.source.local.entity.WeatherCacheEntity

@Database(
    entities = [
        LocationEntity::class,
        SettingsEntity::class,
        WeatherCacheEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun settingsDao(): SettingsDao
    abstract fun weatherCacheDao(): WeatherCacheDao

    companion object {
        const val DATABASE_NAME = "weather_database"
    }
}