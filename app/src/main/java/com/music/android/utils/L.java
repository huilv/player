package com.music.android.utils;

import android.util.Log;

import com.music.android.BuildConfig;

/**
 * Created by liuyun on 17/3/13.
 */

public class L {

    public static final boolean DEBUG = BuildConfig.DEBUG;

    static final String TAG = "supo_music";

    public static void d(Object o) {
        if (DEBUG || Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, String.valueOf(o));
        }
    }

    public static void e(Object o) {
        if (DEBUG || Log.isLoggable(TAG, Log.DEBUG)) {
            Log.e(TAG, String.valueOf(o));
        }

    }

    public static void d(String tag, Object o) {
        if (DEBUG || Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, String.valueOf(o));
        }

    }

    public static void e(String tag, Object o) {
        if (DEBUG || Log.isLoggable(tag, Log.ERROR)) {
            Log.e(tag, String.valueOf(o));
        }

    }

    public static void i(String tag, Object o) {
        if (DEBUG || Log.isLoggable(tag, Log.DEBUG)) {
            String xml = String.valueOf(o);
            Log.i(tag, xml);
        }
    }

    private static int LOG_MAXLENGTH = 2000;

    public static void e(String TAG, String msg) {
        int strLength = msg.length();
        int start = 0;
        int end = LOG_MAXLENGTH;
        for (int i = 0; i < 100; i++) {
            if (strLength > end) {
                Log.e(TAG + i, msg.substring(start, end));
                start = end;
                end = end + LOG_MAXLENGTH;
            } else {
                Log.e(TAG, msg.substring(start, strLength));
                break;
            }
        }
    }
}
