package com.darker.appsettings.xposed.hooks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
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
                null,
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

        XposedHelpers.findAndHookMethod(
                Class.forName("android.app.ContextImpl").getName(),
                lpparam.classLoader,
                "getResources",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        var resources = (Resources) param.getResult();
                        var config = resources.getConfiguration();
                        var metrics = resources.getDisplayMetrics();

                        ScreenSettings.hook(lpparam, metrics, config);

                        XposedBridge.log(
                                "\nDisplay metrics:\n"
                                        + "------- Before setResult Context impl ---------"
                                        + "\n(M) Density: " + metrics.density
                                        + "\n(M) Density DPI: " + metrics.densityDpi
                                        + "\n(C) Density DPI: " + config.densityDpi
                                        + "\n(C) Smallest Width: " + config.smallestScreenWidthDp
                                        + "\n(C) Screen height: " + config.screenHeightDp
                                        + "\n(C) Screen width: " + config.screenWidthDp
                        );
                        param.setResult(new Resources(resources.getAssets(), metrics, config));

                        resources = (Resources) param.getResult();
                        config = resources.getConfiguration();
                        metrics = resources.getDisplayMetrics();

                        XposedBridge.log(
                                "\n\nDisplayMetrics"
                                        + "------- After setResult Context impl ---------"
                                        + "\n(M) Density: " + metrics.density
                                        + "\n(M) Density DPI: " + metrics.densityDpi
                                        + "\n(C) Density DPI: " + config.densityDpi
                                        + "\n(C) Smallest Width: " + config.smallestScreenWidthDp
                                        + "\n(C) Screen height: " + config.screenHeightDp
                                        + "\n(C) Screen width: " + config.screenWidthDp
                        );
                    }
                }
        );

        XposedHelpers.findAndHookMethod(
                Activity.class.getName(),
                lpparam.classLoader,
                "onCreate",
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        var className = param.thisObject.getClass().getName();
                        var resources = ((Activity) param.thisObject).getResources();
                        var config = resources.getConfiguration();
                        var metrics = resources.getDisplayMetrics();

                        XposedBridge.log(
                                "\nDisplay metrics:\n\n\n\n\n\n-------- onCreate after hook -------------------"
                                        + "\n (" + className + ")"
                                        + "\n(M) Density: " + metrics.density
                                        + "\n(M) Density DPI: " + metrics.densityDpi
                                        + "\n(C) Density DPI: " + config.densityDpi
                                        + "\n(C) Smallest Width: " + config.smallestScreenWidthDp
                                        + "\n(C) Screen height: " + config.screenHeightDp
                                        + "\n(C) Screen width: " + config.screenWidthDp
                        );
                        super.afterHookedMethod(param);
                    }
                }
        );

//        XposedHelpers.findAndHookMethod(
//                Context.class.getName(),
//                lpparam.classLoader,
//                "getResources",
//                new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
////                        XposedBridge.log("Stack trace: \n\n" + TextUtils.join("\n",Thread.currentThread().getStackTrace()));
//                        super.beforeHookedMethod(param);
//                    }
//                }
//        );
    }
}
