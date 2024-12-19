package com.angelwong.infoweather.domain.model

data class CurrentWeather(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val description: String,
    val iconCode: String,
    val timestamp: Long,
    val cityName: String = "",
    val countryCode: String = ""
) {
    init {
        require(temperature >= -273.15) { "La temperatura no puede ser inferior al cero absoluto" }
        require(humidity in 0..100) { "La humedad debe estar entre 0 y 100%" }
        require(windSpeed >= 0) { "La velocidad del viento no puede ser negativa" }
    }

    fun getTemperatureFormatted(useCelsius: Boolean): String {
        return if (useCelsius) {
            "${temperature.toInt()}°C"
        } else {
            "${(temperature * 9/5 + 32).toInt()}°F"
        }
    }
}

data class DailyForecast(
    val date: Long,
    val maxTemp: Double,
    val minTemp: Double,
    val humidity: Int,
    val description: String,
    val iconCode: String,
    val precipitation: Int
) {
    init {
        require(maxTemp >= minTemp) { "La temperatura máxima debe ser mayor o igual a la mínima" }
        require(humidity in 0..100) { "La humedad debe estar entre 0 y 100%" }
        require(precipitation in 0..100) { "La probabilidad de precipitación debe estar entre 0 y 100%" }
    }
}

data class WeatherAlert(
    val id: String,
    val senderName: String,
    val event: String,
    val startTime: Long,
    val endTime: Long,
    val description: String,
    val severity: Severity
) {
    enum class Severity {
        LOW, MEDIUM, HIGH, EXTREME
    }
}