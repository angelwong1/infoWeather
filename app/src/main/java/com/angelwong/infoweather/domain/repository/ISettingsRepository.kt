package com.angelwong.infoweather.domain.repository

import com.angelwong.infoweather.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlin.Result

interface ISettingsRepository {
    /**
     * Obtiene el flujo de configuraciones actuales
     */
    fun getSettings(): Flow<Settings>

    /**
     * Actualiza la unidad de temperatura
     * @param useCelsius true para usar Celsius, false para Fahrenheit
     */
    suspend fun updateTemperatureUnit(useCelsius: Boolean): Result<Unit>

    /**
     * Actualiza el tema de la aplicación
     * @param isDarkMode true para tema oscuro, false para tema claro
     */
    suspend fun updateTheme(isDarkMode: Boolean): Result<Unit>

    /**
     * Actualiza las preferencias de notificaciones
     * @param enabled true para activar notificaciones, false para desactivarlas
     */
    suspend fun updateNotifications(enabled: Boolean): Result<Unit>

    /**
     * Actualiza el idioma de la aplicación
     * @param languageCode código de idioma (ej: "es", "en")
     */
    suspend fun updateLanguage(languageCode: String): Result<Unit>
}