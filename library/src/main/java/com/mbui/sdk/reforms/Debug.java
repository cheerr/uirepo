package com.mbui.sdk.reforms;

import android.util.Log;

import com.mbui.sdk.configs.UIConfig;

/**
 * Created by chenwei on 15/1/14.
 */
public class Debug {

    public static void e(String debug, String message) {
        Log.e(debug, message);
    }

    public static void i(String debug, String message) {
        if (UIConfig.DEBUG) Log.i(debug, message);
    }

    public static void d(String debug, String message) {
        if (UIConfig.DEBUG) Log.d(debug, message);
    }

    public static void print(String debug, String message) {
        if (UIConfig.DEBUG) Log.d(debug, message);
    }

    public static void debug(String message) {
        if (UIConfig.DEBUG) Log.d("debug", message);
    }
}
