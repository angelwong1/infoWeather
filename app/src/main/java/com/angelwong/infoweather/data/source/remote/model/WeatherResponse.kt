package com.angelwong.infoweather.data.source.remote.model

import com.google.gson.annotations.SerializedName

// Respuesta del clima actual
data class CurrentWeatherResponse(
    @SerializedName("main") val mainInfo: MainInfo,
    @SerializedName("wind") val wind: WindInfo,
    @SerializedName("weather") val weather: List<WeatherDescription>,
    @SerializedName("dt") val timestamp: Long,
    @SerializedName("name") val cityName: String,
    @SerializedName("sys") val sys: SystemInfo
) {
    data class MainInfo(
        @SerializedName("temp") val temperature: Double,
        @SerializedName("feels_like") val feelsLike: Double,
        @SerializedName("humidity") val humidity: Int,
        @SerializedName("pressure") val pressure: Int
    )

    data class WindInfo(
        @SerializedName("speed") val speed: Double
    )

    data class WeatherDescription(
        @SerializedName("description") val description: String,
        @SerializedName("icon") val iconCode: String
    )

    data class SystemInfo(
        @SerializedName("country") val country: String
    )
}

// Respuesta del pronóstico
data class ForecastResponse(
    @SerializedName("list") val forecastList: List<ForecastItem>
) {
    data class ForecastItem(
        @SerializedName("dt") val timestamp: Long,
        @SerializedName("main") val mainInfo: MainInfo,
        @SerializedName("weather") val weather: List<WeatherDescription>
    ) {
        data class MainInfo(
            @SerializedName("temp") val temperature: Double,
            @SerializedName("temp_max") val maxTemp: Double,
            @SerializedName("temp_min") val minTemp: Double,
            @SerializedName("humidity") val humidity: Int
        )

        data class WeatherDescription(
            @SerializedName("description") val description: String,
            @SerializedName("icon") val iconCode: String
        )
    }
}
