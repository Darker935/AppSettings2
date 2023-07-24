package com.darker.appsettings.app.ui.dialogs

import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import com.darker.appsettings.app.ui.lifecycle.OnLifecycleEvent
import com.darker.appsettings.app.utils.LoadApps

var listItems: MutableList<Unit> = mutableListOf()
var totalApps = 0

@Composable
fun LoadingAppsDialog(pm: PackageManager, onFinish: () -> Unit) {
    var startLoadApps by remember { mutableStateOf(false) }
    var loadedApps by remember { mutableIntStateOf(0) }

    Log.i("• Before Loading apps dialog:", "before totalApps=$totalApps")

    if (startLoadApps) {
        Log.i("ANything", "----> startLoadApps condition")
        if (totalApps == 0) {
            LoadApps(pm, loadedApps) {
                loadedApps++
                Log.i("Value", loadedApps.toString())
            }
        }
        if (loadedApps == totalApps) {
            onFinish()
        }
    } else {
        Log.i("Anything", "----> startDialog condition")
        Dialog(
            onDismissRequest = { /*TODO*/ },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Column(
                modifier = Modifier
                    .widthIn()
                    .background(
                        color = AlertDialogDefaults.containerColor,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(vertical = 15.dp, horizontal = 30.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(65.dp)
                        .align(Alignment.CenterHorizontally),
                    strokeWidth = 5.dp
                )
                Text(
                    text = "${loadedApps} / $totalApps",
                    fontSize = 17.sp,
                    fontWeight = Bold,
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Log.i("• Dialog showing", "• (1) showing dialog")
            }
        }
    }

    DialogWithLifecycle {
        Log.i("Anything", "-----> Load dialog lifecycle")
        startLoadApps = true
    }
}

@Composable
fun DialogWithLifecycle(onLoad: () -> Unit) {
    OnLifecycleEvent { owner, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                onLoad()
            }

            else -> {}
        }
    }
}