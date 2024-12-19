package com.angelwong.infoweather.presentation.screens.home

import com.angelwong.infoweather.domain.model.Location
import com.angelwong.infoweather.domain.model.CurrentWeather
import com.angelwong.infoweather.domain.model.DailyForecast

data class HomeState(
    val selectedLocation: Location? = null,
    val currentWeather: CurrentWeather? = null,
    val forecast: List<DailyForecast> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

sealed interface HomeEvent {
    data object RefreshWeather : HomeEvent
    data class LocationSelected(val latitude: Double, val longitude: Double) : HomeEvent
    data object NavigateToLocations : HomeEvent
    data object NavigateToSettings : HomeEvent
    data object ClearError : HomeEvent
}
