package com.darker.appsettings.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.darker.appsettings.app.ui.model.ScreenModel

@Composable
fun EnabledAppsScreen(navController: NavController) {

    val title = "Enabled apps"

    ScreenModel(title, navController, content = { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

        }
    })
}