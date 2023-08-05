package com.darker.appsettings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.darker.appsettings.app.ui.controller.NavigationDrawerController
import com.darker.appsettings.app.ui.dialogs.FrameworkNotRunning
import com.darker.appsettings.app.utils.Shell
import com.darker.appsettings.ui.theme.AppSettings2Theme
import com.darker.appsettings.xposed.Utils


lateinit var act: MainActivity
lateinit var shell: Shell

var grantedPermissions = mutableListOf<String>()

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestMultiplePermissions = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions.entries.forEach {
                if (!it.value) {
//                    exitProcess(0)
                } else {
                    grantedPermissions.add(it.key)
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                )
            )
        } else {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

        act = this
        shell = Shell()

        setContent {
            AppSettings2Theme {

                // Check if module is enabled
                if (!Utils.isModuleEnabled()) FrameworkNotRunning()
                else NavigationDrawerController()
            }
        }
    }

    fun isModuleEnabled(): Boolean {
        return false
    }

}


