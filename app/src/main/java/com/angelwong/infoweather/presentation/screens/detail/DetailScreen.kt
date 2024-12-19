package com.angelwong.infoweather.presentation.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.angelwong.infoweather.domain.model.CurrentWeather
import com.angelwong.infoweather.domain.model.DailyForecast
import com.angelwong.infoweather.domain.model.WeatherAlert
import com.angelwong.infoweather.domain.util.DateUtils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Detalles del Clima")
                        state.currentWeather?.let { weather ->
                            Text(
                                text = "${weather.cityName}, ${weather.countryCode}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(DetailEvent.RefreshData) }) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Actualizar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Clima actual en detalle
                state.currentWeather?.let { weather ->
                    DetailedWeatherCard(weather)
                }

                // Pronóstico diario
                if (state.forecast.isNotEmpty()) {
                    DailyForecastSection(
                        forecast = state.forecast,
                        onDaySelected = { day ->
                            viewModel.onEvent(DetailEvent.SelectDay(day))
                        }
                    )
                }

                // Alertas meteorológicas
                if (state.alerts.isNotEmpty()) {
                    WeatherAlertsSection(state.alerts)
                }
            }

            // Indicador de carga
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                )
            }

            // Manejo de errores
            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = { viewModel.onEvent(DetailEvent.ClearError) }) {
                            Text("Cerrar")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
fun DetailedWeatherCard(weather: CurrentWeather) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Temperatura y descripción
            Text(
                text = "${weather.temperature.toInt()}°",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = weather.description.capitalize(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Detalles adicionales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem(
                    icon = Icons.Outlined.Air,
                    label = "Viento",
                    value = "${weather.windSpeed.toInt()} km/h"
                )

                WeatherDetailItem(
                    icon = Icons.Outlined.WaterDrop,
                    label = "Humedad",
                    value = "${weather.humidity}%"
                )

                WeatherDetailItem(
                    icon = Icons.Outlined.Thermostat,
                    label = "Sensación",
                    value = "${weather.feelsLike.toInt()}°"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hora de actualización
            Text(
                text = "Actualizado: ${DateUtils.formatTime(weather.timestamp)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DailyForecastSection(
    forecast: List<DailyForecast>,
    onDaySelected: (DailyForecast) -> Unit
) {
    Column {
        Text(
            text = "Pronóstico Semanal",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Horizontal scrollable forecast
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            forecast.forEach { day ->
                DailyForecastItem(
                    forecast = day,
                    onClick = { onDaySelected(day) }
                )
            }
        }
    }
}

@Composable
fun DailyForecastItem(
    forecast: DailyForecast,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .width(120.dp)
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Día de la semana
            Text(
                text = DateUtils.formatDayOfWeek(forecast.date).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Temperaturas
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${forecast.maxTemp.toInt()}°",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${forecast.minTemp.toInt()}°",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Probabilidad de precipitación
            if (forecast.precipitation > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WaterDrop,
                        contentDescription = "Precipitación",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${forecast.precipitation}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherDetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun WeatherAlertsSection(alerts: List<WeatherAlert>) {
    Column {
        Text(
            text = "Alertas Meteorológicas",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        alerts.forEach { alert ->
            WeatherAlertItem(alert)
        }
    }
}

@Composable
fun WeatherAlertItem(alert: WeatherAlert) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Título y severidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = alert.event,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Indicador de severidad
                Surface(
                    color = when (alert.severity) {
                        WeatherAlert.Severity.LOW -> Color.Green
                        WeatherAlert.Severity.MEDIUM -> Color.Yellow
                        WeatherAlert.Severity.HIGH -> Color(0xFFFFA500) // Orange
                        WeatherAlert.Severity.EXTREME -> Color.Red
                    }.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = alert.severity.name,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = when (alert.severity) {
                            WeatherAlert.Severity.LOW -> Color.Green
                            WeatherAlert.Severity.MEDIUM -> Color.Yellow
                            WeatherAlert.Severity.HIGH -> Color(0xFFFFA500) // Orange
                            WeatherAlert.Severity.EXTREME -> Color.Red
                        }
                    )
                }
            }

            // Descripción
            Text(
                text = alert.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Tiempo de la alerta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Inicio: ${DateUtils.formatDateTime(alert.startTime)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Fin: ${DateUtils.formatDateTime(alert.endTime)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}