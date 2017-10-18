package com.music.android.ui.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.music.android.R;
import com.music.android.utils.L;
import com.music.android.utils.SizeUtils;

/**
 * Created by liuyun on 17/4/6.
 */

public class RoundIndicatorView extends View {

    private Paint mPaint;

    private RectF mRectF;

    private RectF mLayerRectF;

    private int radius = 0;

    private float mOffset = 0;

    public RoundIndicatorView(Context context) {
        super(context);
        init();
    }

    public RoundIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFakeBoldText(true);
        mRectF = new RectF();
        mLayerRectF = new RectF();
        radius = SizeUtils.dp2Px(getContext(), 15);
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
        mRectF.left = 0;
        mRectF.top = 0;
        mRectF.right = getWidth();
        mRectF.bottom = getHeight();
        mPaint.setColor(getContext().getResources().getColor(R.color.round_indicator_bg));
        canvas.drawRoundRect(mRectF, radius, radius, mPaint);

        mLayerRectF.left = computeScrollWidth();
        mLayerRectF.top = 0;
        mLayerRectF.right = getWidth() / 2 + computeScrollWidth();
        mLayerRectF.bottom = getHeight();
        mPaint.setColor(getContext().getResources().getColor(R.color.radius_green_bg));
        canvas.drawRoundRect(mLayerRectF, radius, radius, mPaint);
    }

    private float computeScrollWidth() {
        return getWidth() / 2 * mOffset;
    }

}
