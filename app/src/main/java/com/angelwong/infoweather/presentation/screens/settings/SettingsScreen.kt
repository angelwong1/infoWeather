package com.angelwong.infoweather.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.angelwong.infoweather.domain.model.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuraciones") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
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
                // Sección de Apariencia
                SettingsSectionCard(title = "Apariencia") {
                    // Tema de la aplicación
                    SettingsThemeSelector(
                        currentTheme = state.settings.theme,
                        onThemeChange = { theme ->
                            // TODO: Implementar cambio de tema
                            // Por ahora solo lo dejamos como referencia
                        }
                    )

                    // Modo oscuro
                    SettingsToggleItem(
                        icon = Icons.Outlined.DarkMode,
                        title = "Modo Oscuro",
                        description = "Cambiar entre modo claro y oscuro",
                        checked = state.settings.isDarkMode,
                        onCheckedChange = {
                            viewModel.onEvent(SettingsEvent.ToggleDarkMode(it))
                        }
                    )
                }

                // Sección de Unidades
                SettingsSectionCard(title = "Unidades") {
                    // Unidad de temperatura
                    SettingsToggleItem(
                        icon = Icons.Outlined.Thermostat,
                        title = "Unidad de Temperatura",
                        description = if (state.settings.useCelsius) "Celsius" else "Fahrenheit",
                        checked = state.settings.useCelsius,
                        onCheckedChange = {
                            viewModel.onEvent(SettingsEvent.ToggleTemperatureUnit(it))
                        }
                    )
                }

                // Sección de Notificaciones
                SettingsSectionCard(title = "Notificaciones") {
                    // Activar/desactivar notificaciones
                    SettingsToggleItem(
                        icon = Icons.Outlined.Notifications,
                        title = "Notificaciones",
                        description = "Recibir alertas meteorológicas",
                        checked = state.settings.showNotifications,
                        onCheckedChange = {
                            viewModel.onEvent(SettingsEvent.ToggleNotifications(it))
                        }
                    )
                }

                // Sección de Idioma
                SettingsSectionCard(title = "Idioma") {
                    // Selector de idioma
                    var expanded by remember { mutableStateOf(false) }
                    val languages = listOf(
                        "es" to "Español",
                        "en" to "English",
                        "pt" to "Português"
                    )

                    SettingsDropdownItem(
                        icon = Icons.Outlined.Language,
                        title = "Idioma de la Aplicación",
                        description = languages.find { it.first == state.settings.languageCode }?.second ?: "Español",
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        onDismiss = { expanded = false }
                    ) {
                        languages.forEach { (code, name) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    viewModel.onEvent(SettingsEvent.UpdateLanguage(code))
                                    expanded = false
                                }
                            )
                        }
                    }
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
                        TextButton(onClick = { viewModel.onEvent(SettingsEvent.ClearError) }) {
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
fun SettingsThemeSelector(
    currentTheme: Settings.Theme,
    onThemeChange: (Settings.Theme) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Palette,
                contentDescription = "Tema",
                tint = MaterialTheme.colorScheme.primary
            )

            Column {
                Text(
                    text = "Tema",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = when (currentTheme) {
                        Settings.Theme.LIGHT -> "Claro"
                        Settings.Theme.DARK -> "Oscuro"
                        Settings.Theme.SYSTEM -> "Sistema"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Selector de tema
        var expanded by remember { mutableStateOf(false) }

        Box {
            TextButton(onClick = { expanded = true }) {
                Text("Cambiar")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                Settings.Theme.values().forEach { theme ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                when (theme) {
                                    Settings.Theme.LIGHT -> "Claro"
                                    Settings.Theme.DARK -> "Oscuro"
                                    Settings.Theme.SYSTEM -> "Sistema"
                                }
                            )
                        },
                        onClick = {
                            onThemeChange(theme)
                            expanded = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = when (theme) {
                                    Settings.Theme.LIGHT -> Icons.Outlined.LightMode
                                    Settings.Theme.DARK -> Icons.Outlined.DarkMode
                                    Settings.Theme.SYSTEM -> Icons.Outlined.Contrast
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            content()
        }
    }
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsDropdownItem(
    icon: ImageVector,
    title: String,
    description: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box {
            TextButton(onClick = { onExpandedChange(true) }) {
                Text("Cambiar")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismiss
            ) {
                content()
            }
        }
    }
}