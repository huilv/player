package com.music.android.ui.widgets;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.music.android.R;

/**
 * Created by liuyun on 17/3/21.
 */

public class LoadingLayout extends LinearLayout {

    private ImageView loading_ImageView;

    private AnimationDrawable animationDrawable;

    public LoadingLayout(Context context) {
        super(context);
        init();
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, this, true);
        loading_ImageView = (ImageView) view.findViewById(R.id.loading_ImageView);
        animationDrawable = (AnimationDrawable) loading_ImageView.getDrawable();
        this.setVisibility(View.INVISIBLE);
    }

    public void shown() {
        this.setVisibility(View.VISIBLE);
        animationDrawable.start();
    }

    public void hide() {
        this.setVisibility(View.INVISIBLE);
        animationDrawable.stop();
    }

}
