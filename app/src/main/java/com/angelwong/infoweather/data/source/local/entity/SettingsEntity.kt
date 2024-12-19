package com.angelwong.infoweather.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val useCelsius: Boolean = true,
    val isDarkMode: Boolean = false,
    val showNotifications: Boolean = true,
    val languageCode: String = "es",
    val theme: String = "system"
)