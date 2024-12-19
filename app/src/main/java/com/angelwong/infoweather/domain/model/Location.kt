package com.angelwong.infoweather.domain.model

data class Location(
    val id: String,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) {
    init {
        require(name.isNotBlank()) { "El nombre no puede estar vacío" }
        require(latitude in -90.0..90.0) { "Latitud inválida: $latitude" }
        require(longitude in -180.0..180.0) { "Longitud inválida: $longitude" }
    }

    fun toDisplayName(): String = "$name, $country"

    companion object {
        val DEFAULT = Location(
            id = "default",
            name = "Asunción",
            country = "PY",
            latitude = -25.2867,
            longitude = -57.3333
        )
    }
}