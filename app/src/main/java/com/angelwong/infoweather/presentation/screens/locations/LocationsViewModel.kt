package com.angelwong.infoweather.presentation.screens.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angelwong.infoweather.domain.model.Location
import com.angelwong.infoweather.domain.repository.ILocationRepository
import com.angelwong.infoweather.domain.usecase.SearchLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(
    private val locationRepository: ILocationRepository,
    private val searchLocationsUseCase: SearchLocationsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LocationsState())
    val state = _state.asStateFlow()

    init {
        loadSavedLocations()
    }

    fun onEvent(event: LocationsEvent) {
        when (event) {
            is LocationsEvent.AddLocation -> addLocation(event.query)
            is LocationsEvent.DeleteLocation -> deleteLocation(event.location)
            is LocationsEvent.ToggleFavorite -> toggleFavorite(event.location)
            is LocationsEvent.ClearError -> clearError()
        }
    }

    private fun loadSavedLocations() {
        viewModelScope.launch {
            try {
                locationRepository.getSavedLocations()
                    .collect { locations ->
                        _state.update {
                            it.copy(
                                locations = locations.sortedWith(
                                    compareByDescending<Location> { it.isFavorite }
                                        .thenBy { it.name }
                                )
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Error al cargar ubicaciones: ${e.message}")
                }
                Timber.e(e, "Error al cargar ubicaciones")
            }
        }
    }

    private fun addLocation(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Buscar ubicaciones con el query
                val searchResult = searchLocationsUseCase(query)

                searchResult
                    .onSuccess { locations ->
                        if (locations.isNotEmpty()) {
                            // Tomar la primera ubicación encontrada
                            val locationToSave = locations.first()

                            // Guardar ubicación
                            locationRepository.saveLocation(locationToSave)
                                .onSuccess {
                                    _state.update {
                                        it.copy(
                                            isLoading = false,
                                            error = null
                                        )
                                    }
                                }
                                .onFailure { error ->
                                    _state.update {
                                        it.copy(
                                            isLoading = false,
                                            error = "Error al guardar ubicación: ${error.message}"
                                        )
                                    }
                                }
                        } else {
                            // No se encontraron ubicaciones
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "No se encontraron ubicaciones para: $query"
                                )
                            }
                        }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Error al buscar ubicación: ${error.message}"
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Error inesperado: ${e.message}"
                    )
                }
                Timber.e(e, "Error al añadir ubicación")
            }
        }
    }

    private fun deleteLocation(location: Location) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            locationRepository.removeLocation(location)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Error al eliminar ubicación: ${error.message}"
                        )
                    }
                    Timber.e(error, "Error al eliminar ubicación")
                }
        }
    }

    private fun toggleFavorite(location: Location) {
        viewModelScope.launch {
            try {
                // Crear una nueva instancia de Location con el estado de favorito invertido
                val updatedLocation = location.copy(isFavorite = !location.isFavorite)

                // Guardar la ubicación actualizada
                locationRepository.saveLocation(updatedLocation)
                    .onSuccess {
                        // No es necesario actualizar el estado aquí,
                        // ya que el flujo de getSavedLocations lo manejará
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                error = "Error al actualizar favorito: ${error.message}"
                            )
                        }
                        Timber.e(error, "Error al actualizar favorito")
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error inesperado: ${e.message}"
                    )
                }
                Timber.e(e, "Error al cambiar estado de favorito")
            }
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}