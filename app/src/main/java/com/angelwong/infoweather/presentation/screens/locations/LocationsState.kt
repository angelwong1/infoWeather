package com.angelwong.infoweather.presentation.screens.locations

import com.angelwong.infoweather.domain.model.Location

data class LocationsState(
    val locations: List<Location> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface LocationsEvent {
    data class AddLocation(val query: String) : LocationsEvent
    data class DeleteLocation(val location: Location) : LocationsEvent
    data class ToggleFavorite(val location: Location) : LocationsEvent
    data object ClearError : LocationsEvent
}