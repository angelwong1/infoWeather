package com.angelwong.infoweather.domain.usecase

import com.angelwong.infoweather.di.IoDispatcher
import com.angelwong.infoweather.domain.model.Location
import com.angelwong.infoweather.domain.repository.ILocationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SearchLocationsUseCase @Inject constructor(
    private val locationRepository: ILocationRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(query: String): Result<List<Location>> =
        withContext(dispatcher) {
            try {
                // Validación de entrada
                if (query.length < 3) {
                    return@withContext Result.failure(
                        IllegalArgumentException("La búsqueda debe tener al menos 3 caracteres")
                    )
                }

                // Realizar búsqueda
                val result = locationRepository.searchLocations(query)

                result.onSuccess { locations ->
                    Timber.d("Búsqueda exitosa para '$query': ${locations.size} resultados")
                }.onFailure { error ->
                    Timber.e(error, "Error en búsqueda para '$query'")
                }

                result
            } catch (e: Exception) {
                Timber.e(e, "Error inesperado en búsqueda de ubicaciones")
                Result.failure(e)
            }
        }
}