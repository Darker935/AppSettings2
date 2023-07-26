package com.darker.appsettings.app.ui.dialogs

import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darker.appsettings.R
import com.darker.appsettings.app.ui.lifecycle.OnLifecycleEvent

@Composable
fun FrameworkNotRunning(pm: PackageManager, onFinish: () -> Unit) {
    Dialog(
        onDismissRequest = { /*TODO*/ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.93f)
                .background(
                    color = AlertDialogDefaults.containerColor,
                    shape = RoundedCornerShape(25.dp)
                )
                .padding(vertical = 15.dp, horizontal = 30.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 9.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.lsposed_icon),
                    contentDescription = "Framework Not Running",
                    Modifier.size(45.dp)
                )
                Text(
                    text = "Framework not running",
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
            }
            Text(
                text = "",
                fontSize = 17.sp,
                fontWeight = Bold,
                modifier = Modifier
                    .padding(top = 15.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Log.i("• Dialog showing", "• (1) showing dialog")
        }
    }

    if (startLoadApps) {
        Log.i("ANything", "----> startLoadApps condition")
        if (totalApps == 0) {
            viewModel.startThread(pm, loadedApps, {
                onFinish()
            })
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