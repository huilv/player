package com.music.android.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


/**
 * Created by Administrator on 2017/4/12.
 */

public class ShareHelper {
    private static final String EMAIL = "ylivecustomer@gmail.com";

    public static void startShare(Context context) {
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:" + EMAIL));
        try {
            context.startActivity(data);
        }catch (Exception e){}

    }
}
