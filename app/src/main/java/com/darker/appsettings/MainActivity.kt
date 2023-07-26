package com.darker.appsettings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.darker.appsettings.app.ui.controller.NavigationDrawerController
import com.darker.appsettings.ui.theme.AppSettings2Theme

class MainActivity : ComponentActivity() {

    companion object {
        fun isModuleActive(): Boolean {
            return false;
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppSettings2Theme {
                // A surface container using the 'background' color from the theme
                NavigationDrawerController()
            }
        }
    }
}


