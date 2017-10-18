package com.music.android.ui.mvp.main;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.music.android.utils.L;

/**
 * Created by liuyun on 17/3/20.
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);
        switch (position) {
            case 0:
                outRect.left = 0;
                outRect.right = 0;
                break;
            case 1:
                outRect.left = space;
                outRect.right = space;
                break;
            case 2:
                outRect.left = 0;
                outRect.right = 0;
                break;
        }
    }
}
