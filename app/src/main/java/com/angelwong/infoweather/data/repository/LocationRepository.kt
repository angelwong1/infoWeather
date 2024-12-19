package com.angelwong.infoweather.data.repository

import com.angelwong.infoweather.data.source.local.dao.LocationDao
import com.angelwong.infoweather.data.source.local.entity.LocationEntity
import com.angelwong.infoweather.data.source.remote.api.GeocodingApi
import com.angelwong.infoweather.data.util.NetworkManager
import com.angelwong.infoweather.di.IoDispatcher
import com.angelwong.infoweather.domain.model.Location
import com.angelwong.infoweather.domain.repository.ILocationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val locationDao: LocationDao,
    private val geocodingApi: GeocodingApi,
    private val networkManager: NetworkManager,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ILocationRepository {

    override fun getSavedLocations(): Flow<List<Location>> {
        return locationDao.getAllLocations()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun saveLocation(location: Location): Result<Unit> =
        withContext(dispatcher) {
            runCatching {
                locationDao.insertLocation(location.toEntity())
                Timber.d("Location saved: $location")
            }
        }

    override suspend fun removeLocation(location: Location): Result<Unit> =
        withContext(dispatcher) {
            runCatching {
                locationDao.deleteLocation(location.toEntity())
                Timber.d("Location removed: $location")
            }
        }

    override suspend fun searchLocations(query: String): Result<List<Location>> =
        withContext(dispatcher) {
            runCatching {
                // Validación básica
                require(query.length >= 3) { "La búsqueda debe tener al menos 3 caracteres" }

                // Verificar conexión a internet
                if (!networkManager.isCurrentlyConnected()) {
                    throw Exception("No hay conexión a internet disponible")
                }

                // Realizar búsqueda
                val response = geocodingApi.searchLocation(query)
                Timber.d("Search results for '$query': ${response.size} locations")

                response.map { it.toDomain() }
            }
        }

    override suspend fun getLocationByCoordinates(lat: Double, lon: Double): Result<Location> =
        withContext(dispatcher) {
            runCatching {
                if (!networkManager.isCurrentlyConnected()) {
                    throw Exception("No hay conexión a internet disponible")
                }

                val response = geocodingApi.getLocationByCoordinates(lat, lon)
                response.firstOrNull()?.toDomain()
                    ?: throw Exception("No se encontró información para las coordenadas proporcionadas")
            }
        }

    override suspend fun getLocationFromCoordinates(lat: Double, lon: Double): Result<Location> =
        withContext(dispatcher) {
            // Primero intentamos obtener la ubicación por coordenadas
            val result = getLocationByCoordinates(lat, lon)

            // Si falla, creamos una ubicación genérica
            when {
                result.isSuccess -> result
                else -> Result.success(Location(
                    id = "${lat}_${lon}",
                    name = "Ubicación desconocida",
                    country = "",
                    latitude = lat,
                    longitude = lon
                ))
            }
        }

    private fun Location.toEntity() = LocationEntity(
        id = id,
        name = name,
        country = country,
        latitude = latitude,
        longitude = longitude,
        timestamp = timestamp,
        isFavorite = isFavorite
    )

    private fun LocationEntity.toDomain() = Location(
        id = id,
        name = name,
        country = country,
        latitude = latitude,
        longitude = longitude,
        timestamp = timestamp,
        isFavorite = isFavorite
    )

    private fun com.angelwong.infoweather.data.source.remote.model.GeocodingResponse.toDomain() = Location(
        id = "${lat}_${lon}",
        name = name,
        country = country,
        latitude = lat,
        longitude = lon,
        timestamp = System.currentTimeMillis()
    )
}