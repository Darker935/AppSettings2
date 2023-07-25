package com.darker.appsettings.app.ui.model

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darker.appsettings.app.ui.dialogs.totalApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyViewModel : ViewModel() {
    // Dialog box
    var packages: MutableList<PackageInfo>? = null
    var appList = mutableListOf<ApplicationInfo>()

    fun startThread(pm: PackageManager, loadedApps: MutableState<Int>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
//                LoadApps(pm, loadedApps) {
//                    loadedApps.postValue(loadedApps.value!! + 1)
//                }
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
                    loadedApps.value++
                    delay(50L)
                    Log.i("Loaded apps", "loadedApps=${loadedApps.value}, totalApps=$totalApps")
//                    onProgressUpdate()
                }
            }
        }
    }
}