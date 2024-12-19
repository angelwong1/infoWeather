package com.angelwong.infoweather.domain.util

import com.angelwong.infoweather.data.util.NetworkManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class NetworkUtils @Inject constructor(
    private val networkManager: NetworkManager
) {
    suspend fun isNetworkAvailable(): Boolean {
        return networkManager.isNetworkAvailable.first()
    }

    suspend fun requireNetwork(): Result<Unit> {
        return if (isNetworkAvailable()) {
            Result.Success(Unit)
        } else {
            Result.Error(Exception("No hay conexión a internet"))
        }
    }
}