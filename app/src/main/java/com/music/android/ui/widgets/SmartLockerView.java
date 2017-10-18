package com.music.android.ui.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.music.android.R;
import com.music.android.utils.SizeUtils;

/**
 * Created by hui.lv on 2017/6/8.
 */

public class SmartLockerView extends View {

    private String value;
    private Rect bounds;
    private Paint paint;
    private Paint mPaint;
    private int left;
    private ValueAnimator animator;
    private  int moveWith;
    public SmartLockerView(Context context) {
        super(context);
        init();
    }

    public SmartLockerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmartLockerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#fff0f0f0"));
        paint.setTextSize(SizeUtils.sp2px(getContext(),22));
        value = getResources().getString(R.string.unlock);
        bounds = new Rect();
        paint.getTextBounds(value,0,value.length(), bounds);
        moveWith  =bounds.width()/4;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#73ffffff"));
        mPaint.setTextSize(SizeUtils.sp2px(getContext(),22));
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
          setMeasuredDimension(bounds.width(),bounds.height());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        animator = ValueAnimator.ofInt(0,bounds.width());
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1000);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                left = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(animator!=null){
            animator.cancel();
            animator=null;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(value,0,bounds.height(),mPaint);
        canvas.save();
        canvas.clipRect(left,0,left+ moveWith,bounds.height());
        canvas.drawText(value,0,bounds.height(),paint);
        canvas.restore();
    }
}
