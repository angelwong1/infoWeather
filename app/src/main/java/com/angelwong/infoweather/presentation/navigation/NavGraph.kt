package com.angelwong.infoweather.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.angelwong.infoweather.presentation.screens.alerts.AlertsScreen
import com.angelwong.infoweather.presentation.screens.detail.DetailScreen
import com.angelwong.infoweather.presentation.screens.home.HomeScreen
import com.angelwong.infoweather.presentation.screens.locations.LocationsScreen
import com.angelwong.infoweather.presentation.screens.settings.SettingsScreen
import com.angelwong.infoweather.presentation.screens.welcome.WelcomeScreen

@Composable
fun WeatherNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.Welcome.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = NavRoutes.Welcome.route) {
            WelcomeScreen(
                onStartClick = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = NavRoutes.Home.route) {
            HomeScreen(
                onNavigateToDetail = { lat, lon ->
                    navController.navigate(NavRoutes.Detail.createRoute(lat, lon))
                },
                onNavigateToLocations = {
                    navController.navigate(NavRoutes.Locations.route)
                },
                onNavigateToSettings = {
                    navController.navigate(NavRoutes.Settings.route)
                }
            )
        }

        composable(
            route = NavRoutes.Detail.route,
            arguments = NavRoutes.Detail.arguments
        ) {
            DetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = NavRoutes.Locations.route) {
            LocationsScreen(
                onBackClick = { navController.popBackStack() },
                onLocationSelected = { lat, lon ->
                    // En lugar de pasar el objeto Location completo, pasamos los datos necesarios
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "selected_latitude", lat
                    )
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "selected_longitude", lon
                    )
                    navController.popBackStack()
                }
            )
        }

        composable(route = NavRoutes.Alerts.route) {
            AlertsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = NavRoutes.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}