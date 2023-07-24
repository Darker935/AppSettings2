package com.darker.appsettings.app.ui.screens

import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.darker.appsettings.app.ui.dialogs.LoadingAppsDialog
import com.darker.appsettings.app.ui.dialogs.totalApps
import com.darker.appsettings.app.ui.lifecycle.OnLifecycleEvent
import com.darker.appsettings.app.ui.model.ScreenModel
import com.darker.appsettings.app.utils.LoadApps

var list = mutableListOf<String>();
var pm: PackageManager? = null

@Composable
fun AppsScreen(navController: NavController) {

    val title = "Apps"
    var isLoaded by remember { mutableStateOf(false) }
    var showDialogListener by remember { mutableStateOf(false) }
    var startScreenLoad by remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    Log.i("• Loading AppsScreen", "")

    // If pm is null, put current PackageManager on it
    if (pm == null) {
        pm = LocalContext.current.packageManager
    }

    ScreenModel(
        title,
        navController,
        content = { innerPadding ->

            if (showDialogListener && isLoaded) {
                Log.i(
                    "Screen model:",
                    "-> ShowDialogListener: (true), isLoaded: ($isLoaded)"
                )
//                var loadedApps = remember { mutableIntStateOf(0) }
                LoadingAppsDialog(pm!!) {
                    Toast.makeText(ctx, "onFinish", Toast.LENGTH_SHORT).show()
                    showDialogListener = false
                }
                Log.i("Cu","DESGRAÇA DA PORRA DO DIALOG VSFD")
//                if (totalApps == 0) {
//                    LoadApps(pm!!, loadedApps)
//                }
//                Log.i("• After Loading apps dialog:", "after totalApps=$totalApps")
//                showDialogListener = false
            } else if (!showDialogListener){
                Log.i(
                    "Screen model:",
                    "-> ShowLazyColumn: (true), ShowDialog: ($showDialogListener), isLoaded: ($isLoaded)"
                )
                LazyColumn(
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        Text(
                            text = "CABOU",
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            AppScreenWithLifecycle {
                Log.i("Screen model:", "-> Before onLoad: $showDialogListener")
                Toast.makeText(ctx, "onLoad", Toast.LENGTH_SHORT).show()
                showDialogListener = true
                isLoaded = true
                Log.i("Screen model:", "-> After onLoad: $showDialogListener")
            }
        }
    )
}

@Composable
fun AppScreenWithLifecycle(onLoad: () -> Unit) {
    OnLifecycleEvent { owner, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                onLoad()
            }

            else -> {}
        }
    }
}
