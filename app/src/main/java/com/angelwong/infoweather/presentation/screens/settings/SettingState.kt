package com.angelwong.infoweather.presentation.screens.settings

import com.angelwong.infoweather.domain.model.Settings

data class SettingsState(
    val settings: Settings = Settings(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface SettingsEvent {
    data class ToggleTemperatureUnit(val useCelsius: Boolean) : SettingsEvent
    data class ToggleDarkMode(val isDarkMode: Boolean) : SettingsEvent
    data class ToggleNotifications(val enabled: Boolean) : SettingsEvent
    data class UpdateLanguage(val languageCode: String) : SettingsEvent
    data object ClearError : SettingsEvent
}

sealed interface SettingsAction {
    data class ShowError(val message: String) : SettingsAction
    data object NavigateBack : SettingsAction
}