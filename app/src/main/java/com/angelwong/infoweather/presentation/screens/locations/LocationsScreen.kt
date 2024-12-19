package com.angelwong.infoweather.presentation.screens.locations

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.angelwong.infoweather.domain.model.Location
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    onBackClick: () -> Unit,
    onLocationSelected: (Double, Double) -> Unit,
    viewModel: LocationsViewModel = hiltViewModel()
) {
    // Estado de la pantalla
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            if (showSearchBar) {
                // Barra de búsqueda expandida
                SearchTopBar(
                    query = searchQuery,
                    onQueryChange = { query ->
                        searchQuery = query
                        // Realizar búsqueda después de 300ms de inactividad y si hay al menos 3 caracteres
                        if (query.length >= 3) {
                            scope.launch {
                                delay(300)
                                viewModel.onEvent(LocationsEvent.AddLocation(query))
                            }
                        }
                    },
                    onCloseClick = {
                        showSearchBar = false
                        searchQuery = ""
                    }
                )
            } else {
                // Barra superior normal
                TopAppBar(
                    title = { Text("Mis Ubicaciones") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    },
                    actions = {
                        // Botón de búsqueda
                        IconButton(onClick = { showSearchBar = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar ubicación"
                            )
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.locations.isEmpty()) {
                // Estado vacío
                EmptyState(
                    onAddLocationClick = { showSearchBar = true }
                )
            } else {
                // Lista de ubicaciones
                LocationList(
                    locations = state.locations,
                    onLocationClick = onLocationSelected,
                    onDeleteLocation = { location ->
                        viewModel.onEvent(LocationsEvent.DeleteLocation(location))
                    },
                    onToggleFavorite = { location ->
                        viewModel.onEvent(LocationsEvent.ToggleFavorite(location))
                    }
                )
            }

            // Indicador de carga
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Mensaje de error
            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = { viewModel.onEvent(LocationsEvent.ClearError) }) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onCloseClick: () -> Unit
) {
    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = { },
        active = true,
        onActiveChange = { },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar búsqueda"
                )
            }
        },
        placeholder = { Text("Buscar ciudad...") },
        modifier = Modifier.fillMaxWidth()
    ) { }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LocationList(
    locations: List<Location>,
    onLocationClick: (Double, Double) -> Unit,
    onDeleteLocation: (Location) -> Unit,
    onToggleFavorite: (Location) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Sección de favoritos
        val favorites = locations.filter { it.isFavorite }
        if (favorites.isNotEmpty()) {
            stickyHeader {
                Text(
                    text = "Favoritas",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = 8.dp)
                )
            }

            items(
                items = favorites,
                key = { it.id }
            ) { location ->
                LocationItem(
                    location = location,
                    onLocationClick = { onLocationClick(location.latitude, location.longitude) },
                    onDeleteLocation = { onDeleteLocation(location) },
                    onFavoriteClick = { onToggleFavorite(location) },
                    modifier = Modifier.animateItemPlacement()
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // Otras ubicaciones
        val otherLocations = locations.filterNot { it.isFavorite }
        if (otherLocations.isNotEmpty()) {
            stickyHeader {
                Text(
                    text = "Otras ubicaciones",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = 8.dp)
                )
            }

            items(
                items = otherLocations,
                key = { it.id }
            ) { location ->
                LocationItem(
                    location = location,
                    onLocationClick = { onLocationClick(location.latitude, location.longitude) },
                    onDeleteLocation = { onDeleteLocation(location) },
                    onFavoriteClick = { onToggleFavorite(location) },
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}

@Composable
private fun LocationItem(
    location: Location,
    onLocationClick: () -> Unit,
    onDeleteLocation: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onLocationClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Información de ubicación
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = location.country,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Acciones
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón de favorito
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (location.isFavorite) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Outlined.FavoriteBorder
                        },
                        contentDescription = if (location.isFavorite) {
                            "Quitar de favoritos"
                        } else {
                            "Añadir a favoritos"
                        },
                        tint = if (location.isFavorite) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                // Botón de eliminar con confirmación
                Box {
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }

                    if (showDeleteConfirmation) {
                        DeleteConfirmation(
                            onConfirm = {
                                onDeleteLocation()
                                showDeleteConfirmation = false
                            },
                            onDismiss = { showDeleteConfirmation = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmation(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .offset(y = (-50).dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "¿Eliminar?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
        IconButton(
            onClick = onConfirm,
            modifier = Modifier
                .size(24.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.error)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Confirmar",
                tint = MaterialTheme.colorScheme.onError,
                modifier = Modifier.size(16.dp)
            )
        }
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .size(24.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancelar",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun EmptyState(
    onAddLocationClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No hay ubicaciones guardadas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Añade una ubicación para ver el pronóstico del tiempo",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        FilledTonalButton(
            onClick = onAddLocationClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Añadir ubicación")
        }
    }
}