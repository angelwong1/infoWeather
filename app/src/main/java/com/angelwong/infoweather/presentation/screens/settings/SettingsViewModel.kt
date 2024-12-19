package com.angelwong.infoweather.presentation.screens.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angelwong.infoweather.domain.repository.ISettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: ISettingsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        loadSettings()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ToggleTemperatureUnit -> updateTemperatureUnit(event.useCelsius)
            is SettingsEvent.ToggleDarkMode -> updateTheme(event.isDarkMode)
            is SettingsEvent.ToggleNotifications -> updateNotifications(event.enabled)
            is SettingsEvent.UpdateLanguage -> updateLanguage(event.languageCode)
            is SettingsEvent.ClearError -> clearError()
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                settingsRepository.getSettings()
                    .collect { settings ->
                        _state.update {
                            it.copy(
                                settings = settings,
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error al cargar configuraciones: ${e.message}",
                        isLoading = false
                    )
                }
                Timber.e(e, "Error al cargar configuraciones")
            }
        }
    }

    private fun updateTemperatureUnit(useCelsius: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                settingsRepository.updateTemperatureUnit(useCelsius)
                    .onSuccess {
                        _state.update { it.copy(isLoading = false) }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                error = "Error al actualizar unidad de temperatura: ${error.message}",
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error inesperado: ${e.message}",
                        isLoading = false
                    )
                }
                Timber.e(e, "Error al actualizar unidad de temperatura")
            }
        }
    }

    private fun updateTheme(isDarkMode: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                settingsRepository.updateTheme(isDarkMode)
                    .onSuccess {
                        _state.update { it.copy(isLoading = false) }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                error = "Error al actualizar tema: ${error.message}",
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error inesperado: ${e.message}",
                        isLoading = false
                    )
                }
                Timber.e(e, "Error al actualizar tema")
            }
        }
    }

    private fun updateNotifications(enabled: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                settingsRepository.updateNotifications(enabled)
                    .onSuccess {
                        _state.update { it.copy(isLoading = false) }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                error = "Error al actualizar notificaciones: ${error.message}",
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error inesperado: ${e.message}",
                        isLoading = false
                    )
                }
                Timber.e(e, "Error al actualizar notificaciones")
            }
        }
    }

    private fun updateLanguage(languageCode: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                settingsRepository.updateLanguage(languageCode)
                    .onSuccess {
                        _state.update { it.copy(isLoading = false) }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                error = "Error al actualizar idioma: ${error.message}",
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error inesperado: ${e.message}",
                        isLoading = false
                    )
                }
                Timber.e(e, "Error al actualizar idioma")
            }
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}