package com.darker.appsettings.app.ui.appbar

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.darker.appsettings.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LTopAppBar(
    title: String,
    scope: CoroutineScope,
    drawerState: DrawerState,
    scrollBehavior: TopAppBarScrollBehavior,
    onQueryChange: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var showSearchBar by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    LargeTopAppBar(
        title = {
            if (!showSearchBar) {
                Text(text = searchText.ifEmpty { title })
            } else {
                SearchBar(
                    modifier = Modifier.padding(bottom = 5.dp),
                    query = searchText,
                    onQueryChange = {
                        searchText = it
                        Log.i(Constants.TAG, "LTopAppBar: $searchText")
                        onQueryChange(searchText)
                    },
                    onSearch = {
                        showSearchBar = false
                        keyboardController?.hide()
                    },
                    active = isSearchActive,
                    onActiveChange = {},
                ) {

                }
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch { drawerState.open() }
            }) {
                Icon(Icons.Filled.Menu, "Menu Button")
            }
        },
        actions = {
            IconButton(onClick = {
                showSearchBar = true
            }) {
                Icon(Icons.Filled.Search, "Search Button")
            }
        },
        scrollBehavior = scrollBehavior
    )
}
