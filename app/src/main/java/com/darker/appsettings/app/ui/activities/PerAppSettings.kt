package com.darker.appsettings.app.ui.activities

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darker.appsettings.ui.theme.AppSettings2Theme
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class PerAppSettings : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = intent.getStringExtra("title") ?: packageName
        val packageName = intent.getStringExtra("packageName") ?: packageName
        val icon = packageManager.getApplicationIcon(packageName)
        val sharedPrefs = getPreferences(MODE_PRIVATE)
        val prefsEnabledApps = sharedPrefs.getStringSet("enabled_apps", setOf())!!
        val enabledApps = HashSet<String>(prefsEnabledApps)
        val isAppEnabled = enabledApps.contains(packageName)

        setContent {
            AppSettings2Theme {
                Actionbar(title, icon, this)
                SwitchButton(enabledApps, sharedPrefs, packageName)
            }
        }
    }
}

@Composable
fun SwitchButton(
    enabledApps: HashSet<String>,
    sharedPrefs: SharedPreferences,
    packageName: String
) {
    val isAppEnabled = enabledApps.contains(packageName)
    Switch(
        modifier = Modifier.padding(vertical = 8.dp),
        checked = isAppEnabled,
        onCheckedChange = {
            val editor = sharedPrefs.edit()
            if (it) {
                enabledApps.add(packageName)
            } else {
                enabledApps.remove(packageName)
            }
            editor.putStringSet("enabled_apps", enabledApps)
            editor.apply()
            editor.commit()
        })
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StatusBar() {
    val systemUiController = rememberSystemUiController()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val alpha = scrollBehavior.state.collapsedFraction
    val fgColor = TopAppBarDefaults.largeTopAppBarColors().scrolledContainerColor
    val currentColor = fgColor.copy(alpha = alpha)
    systemUiController.setStatusBarColor(currentColor)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Actionbar(title: String, icon: Drawable, activity: ComponentActivity) {

    // Configure status bar to follow app bar color
    StatusBar()

    LargeTopAppBar(
        title = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    modifier = Modifier
                        .size(60.dp)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(7.dp)),
                    painter = rememberDrawablePainter(icon),
                    contentDescription = title
                )
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }, navigationIcon = {
            IconButton(onClick = { activity.onBackPressed() }) {
                Icon(Icons.TwoTone.ArrowBack, "Back")
            }
        },
        actions = {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Filled.CheckCircle, "Save")
            }
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}