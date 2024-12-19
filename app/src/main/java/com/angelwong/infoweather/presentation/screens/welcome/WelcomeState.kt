package com.angelwong.infoweather.presentation.screens.welcome

import com.angelwong.infoweather.domain.model.Location

data class WelcomeState(
    val selectedLocation: Location? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentStep: WelcomeStep = WelcomeStep.INTRO
)

enum class WelcomeStep {
    INTRO,
    LOCATION_PERMISSION,
    SELECT_LOCATION
}

sealed interface WelcomeEvent {
    data object NextStep : WelcomeEvent
    data object PreviousStep : WelcomeEvent
    data class SelectLocation(val location: Location) : WelcomeEvent
    data object ClearError : WelcomeEvent
}

sealed interface WelcomeAction {
    data object NavigateToHome : WelcomeAction
    data class ShowError(val message: String) : WelcomeAction
}