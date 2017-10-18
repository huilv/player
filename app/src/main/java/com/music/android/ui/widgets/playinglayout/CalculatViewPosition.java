package com.music.android.ui.widgets.playinglayout;

import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.ui.widgets.MusicProgressView;
import com.music.android.utils.L;
import com.music.android.utils.PrefUtils;
import com.music.android.utils.SizeUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CTer on 17/4/27.
 */

public class CalculatViewPosition {

    public static final String IS_CALULATE = "calculate_complete";

    private ViewGroup mRootView;

    private int parentY;

    public CalculatViewPosition(ViewGroup view) {
        mRootView = view;
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int[] location = new int[2];
                mRootView.getLocationOnScreen(location);
                parentY = location[1];
                calculatPosition(mRootView);
            }
        });

    }

    private void calculatPosition(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();

        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                if (view instanceof ViewPager) {
                    continue;
                }
                calculatPosition((ViewGroup)view);
            } else {
                String tag = (String) view.getTag();
                if (!TextUtils.isEmpty(tag)) {
                    int x, y;
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    x = location[0];
                    y = location[1];

                    L.d("CalculatViewPosition", tag + ": x = " + x + ", view.getPaddingLeft() = " + view.getPaddingLeft() );

                    PrefUtils.putInt(MusicApp.context, tag + ViewTag.X, x + view.getPaddingLeft());
                    PrefUtils.putInt(MusicApp.context, tag + ViewTag.Y, y + view.getPaddingTop() - parentY);

                    PrefUtils.putInt(MusicApp.context, tag + ViewTag.W, view.getWidth() - view.getPaddingLeft() - view.getPaddingRight());
                    PrefUtils.putInt(MusicApp.context, tag + ViewTag.H, view.getHeight() - view.getPaddingTop() - view.getPaddingBottom());

                    if (view instanceof TextView) {
                        PrefUtils.putFloat(MusicApp.context, tag + ViewTag.S, ((TextView)view).getTextSize());
                    }


                }
            }
        }

        PrefUtils.putBoolean(MusicApp.context, IS_CALULATE, true);
    }

}
