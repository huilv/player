package com.music.android.ui.widgets.playinglayout;

import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.ui.widgets.BlurView;
import com.music.android.ui.widgets.SeekBarView;
import com.music.android.utils.L;
import com.music.android.utils.PrefUtils;
import com.music.android.utils.SizeUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CTer on 17/4/27.
 */

public class TransformLayout extends FrameLayout implements SlidingUpPanelLayout.PanelSlideListener {

    private Map<String, Position> mViewTransformPosition = new HashMap<>();
    private Map<String, View> mViews = new HashMap<>();

//    private ImageView music_image_imageView;
//    private TextView title_TextView;
//    private TextView singer_TextView;
//    private TextView first_show_TextView;
//    private ImageView play_list_btn;
//    private ImageView play_btn;
//    private ImageView skip_next_btn;


//    private RelativeLayout mRealPlayingLayout;

    private static final int DP_10 = SizeUtils.dp2Px(MusicApp.context, 10);

    private IntEvaluator intEvaluator = new IntEvaluator();
    private FloatEvaluator floatEvaluator = new FloatEvaluator();

    public TransformLayout(@NonNull Context context) {
        this(context, null);
    }

    public TransformLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransformLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        for (String tag : ViewTag.ALLVIEWTAG) {
            moveViewByTag(tag, slideOffset);
        }
    }

    private void moveViewByTag(String tag, float slideOffset) {

        View view = mViews.get(tag);
        if (view == null) {
            view = findViewWithTag(tag);
        }

        if (view != null) {

            Position position = mViewTransformPosition.get(view.getTag());
            FrameLayout.LayoutParams frameLayout = (FrameLayout.LayoutParams) view.getLayoutParams();
            //animate albumImage
            int temptwidth = intEvaluator.evaluate(slideOffset, position.start_width, position.end_width);
            int temptheight = intEvaluator.evaluate(slideOffset, position.start_height, position.end_height);
            frameLayout.width = temptwidth;
            frameLayout.height = temptheight;
            view.setLayoutParams(frameLayout);
            view.setX(intEvaluator.evaluate(slideOffset, position.start_x, position.end_x));
            view.setY(intEvaluator.evaluate(slideOffset, position.start_y, position.end_y));

            if (ViewTag.MUSIC_BG.equals(tag) ||
                    ViewTag.PLAY_MODE.equals(tag) ||
                    ViewTag.PLAY_PREV.equals(tag) ||
                    ViewTag.CD_BG.equals(tag)) {
                view.setAlpha(floatEvaluator.evaluate(slideOffset, 0, 1));
            }

//            if (view instanceof TextView) {
//                TextView textView = (TextView) view;
//                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, floatEvaluator.evaluate(slideOffset, position.start_size, position.end_size));
//            }

        }

    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

    }

    public void copyViewToTransformLayout(ImageView imageView, int parentY) {

        ImageView view = (ImageView) findViewWithTag(imageView.getTag());
        if (view != null) {
            view.setImageDrawable(imageView.getDrawable());
            String tag = (String) view.getTag();
            Position position = mViewTransformPosition.get(tag);
            if (position == null) {
                return;
            }
            if (position.end_y == 0) {
                position.end_width = PrefUtils.getInt(MusicApp.context, tag + ViewTag.W, 0);
                position.end_height = PrefUtils.getInt(MusicApp.context, tag + ViewTag.H, 0);
                position.end_x = PrefUtils.getInt(MusicApp.context, tag + ViewTag.X, 0);
                position.end_y = PrefUtils.getInt(MusicApp.context, tag + ViewTag.Y, 0);
            }

            return;
        }

        Position position = new Position();

        String tag = (String) imageView.getTag();

        view = new ImageView(imageView.getContext());
        view.setTag(tag);
        view.setImageDrawable(imageView.getDrawable());

        position.start_width = imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
        position.start_height = imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();


        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(position.start_width, position.start_height);

        int x, y;
        int[] location = new int[2];
        imageView.getLocationOnScreen(location);
        x = location[0];
        y = location[1];

        position.start_x = x + imageView.getPaddingLeft();
        position.start_y = y - parentY + imageView.getPaddingTop();
        layoutParams.setMargins(position.start_x, position.start_y, 0, 0);
        view.setLayoutParams(layoutParams);

        position.end_width = PrefUtils.getInt(MusicApp.context, tag + ViewTag.W, 0);
        position.end_height = PrefUtils.getInt(MusicApp.context, tag + ViewTag.H, 0);
        position.end_x = PrefUtils.getInt(MusicApp.context, tag + ViewTag.X, 0);
        position.end_y = PrefUtils.getInt(MusicApp.context, tag + ViewTag.Y, 0);

        mViewTransformPosition.put(tag, position);
        mViews.put(tag, view);

        L.i("TransformLayout", tag + ": \n" + position.toString());

        addView(view);
    }

    public void copyViewToTransformLayout(TextView textView, int parentY, int width) {

        TextView view = (TextView) findViewWithTag(textView.getTag());

        if (view != null) {
            view.setText(textView.getText());
            Position position = mViewTransformPosition.get(textView.getTag());
            if (position == null) {
                return;
            }
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getTextSize());
            TextPaint textPaint = view.getPaint();
            String downloadText = view.getText().toString();
            float downloadTextWidth = textPaint.measureText(downloadText);

            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getTextSize());

            int maxWidth = (width - 2 * DP_10);

            position.end_width = downloadTextWidth >= maxWidth ? maxWidth : (int) downloadTextWidth;

            position.end_x = downloadTextWidth >= maxWidth ? DP_10 : (int) (width - downloadTextWidth) / 2;
            String tag = (String) view.getTag();
            if (position.end_y == 0) {
                position.end_height = PrefUtils.getInt(MusicApp.context, tag + ViewTag.H, 0);
                position.end_y = PrefUtils.getInt(MusicApp.context, tag + ViewTag.Y, 0);
            }

            L.i("TransformLayout", tag + ": \n" + position.toString());

            //PrefUtils.getInt(MusicApp.context, tag + ViewTag.X, 0);
            return;
        }
        Position position = new Position();
        String tag = (String) textView.getTag();

        view = new TextView(textView.getContext());
        view.setTag(tag);
        view.setText(textView.getText());
        view.setTextColor(textView.getCurrentTextColor());
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getTextSize());
        view.setMaxLines(textView.getMaxLines());
        view.setEllipsize(TextUtils.TruncateAt.MARQUEE);

        position.start_size = textView.getTextSize();
        position.start_width = textView.getWidth();
        position.start_height = textView.getHeight();

        position.end_height = PrefUtils.getInt(MusicApp.context, tag + ViewTag.H, 0);
        position.end_size = PrefUtils.getFloat(MusicApp.context, tag + ViewTag.S, 0);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(position.start_width, position.start_height);

        int x, y;
        int[] location = new int[2];
        textView.getLocationOnScreen(location);
        x = location[0];
        y = location[1];

        position.start_x = x;
        position.start_y = y - parentY;
        layoutParams.setMargins(position.start_x, position.start_y, 0, 0);
        view.setLayoutParams(layoutParams);

        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, position.end_size);
        TextPaint textPaint = view.getPaint();
        String downloadText = view.getText().toString();
        float downloadTextWidth = textPaint.measureText(downloadText);

        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getTextSize());

        position.end_width = downloadTextWidth >= width ? width : (int) downloadTextWidth;

        position.end_x = downloadTextWidth >= width ? 0 : (int) (width - downloadTextWidth) / 2; //PrefUtils.getInt(MusicApp.context, tag + ViewTag.X, 0);
        position.end_y = PrefUtils.getInt(MusicApp.context, tag + ViewTag.Y, 0);

        L.i("TransformLayout", tag + ": \n" + position.toString());

        mViewTransformPosition.put((String) textView.getTag(), position);
        mViews.put(tag, view);
        addView(view);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mViewTransformPosition.clear();
        mViews.clear();
    }

    public static class Position {

        public int start_x;

        public int start_y;

        public int end_x;

        public int end_y;

        public int start_width;

        public int start_height;

        public int end_width;

        public int end_height;

        public float start_size;

        public float end_size;

        @Override
        public String toString() {
            return "x : " + start_x + " -> " + end_x + "\n" +
                    "y : " + start_y + " -> " + end_y + "\n" +
                    "w : " + start_width + " -> " + end_width + "\n" +
                    "h : " + start_height + " -> " + end_height + "\n" +
                    "s : " + start_size + " -> " + end_size + "\n";
        }
    }

}
