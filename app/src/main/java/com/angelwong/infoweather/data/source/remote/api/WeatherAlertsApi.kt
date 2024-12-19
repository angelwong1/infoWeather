package com.angelwong.infoweather.data.source.remote.api

import com.angelwong.infoweather.data.source.remote.model.AlertResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAlertsApi {
    /**
     * Obtiene alertas meteorológicas para una ubicación
     */
    @GET("onecall")
    suspend fun getWeatherAlerts(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "minutely,hourly",
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "es"
    ): AlertResponse
}