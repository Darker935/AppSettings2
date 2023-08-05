package com.darker.appsettings.xposed.hooks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

import com.darker.appsettings.xposed.hooks.screen.ScreenSettings;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHooks {

    public XC_LoadPackage.LoadPackageParam lpparam;
    public XC_MethodHook.MethodHookParam param;
    public Context context;

    @SuppressLint("PrivateApi")
    public MainHooks(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        this.lpparam = lpparam;
        MainHooks thisClass = this;
        XposedHelpers.findAndHookConstructor(
                "android.content.res.ResourcesImpl",
                lpparam.classLoader,
                Class.forName("android.content.res.AssetManager"),
                DisplayMetrics.class, Configuration.class,
                Class.forName("android.view.DisplayAdjustments"),
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        var displayMetrics = (DisplayMetrics) param.args[1];
                        var configuration = (Configuration) param.args[2];

                        ScreenSettings.hook(lpparam, displayMetrics, configuration);

                        param.args[1] = ScreenSettings.metrics;
                        param.args[2] = ScreenSettings.configuration;

                        // android.content.res.CompatibilityInfo
                        Object compatibilityInfo = XposedHelpers.callMethod(param.args[3], "getCompatibilityInfo");

                        XposedBridge.log("[-] Display - Dpi: " + ScreenSettings.metrics.densityDpi);
                        XposedBridge.log("[-] Display - Density: " + ScreenSettings.metrics.density);
                        XposedBridge.log("[-] Config - Density DPI: " + ScreenSettings.configuration.densityDpi);
                        XposedBridge.log("[-] Config - Smallest width: " + ScreenSettings.configuration.smallestScreenWidthDp);
                        XposedBridge.log("[-] Config - Scree width DP: " + ScreenSettings.configuration.screenWidthDp);
                        XposedBridge.log("[-] Config - Screen height DP: " + ScreenSettings.configuration.screenHeightDp);

                        /**
                         *  Trying to override ALL in constructor, but no changes
                         *
                         *  All methods is here:
                         *  https://android.googlesource.com/platform/frameworks/base/+/refs/heads/main/core/java/android/content/res/ResourcesImpl.java}
                         */
                        XposedHelpers.callMethod(
                                param.thisObject,
                                "updateConfiguration",
                                new Class<?>[]{
                                        Configuration.class,
                                        DisplayMetrics.class,
                                        Class.forName("android.content.res.CompatibilityInfo")
                                },
                                param.args[2], param.args[1], compatibilityInfo
                        );
                    }
                }
        );
    }
}
