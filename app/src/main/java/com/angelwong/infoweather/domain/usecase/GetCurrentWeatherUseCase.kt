package com.angelwong.infoweather.domain.usecase

import com.angelwong.infoweather.di.IoDispatcher
import com.angelwong.infoweather.domain.model.CurrentWeather
import com.angelwong.infoweather.domain.repository.IWeatherRepository
import com.angelwong.infoweather.domain.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val weatherRepository: IWeatherRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(lat: Double, lon: Double): kotlin.Result<CurrentWeather> =
        withContext(dispatcher) {
            weatherRepository.getCurrentWeather(lat, lon)
        }
}