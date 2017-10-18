package com.music.android.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.music.android.R;
import com.music.android.utils.SizeUtils;

public class TabIndicatorView extends View {

    private Paint mPaint;

    private float mOffset;

    private int tabWidth;

    private int page = 3;

    public TabIndicatorView(Context context) {
        super(context);

        init();
    }

    public TabIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.tab_indicator));
        tabWidth = SizeUtils.dp2Px(getContext(), 50);
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setOffset(float offset) {
        if (offset != 0) {
            mOffset = offset;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float left = (getWidth() / page - tabWidth) / 2;
        canvas.drawRect(left + computeScrollWidth(), 0, left + computeScrollWidth() + tabWidth, getHeight(), mPaint);
    }

    private float computeScrollWidth() {
        return getWidth() / page * mOffset;
    }

}


