package com.darker.appsettings.xposed.hooks.screen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.darker.appsettings.xposed.hooks.MainHooks;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ScreenSettings {

    public static Resources resources;
    public static Configuration configuration;
    public static DisplayMetrics metrics;

    @SuppressLint("PrivateApi")
    public static void hook(MainHooks mainHooks) throws ClassNotFoundException {

        Context context = (Context) mainHooks.param.args[0];
        resources = context.getResources();
        configuration = resources.getConfiguration();
        metrics = resources.getDisplayMetrics();
        mainHooks.param.args[0] = getHookedContext(context, mainHooks.lpparam);
        XposedHelpers.findAndHookMethod(
                "android.app.ConfigurationController",
                mainHooks.lpparam.classLoader,
                "updateDefaultDensity",
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[0] = configuration.densityDpi;
                        super.beforeHookedMethod(param);
                    }
                }
        );
        XposedHelpers.findAndHookMethod(
                "android.app.ConfigurationController",
                mainHooks.lpparam.classLoader,
                "handleConfigurationChanged",
                Configuration.class,
                Class.forName("android.content.res.CompatibilityInfo"),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[0] = configuration;
                        super.beforeHookedMethod(param);
                    }
                }
        );
        XposedBridge.log(context.getPackageName() + " is hooked on " + mainHooks.param.method.getName());

    }

    private static Context getHookedContext(Context context, XC_LoadPackage.LoadPackageParam lpparam) {
        ScreenSettings$Dpi.hook(context.getPackageName());
        applyChanges(lpparam, context);
        metrics.setTo(metrics);
        return context.createConfigurationContext(configuration);
    }

    @SuppressLint("PrivateApi")
    private static void applyChanges(XC_LoadPackage.LoadPackageParam lpparam, Context context) {
        try {
            XposedHelpers.findAndHookMethod(
                    "android.app.ResourcesManager",
                    lpparam.classLoader,
                    "getConfiguration",
                    XC_MethodReplacement.returnConstant(configuration)
            );
            XposedHelpers.findAndHookMethod(
                    Class.forName("android.app.ResourcesManager"),
                    "getDisplayMetrics",
                    int.class,
                    Class.forName("android.view.DisplayAdjustments"),
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Toast.makeText(context, "getDisplayMetrics", Toast.LENGTH_SHORT).show();
                            param.setResult(metrics);
                        }
                    }
            );
            XposedHelpers.findAndHookMethod(
                    Resources.class.getName(),
                    lpparam.classLoader,
                    "updateConfiguration",
                    Configuration.class,
                    DisplayMetrics.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Toast.makeText(context, "updateConfiguration", Toast.LENGTH_SHORT).show();
                            XposedBridge.log("-> Updating configuration");
                            super.afterHookedMethod(param);
                        }
                    }
            );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        resources.updateConfiguration(configuration, metrics);
    }

    public static DisplayMetrics hook(MainHooks xcMethodHook, DisplayMetrics result) {
        return ScreenSettings$Dpi.hook(xcMethodHook, result);
    }

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam, DisplayMetrics displayMetrics, Configuration configs) {
        configuration = configs;
        metrics = displayMetrics;
        ScreenSettings$Dpi.hook(lpparam.packageName);
    }
}
