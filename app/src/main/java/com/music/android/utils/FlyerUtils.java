package com.music.android.utils;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

public class FlyerUtils {


    public static String getIMEI(Context context) {
        String imei = "";
        try {
            TelephonyManager collectAndroidId = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = (String) collectAndroidId.getClass().getMethod("getDeviceId", new Class[0]).invoke(collectAndroidId, new Object[0]);
            if (imei == null) imei = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }

    public static String getAndroidId(Context context) {
        String androidId = null;
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {

        }
        return androidId == null ? "" : androidId;
    }
}
