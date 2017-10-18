package com.music.android.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.music.android.utils.L;
import com.music.android.utils.SizeUtils;

/**
 * Created by hui.lv on 2017/3/20.
 */

public class PlayListItemDecoration extends RecyclerView.ItemDecoration {

    private final Paint paint;
    private int dividerHeight = 2;
    private int left;

    public PlayListItemDecoration(Context context, int left) {
        dividerHeight = SizeUtils.dp2Px(context, 0.5F);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#2b2b2b"));
        this.left = left;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        int right = parent.getWidth();

        for (int i = 0; i < childCount ; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + dividerHeight;
            c.drawRect(left, top, right, bottom, paint);
            L.d("PlayListItemDecoration",view.getBottom()+"---"+bottom);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemCount = parent.getAdapter().getItemCount();
        int adapterPosition = parent.getChildAdapterPosition(view);
        if (itemCount == 1 || adapterPosition == itemCount - 1) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        outRect.set(0, 0, 0, dividerHeight);
    }
}
