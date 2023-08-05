package com.darker.appsettings.app.ui.dropdown

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PerAppSettingsMenu(showMenu: MutableState<Boolean>, app_package: String) {

    DropdownMenu(
        expanded = showMenu.value,
        modifier = Modifier.shadow(1.dp),
        onDismissRequest = { showMenu.value = false }
    ) {
        Column(Modifier.padding(horizontal = 2.dp)) {
            DropdownMenuItem(
                text = { Text("Save") },
                onClick = { /* Handle refresh! */ }
            )
            DropdownMenuItem(
                text = { Text("Launch") },
                onClick = { /* Handle settings! */ }
            )
            Divider(color = Color.White.copy(0.3f))
            DropdownMenuItem(
                text = { Text("Send feedback") },
                onClick = { /* Handle settings! */ }
            )
        }
    }
}
