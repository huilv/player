package com.music.android.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.music.android.listener.OnOpenListener;
import com.music.android.utils.L;
import com.music.android.utils.ScreenUtils;

/**
 * Created by hui.lv on 2017/3/9.
 */

public class SlidingScrollView extends HorizontalScrollView {
    private final String TAG="SlidingScrollView";
    private int mScreenWidth;
    private int mHalfMenuWidth;
    private OnOpenListener listener;
    private boolean flag;
    private VelocityTracker tracker;
    private final int LIMIT=3000;
    public SlidingScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public SlidingScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScreenWidth = ScreenUtils.getScreenWidth(context);
        tracker = VelocityTracker.obtain();

    }

    public SlidingScrollView(Context context) {
        this(context, null, 0);
    }
    public  void setOnOpenListener(OnOpenListener listener){
        this.listener=listener;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!flag) {
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            ViewGroup menu = (ViewGroup) wrapper.getChildAt(0);
            ViewGroup content = (ViewGroup) wrapper.getChildAt(1);
            mHalfMenuWidth = mScreenWidth / 2;
            menu.getLayoutParams().width = mScreenWidth;
            content.getLayoutParams().width = mScreenWidth;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.scrollTo(mScreenWidth, 0);
            flag = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        tracker.addMovement(ev);
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                tracker.computeCurrentVelocity(1000);
                float xVelocity = tracker.getXVelocity();
                L.d("onTouchEvent","xVelocity="+xVelocity);
                int scrollX = getScrollX();
                if (scrollX > mHalfMenuWidth) {
                    if(xVelocity>LIMIT){
                        this.smoothScrollTo(0, 0);
                        return true;
                    }
                    this.smoothScrollTo(mScreenWidth, 0);
                } else {
                    if(xVelocity<-LIMIT){
                        this.smoothScrollTo(mScreenWidth, 0);
                        return true;
                    }
                    this.smoothScrollTo(0, 0);
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseVelocityTracker();
    }

    private void releaseVelocityTracker() {
        if(null != tracker) {
            tracker.clear();
            tracker.recycle();
            tracker = null;
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        L.d(TAG,"l======"+l);
        if(l==0&&listener!=null){
            listener.onOpen();
        }
    }
}
