package com.darker.appsettings.app.utils

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.rememberCoroutineScope
import com.darker.appsettings.app.ui.dialogs.totalApps
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

var packages: MutableList<PackageInfo>? = null
var appList = mutableListOf<ApplicationInfo>()

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: String
)

@Composable
fun LoadApps(pm: PackageManager, loadedApps: Int, onProgressUpdate: () -> Unit) {
    val scope = rememberCoroutineScope()
    if (packages == null) {
        packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        totalApps = packages!!.size
    }

    for (info in packages!!) {
        Log.i("• Loading LoadApps:", "loadedApps=${loadedApps} - totalApps=$totalApps")
        val appInfo = info.applicationInfo
        if (appInfo != null) {
            appInfo.name = appInfo.loadLabel(pm).toString()
        }
        if (loadedApps == totalApps) {
            appList.sortWith(compareBy { it.name })
        }
        onProgressUpdate()
    }
}
