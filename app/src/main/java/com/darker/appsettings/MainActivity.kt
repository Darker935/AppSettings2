package com.darker.appsettings

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.darker.appsettings.app.ui.controller.NavigationDrawerController
import com.darker.appsettings.app.ui.dialogs.FrameworkNotRunning
import com.darker.appsettings.ui.theme.AppSettings2Theme


lateinit var act: MainActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        act = this
        Toast.makeText(this, "enabled = ${isModuleEnabled()}", Toast.LENGTH_SHORT).show()
        setContent {
            AppSettings2Theme {
                // Check if module is enabled
                if (!isModuleEnabled()) FrameworkNotRunning()
                else NavigationDrawerController()
            }
        }
    }

    fun isModuleEnabled(): Boolean {
        return false
    }

}


