package com.darker.appsettings

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.darker.appsettings.app.ui.controller.NavigationDrawerController
import com.darker.appsettings.app.ui.dialogs.FrameworkNotRunning
import com.darker.appsettings.ui.theme.AppSettings2Theme
import com.darker.appsettings.xposed.Utils.isModuleEnabled

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, "enabled = ${isModuleEnabled()}", Toast.LENGTH_SHORT).show()
        setContent {
            AppSettings2Theme {
                if (!isModuleEnabled()) {
                    FrameworkNotRunning()
                } else {
                    // A surface container using the 'background' color from the theme
                    NavigationDrawerController()
                }
            }
        }
    }

}


