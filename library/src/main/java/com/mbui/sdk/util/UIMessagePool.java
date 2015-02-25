package com.mbui.sdk.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by chenwei on 15/1/22.
 */
public class UIMessagePool {

    public static final String ACTION_MESSAGE_POOL = "ACTION_MESSAGE_POOL";

    public static final int MESSAGE = 0x41, THEME = 0x42, MAP = 0x43;

    public static void sendMessage(@NonNull Context context, String message) {
        Intent intent = new Intent(ACTION_MESSAGE_POOL);
        intent.putExtra("action", MESSAGE);
        intent.putExtra("message", message);
        context.sendBroadcast(intent);
    }

    public static void sendMessage(@NonNull Context context, String theme, String message) {
        Intent intent = new Intent(ACTION_MESSAGE_POOL);
        intent.putExtra("action", THEME);
        intent.putExtra("theme", theme);
        intent.putExtra("message", message);
        context.sendBroadcast(intent);
    }

    public static void sendMessage(@NonNull Context context, String theme, Map<String, String> map) {
        Intent intent = new Intent(ACTION_MESSAGE_POOL);
        intent.putExtra("action", MAP);
        intent.putExtra("theme", theme);
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        for (String s : map.keySet()) {
            keys.add(s);
            values.add(map.get(s));
        }
        intent.putStringArrayListExtra("keys", keys);
        intent.putStringArrayListExtra("values", values);
        context.sendBroadcast(intent);
    }
}
