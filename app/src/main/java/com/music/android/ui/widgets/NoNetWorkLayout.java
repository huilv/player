package com.music.android.ui.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.music.android.R;
import com.music.android.listener.OnRefreshListener;

/**
 * Created by liuyun on 17/4/10.
 */

public class NoNetWorkLayout extends LinearLayout implements View.OnClickListener {

    private OnRefreshListener mOnRefreshListener;

    public NoNetWorkLayout(Context context) {
        super(context);
        init();
    }

    public NoNetWorkLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoNetWorkLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.empty_no_network, this, true);
        TextView refresh_TextView = (TextView) view.findViewById(R.id.refresh_TextView);
        refresh_TextView.setOnClickListener(this);
    }


    public void setOnRefreshListener(OnRefreshListener mOnRefreshListener) {
        this.mOnRefreshListener = mOnRefreshListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refresh_TextView:
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
                break;
        }
    }
}
