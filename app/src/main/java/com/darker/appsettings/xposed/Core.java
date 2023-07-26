package com.darker.appsettings.xposed;

import android.content.res.XModuleResources;

import com.darker.appsettings.Constants;
import com.darker.appsettings.MainActivity;
import com.darker.appsettings.xposed.hooks.screen.ScreenSettings;

import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Core implements IXposedHookZygoteInit, IXposedHookLoadPackage {

    public static XModuleResources resources;
    public static XSharedPreferences prefs;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        resources = XModuleResources.createInstance(startupParam.modulePath, null);
        prefs = getModulePrefs();

        if (prefs != null) {
            prefs.reload();
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(Constants.PACKAGE_NAME)) {
            modifyModuleState(lpparam);
        }

        if (prefs == null) return;
        else prefs.reload();
        Set<String> enabledApps = prefs.getStringSet("enabled_apps", new HashSet<>());

        if (enabledApps.contains(lpparam.packageName)) {
            ScreenSettings.hook(lpparam);
        }
    }

    private void modifyModuleState(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedBridge.log("IsModuleActive -> hooking");
        XposedHelpers.findAndHookMethod(
                MainActivity.class,
                "isModuleActive",
                XC_MethodReplacement.returnConstant(true));
    }

    public static XSharedPreferences getModulePrefs() {
        if (prefs != null) return prefs;
        else {
            XSharedPreferences pref = new XSharedPreferences(Constants.PACKAGE_NAME, Constants.PREFS_NAME);
            return pref.getFile().canRead() ? pref : null;
        }
    }
}
