package com.angelwong.infoweather.domain.usecase

import com.angelwong.infoweather.di.IoDispatcher
import com.angelwong.infoweather.domain.model.DailyForecast
import com.angelwong.infoweather.domain.repository.IWeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(
    private val weatherRepository: IWeatherRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<List<DailyForecast>> =
        withContext(dispatcher) {
            weatherRepository.getForecast(lat, lon)
        }
}