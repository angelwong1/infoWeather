package com.angelwong.infoweather.data.source.local.dao

import androidx.room.*
import com.angelwong.infoweather.data.source.local.entity.WeatherCacheEntity

@Dao
interface WeatherCacheDao {
    @Query("SELECT * FROM weather_cache WHERE id = :id AND type = :type")
    suspend fun getCacheById(id: String, type: WeatherCacheEntity.CacheType): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cache: WeatherCacheEntity)

    @Query("DELETE FROM weather_cache WHERE expirationTime < :currentTime")
    suspend fun deleteExpiredCache(currentTime: Long = System.currentTimeMillis())
}