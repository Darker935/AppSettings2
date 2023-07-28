package com.darker.appsettings.xposed;

import static com.darker.appsettings.xposed.Utils.isModuleEnabled;

import android.content.res.XModuleResources;
import android.os.Bundle;

import com.darker.appsettings.Constants;
import com.darker.appsettings.MainActivity;
import com.darker.appsettings.xposed.hooks.screen.ScreenSettings;

import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
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

        XposedBridge.log("Zygote - Package name -> " + Constants.PACKAGE_NAME);
        XposedBridge.log("Zygote - MainActivity -> " + MainActivity.class.getName());

        if (prefs != null) {
            prefs.reload();
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("Loading package -> " + lpparam.packageName);
        XposedBridge.log("Package name -> " + Constants.PACKAGE_NAME);

        if (lpparam.packageName.equals(Constants.PACKAGE_NAME)) {
            XposedHelpers.findAndHookMethod(
                    MainActivity.class.getName(),
                    lpparam.classLoader,
                    "onCreate",
                    Bundle.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("On create (before/before super): isAppEnabled = " + isModuleEnabled());
                            super.beforeHookedMethod(param);
                            XposedBridge.log("On create (before/after super): isAppEnabled = " + isModuleEnabled());
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("On create (after/before super): isAppEnabled = " + isModuleEnabled());
                            super.afterHookedMethod(param);
                            XposedBridge.log("On create (after/after super): isAppEnabled = " + isModuleEnabled());
                        }
                    }
            );
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
//        XposedBridge.log("Before hook: isAppEnabled = " + isModuleEnabled());
        XposedHelpers.findAndHookMethod(
                MainActivity.class.getName(),
                lpparam.classLoader,
                "isModuleEnabled",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(true);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("After hook / before super: " + param.getResult());
                        super.afterHookedMethod(param);
                        XposedBridge.log("After hook / after super: " + param.getResult());
                    }
                }
        );
//        XposedBridge.log("After hook: isAppEnabled = " + isModuleEnabled());
//        XposedBridge.log("MainActivity: " + MainActivity.class.getName());
    }

    public static XSharedPreferences getModulePrefs() {
        if (prefs != null) return prefs;
        else {
            XSharedPreferences pref = new XSharedPreferences(Constants.PACKAGE_NAME, Constants.PREFS_NAME);
            return pref.getFile().canRead() ? pref : null;
        }
    }
}
