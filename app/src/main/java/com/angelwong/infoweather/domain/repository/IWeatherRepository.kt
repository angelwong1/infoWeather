// IWeatherRepository.kt
package com.angelwong.infoweather.domain.repository

import com.angelwong.infoweather.domain.model.CurrentWeather
import com.angelwong.infoweather.domain.model.DailyForecast
import com.angelwong.infoweather.domain.model.WeatherAlert
import kotlin.Result

interface IWeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double): Result<CurrentWeather>
    suspend fun getForecast(lat: Double, lon: Double): Result<List<DailyForecast>>
    suspend fun getWeatherAlerts(lat: Double, lon: Double): Result<List<WeatherAlert>>
}