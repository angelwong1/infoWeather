package com.angelwong.infoweather.domain.repository

import com.angelwong.infoweather.domain.model.Location
import kotlinx.coroutines.flow.Flow
import kotlin.Result

interface ILocationRepository {
    /**
     * Obtiene el flujo de ubicaciones guardadas
     */
    fun getSavedLocations(): Flow<List<Location>>

    /**
     * Guarda una ubicación en la base de datos local
     */
    suspend fun saveLocation(location: Location): Result<Unit>

    /**
     * Elimina una ubicación de la base de datos local
     */
    suspend fun removeLocation(location: Location): Result<Unit>

    /**
     * Busca ubicaciones por nombre
     * @param query Texto de búsqueda (mínimo 3 caracteres)
     */
    suspend fun searchLocations(query: String): Result<List<Location>>

    /**
     * Obtiene información de ubicación por coordenadas
     */
    suspend fun getLocationByCoordinates(lat: Double, lon: Double): Result<Location>

    /**
     * Obtiene información de ubicación por coordenadas, con fallback a ubicación genérica
     */
    suspend fun getLocationFromCoordinates(lat: Double, lon: Double): Result<Location>
}