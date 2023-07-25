package com.darker.appsettings.app.utils

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import com.darker.appsettings.app.ui.dialogs.totalApps

var packages: MutableList<PackageInfo>? = null
var appList = mutableListOf<ApplicationInfo>()

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: String
)

fun LoadApps(pm: PackageManager, loadedApps: MutableLiveData<Int>, onProgressUpdate: () -> Unit) {
    if (packages == null) {
        packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        totalApps = packages!!.size
    }

    for (info in packages!!) {
        val appInfo = info.applicationInfo
        if (appInfo != null) {
            appInfo.name = appInfo.loadLabel(pm).toString()
        }
        if (loadedApps.value == totalApps) {
            appList.sortWith(compareBy { it.name })
        }
        onProgressUpdate()
    }
}
