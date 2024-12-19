package com.angelwong.infoweather.presentation.screens.detail

import com.angelwong.infoweather.domain.model.CurrentWeather
import com.angelwong.infoweather.domain.model.DailyForecast
import com.angelwong.infoweather.domain.model.WeatherAlert

data class DetailState(
    val currentWeather: CurrentWeather? = null,
    val forecast: List<DailyForecast> = emptyList(),
    val selectedDay: DailyForecast? = null,
    val alerts: List<WeatherAlert> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface DetailEvent {
    data object RefreshData : DetailEvent
    data class SelectDay(val forecast: DailyForecast) : DetailEvent
    data object ClearError : DetailEvent
}