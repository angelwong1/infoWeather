package com.angelwong.infoweather.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angelwong.infoweather.domain.repository.IWeatherRepository
import com.angelwong.infoweather.domain.repository.ILocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: IWeatherRepository,
    private val locationRepository: ILocationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private var currentLatitude: Double = DEFAULT_LATITUDE
    private var currentLongitude: Double = DEFAULT_LONGITUDE

    init {
        loadInitialData()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.RefreshWeather -> refreshWeatherData()
            is HomeEvent.LocationSelected -> updateLocation(event.latitude, event.longitude)
            is HomeEvent.ClearError -> clearError()
            else -> {}
        }
    }

    private fun loadInitialData() {
        loadWeatherData(currentLatitude, currentLongitude)
    }

    private fun loadWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // Cargar datos del clima actual
                weatherRepository.getCurrentWeather(lat, lon)
                    .onSuccess { weather ->
                        _state.update { it.copy(currentWeather = weather) }
                    }
                    .onFailure { error ->
                        _state.update { it.copy(error = error.message) }
                    }

                // Cargar pronóstico
                weatherRepository.getForecast(lat, lon)
                    .onSuccess { forecast ->
                        _state.update { it.copy(forecast = forecast) }
                    }
                    .onFailure { error ->
                        _state.update { it.copy(error = error.message) }
                    }

                // Cargar información de la ubicación
                locationRepository.getLocationFromCoordinates(lat, lon)
                    .onSuccess { location ->
                        _state.update { it.copy(selectedLocation = location) }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "Error desconocido")
                }
            } finally {
                _state.update { it.copy(isLoading = false, isRefreshing = false) }
            }
        }
    }

    private fun updateLocation(lat: Double, lon: Double) {
        currentLatitude = lat
        currentLongitude = lon
        loadWeatherData(lat, lon)
    }

    private fun refreshWeatherData() {
        _state.update { it.copy(isRefreshing = true) }
        loadWeatherData(currentLatitude, currentLongitude)
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun getCurrentLatitude() = currentLatitude
    fun getCurrentLongitude() = currentLongitude

    companion object {
        private const val DEFAULT_LATITUDE = -25.2867
        private const val DEFAULT_LONGITUDE = -57.3333
    }
}