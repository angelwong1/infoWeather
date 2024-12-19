package com.angelwong.infoweather.presentation.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class NavRoutes(val route: String) {
    object Welcome : NavRoutes("welcome")
    object Home : NavRoutes("home")
    object Detail : NavRoutes("detail/{latitude}/{longitude}") {
        fun createRoute(latitude: Double, longitude: Double) =
            "detail/$latitude/$longitude"

        val arguments = listOf(
            navArgument("latitude") { type = NavType.FloatType },
            navArgument("longitude") { type = NavType.FloatType }
        )
    }
    object Locations : NavRoutes("locations")
    object Alerts : NavRoutes("alerts")
    object Settings : NavRoutes("settings")
}