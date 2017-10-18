package com.music.android.ui.widgets;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.music.android.utils.FastBlurUtils;
import com.music.android.utils.SizeUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hui.lv on 2017/3/16.
 */

public class BlurView extends View {

    private final String TAG = "BlurView";
    private Bitmap mBlurBitmap;
    private Bitmap mLastBitmap;

    private Bitmap mCacheBitmap;

    private Paint paint;
    private Paint mGradientpaint;
    private final int DURATION_ANIMATION = 500;
    private int[] colors = {Color.argb(178, 0, 0, 0), Color.argb(178, 0, 0, 0)};

    private int height;
    private int width;

    private ObjectAnimator objectAnimator;
    private boolean isAnimatorRunning = false;
    private RectF fullRectF;

    public BlurView(Context context) {
        this(context, null);

    }

    public BlurView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public BlurView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        width = SizeUtils.dp2Px(context, 640);
        paint = new Paint();
        paint.setAntiAlias(true);

        mGradientpaint = new Paint();
//        mGradientpaint.setAlpha((int)(0.8 * 255));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getMeasuredWidth();
        height = getMeasuredHeight();

        fullRectF = new RectF(0, 0, width, height);

        RadialGradient radialGradient = new RadialGradient(width / 2, height / 2, (height==0?1:height / 2), colors, null, Shader.TileMode.CLAMP);

//        LinearGradient linearGradient = new LinearGradient(w / 2, 0, w / 2, width, colors, null, Shader.TileMode.CLAMP);

        mGradientpaint.setShader(radialGradient);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLastBitmap != null) {
            canvas.drawBitmap(mLastBitmap, null, fullRectF, null);
        }
        if (mBlurBitmap != null) {
            canvas.drawBitmap(mBlurBitmap, null, fullRectF, paint);
        }
        canvas.drawRect(0, 0, width, height, mGradientpaint);
    }

    public void setBlurBitmap(final Bitmap blurBitmap) {
        if (isAnimatorRunning) {
            mCacheBitmap = blurBitmap;
            return;
        }

        if (mBlurBitmap != null && !mBlurBitmap.isRecycled()) {
            mBlurBitmap.recycle();
            mBlurBitmap = null;
        }

        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> e) throws Exception {
                if(blurBitmap!=null&&!blurBitmap.isRecycled()){
                    Bitmap temp = Bitmap.createScaledBitmap(blurBitmap, width > 0 ? width / 10 : 100 , height > 0 ? height / 10 : 100, true);
                    e.onNext(FastBlurUtils.doBlur(temp, 20, false));
                    e.onComplete();
                }
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        mBlurBitmap = bitmap;
                        objectAnimator = ObjectAnimator.ofFloat(this, "number", 0f, 1.0f);
                        objectAnimator.setDuration(DURATION_ANIMATION);
                        objectAnimator.setInterpolator(new AccelerateInterpolator());
                        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int foregroundAlpha = (int) ((float) animation.getAnimatedValue() * 255);
                                paint.setAlpha(foregroundAlpha);
                                invalidate();
                            }
                        });

                        objectAnimator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                isAnimatorRunning = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (mLastBitmap != null && !mLastBitmap.isRecycled()) {
                                    mLastBitmap.recycle();
                                    mLastBitmap = null;
                                }
                                mLastBitmap = mBlurBitmap;
                                mBlurBitmap = null;
                                isAnimatorRunning = false;

                                if (mCacheBitmap != null) {
                                    setBlurBitmap(mCacheBitmap);
                                    mCacheBitmap = null;
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        objectAnimator.start();
                    }
                });
    }



    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBlurBitmap != null && !mBlurBitmap.isRecycled()) {
            mBlurBitmap.recycle();
            mBlurBitmap = null;
        }
        if (mLastBitmap != null && !mLastBitmap.isRecycled()) {
            mLastBitmap.recycle();
            mLastBitmap = null;
        }
    }
}
