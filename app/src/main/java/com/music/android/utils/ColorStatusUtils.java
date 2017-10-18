package com.music.android.utils;

import android.app.Activity;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by hui.lv on 2017/3/23.
 */

public class ColorStatusUtils {
    public static void buildPadding(Activity activity){
        ViewGroup contentFrameLayout = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        View parentView = contentFrameLayout.getChildAt(0);
        if (parentView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
            parentView.setFitsSystemWindows(true);
        }
    }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window window = getWindow();
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
}
