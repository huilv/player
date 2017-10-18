package com.music.android.ui.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

import com.music.android.R;
import com.music.android.listener.OnProgressChangeListener;
import com.music.android.utils.L;
import com.music.android.utils.ScreenUtils;
import com.music.android.utils.SizeUtils;

import java.util.Random;


/**
 * Created by hui.lv on 2017/3/29.
 */

public class SeekBarView extends View {
    private final String TAG = "SeekBarView";
    private int screenWidth;
    private int right;
    private Paint paint;
    private Paint mPaint;
    private boolean canDrag;
    private Bitmap pointOne;
    private Bitmap pointTwo;
    private int MARGIN;
    private Random random;
    private OnProgressChangeListener listener;
    private RectF rectF = new RectF();
    private ValueAnimator valueAnimator;
    private Paint greenPaint;

    public SeekBarView(Context context) {
        super(context);
        init(context);
    }

    public SeekBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SeekBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setCurrentProgress(float percent) {
        right = (int) (percent * (screenWidth - MARGIN * 2)) + MARGIN;
        invalidate();
    }

    private void init(Context context) {
        pointOne = BitmapFactory.decodeResource(context.getResources(), R.drawable.point);
        pointTwo = BitmapFactory.decodeResource(context.getResources(), R.drawable.point2);
        random = new Random();
        screenWidth = ScreenUtils.getScreenWidth(context);
        MARGIN = SizeUtils.dp2Px(context, 16);

        right = MARGIN;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#d5ff44"));

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#3b3b3b"));

        greenPaint = new Paint();
        greenPaint.setAntiAlias(true);
        greenPaint.setColor(Color.parseColor("#ff0000"));

    }

    public void setOnProgressChangeListener(OnProgressChangeListener listener) {
        this.listener = listener;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       L.d(TAG, "onMeasure");
        setMeasuredDimension(screenWidth, SizeUtils.dp2Px(getContext(), 28));
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGrayRect(canvas);
        drawGreenRect(canvas);
        drawBigBitmap(canvas);
        drawSmallBitmap(canvas);
    }


    private void drawBigBitmap(Canvas canvas) {
        if(canDrag){
            return;
        }
        Rect rect = new Rect(0, 0, pointTwo.getWidth(), pointTwo.getHeight());
        canvas.drawBitmap(pointTwo, rect, rectF, null);
//        int cx= (int) (rectF.right-rectF.width()/2);
//        int cy= (int) (rectF.bottom-rectF.height()/2);
//        canvas.drawCircle(cx,cy,rectF.width()/2,greenPaint);
    }

    private void drawSmallBitmap(Canvas canvas) {
        int left = right - pointOne.getWidth() / 2;
        int top = getHeight() / 2 - pointOne.getWidth() / 2;
        canvas.drawBitmap(pointOne, left, top, null);
    }

    private void drawGreenRect(Canvas canvas) {
        paint.setColor(Color.parseColor("#bdde3e"));
        canvas.drawRect(MARGIN, getHeight() / 2 - SizeUtils.dp2Px(getContext(), 3) / 2, right, getHeight() / 2 + SizeUtils.dp2Px(getContext(), 3) / 2, paint);
        paint.setColor(Color.parseColor("#d5ff44"));
        canvas.drawRect(MARGIN, getHeight() / 2 - SizeUtils.dp2Px(getContext(), 2) / 2, right, getHeight() / 2 + SizeUtils.dp2Px(getContext(), 2) / 2, paint);
    }

    private void drawGrayRect(Canvas canvas) {
        int rectHeight = SizeUtils.dp2Px(getContext(), 2);
        int left = MARGIN;
        int top = getHeight() / 2 - rectHeight / 2;
        int right = screenWidth - MARGIN;
        int bottom = getHeight() / 2 + rectHeight / 2;
        canvas.drawRect(left, top, right, bottom, mPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!canDrag) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                right = (int) event.getX();
                redress();


                if (listener != null) {
                    listener.onMove(this, (right - MARGIN) * 1.0f / (getWidth() - MARGIN * 2));
                }
                invalidate();

                break;
            case MotionEvent.ACTION_UP:
                right = (int) event.getX();
                redress();


                if (listener != null) {
                    listener.onUp(this, (right - MARGIN) * 1.0f / (getWidth() - MARGIN * 2));
                }
                invalidate();
                break;
        }
        return true;
    }

    private void redress() {
        if (right <= MARGIN) {
            right = MARGIN+5+random.nextInt(20);
            return;
        }
        if (right >= screenWidth - MARGIN) {
            int i = random.nextInt(10) + 10;
            right = screenWidth - i - MARGIN;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        if(pointOne!=null&&!pointOne.isRecycled()){
            pointOne.recycle();
            pointOne=null;
        }
        if(pointTwo!=null&&!pointTwo.isRecycled()){
            pointTwo.recycle();
            pointTwo=null;
        }

    }

    public void setCanDrag(boolean canDrag) {
        this.canDrag = canDrag;
    }

    public void startAnimation() {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            L.d(TAG, "-null--");
            return;
        }
        valueAnimator = ValueAnimator.ofInt(pointTwo.getWidth(), SizeUtils.dp2Px(getContext(), 28));
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int value = (int) animator.getAnimatedValue();
                L.d(TAG, "---" + value);
                rectF.left = right - value / 2;
                rectF.top = getHeight() / 2 - value / 2;
                rectF.right = right + value / 2;
                rectF.bottom = getHeight() / 2 + value / 2;
                postInvalidate();
            }
        });
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.start();

    }

    public void stopAnimation() {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            L.d(TAG, "-stopAnimation--");
            valueAnimator.end();
            valueAnimator.cancel();
            valueAnimator = null;

            postInvalidate();
        }

    }
}
