package com.angelwong.infoweather.data.source.local.converter

import androidx.room.TypeConverter
import com.angelwong.infoweather.data.source.local.entity.WeatherCacheEntity

class Converters {
    @TypeConverter
    fun fromCacheType(value: WeatherCacheEntity.CacheType): String {
        return value.name
    }

    @TypeConverter
    fun toCacheType(value: String): WeatherCacheEntity.CacheType {
        return WeatherCacheEntity.CacheType.valueOf(value)
    }
}