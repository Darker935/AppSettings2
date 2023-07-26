package com.darker.appsettings.app.ui.activities

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.darker.appsettings.ui.theme.AppSettings2Theme
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class PerAppSettings : ComponentActivity() {

    companion object {
        lateinit var app_icon: Drawable
        lateinit var app_title: String
        lateinit var app_package: String
        lateinit var sharedPrefs: SharedPreferences
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app_title = intent.getStringExtra("title") ?: packageName
        app_package = intent.getStringExtra("packageName") ?: packageName
        app_icon = packageManager.getApplicationIcon(app_package)
        sharedPrefs = getPreferences(MODE_PRIVATE)

        val prefsEnabledApps = sharedPrefs.getStringSet("enabled_apps", setOf())!!
        val enabledApps = HashSet<String>(prefsEnabledApps)
        val isAppEnabled = enabledApps.contains(packageName)

        setContent {
            AppSettings2Theme {
                MainContent(app_title, app_icon, this) {

                    item {
                        SwitchButton(enabledApps, sharedPrefs, packageName)
                    }

                    item {
                        var dpi_value = 0
                        var font_size = 0

                        EditIntPreference(app_package, "dpi", dpi_value, "DPI")
                        EditIntPreference(app_package, "font", font_size, "FONT")
                    }
                }
            }
        }
    }
}

@Composable
fun EditIntPreference(
    packageName: String,
    preference: String,
    value: Int,
    label: String
) {
    val context = LocalContext.current
    var mutableValue by remember { mutableIntStateOf(value) }
    val pref = "${packageName}_${preference}"
    mutableValue = PerAppSettings.sharedPrefs.getInt(pref, 0)
    TextField(
        label = { Text(label) },
        placeholder = { Text(mutableValue.toString()) },
        value = "$mutableValue",
        onValueChange = { mutableValue = it.ifEmpty { "0" }.filter { c -> c.isDigit() }.toInt() },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.onFocusChanged {
            if (!it.isFocused) {
                editPrefs(pref, mutableValue)
                Toast.makeText(context, mutableValue.toString(), Toast.LENGTH_SHORT).show()
            }
        })
}

fun editPrefs(preference: String, value: Any) {
    PerAppSettings.sharedPrefs.edit(commit = true) {
        if (value is Int) putInt(preference, value)
        if (value is Long) putLong(preference, value)
        if (value is Float) putFloat(preference, value)
        if (value is String) putString(preference, value)
        if (value is Boolean) putBoolean(preference, value)
        apply()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    title: String,
    icon: Drawable,
    activity: PerAppSettings,
    content: LazyListScope.() -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Actionbar(scrollBehavior, title, icon, activity)
        },
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxHeight(),
                content = content
            )
        }
    )
}

@Composable
fun SwitchButton(
    enabledApps: HashSet<String>,
    sharedPrefs: SharedPreferences,
    packageName: String
) {
    var isAppEnabled by remember { mutableStateOf(enabledApps.contains(packageName)) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 12.dp)
            .background(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(25.dp)
            )
            .clickable {
                isAppEnabled = true
            }
    ) {
        Text(
            fontSize = 22.sp,
            text = "Enable module",
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .padding(start = 15.dp)
                .align(Alignment.CenterStart)
        )
        Switch(
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.CenterEnd),
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