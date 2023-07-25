package com.darker.appsettings.app.ui.activities

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darker.appsettings.ui.theme.AppSettings2Theme
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class PerAppSettings : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
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
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = { Actionbar(scrollBehavior, title, icon, this) },
                    content = { innerPadding ->
                        LazyColumn(
                            contentPadding = innerPadding,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            item {
                                SwitchButton(enabledApps, sharedPrefs, packageName)
                            }
                        }
                    }
                )
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
    var isAppEnabled by remember { mutableStateOf(enabledApps.contains(packageName)) }
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(vertical = 20.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Text(
            text = "Enable module",
            fontSize = 24.sp,
            modifier = Modifier.padding(start = 15.dp),
        )
        Switch(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.End),
            checked = isAppEnabled,
            onCheckedChange = {
                isAppEnabled = it
                val editor = sharedPrefs.edit()
                if (it) {
                    enabledApps.add(packageName)
                } else {
                    enabledApps.remove(packageName)
                }
                editor.putStringSet("enabled_apps", enabledApps)
                editor.apply()
                editor.commit()
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StatusBar(scrollBehavior: TopAppBarScrollBehavior) {
    val systemUiController = rememberSystemUiController()
    val alpha = scrollBehavior.state.collapsedFraction
    val fgColor = TopAppBarDefaults.largeTopAppBarColors().scrolledContainerColor
    val currentColor = fgColor.copy(alpha = alpha)
    systemUiController.setStatusBarColor(currentColor)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Actionbar(
    scrollBehavior: TopAppBarScrollBehavior,
    title: String,
    icon: Drawable,
    activity: ComponentActivity
) {

    // Configure status bar to follow app bar color
    StatusBar(scrollBehavior)

    LargeTopAppBar(
        title = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    modifier = Modifier
                        .size(60.dp)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .align(Alignment.CenterVertically),
                    painter = rememberDrawablePainter(icon),
                    contentDescription = title
                )
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
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
        scrollBehavior = scrollBehavior
    )
}