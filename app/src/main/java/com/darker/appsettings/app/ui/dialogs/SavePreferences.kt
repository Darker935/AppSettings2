package com.darker.appsettings.app.ui.dialogs

import android.app.ActivityManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.darker.appsettings.R


@Composable
fun SavePreferences(showDialog: MutableState<Boolean>, packageName: String) {
    val ctx = LocalContext.current
    Dialog(
        onDismissRequest = { showDialog.value = false },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            shape = RoundedCornerShape(25.dp),
            color = AlertDialogDefaults.containerColor,
        ) {
            Column(
                Modifier
                    .padding(vertical = 20.dp, horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.save_prefs),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 0.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { showDialog.value = false },
                        Modifier
                            .weight(1f)
                            .padding(end = 10.dp)
                    ) {
                        Text(text = "Cancel", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val am =
                                ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                            am.killBackgroundProcesses(packageName)
                            showDialog.value = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp)
                    ) { Text(text = "Save", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}