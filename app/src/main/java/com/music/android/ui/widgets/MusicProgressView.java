package com.music.android.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.music.android.R;
import com.music.android.utils.L;

/**
 * Created by liuyun on 17/3/27.
 */

public class MusicProgressView extends View {

    private Paint mPaint;

    private float mOffset = -1;

    public MusicProgressView(Context context) {
        super(context);
        init();
    }

    public MusicProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MusicProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(getContext().getResources().getColor(R.color.progress_bg));
        mPaint.setColor(getContext().getResources().getColor(R.color.tab_indicator));
        canvas.drawRect(0, 0, mOffset, getHeight(), mPaint);
    }

    public void setProgress(long currentProgress, long maxProgress) {
        if (currentProgress > 0 && maxProgress > 0) {
            mOffset = getWidth() * currentProgress / maxProgress;
            invalidate();
        } else {
            mOffset = 0;
            invalidate();
        }
    }
}
