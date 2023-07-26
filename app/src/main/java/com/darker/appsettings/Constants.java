package com.darker.appsettings;

import com.darker.appsettings.app.ui.activities.PerAppSettings;

public class Constants {
    public static String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    public static String PREFS_NAME = PerAppSettings.class.getName().replace(PACKAGE_NAME, "").substring(1);
}
