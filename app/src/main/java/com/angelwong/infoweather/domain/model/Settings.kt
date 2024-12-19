package com.angelwong.infoweather.domain.model

data class Settings(
    val useCelsius: Boolean = true,
    val isDarkMode: Boolean = false,
    val showNotifications: Boolean = true,
    val languageCode: String = "es",
    val theme: Theme = Theme.SYSTEM
) {
    enum class Theme {
        LIGHT, DARK, SYSTEM
    }

    companion object {
        const val DEFAULT_UPDATE_INTERVAL = 30 * 60 * 1000L // 30 minutos
    }
}