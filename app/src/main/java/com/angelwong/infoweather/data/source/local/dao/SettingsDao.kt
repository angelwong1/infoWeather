package com.angelwong.infoweather.data.source.local.dao

import androidx.room.*
import com.angelwong.infoweather.data.source.local.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<SettingsEntity?>

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun getSettingsSync(): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SettingsEntity)

    @Query("UPDATE settings SET useCelsius = :useCelsius WHERE id = 1")
    suspend fun updateTemperatureUnit(useCelsius: Boolean)

    @Query("UPDATE settings SET isDarkMode = :isDarkMode WHERE id = 1")
    suspend fun updateTheme(isDarkMode: Boolean)

    @Query("UPDATE settings SET showNotifications = :enabled WHERE id = 1")
    suspend fun updateNotifications(enabled: Boolean)

    @Query("UPDATE settings SET languageCode = :languageCode WHERE id = 1")
    suspend fun updateLanguage(languageCode: String)
}
