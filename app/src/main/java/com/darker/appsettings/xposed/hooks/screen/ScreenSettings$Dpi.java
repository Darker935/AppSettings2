package com.darker.appsettings.xposed.hooks.screen;

import static com.darker.appsettings.xposed.Core.prefs;

import android.content.res.Configuration;
import android.content.res.Resources;

public class ScreenSettings$Dpi {
    public static String key_ = "dpi";

    public static void hook(String packageName) {
        int value = prefs.getInt(packageName + "_dpi", 0);
        Resources cu = null;
        Configuration config = cu.getConfiguration();
    }

}
