package com.darker.appsettings.app.ui.screens

import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.darker.appsettings.app.ui.activities.PerAppSettings
import com.darker.appsettings.app.ui.dialogs.LoadingAppsDialog
import com.darker.appsettings.app.ui.dialogs.appList
import com.darker.appsettings.app.ui.lifecycle.OnLifecycleEvent
import com.darker.appsettings.app.ui.model.ScreenModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter

var pm: PackageManager? = null

@Composable
fun AppsScreen(navController: NavController) {

    val title = "Apps"
    var isLoaded by remember { mutableStateOf(false) }
    var showDialogListener by remember { mutableStateOf(false) }
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
                // Current screen is loaded, showing progress dialog
                Log.i(
                    "Screen model:",
                    "-> ShowDialogListener: (true), isLoaded: ($isLoaded)"
                )

                LoadingAppsDialog(pm!!) {
                    Toast.makeText(ctx, "onFinish", Toast.LENGTH_SHORT).show()
                    showDialogListener = false
                }

            } else if (isLoaded) {
                // urreCnt screen is loaded, but dialog shows off
                Log.i(
                    "Screen model:",
                    "-> ShowLazyColumn: (true), ShowDialog: ($showDialogListener), isLoaded: ($isLoaded)"
                )
                LazyColumn(
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                ) {
                    appList.sortWith(compareBy { it.name })
                    items(appList) { item ->
                        Card(
                            elevation = CardDefaults.cardElevation(3.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp)
                                .background(
                                    CardDefaults.cardColors().containerColor.copy(0.4f),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    ctx.startActivity(
                                        Intent()
                                            .setClass(ctx, PerAppSettings().javaClass)
                                            .putExtra("title", item.name)
                                            .putExtra("packageName", item.packageName)
                                    )
                                }
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Start)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .padding(12.dp)
                                        .clip(RoundedCornerShape(7.dp)),
                                    painter = rememberDrawablePainter(item.icon),
                                    contentDescription = item.name
                                )
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.CenterVertically)
                                ) {
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = item.packageName,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 13.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
            AppScreenWithLifecycle {
                Toast.makeText(ctx, "onLoad", Toast.LENGTH_SHORT).show()
                showDialogListener = true
                isLoaded = true
                Log.i("Screen model:", "-> onLoad appScreen: $showDialogListener")
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
