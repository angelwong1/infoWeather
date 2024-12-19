package com.angelwong.infoweather.data.source.remote

/**
 * Objeto que centraliza todas las constantes relacionadas con la API
 * del servicio meteorológico y configuraciones de red.
 */
object ApiConstants {
    // URLs base para diferentes servicios
    const val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val GEO_BASE_URL = "https://api.openweathermap.org/geo/1.0/"
    const val TILE_BASE_URL = "https://tile.openweathermap.org/map/"

    // Configuración por defecto
    const val DEFAULT_LANGUAGE = "es"
    const val DEFAULT_UNITS = "metric"

    // Configuración de caché y red
    const val CACHE_CONTROL_HEADER = "Cache-Control"
    const val CACHE_CONTROL_NO_CACHE = "no-cache"
    const val CACHE_CONTROL_MAX_AGE = "public, max-age=300"
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10 MB
    const val CACHE_MAX_AGE = 5 * 60L // 5 minutos
    const val CACHE_MAX_STALE = 7 * 24 * 60 * 60L // 7 días

    // Timeouts de red
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    // Configuración de reintentos
    const val RETRY_ATTEMPTS = 3
    const val RETRY_DELAY = 1000L // 1 segundo

    /**
     * Enumeración que define las diferentes capas disponibles para el mapa meteorológico
     */
    enum class MapLayer(val value: String) {
        CLOUDS("clouds_new"),
        PRECIPITATION("precipitation_new"),
        PRESSURE("pressure_new"),
        WIND("wind_new"),
        TEMPERATURE("temp_new");

        /**
         * Genera la URL completa para obtener un tile del mapa
         * @param z Nivel de zoom
         * @param x Coordenada X del tile
         * @param y Coordenada Y del tile
         * @return URL completa del tile
         */
        fun getUrl(z: Int, x: Int, y: Int): String =
            "$TILE_BASE_URL$value/$z/$x/$y.png"
    }

    /**
     * Objeto que contiene todos los mensajes de error estándar de la API
     */
    object ErrorMessages {
        const val NETWORK_ERROR = "Error de conexión. Por favor, verifica tu conexión a internet"
        const val SERVER_ERROR = "Error del servidor. Por favor, intenta más tarde"
        const val UNKNOWN_ERROR = "Ha ocurrido un error inesperado"
        const val INVALID_API_KEY = "API key inválida. Por favor, verifica tu configuración"
        const val LOCATION_NOT_FOUND = "No se encontró la ubicación solicitada"
        const val TIMEOUT_ERROR = "La conexión ha tardado demasiado. Por favor, intenta nuevamente"
        const val PARSING_ERROR = "Error al procesar la respuesta del servidor"
        const val CACHE_ERROR = "Error al acceder a datos almacenados"
    }

    /**
     * Objeto que define los endpoints de la API
     */
    object Endpoints {
        const val CURRENT_WEATHER = "weather"
        const val FORECAST = "forecast"
        const val ONE_CALL = "onecall"
        const val GEOCODING = "geo/1.0/direct"
        const val REVERSE_GEOCODING = "geo/1.0/reverse"
    }
}