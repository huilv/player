package com.music.android.ui.widgets;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.music.android.R;
import com.music.android.utils.SizeUtils;

/**
 * Created by hui.lv on 2017/3/22.
 */

public class TipToast {
    private Toast mToast;
    private TipToast(Context context, CharSequence text, int duration) {
        View v = LayoutInflater.from(context).inflate(R.layout.toast, null);
        TextView textView = (TextView) v.findViewById(R.id.tv);
        textView.setText(text);
        mToast = new Toast(context);
        mToast.setDuration(duration);
        mToast.setView(v);
        setGravity(context);
    }

    public static TipToast makeText(Context context, CharSequence text, int duration) {
        return new TipToast(context, text, duration);
    }
    public void show() {
        if (mToast != null) {
            mToast.show();
        }
    }
    public void setGravity(Context context) {
        if (mToast != null) {
            mToast.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2Px(context,20));
        }
    }
}
