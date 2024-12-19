package com.angelwong.infoweather.data.source.remote.interceptor

import com.angelwong.infoweather.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // Construimos una nueva URL añadiendo los parámetros necesarios
        val url = original.url.newBuilder()
            // API key de OpenWeatherMap
            .addQueryParameter("appid", BuildConfig.WEATHER_API_KEY)
            // Aseguramos que las respuestas estén en sistema métrico
            .addQueryParameter("units", "metric")
            // Respuestas en español
            .addQueryParameter("lang", "es")
            // Limitamos a 5 resultados para búsquedas
            .addQueryParameter("limit", "5")
            .build()

        // Creamos una nueva petición con la URL modificada
        val request = original.newBuilder()
            .url(url)
            .build()

        return chain.proceed(request)
    }
}