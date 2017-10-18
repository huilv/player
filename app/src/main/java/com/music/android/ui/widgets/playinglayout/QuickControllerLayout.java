package com.music.android.ui.widgets.playinglayout;

import android.Manifest;
import android.animation.FloatEvaluator;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.bean.MessageEventBean;
import com.music.android.bean.MessageIntentBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.managers.AbsADManager;
import com.music.android.managers.Constant;
import com.music.android.managers.ImageLoaderManager;
import com.music.android.managers.LocalMusicImpl;
import com.music.android.service.MusicPlayService;
import com.music.android.ui.mvp.main.MainActivity;
import com.music.android.ui.widgets.BlurView;
import com.music.android.ui.widgets.MusicProgressView;
import com.music.android.ui.widgets.PlayListPopupWindow;
import com.music.android.utils.AnalyticsConstants;
import com.music.android.utils.AnalyticsUtils;
import com.music.android.utils.Constants;
import com.music.android.utils.L;
import com.music.android.utils.LocalMusicUtils;
import com.music.android.utils.PermissionUtils;
import com.music.android.utils.PlayHelper;
import com.music.android.utils.PlayUtils;
import com.music.android.utils.SharedPreferencesHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.DRAGGING;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED;

/**
 * Created by liuyun on 17/3/27.
 */

public class QuickControllerLayout extends RelativeLayout implements View.OnClickListener, SlidingUpPanelLayout.PanelSlideListener {

    private static final String TAG = "QuickControllerLayout";
    private TransformLayout mCopyLayout;
    private PlayingLayout mRealPlayingLayout;
    private RelativeLayout mQuickControllerView;

    private ImageView music_image_imageView;
    private TextView title_TextView;
    private TextView singer_TextView;
    private TextView first_show_TextView;
    private ImageView play_list_btn;
    private ImageView play_btn;
    private ImageView skip_next_btn;
    private MusicProgressView mMusicProgressView;

    private int currentPosition = 0;
    private long currentProgress = 0;
    private int parentY;

    private ImageView imgMode;
    private BlurView llBlur;

    private View toolbar, bottom, muisc_pager;

    private ArrayList<MusicInfoBean> musics = new ArrayList<>();

    private boolean isExpanded = false;

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;

        if (isExpanded) {
            if (muisc_pager != null) {
                muisc_pager.setVisibility(VISIBLE);
            }
            if (bottom != null) {
                bottom.setVisibility(VISIBLE);
            }
            if (mCopyLayout != null) {
                mCopyLayout.setVisibility(INVISIBLE);
            }
            if (mQuickControllerView != null) {
                mQuickControllerView.setVisibility(INVISIBLE);
            }
            if (llBlur != null) {
                llBlur.setAlpha(1);
            }
        }
    }

    public QuickControllerLayout(Context context) {
        this(context, null);
    }

    public QuickControllerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickControllerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setBackgroundColor(Color.parseColor("#212121"));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        mRealPlayingLayout = new PlayingLayout(getContext());
        mRealPlayingLayout.setLayoutParams(params);
        addView(mRealPlayingLayout);

        mQuickControllerView = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_player_controller, null, false);
        mQuickControllerView.setLayoutParams(params);
        addView(mQuickControllerView);

        mCopyLayout = new TransformLayout(mQuickControllerView.getContext());
        mCopyLayout.setLayoutParams(params);
        addView(mCopyLayout);
        mCopyLayout.setVisibility(INVISIBLE);

        initQuickControllerView();
        initRealPlayingLayout();

        if (isExpanded) {
            muisc_pager.setVisibility(VISIBLE);
            bottom.setVisibility(VISIBLE);
            mCopyLayout.setVisibility(INVISIBLE);
            mQuickControllerView.setVisibility(INVISIBLE);
            llBlur.setAlpha(1);
        }
    }

    public void setContextActivity(FragmentActivity contextActivity) {
        mRealPlayingLayout.setContextActivity(contextActivity);
    }

    private void initRealPlayingLayout() {
        bottom = mRealPlayingLayout.findViewById(R.id.playing_bottom);
        bottom.setVisibility(INVISIBLE);

        muisc_pager = mRealPlayingLayout.findViewById(R.id.music_layout);
        muisc_pager.setVisibility(INVISIBLE);

        toolbar = mRealPlayingLayout.findViewById(R.id.tool_bar);
        llBlur = (BlurView) mRealPlayingLayout.findViewById(R.id.blur);
        llBlur.setAlpha(0);
    }

    private void initQuickControllerView() {
        music_image_imageView = (ImageView) mQuickControllerView.findViewById(R.id.music_image_ImageView);
        title_TextView = (TextView) mQuickControllerView.findViewById(R.id.title_TextView);
        title_TextView.setSelected(true);
        singer_TextView = (TextView) mQuickControllerView.findViewById(R.id.singer_TextView);
        first_show_TextView = (TextView) mQuickControllerView.findViewById(R.id.first_show_TextView);
        play_list_btn = (ImageView) mQuickControllerView.findViewById(R.id.play_list_btn);
        play_btn = (ImageView) mQuickControllerView.findViewById(R.id.play_ImageView);
        skip_next_btn = (ImageView) mQuickControllerView.findViewById(R.id.skip_next);
        imgMode = (ImageView) mQuickControllerView.findViewById(R.id.play_mode);

        mMusicProgressView = (MusicProgressView) mQuickControllerView.findViewById(R.id.musicProgressView);

        mQuickControllerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                getParentY();
                copyAllViewToTransformlayout();

            }
        });

        initListener();
    }


    private void getParentY() {
        int[] location = new int[2];
        getLocationOnScreen(location);
        parentY = location[1];
    }

    private void copyAllViewToTransformlayout() {
        setMode();
        copyAllView(mQuickControllerView);
    }

    public void setMode() {
        switch (SharedPreferencesHelper.getPlayingType()) {
            case 0://默认的顺序
                imgMode.setImageResource(R.drawable.selector_mode_list);
                break;
            case 1://单曲
                imgMode.setImageResource(R.drawable.selector_mode_oneloop);
                break;
            case 2://随机
                imgMode.setImageResource(R.drawable.selector_mode_random);
                break;
            default:
                break;
        }
    }

    private void toolbarSlideIn() {
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.toolbar_slide_in);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        toolbar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                toolbar.startAnimation(animation);
            }
        });
    }

    private void copyAllView(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();

        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                copyAllView((ViewGroup) view);
            } else {
                String tag = (String) view.getTag();
                if (!TextUtils.isEmpty(tag)) {
                    if (view instanceof ImageView) {
                        mCopyLayout.copyViewToTransformLayout((ImageView) view, parentY);
                    } else if (view instanceof TextView) {
                        mCopyLayout.copyViewToTransformLayout((TextView) view, parentY, getWidth());
                    }
                }
            }
        }
//        mCopyLayout.setVisibility(VISIBLE);
    }


    public View getMusicImageImageView() {
        return music_image_imageView;
    }

    public void showUI() {
        title_TextView.setVisibility(View.VISIBLE);
        singer_TextView.setVisibility(View.VISIBLE);
        music_image_imageView.setVisibility(View.VISIBLE);
        first_show_TextView.setVisibility(View.INVISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setData(MessageEventBean eventBean) {
        this.path = eventBean.imgUrl;
        if (path != null) {
            if (path.startsWith("http")) {
                ImageLoaderManager.imageLoader(music_image_imageView, R.mipmap.icon_loading_default, path);
            } else {
                Uri uri = Uri.parse(path);
                ImageLoaderManager.imageLoader(music_image_imageView, R.mipmap.icon_loading_default, uri);
            }
        } else {
            music_image_imageView.setImageResource(R.mipmap.icon_loading_default);
        }
        if (playlistPopupWindow != null) {
            playlistPopupWindow.changeMusic();
        }
        title_TextView.setText(eventBean.musicName);
        singer_TextView.setText(eventBean.authorName);

        if (PermissionUtils.checkSelfPermission((MainActivity) getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            mLocalMusicImpl.getLocalSongs();
        }
    }

    private String path;

    LocalMusicImpl<MusicInfoBean> mLocalMusicImpl = new LocalMusicImpl<MusicInfoBean>() {

        @Override
        protected void buildMapData(Cursor cursor, Map<String, MusicInfoBean> commonBeanMap) {

        }

        @Override
        protected MusicInfoBean buildData(Cursor cursor) {
            return LocalMusicUtils.getAllSong(cursor);

        }

        @Override
        public void getResult(List<MusicInfoBean> list) {
            for (int i = 0; i < list.size(); i++) {
                MusicInfoBean musicInfoBean = list.get(i);
                if (path != null && list.get(i).path != null && path.equals(list.get(i).path)) {
                    Uri uri = Uri.parse(musicInfoBean.artwork_url);
                    ImageLoaderManager.imageLoader(music_image_imageView, R.mipmap.icon_loading_default, uri);
                }
            }
        }
    };

    public void setProgress(long currentProgress, long maxProgress) {
        mMusicProgressView.setProgress(currentProgress, maxProgress);
    }

    public void savePosition(int currentPosition, long currentProgress) {
        this.currentPosition = currentPosition;
        this.currentProgress = currentProgress;
    }

    public void setMusic(ArrayList<MusicInfoBean> musics) {
        L.d(TAG,"ArrayList"+musics.size());
        this.musics = musics;
    }

    public void isPlaying(boolean isPlaying) {
        if (isPlaying) {
            play_btn.setImageResource(R.drawable.selector_pause);
        } else {
            play_btn.setImageResource(R.drawable.selector_play);
        }
    }

    private void initListener() {
        play_list_btn.setOnClickListener(this);
        play_btn.setOnClickListener(this);
        skip_next_btn.setOnClickListener(this);
    }

    private PlayListPopupWindow playlistPopupWindow;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.play_list_btn:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYBANNER, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.SONGLIST);
                if (!first_show_TextView.isShown()) {
                    playlistPopupWindow = new PlayListPopupWindow(getContext());
                    playlistPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                }
                break;
            case R.id.play_ImageView:

                AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYBANNER, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.PLAY);

                if (!first_show_TextView.isShown()) {
                    play();
                } else {
                    if (PermissionUtils.checkSelfPermission((MainActivity) getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        localMusic.getLocalSongs();
                    }
                }

                break;
            case R.id.skip_next:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYBANNER, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.NEXT);
                if (!first_show_TextView.isShown()) {
                    next();
                }
                break;
        }
    }

    LocalMusicImpl<MusicInfoBean> localMusic = new LocalMusicImpl<MusicInfoBean>() {
        @Override
        protected void buildMapData(Cursor cursor, Map<String, MusicInfoBean> commonBeanMap) {

        }

        @Override
        protected MusicInfoBean buildData(Cursor cursor) {
            return LocalMusicUtils.getAllSong(cursor);
        }

        @Override
        protected void getResult(List<MusicInfoBean> list) {
            if (list != null && list.size() > 0) {
                PlayUtils.randomPlay(list);
            }
        }
    };

    private void next() {
        if (!PlayHelper.getInstance().isServiceExist()) {
            initPlayer();
            return;
        }
        Intent i3 = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        i3.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.NEXT_FLAG);
        MusicApp.context.sendBroadcast(i3);
    }

    private void play() {
        if (!PlayHelper.getInstance().isServiceExist()) {
            initPlayer();
            return;
        }
        Intent i2 = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        i2.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.PAUSE_OPEN_FLAG);
        MusicApp.context.sendBroadcast(i2);
    }

    private void initPlayer() {

        MusicPlayService.setMusics(musics);
        Intent intent = new Intent(getContext(), MusicPlayService.class);
        MessageIntentBean intentBean = new MessageIntentBean();
        intentBean.onPlaying = true;
        intentBean.currentPosition = currentPosition;
        intentBean.currentProgress = currentProgress;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.IntentType.BUNDLE_KEY_FLAG, intentBean);
        intent.putExtras(bundle);
        MusicApp.context.startService(intent);
    }

    private FloatEvaluator floatEvaluator = new FloatEvaluator();

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        if (mCopyLayout != null) {
            mCopyLayout.onPanelSlide(panel, slideOffset);
        }
        llBlur.setAlpha(floatEvaluator.evaluate(slideOffset, 0, 1));
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

        if (previousState == COLLAPSED && newState == DRAGGING) {
            copyAllViewToTransformlayout();
            mCopyLayout.setVisibility(VISIBLE);
            mQuickControllerView.setVisibility(INVISIBLE);
            mRealPlayingLayout.setCurrentMusicList(MusicPlayService.getMusics());
        } else if (previousState == EXPANDED && newState == DRAGGING) {
            copyAllViewToTransformlayout();
        }

        if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYBANNER, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.TOPLAYPAGE);
            toolbarSlideIn();
            muisc_pager.setVisibility(VISIBLE);
            bottom.setVisibility(VISIBLE);
            mCopyLayout.setVisibility(INVISIBLE);
            loadAd();
        } else if (newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
            toolbar.setVisibility(View.INVISIBLE);
            mCopyLayout.setVisibility(VISIBLE);
            mQuickControllerView.setVisibility(INVISIBLE);
            muisc_pager.setVisibility(INVISIBLE);
            bottom.setVisibility(INVISIBLE);

        } else if (newState == COLLAPSED) {
            mCopyLayout.setVisibility(INVISIBLE);
            mQuickControllerView.setVisibility(VISIBLE);
        }

        if (mCopyLayout != null) {
            mCopyLayout.onPanelStateChanged(panel, previousState, newState);
        }
    }

    private void loadAd() {
        new AbsADManager(Constant.SlotIdConstant.FULL_SCREEN_PLAYING_AD, true) {

            @Override
            public void onViewLoaded(View view) {

            }
        };
    }

}
