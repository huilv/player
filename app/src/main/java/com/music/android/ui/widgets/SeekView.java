package com.music.android.ui.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.music.android.R;
import com.music.android.listener.OnSeekChangeListener;
import com.music.android.utils.L;
import com.music.android.utils.ScreenUtils;
import com.music.android.utils.SizeUtils;


/**
 * Created by hui.lv on 2017/3/14.
 */

public class SeekView extends View {
    private final String TAG = "SeekView";
    private Bitmap soundGray;
    private Bitmap soundGreen;
    private Bitmap bitmap;
    private int width;
    private int height;
    private int screenWidth;
    private int left = 1;
    private Paint paint;
    private OnSeekChangeListener listener;
    private Paint mPaint;
    private final int SPACE = 3;
    private boolean canDrag;

    public SeekView(Context context) {
        super(context);
        init(context);
    }

    public SeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setCurrentProgress(float percent) {
        left = (int) (percent * screenWidth);
        invalidate();
    }

    private void init(Context context) {
        soundGray = BitmapFactory.decodeResource(context.getResources(), R.drawable.sound_effect_gray);
        soundGreen = BitmapFactory.decodeResource(context.getResources(), R.drawable.sound_effect_green);
        width = soundGray.getWidth();
        height = soundGray.getHeight();

        screenWidth = ScreenUtils.getScreenWidth(context);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#d5ff44"));

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#3b3b3b"));
    }

    public void setOnSeekChangeListener(OnSeekChangeListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(screenWidth, height + SizeUtils.dp2Px(getContext(), 5.5f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGrayBitmap(canvas);
        drawGreenBitmap(canvas);
        drawRect(canvas);
        drawGreenCircle(canvas);
    }

    private void drawGrayBitmap(Canvas canvas) {
        int count = -1;
        do {
            count++;
            L.i(TAG, count + "-----" + width);
            canvas.drawBitmap(soundGray, (count == 0 ? 0 : width * count + SPACE), 0, null);
        } while (width * (count + 1) < screenWidth);

    }

    private void drawGreenBitmap(Canvas canvas) {
        if (left > width) {
            int multiple = left / width;
            for (int i = 0; i < multiple; i++) {
                canvas.drawBitmap(soundGreen, (i == 0 ? 0 : width * i + SPACE), 0, null);
            }
            bitmap = Bitmap.createBitmap(soundGreen, 0, 0, left - width * multiple, height);
            canvas.drawBitmap(bitmap, width * multiple + SPACE, 0, null);
        } else {

            bitmap = Bitmap.createBitmap(soundGreen, 0, 0, (left==0?1:left), height);
            canvas.drawBitmap(bitmap, 0, 0, null);
        }

    }

    private void drawRect(Canvas canvas) {
        canvas.drawRect(0, height, screenWidth, height + SizeUtils.dp2Px(getContext(), 2f), mPaint);
        canvas.drawRect(0, height, left, height + SizeUtils.dp2Px(getContext(), 2f), paint);
    }

    private void drawGreenCircle(Canvas canvas) {
        canvas.drawCircle(left, height + SizeUtils.dp2Px(getContext(), 2f) / 2, SizeUtils.dp2Px(getContext(), 5.5f) / 2, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!canDrag){
            return  true;
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                left = (int) event.getX();
                invalidate();
                if (listener != null) {
                    listener.onMove(this, event.getX()*1.0f / screenWidth);
                }
                break;
            case MotionEvent.ACTION_UP:
                left = (int) event.getX();
                invalidate();
                if (listener != null) {
                    listener.onProgressChanged(this, event.getX()*1.0f / screenWidth);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (soundGray != null && !soundGray.isRecycled()) {
            soundGray.recycle();
            soundGray = null;
        }
        if (soundGreen != null && !soundGreen.isRecycled()) {
            soundGreen.recycle();
            soundGreen = null;
        }

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public void setCanDrag(boolean canDrag) {
        this.canDrag = canDrag;
    }
}
