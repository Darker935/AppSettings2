package com.darker.appsettings.app.ui.dialogs

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.darker.appsettings.Constants
import com.darker.appsettings.R
import java.io.DataOutputStream
import java.io.IOException
import kotlin.system.exitProcess


@Preview
@Composable
fun FrameworkNotRunning() {

    Dialog(
        onDismissRequest = { /*TODO*/ },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(25.dp),
            color = AlertDialogDefaults.containerColor,
            modifier = Modifier.fillMaxWidth(0.93f)

        ) {
            Column(
                Modifier
                    .padding(vertical = 20.dp, horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.lsposed_icon),
                        contentDescription = "Framework Not Running",
                        modifier = Modifier
                            .size(55.dp)
                            .background(
                                shape = RoundedCornerShape(9.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                    )
                    Text(
                        text = "Framework not running",
                        textAlign = TextAlign.Center,
                        fontWeight = Bold,
                        fontSize = 19.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .fillMaxWidth(0.90f)
                    )
                }

                Text(
                    text = stringResource(R.string.framework_not_running_message),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 30.dp, horizontal = 5.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { exitProcess(0) },
                        Modifier
                            .weight(1f)
                            .padding(end = 10.dp)
                    ) {
                        Text(text = "Cancel", fontWeight = Bold)
                    }

                    Button(
                        onClick = {
                            openLSPosedSettings()
                            exitProcess(0)
                        },
                        Modifier
                            .weight(1f)
                            .padding(start = 10.dp)
                    ) { Text(text = "Fix", fontWeight = Bold) }
                }
            }
        }
    }

}

fun openLSPosedSettings() {
    // Starting module settings on LSPosed
    val text = "am start -c org.lsposed.manager.LAUNCH_MANAGER -d " + Uri.Builder().scheme("module")
        .encodedAuthority(Constants.PACKAGE_NAME + ":0")
        .build() + " com.android.shell/com.android.shell.BugreportWarningActivity"
    try {
        val process = Runtime.getRuntime().exec("su 2000")
        val dataOutputStream = DataOutputStream(process.outputStream)
        dataOutputStream.writeBytes("echo \"AppSettingsReborn - By Quishot / Darker (mesma pessoa)\" > /data/local/tmp/AppSettings\n")
        dataOutputStream.flush()
        dataOutputStream.writeBytes(text)
        dataOutputStream.flush()
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}