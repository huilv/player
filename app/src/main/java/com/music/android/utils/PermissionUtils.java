package com.music.android.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by liuyun on 17/4/25.
 */

public class PermissionUtils {

    public static final int REQUEST_CODE = 66;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkSelfPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissions(AppCompatActivity appCompatActivity, String... strings) {
//        ActivityCompat.requestPermissions(appCompatActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        ActivityCompat.requestPermissions(appCompatActivity, new String[]{strings[0], strings[1]}, REQUEST_CODE);
    }

}
