package com.darker.appsettings.app.ui.controller

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darker.appsettings.app.ui.route.NavigationDrawerRoute
import com.darker.appsettings.app.ui.screens.AppsScreen
import com.darker.appsettings.app.ui.screens.EnabledAppsScreen


@Composable
fun NavigationDrawerController() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationDrawerRoute.AppsScreen.route
    ) {
        // Main app screen
        composable(route = NavigationDrawerRoute.AppsScreen.route) {
            AppsScreen(navController)
        }
        // Enabled apps screen
        composable(route = NavigationDrawerRoute.EnabledAppsScreen.route) {
            EnabledAppsScreen(navController = navController)
        }
    }
}