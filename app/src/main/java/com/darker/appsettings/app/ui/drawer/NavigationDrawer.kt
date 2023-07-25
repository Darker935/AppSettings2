package com.darker.appsettings.app.ui.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.darker.appsettings.R
import com.darker.appsettings.app.ui.route.NavigationDrawerRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun NavigationDrawer(
    drawerState: DrawerState, drawerContent: @Composable () -> Unit, content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = drawerContent,
        content = content,
        gesturesEnabled = true
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(
    navController: NavController,
    scope: CoroutineScope,
    drawerState: DrawerState,
) {
    // Frequent navItems first and related together
    val itemsList = listOf(
        NavigationDrawerRoute.AppsScreen, NavigationDrawerRoute.EnabledAppsScreen
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(0.80f),
        drawerContainerColor = TopAppBarDefaults.largeTopAppBarColors().scrolledContainerColor
    ) {
        // Setting the drawer header image
        HeaderImage()

        Divider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = Color(0.5f, 0.5f, 0.5f, 0.5f)
        )
        // Filling drawer buttons
        DrawerItems(itemsList, currentRoute, scope, drawerState, navController)
    }
}

@Composable
fun HeaderImage() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.lsposed_icon),
            contentDescription = stringResource(id = R.string.app_name),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = stringResource(id = R.string.app_name),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(10.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DrawerItems(
    items: List<NavigationDrawerRoute>,
    currentRoute: String?,
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController
) {
    items.forEach { navDrawerItem ->
        NavigationDrawerItem(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 8.dp),
            shape = RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50),
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent
            ), icon = {
                Icon(
                    painter = painterResource(id = navDrawerItem.drawerIcon),
                    contentDescription = navDrawerItem.drawerName
                )
            }, label = {
                Text(
                    text = navDrawerItem.drawerName, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }, selected = currentRoute == navDrawerItem.route, onClick = {
                scope.launch { drawerState.close() }
                if (navDrawerItem.route != currentRoute) {
                    navController.navigate(navDrawerItem.route)
                }
            })
    }
}