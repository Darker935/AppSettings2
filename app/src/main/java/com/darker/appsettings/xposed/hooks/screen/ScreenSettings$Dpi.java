package com.darker.appsettings.xposed.hooks.screen;

import static com.darker.appsettings.xposed.Core.prefs;
import static com.darker.appsettings.xposed.hooks.screen.ScreenSettings.configuration;
import static com.darker.appsettings.xposed.hooks.screen.ScreenSettings.metrics;

public class ScreenSettings$Dpi {
    public static String key_ = "dpi";

    public static void hook(String packageName) {
        int wdp = prefs.getInt(packageName + "_dpi", 0);
        float density = (float) metrics.widthPixels / wdp;
        int hdp = (int) (metrics.heightPixels / density);
        int dpi = (int) (160 * density);

        metrics.density = density;
        metrics.densityDpi = dpi;
        configuration.densityDpi = dpi;
        configuration.smallestScreenWidthDp = wdp;
        configuration.screenHeightDp = hdp;
    }

}
