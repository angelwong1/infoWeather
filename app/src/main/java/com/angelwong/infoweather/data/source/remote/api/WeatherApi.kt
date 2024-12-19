// WeatherApi.kt
package com.angelwong.infoweather.data.source.remote.api

import com.angelwong.infoweather.data.source.remote.model.CurrentWeatherResponse
import com.angelwong.infoweather.data.source.remote.model.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): CurrentWeatherResponse

    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("cnt") count: Int = 40
    ): ForecastResponse
}