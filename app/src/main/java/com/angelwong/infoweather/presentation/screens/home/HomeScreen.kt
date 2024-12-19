package com.angelwong.infoweather.presentation.screens.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.angelwong.infoweather.domain.model.DailyForecast
import com.angelwong.infoweather.presentation.components.WeatherCard
import com.angelwong.infoweather.presentation.components.ForecastItem
import com.angelwong.infoweather.presentation.navigation.NavRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (Double, Double) -> Unit,
    onNavigateToLocations: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    // Use a local NavController if not provided
    val localNavController = navController ?: rememberNavController()
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    // Observe changes in the selected location
    LaunchedEffect(localNavController) {
        val navBackStackEntry = localNavController.currentBackStackEntry
        navBackStackEntry?.savedStateHandle?.let { handle ->
            // Retrieve and process the selected location
            handle.get<Double>("selected_latitude")?.let { latitude ->
                handle.get<Double>("selected_longitude")?.let { longitude ->
                    viewModel.onEvent(HomeEvent.LocationSelected(latitude, longitude))
                    // Clear the values after using them
                    handle.remove<Double>("selected_latitude")
                    handle.remove<Double>("selected_longitude")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.selectedLocation?.name ?: "InfoWeather",
                            style = MaterialTheme.typography.titleLarge
                        )
                        state.selectedLocation?.let { location ->
                            Text(
                                text = location.country,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLocations) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Ubicaciones",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    // Refresh button
                    IconButton(
                        onClick = {
                            scope.launch {
                                viewModel.onEvent(HomeEvent.RefreshWeather)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar"
                        )
                    }

                    // Settings button
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuración"
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Current weather card
                state.currentWeather?.let { weather ->
                    WeatherCard(
                        currentWeather = weather,
                        onClick = {
                            state.selectedLocation?.let { location ->
                                onNavigateToDetail(location.latitude, location.longitude)
                            }
                        }
                    )
                }

                // Forecast section
                if (state.forecast.isNotEmpty()) {
                    Text(
                        text = "Próximos días",
                        style = MaterialTheme.typography.titleMedium
                    )

                    SingleRowScrollableContent(
                        items = state.forecast,
                        itemContent = { forecast ->
                            ForecastItem(
                                forecast = forecast,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    )
                }
            }

            // Loading indicator
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Error handling
            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = { viewModel.onEvent(HomeEvent.ClearError) }) {
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
fun SingleRowScrollableContent(
    items: List<DailyForecast>,
    itemContent: @Composable (DailyForecast) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            itemContent(item)
        }
    }
}