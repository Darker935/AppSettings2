package com.darker.appsettings.xposed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.XModuleResources;

import com.darker.appsettings.Constants;
import com.darker.appsettings.xposed.hooks.MainHooks;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Core implements IXposedHookZygoteInit, IXposedHookLoadPackage {

    public static XModuleResources resources;
    public static XSharedPreferences prefs;

    @Override
    public void initZygote(StartupParam startupParam) {
        resources = XModuleResources.createInstance(startupParam.modulePath, null);
    }
    @SuppressLint("PrivateApi")
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        // If the loaded package is equals to module package, check if it's enabled on LSPosed
        if (lpparam.packageName.equals(Constants.PACKAGE_NAME)) {
            modifyModuleState(lpparam);
            makePrefsWorldReadable();
            return;
        }

        prefs = getModulePrefs();

        if (prefs == null) return;
        else prefs.reload();

        Set<String> enabledApps = prefs.getStringSet("enabled_apps", new HashSet<>());
        if (enabledApps.contains(lpparam.packageName)) {
            try {
                new MainHooks(lpparam);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressLint("PrivateApi")
    private void makePrefsWorldReadable() {
        try {
            XposedHelpers.findAndHookMethod(
                    Class.forName("android.app.ContextImpl"),
                    "setFilePermissionsFromMode",
                    String.class, int.class, int.class,
                    new XC_MethodHook() {
                        @SuppressLint("WorldReadableFiles")
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            var file = new File((String) param.args[0]);
                            if (file.getParentFile().getParent().endsWith("prefs")) {
                                param.args[1] = Context.MODE_WORLD_READABLE;
                            }
                            super.beforeHookedMethod(param);
                        }
                    }
            );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void modifyModuleState(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(
                Utils.class.getName(),
                lpparam.classLoader,
                "isModuleEnabled",
                XC_MethodReplacement.returnConstant(true)
        );
    }

    public static XSharedPreferences getModulePrefs() {
        if (prefs != null) return prefs;
        else {
            XSharedPreferences pref = new XSharedPreferences(Constants.PACKAGE_NAME, Constants.PREFS_NAME);
            XposedBridge.log("Prefs absolute path: " + pref.getFile().getAbsolutePath());
            XposedBridge.log("Prefs can be readed: " + pref.getFile().canRead());
            XposedBridge.log("Prefs is null: " + (pref == null));
            XposedBridge.log("Prefs getAll(): " + pref.getAll().toString());
            return pref.getFile().canRead() ? pref : null;
        }
    }
}
