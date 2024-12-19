package com.angelwong.infoweather.data.source.remote

import com.angelwong.infoweather.domain.util.Result
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class ErrorHandler @Inject constructor() {
    fun handleError(throwable: Throwable): Result.Error {
        val error = when (throwable) {
            is IOException -> Exception(
                "Error de conexión. Por favor, verifica tu conexión a internet."
            )
            is SocketTimeoutException -> Exception(
                "Tiempo de espera agotado. Por favor, intenta nuevamente."
            )
            is HttpException -> when (throwable.code()) {
                401 -> Exception("Error de autenticación. Por favor, verifica tu API key.")
                404 -> Exception("Recurso no encontrado.")
                500 -> Exception("Error del servidor. Por favor, intenta más tarde.")
                else -> Exception("Error de red: ${throwable.message}")
            }
            else -> Exception(throwable.message ?: "Error desconocido")
        }
        return Result.Error(error)
    }
}