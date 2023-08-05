package com.darker.appsettings.xposed;

import java.io.DataOutputStream;
import java.io.IOException;

import de.robv.android.xposed.XSharedPreferences;

public class Utils {
    public static boolean isModuleEnabled() {
        return false;
    }

    public static void setReadable(XSharedPreferences pref) {
        try {
            Process suProcess = Runtime.getRuntime().exec("su");
            DataOutputStream stream = new DataOutputStream(suProcess.getOutputStream());
            stream.writeBytes("chmod 777 " + pref.getFile().getAbsolutePath() + "\n");
            stream.flush();
            stream.writeBytes("exit" + "\n");
            stream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
