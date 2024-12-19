package com.angelwong.infoweather.data.source.remote.interceptor

import com.angelwong.infoweather.data.source.remote.ApiConstants.ErrorMessages
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor que maneja los errores HTTP de manera centralizada
 */
@Singleton
class ErrorInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            val request = chain.request()
            val response = chain.proceed(request)

            // Manejamos diferentes códigos de error HTTP
            when (response.code) {
                HttpURLConnection.HTTP_UNAUTHORIZED -> {
                    throw IOException(ErrorMessages.INVALID_API_KEY)
                }
                HttpURLConnection.HTTP_NOT_FOUND -> {
                    throw IOException(ErrorMessages.LOCATION_NOT_FOUND)
                }
                in 500..599 -> {
                    throw IOException(ErrorMessages.SERVER_ERROR)
                }
            }

            return response
        } catch (e: Exception) {
            // Convertimos cualquier excepción no manejada en IOException
            throw when (e) {
                is IOException -> e
                else -> IOException(ErrorMessages.UNKNOWN_ERROR, e)
            }
        }
    }
}