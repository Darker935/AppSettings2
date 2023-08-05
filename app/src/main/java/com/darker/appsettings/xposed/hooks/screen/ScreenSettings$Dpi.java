package com.darker.appsettings.xposed.hooks.screen;

import static com.darker.appsettings.xposed.Core.prefs;
import static com.darker.appsettings.xposed.hooks.screen.ScreenSettings.configuration;
import static com.darker.appsettings.xposed.hooks.screen.ScreenSettings.metrics;

import android.util.DisplayMetrics;

import com.darker.appsettings.xposed.hooks.MainHooks;

import de.robv.android.xposed.XposedHelpers;

public class ScreenSettings$Dpi {
    public static String key_ = "dpi";

    public static void hook(String packageName) {
        int wdp = prefs.getInt(packageName + "_dpi", 0);
        float density = (float) metrics.widthPixels / wdp;
        int hdp = (int) (metrics.heightPixels / density);
        int dpi = (int) (160 * density);

        metrics.widthPixels = 1080;
        metrics.heightPixels = 1080;
        metrics.density = density;
        metrics.scaledDensity = density;
        metrics.densityDpi = dpi;
        configuration.densityDpi = dpi;
        configuration.smallestScreenWidthDp = wdp;
        configuration.screenHeightDp = hdp;

        XposedHelpers.setFloatField(metrics, "density", density);
        XposedHelpers.setIntField(metrics, "densityDpi", dpi);
        XposedHelpers.setIntField(configuration, "densityDpi", dpi);
        XposedHelpers.setIntField(configuration, "screenHeightDp", hdp);
        XposedHelpers.setIntField(configuration, "smallestScreenWidthDp", wdp);

    }

    public static DisplayMetrics hook(MainHooks xcMethodHook, DisplayMetrics result) {
        int wdp = prefs.getInt(xcMethodHook.lpparam.packageName + "_dpi", 0);
        float density = (float) result.widthPixels / wdp;
        int hdp = (int) (result.heightPixels / density);
        int dpi = (int) (160 * density);

        result.density = density;
        result.densityDpi = dpi;

        return result;
    }
}
