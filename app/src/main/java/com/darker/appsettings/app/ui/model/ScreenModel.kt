package com.darker.appsettings.app.ui.model

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavController
import com.darker.appsettings.Constants
import com.darker.appsettings.app.ui.appbar.LTopAppBar
import com.darker.appsettings.app.ui.drawer.DrawerContent
import com.darker.appsettings.app.ui.drawer.NavigationDrawer
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenModel(
    title: String,
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit,
    function: @Composable (String) -> Unit = {}
) {

    val scope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var searchText by remember { mutableStateOf("") }

    NavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController = navController, scope = scope, drawerState = drawerState)
        }
    ) {

        val alpha = scrollBehavior.state.collapsedFraction
        val fgColor = TopAppBarDefaults.largeTopAppBarColors().scrolledContainerColor
        val currentColor = fgColor.copy(alpha = alpha)
        systemUiController.setStatusBarColor(currentColor)

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LTopAppBar(title, scope, drawerState, scrollBehavior) {
                    searchText = it
                    Log.i(Constants.TAG, "----> ScreenModel: $searchText")

                }
            },
            content = content
        )
    }

    function(searchText)
}