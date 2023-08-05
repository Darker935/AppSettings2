package com.darker.appsettings.app.utils

import android.util.Log
import com.darker.appsettings.Constants
import java.io.DataOutputStream

class Shell() {
    val stream: DataOutputStream

    init {
        val p = Runtime.getRuntime().exec("su")
        stream = DataOutputStream(p.outputStream)
    }

    fun run(cmd: String) {
        stream.writeBytes("${cmd}\n")
        stream.flush()
    }

    fun makeWorldReadable(path: String) {
        Log.i(Constants.TAG, "makeWorldReadable path: $path")
        run("chmod 777 $path")
    }
}