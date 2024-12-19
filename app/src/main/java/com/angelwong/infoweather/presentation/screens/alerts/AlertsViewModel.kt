package com.angelwong.infoweather.presentation.screens.alerts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angelwong.infoweather.domain.repository.IWeatherRepository
import com.angelwong.infoweather.domain.repository.ILocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val weatherRepository: IWeatherRepository,
    private val locationRepository: ILocationRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AlertsState())
    val state = _state.asStateFlow()

    init {
        // Intentar obtener la ubicación inicial desde los argumentos de navegación o estado guardado
        val initialLatitude = savedStateHandle.get<Double>("latitude")
            ?: DEFAULT_LATITUDE
        val initialLongitude = savedStateHandle.get<Double>("longitude")
            ?: DEFAULT_LONGITUDE

        // Establecer la ubicación seleccionada y cargar alertas
        _state.update { it.copy(selectedLocation = Pair(initialLatitude, initialLongitude)) }
        loadWeatherAlerts(initialLatitude, initialLongitude)
    }

    fun onEvent(event: AlertsEvent) {
        when (event) {
            is AlertsEvent.RefreshAlerts -> {
                state.value.selectedLocation?.let { (lat, lon) ->
                    loadWeatherAlerts(lat, lon)
                }
            }
            is AlertsEvent.SelectLocation -> {
                _state.update { it.copy(selectedLocation = Pair(event.latitude, event.longitude)) }
                loadWeatherAlerts(event.latitude, event.longitude)
            }
            is AlertsEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun loadWeatherAlerts(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                // Actualizar estado de carga
                _state.update { it.copy(isLoading = true, error = null) }

                // Obtener alertas meteorológicas
                val alertsResult = weatherRepository.getWeatherAlerts(latitude, longitude)

                alertsResult
                    .onSuccess { alerts ->
                        _state.update {
                            it.copy(
                                alerts = alerts,
                                isLoading = false
                            )
                        }

                        // Log para depuración
                        Timber.d("Alertas meteorológicas cargadas: ${alerts.size}")
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                error = error.message ?: "Error desconocido al cargar alertas",
                                isLoading = false
                            )
                        }

                        // Log de error
                        Timber.e(error, "Error al cargar alertas meteorológicas")
                    }
            } catch (e: Exception) {
                // Manejar cualquier excepción inesperada
                _state.update {
                    it.copy(
                        error = "Error inesperado: ${e.message}",
                        isLoading = false
                    )
                }

                Timber.e(e, "Excepción inesperada al cargar alertas")
            }
        }
    }

    companion object {
        // Coordenadas predeterminadas (Asunción, Paraguay)
        private const val DEFAULT_LATITUDE = -25.2867
        private const val DEFAULT_LONGITUDE = -57.3333
    }
}