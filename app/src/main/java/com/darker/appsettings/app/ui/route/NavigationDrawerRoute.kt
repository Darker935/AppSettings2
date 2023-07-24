package com.darker.appsettings.app.ui.route

import androidx.annotation.DrawableRes
import com.darker.appsettings.R

sealed class NavigationDrawerRoute(
    val route: String,
    val drawerName: String,
    @DrawableRes val drawerIcon: Int
) {
    object AppsScreen : NavigationDrawerRoute(
        route = "nav_apps_screen",
        drawerName = "Apps",
        drawerIcon = R.drawable.apps_icon
    )

    object EnabledAppsScreen : NavigationDrawerRoute(
        route = "nav_enabled_apps_screen",
        drawerName = "Apps",
        drawerIcon = R.drawable.apps_icon
    )
}