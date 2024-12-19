package com.angelwong.infoweather.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angelwong.infoweather.domain.repository.IWeatherRepository
import com.angelwong.infoweather.domain.repository.ILocationRepository
import com.angelwong.infoweather.domain.model.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val weatherRepository: IWeatherRepository,
    private val locationRepository: ILocationRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state = _state.asStateFlow()

    private var currentLocation: Location? = null

    init {
        // Obtener coordenadas de los argumentos de navegación
        val latitude = savedStateHandle.get<Float>("latitude")?.toDouble()
            ?: DEFAULT_LATITUDE
        val longitude = savedStateHandle.get<Float>("longitude")?.toDouble()
            ?: DEFAULT_LONGITUDE

        // Cargar datos de ubicación y clima
        loadLocationAndWeatherData(latitude, longitude)
    }

    fun onEvent(event: DetailEvent) {
        when (event) {
            is DetailEvent.RefreshData -> {
                currentLocation?.let {
                    loadWeatherData(it.latitude, it.longitude)
                }
            }
            is DetailEvent.SelectDay -> {
                _state.update { it.copy(selectedDay = event.forecast) }
            }
            is DetailEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun loadLocationAndWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                // Actualizar estado de carga
                _state.update { it.copy(isLoading = true, error = null) }

                // Obtener información de ubicación
                val locationResult = locationRepository.getLocationFromCoordinates(latitude, longitude)
                locationResult
                    .onSuccess { location ->
                        currentLocation = location

                        // Cargar datos del clima
                        loadWeatherData(latitude, longitude)
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                error = "Error al obtener ubicación: ${error.message}",
                                isLoading = false
                            )
                        }
                        Timber.e(error, "Error al obtener ubicación")
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error inesperado: ${e.message}",
                        isLoading = false
                    )
                }
                Timber.e(e, "Excepción inesperada al cargar datos de ubicación")
            }
        }
    }

    private fun loadWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                // Actualizar estado de carga
                _state.update { it.copy(isLoading = true, error = null) }

                // Cargar datos del clima actual
                val currentWeatherResult = weatherRepository.getCurrentWeather(latitude, longitude)
                currentWeatherResult
                    .onSuccess { weather ->
                        _state.update { it.copy(currentWeather = weather) }
                        Timber.d("Clima actual cargado: $weather")
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                error = "Error al cargar clima actual: ${error.message}",
                                isLoading = false
                            )
                        }
                        Timber.e(error, "Error al cargar clima actual")
                    }

                // Cargar pronóstico
                val forecastResult = weatherRepository.getForecast(latitude, longitude)
                forecastResult
                    .onSuccess { forecast ->
                        _state.update {
                            it.copy(
                                forecast = forecast,
                                selectedDay = forecast.firstOrNull(),
                                isLoading = false
                            )
                        }
                        Timber.d("Pronóstico cargado: ${forecast.size} días")
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                error = "Error al cargar pronóstico: ${error.message}",
                                isLoading = false
                            )
                        }
                        Timber.e(error, "Error al cargar pronóstico")
                    }

                // Cargar alertas meteorológicas
                val alertsResult = weatherRepository.getWeatherAlerts(latitude, longitude)
                alertsResult
                    .onSuccess { alerts ->
                        if (alerts.isNotEmpty()) {
                            _state.update { it.copy(alerts = alerts) }
                            Timber.d("Alertas meteorológicas cargadas: ${alerts.size}")
                        }
                    }
                    .onFailure { error ->
                        Timber.e(error, "Error al cargar alertas meteorológicas")
                        // No actualizar el estado de error para alertas, ya que son opcionales
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error inesperado: ${e.message}",
                        isLoading = false
                    )
                }
                Timber.e(e, "Excepción inesperada al cargar datos del clima")
            }
        }
    }

    companion object {
        // Coordenadas predeterminadas (Asunción, Paraguay)
        private const val DEFAULT_LATITUDE = -25.2867
        private const val DEFAULT_LONGITUDE = -57.3333
    }
}