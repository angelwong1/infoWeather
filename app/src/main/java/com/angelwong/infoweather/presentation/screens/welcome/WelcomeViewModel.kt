package com.angelwong.infoweather.presentation.screens.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angelwong.infoweather.domain.model.Location
import com.angelwong.infoweather.domain.repository.ILocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val locationRepository: ILocationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WelcomeState())
    val state = _state.asStateFlow()

    fun onEvent(event: WelcomeEvent) {
        when (event) {
            is WelcomeEvent.NextStep -> navigateToNextStep()
            is WelcomeEvent.PreviousStep -> navigateToPreviousStep()
            is WelcomeEvent.SelectLocation -> selectLocation(event.location)
            is WelcomeEvent.ClearError -> clearError()
        }
    }

    private fun navigateToNextStep() {
        _state.update { currentState ->
            when (currentState.currentStep) {
                WelcomeStep.INTRO -> currentState.copy(currentStep = WelcomeStep.LOCATION_PERMISSION)
                WelcomeStep.LOCATION_PERMISSION -> currentState.copy(currentStep = WelcomeStep.SELECT_LOCATION)
                WelcomeStep.SELECT_LOCATION -> currentState
            }
        }
    }

    private fun navigateToPreviousStep() {
        _state.update { currentState ->
            when (currentState.currentStep) {
                WelcomeStep.INTRO -> currentState
                WelcomeStep.LOCATION_PERMISSION -> currentState.copy(currentStep = WelcomeStep.INTRO)
                WelcomeStep.SELECT_LOCATION -> currentState.copy(currentStep = WelcomeStep.LOCATION_PERMISSION)
            }
        }
    }

    private fun selectLocation(location: Location) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Guardar la ubicación seleccionada
                locationRepository.saveLocation(location)
                    .onSuccess {
                        _state.update {
                            it.copy(
                                selectedLocation = location,
                                isLoading = false
                            )
                        }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                error = "Error al guardar ubicación: ${error.message}",
                                isLoading = false
                            )
                        }
                        Timber.e(error, "Error al guardar ubicación")
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error inesperado: ${e.message}",
                        isLoading = false
                    )
                }
                Timber.e(e, "Excepción al seleccionar ubicación")
            }
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}