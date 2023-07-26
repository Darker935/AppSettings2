package com.darker.appsettings.app.ui.activities

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.darker.appsettings.R
import com.darker.appsettings.app.ui.activities.PerAppSettings.Companion.app_package
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app_title = intent.getStringExtra("title") ?: packageName
        app_package = intent.getStringExtra("packageName") ?: packageName
        app_icon = packageManager.getApplicationIcon(app_package)
        sharedPrefs = getPreferences(MODE_PRIVATE)

        val prefsEnabledApps = sharedPrefs.getStringSet("enabled_apps", setOf())!!
        val enabledApps = HashSet<String>(prefsEnabledApps)

        setContent {
            AppSettings2Theme {
                val isAppEnabled = remember { mutableStateOf(enabledApps.contains(app_package)) }

                MainContent(app_title, app_icon, this) {

                    // Adding button to change module state (on / off)
                    item { SwitchButton(enabledApps, sharedPrefs, app_package, isAppEnabled) }

                    if (!isAppEnabled.value) {
                        item { AppDisabledWarning() }
                    } else {
                        item {
                            val dpi_value = remember { getIntMutable(sharedPrefs, "dpi") }
                            val font_size = remember { getIntMutable(sharedPrefs, "font") }
                            CardModel {
                                EditIntPreference(app_package, "dpi", dpi_value, "DPI (dp)")
                                EditIntPreference(app_package, "font", font_size, "FONT (%)", 100)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppDisabledWarning() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.app_not_enabled),
            contentDescription = "App not enabled",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary.copy(0.7f)),
            modifier = Modifier.size(80.dp)
        )
        Text(
            fontSize = 19.sp,
            text = "Click on Switch to enable module",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary.copy(0.7f),
            modifier = Modifier.padding(top = 25.dp)
        )
    }
}

@Composable
fun CardModel(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 20.dp)
    ) {
        Card(
            elevation = CardDefaults.elevatedCardElevation(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    content()
                }
            }
        }
    }
}

fun getIntMutable(sharedPrefs: SharedPreferences, key: String): MutableIntState {
    return mutableIntStateOf(sharedPrefs.getInt("${app_package}_${key}", 0))
}

@Composable
fun EditIntPreference(
    packageName: String,
    preference: String,
    value: MutableIntState,
    label: String,
    maxValue: Int = 0
) {
    val pref = "${packageName}_${preference}"
    TextField(
        label = { Text(label) },
        placeholder = { Text(value.intValue.toString()) },
        value = "${if (value.intValue == 0) "" else value.intValue}",
        onValueChange = {
            val intValue = it.ifEmpty { "0" }.filter { c -> c.isDigit() }.toInt()
            if (maxValue != 0 && intValue > maxValue) {
                value.value = maxValue
            } else {
                value.value = intValue
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(vertical = 4.dp, horizontal = 10.dp)
            .onFocusChanged {
                if (!it.isFocused) {
                    editPrefs(pref, value.intValue)
                }
            }
    )
    Spacer(Modifier.height(6.dp))
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
    packageName: String,
    isAppEnabled: MutableState<Boolean>
) {

    fun updatePrefs(b: Boolean) {
        isAppEnabled.value = b
        val editor = sharedPrefs.edit()
        if (b) {
            enabledApps.add(packageName)
        } else {
            enabledApps.remove(packageName)
        }
        editor.putStringSet("enabled_apps", enabledApps)
        editor.apply()
        editor.commit()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 12.dp)
            .background(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(25.dp)
            )
            .clickable { updatePrefs(!isAppEnabled.value) }
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
            checked = isAppEnabled.value,
            onCheckedChange = { updatePrefs(it) }
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