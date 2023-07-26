package com.darker.appsettings.xposed.hooks.screen;

import static com.darker.appsettings.xposed.Core.prefs;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ScreenSettings {

    public static Resources resources;
    public static Configuration configuration;
    public static DisplayMetrics metrics;

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        String packageName = lpparam.packageName;
        XposedHelpers.findAndHookMethod(
                Activity.class,
                "attachBaseToContext",
                Context.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = (Context) param.args[0];
                        resources = context.getResources();
                        configuration = resources.getConfiguration();
                        metrics = resources.getDisplayMetrics();
                        param.args[0] = getHookedContext(context);
                        super.beforeHookedMethod(param);
                    }
                }
        );
    }

    private static Context getHookedContext(Context context) {
        ScreenSettings$Dpi.hook(context.getPackageName());
        applyChanges();
        return context.createConfigurationContext(configuration);
    }

    private static void applyChanges() {
        XposedHelpers.findAndHookMethod(
                Resources.class,
                "getConfiguration",
                XC_MethodReplacement.returnConstant(configuration)
        );
        XposedHelpers.findAndHookMethod(
                Resources.class,
                "getDisplayMetrics",
                XC_MethodReplacement.returnConstant(metrics)
        );
    }

    public static String getPrivacyS(String key, String defValue) {
        return prefs.getString(key, defValue);
    }

}
