package com.darker.appsettings.app.ui.controller

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
//        modifier = Modifier.padding(top = 70.dp),
        navController = navController,
        startDestination = NavigationDrawerRoute.AppsScreen.route
    ) {
        composable(route = NavigationDrawerRoute.AppsScreen.route) {
            AppsScreen(navController)
        }
        composable(route = NavigationDrawerRoute.EnabledAppsScreen.route) {
            EnabledAppsScreen(navController = navController)
        }
    }
}