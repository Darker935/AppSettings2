package com.darker.appsettings.app.ui.model

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darker.appsettings.app.ui.dialogs.appList
import com.darker.appsettings.app.ui.dialogs.totalApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AppsInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable
)

class LoadingAppsThreadModel : ViewModel() {
    // Dialog box
    var packages: MutableList<PackageInfo>? = null

    fun startThread(pm: PackageManager, loadedApps: MutableState<Int>, onFinish: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                if (packages == null) {
                    packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
                    totalApps = packages!!.size
                }

                for (info in packages!!) {
                    val appInfo = info.applicationInfo
                    if (appInfo != null) {
                        appInfo.name = appInfo.loadLabel(pm).toString()
                    }

                    appList.add(
                        AppsInfo(
                            appInfo.name,
                            appInfo.packageName,
                            pm.getApplicationIcon(appInfo)
                        )
                    )

                    loadedApps.value++
//                    Log.i(Constants.TAG, "• Thread > loadedApps=${loadedApps.value} / totalApps=$totalApps")
                }

            }
            onFinish()
        }
    }
}