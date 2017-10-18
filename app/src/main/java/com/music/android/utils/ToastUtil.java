package com.music.android.utils;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.music.android.R;
import com.music.android.ui.widgets.TipToast;

public class ToastUtil {

    private static String oldMsg;
    protected static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;

    public static void showToast(Context context, String s) {
        try {


            if (toast == null) {
                View v = LayoutInflater.from(context).inflate(R.layout.toast, null);
                TextView textView = (TextView) v.findViewById(R.id.tv);
                textView.setText(s);
                toast = new Toast(context);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(v);
                toast.setGravity(Gravity.BOTTOM, 0, SizeUtils.dp2Px(context, 20));
                toast.show();
                oneTime = System.currentTimeMillis();
            } else {
                twoTime = System.currentTimeMillis();
                if (s.equals(oldMsg)) {
                    if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                        toast.show();
                    }
                } else {
                    toast.show();
                }
            }
            oneTime = twoTime;
        } catch (Exception E) {

        }
    }

}