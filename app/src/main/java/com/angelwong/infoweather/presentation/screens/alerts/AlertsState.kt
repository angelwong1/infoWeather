package com.angelwong.infoweather.presentation.screens.alerts

import com.angelwong.infoweather.domain.model.WeatherAlert

data class AlertsState(
    val alerts: List<WeatherAlert> = emptyList(),
    val isLoading: Boolean = false,
    val selectedLocation: Pair<Double, Double>? = null,
    val error: String? = null
)

sealed interface AlertsEvent {
    data object RefreshAlerts : AlertsEvent
    data class SelectLocation(val latitude: Double, val longitude: Double) : AlertsEvent
    data object ClearError : AlertsEvent
}

sealed interface AlertsAction {
    data class ShowError(val message: String) : AlertsAction
    data object NavigateBack : AlertsAction
}