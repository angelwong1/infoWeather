package com.angelwong.infoweather.data.repository

import com.angelwong.infoweather.data.source.local.dao.SettingsDao
import com.angelwong.infoweather.data.source.local.entity.SettingsEntity
import com.angelwong.infoweather.di.IoDispatcher
import com.angelwong.infoweather.domain.model.Settings
import com.angelwong.infoweather.domain.repository.ISettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ISettingsRepository {

    override fun getSettings(): Flow<Settings> {
        return settingsDao.getSettings()
            .map { settingsEntity ->
                settingsEntity?.toDomain() ?: Settings()
            }
    }

    override suspend fun updateTemperatureUnit(useCelsius: Boolean): Result<Unit> =
        withContext(dispatcher) {
            runCatching {
                ensureSettingsExist()
                settingsDao.updateTemperatureUnit(useCelsius)
                Timber.d("Unidad de temperatura actualizada: useCelsius = $useCelsius")
            }
        }

    override suspend fun updateTheme(isDarkMode: Boolean): Result<Unit> =
        withContext(dispatcher) {
            runCatching {
                ensureSettingsExist()
                settingsDao.updateTheme(isDarkMode)
                Timber.d("Tema actualizado: isDarkMode = $isDarkMode")
            }
        }

    override suspend fun updateNotifications(enabled: Boolean): Result<Unit> =
        withContext(dispatcher) {
            runCatching {
                ensureSettingsExist()
                settingsDao.updateNotifications(enabled)
                Timber.d("Notificaciones actualizadas: enabled = $enabled")
            }
        }

    override suspend fun updateLanguage(languageCode: String): Result<Unit> =
        withContext(dispatcher) {
            runCatching {
                ensureSettingsExist()
                settingsDao.updateLanguage(languageCode)
                Timber.d("Idioma actualizado: languageCode = $languageCode")
            }
        }

    private suspend fun ensureSettingsExist() {
        if (settingsDao.getSettingsSync() == null) {
            settingsDao.insertSettings(
                SettingsEntity(
                    useCelsius = true,
                    isDarkMode = false,
                    showNotifications = true,
                    languageCode = "es",
                    theme = "system"
                )
            )
            Timber.d("Configuraciones por defecto creadas")
        }
    }

    private fun SettingsEntity.toDomain() = Settings(
        useCelsius = useCelsius,
        isDarkMode = isDarkMode,
        showNotifications = showNotifications,
        languageCode = languageCode,
        theme = when (theme.lowercase()) {
            "dark" -> Settings.Theme.DARK
            "light" -> Settings.Theme.LIGHT
            else -> Settings.Theme.SYSTEM
        }
    )
}