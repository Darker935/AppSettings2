package com.darker.appsettings.xposed;

import android.annotation.SuppressLint;
import android.os.Environment;

import com.darker.appsettings.Constants;

import java.io.File;
import java.nio.file.Paths;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XSharedPrefs extends Object {
    public XSharedPrefs(XC_LoadPackage.LoadPackageParam lpparam, String packageName) {
        changePrefsPath(lpparam, packageName);
    }

    @SuppressLint("PrivateApi")
    public void changePrefsPath(XC_LoadPackage.LoadPackageParam lpparam, String packageName) {
        try {
            XposedHelpers.findAndHookMethod(
                    Class.forName("android.app.ContextImpl").getName(),
                    lpparam.classLoader,
                    "getSharedPreferencesPath",
                    String.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                            XposedBridge.log("-> Param args[0]: " + param.args[0]);
                            XposedBridge.log("-> Prefs name: " + Constants.PREFS_NAME);
                            if (param.args[0].equals(Constants.PREFS_NAME)) {
                                XposedBridge.log("(param args == prefs name)");
                                File newFile = new File(Paths.get(
                                        Environment.getExternalStorageDirectory().getPath(),
                                        ".xposed_prefs",
                                        Constants.PACKAGE_NAME,
                                        Constants.PREFS_NAME + ".xml"
                                ).toString());

                                XposedBridge.log("-> New File path: " + newFile.getAbsolutePath());

                                if (!newFile.exists()) {
                                    XposedBridge.log("-> Mkdirs: " + newFile.mkdirs());
                                    XposedBridge.log("-> Now (after mkdirs) file exists: " + newFile.exists());
                                }

                                XposedHelpers.setObjectField(param.thisObject, "mPreferencesDir", newFile);

                                File file2 = (File) XposedHelpers.findField(param.thisObject.getClass(), "mPreferencesDir").get(param.thisObject);
                                XposedBridge.log("\nAbsolute file path (xposed): " + newFile.getAbsolutePath());
                                XposedBridge.log("\nXposed file can be readed: " + newFile.canRead());
                                XposedBridge.log("\nIs field null: " + (file2 == null));
                                param.setResult(newFile);
                            }
                        }
                    }
            );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
