// WeatherRepository.kt
package com.angelwong.infoweather.data.repository

import com.angelwong.infoweather.data.source.local.dao.WeatherCacheDao
import com.angelwong.infoweather.data.source.local.entity.WeatherCacheEntity
import com.angelwong.infoweather.data.source.remote.api.WeatherApi
import com.angelwong.infoweather.data.source.remote.api.WeatherAlertsApi
import com.angelwong.infoweather.data.source.remote.model.CurrentWeatherResponse
import com.angelwong.infoweather.data.source.remote.model.ForecastResponse
import com.angelwong.infoweather.data.source.remote.model.AlertResponse
import com.angelwong.infoweather.data.util.NetworkManager
import com.angelwong.infoweather.di.IoDispatcher
import com.angelwong.infoweather.domain.model.CurrentWeather
import com.angelwong.infoweather.domain.model.DailyForecast
import com.angelwong.infoweather.domain.model.WeatherAlert
import com.angelwong.infoweather.domain.repository.IWeatherRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi,
    private val weatherAlertsApi: WeatherAlertsApi,
    private val weatherCacheDao: WeatherCacheDao,
    private val networkManager: NetworkManager,
    private val gson: Gson,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : IWeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): Result<CurrentWeather> =
        withContext(dispatcher) {
            runCatching {
                val cacheKey = generateCacheKey(lat, lon, WeatherCacheEntity.CacheType.CURRENT_WEATHER)
                val cached = weatherCacheDao.getCacheById(cacheKey, WeatherCacheEntity.CacheType.CURRENT_WEATHER)

                if (cached != null && !isCacheExpired(cached.timestamp)) {
                    gson.fromJson(cached.data, CurrentWeather::class.java)
                } else {
                    val response = weatherApi.getCurrentWeather(lat, lon)
                    val weather = response.toDomain()

                    // Cache the result
                    weatherCacheDao.insert(WeatherCacheEntity(
                        id = cacheKey,
                        type = WeatherCacheEntity.CacheType.CURRENT_WEATHER,
                        data = gson.toJson(weather)
                    ))

                    weather
                }
            }
        }

    override suspend fun getForecast(lat: Double, lon: Double): Result<List<DailyForecast>> =
        withContext(dispatcher) {
            runCatching {
                val cacheKey = generateCacheKey(lat, lon, WeatherCacheEntity.CacheType.FORECAST)
                val cached = weatherCacheDao.getCacheById(cacheKey, WeatherCacheEntity.CacheType.FORECAST)

                if (cached != null && !isCacheExpired(cached.timestamp)) {
                    val type = object : TypeToken<List<DailyForecast>>() {}.type
                    gson.fromJson<List<DailyForecast>>(cached.data, type)
                } else {
                    val response = weatherApi.getForecast(lat, lon)
                    val forecasts = response.toDailyForecasts()

                    // Cache the result
                    weatherCacheDao.insert(WeatherCacheEntity(
                        id = cacheKey,
                        type = WeatherCacheEntity.CacheType.FORECAST,
                        data = gson.toJson(forecasts)
                    ))

                    forecasts
                }
            }
        }

    override suspend fun getWeatherAlerts(lat: Double, lon: Double): Result<List<WeatherAlert>> =
        withContext(dispatcher) {
            runCatching {
                val response = weatherAlertsApi.getWeatherAlerts(lat, lon)
                response.alerts?.map { it.toDomain() } ?: emptyList()
            }
        }

    private fun generateCacheKey(lat: Double, lon: Double, type: WeatherCacheEntity.CacheType): String =
        "${type.name.lowercase()}_${lat}_${lon}"

    private fun isCacheExpired(timestamp: Long): Boolean =
        (System.currentTimeMillis() - timestamp) > CACHE_DURATION

    private fun CurrentWeatherResponse.toDomain() = CurrentWeather(
        temperature = mainInfo.temperature,
        feelsLike = mainInfo.feelsLike,
        humidity = mainInfo.humidity,
        windSpeed = wind.speed,
        description = weather.firstOrNull()?.description ?: "",
        iconCode = weather.firstOrNull()?.iconCode ?: "",
        timestamp = timestamp,
        cityName = cityName,
        countryCode = sys.country
    )

    private fun ForecastResponse.toDailyForecasts(): List<DailyForecast> {
        return forecastList.groupBy {
            it.timestamp / (24 * 60 * 60) // Agrupar por día
        }.map { (_, forecasts) ->
            val dayForecast = forecasts.first()
            DailyForecast(
                date = dayForecast.timestamp * 1000,
                maxTemp = forecasts.maxOf { it.mainInfo.temperature },
                minTemp = forecasts.minOf { it.mainInfo.temperature },
                humidity = forecasts.map { it.mainInfo.humidity }.average().toInt(),
                description = dayForecast.weather.firstOrNull()?.description ?: "",
                iconCode = dayForecast.weather.firstOrNull()?.iconCode ?: "",
                precipitation = calculatePrecipitationProbability(forecasts)
            )
        }
    }

    private fun calculatePrecipitationProbability(forecasts: List<ForecastResponse.ForecastItem>): Int {
        return forecasts
            .map { it.mainInfo.humidity }
            .average()
            .let { humidity ->
                when {
                    humidity > 80 -> 75
                    humidity > 70 -> 50
                    humidity > 60 -> 25
                    else -> 0
                }
            }.toInt()
    }

    private fun AlertResponse.Alert.toDomain() = WeatherAlert(
        id = "${start}_${end}",
        senderName = senderName,
        event = event,
        startTime = start,
        endTime = end,
        description = description,
        severity = when {
            event.contains("extreme", ignoreCase = true) -> WeatherAlert.Severity.EXTREME
            event.contains("severe", ignoreCase = true) -> WeatherAlert.Severity.HIGH
            event.contains("moderate", ignoreCase = true) -> WeatherAlert.Severity.MEDIUM
            else -> WeatherAlert.Severity.LOW
        }
    )

    companion object {
        private const val CACHE_DURATION = 30 * 60 * 1000L // 30 minutos
    }
}