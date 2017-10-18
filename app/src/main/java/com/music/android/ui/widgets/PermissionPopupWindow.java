package com.music.android.ui.widgets;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.android.gms.cover.CoverSdk;
import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.managers.ImageLoaderManager;

/**
 * Created by liuyun on 17/6/6.
 */

public class PermissionPopupWindow extends PopupWindow implements View.OnClickListener {

    private ImageView gif_ImageView;

    private Button got_it_btn;

    private Context context;

    public PermissionPopupWindow(Context context) {
        this.context = context;
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_usage, null);

        gif_ImageView = (ImageView) view.findViewById(R.id.gif_ImageView);
        got_it_btn = (Button) view.findViewById(R.id.got_it_btn);
        ImageLoaderManager.imageLoaderAssents(gif_ImageView, "music.gif");
        got_it_btn.setOnClickListener(this);

        setContentView(view);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setFocusable(true);
        setAnimationStyle(R.style.fade_animation);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        setTouchable(true);
    }

    @Override
    public void onClick(View v) {
        CoverSdk.goToUsageStatsSettings(context);
        dismiss();
    }
}
