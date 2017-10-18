package com.music.android.ui.mvp.main;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by liuyun on 17/4/13.
 */

public class SpacesLinearItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesLinearItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);
        switch (position) {
            case 0:
                outRect.top = 0;
                outRect.bottom = 0;
                break;
            case 1:
                outRect.top = space;
                outRect.bottom = space;
                break;
            case 2:
                outRect.top = 0;
                outRect.bottom = 0;
                break;
        }
    }
}
