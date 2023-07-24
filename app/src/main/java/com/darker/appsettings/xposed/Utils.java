package com.darker.appsettings.xposed;

public class Utils {
    public static boolean isAppEnabled() { return false; }

    public static void modifyEnabledStatus() {
//        XposedHelpers.findAndHookMethod(
//                Utils.class,
//                "isAppEnabled"
//        );
    }
}
