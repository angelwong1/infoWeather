package com.angelwong.infoweather.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey
    val id: String,
    val type: CacheType,
    val data: String,
    val timestamp: Long = System.currentTimeMillis(),
    val expirationTime: Long = timestamp + CACHE_DURATION
) {
    enum class CacheType {
        CURRENT_WEATHER,
        FORECAST,
        ALERTS
    }

    companion object {
        const val CACHE_DURATION = 30 * 60 * 1000L // 30 minutos
    }
}