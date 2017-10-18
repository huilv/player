package com.music.android.ui.mvp.audio;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.music.android.R;
import com.music.android.bean.MessageEventBean;
import com.music.android.bean.MessageIntentBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.listener.OnBitmapLoadListener;
import com.music.android.listener.OnOpenListener;
import com.music.android.managers.ImageLoaderManager;
import com.music.android.service.MusicPlayService;
import com.music.android.ui.widgets.SlidingScrollView;
import com.music.android.utils.ActivityHelper;
import com.music.android.utils.AnalyticsConstants;
import com.music.android.utils.AnalyticsUtils;
import com.music.android.utils.Constants;
import com.music.android.utils.L;
import com.music.android.utils.PlayHelper;
import com.music.android.utils.ScreenUtils;
import com.music.android.utils.SharedPreferencesHelper;
import com.music.android.utils.SizeUtils;
import com.music.android.utils.TimeHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hui.lv on 2017/3/8.
 */

public class LockScreenActivity extends Activity implements OnOpenListener, View.OnClickListener {
    private static final String TAG = "LockScreenActivity";
    public static final String ACTION = "com.action.lockScreenActivity";
    private SlidingScrollView slidingMenu;
    private TextView tvTime;
    private TextView tvDate;
    private TextView tvTitle;
    private TextView tvSinger;
    private ImageView imgMode;
    private ImageView imgPre;
    private ImageView imgPlay;
    private ImageView imgNext;
    private List<MusicInfoBean> musics = new ArrayList<>();
    private long currentProgress;
    private ImageView portrait;
    private Timer timer = new Timer();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2222) {

                String time = TimeHelper.getTime(LockScreenActivity.this);
                int data = TimeHelper.getData(LockScreenActivity.this);
                String week = TimeHelper.getWeek(LockScreenActivity.this);
                String month = TimeHelper.getMonth(LockScreenActivity.this);
                tvTime.setText(time);
                tvDate.setText(week + ", " + data + " " + month);
            }
        }
    };
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            handler.sendEmptyMessage(2222);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        unLockSystemScreenActivity();
        initView();
        timer.schedule(task, new Date(System.currentTimeMillis()), 10000);
    }

    private void unLockSystemScreenActivity() {
        try {
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock kl = km.newKeyguardLock("");
            kl.disableKeyguard();
        } catch (Exception e) {

        }
    }

    private void initView() {
        ActivityHelper.getDefault().addActivity(this, getClass());
        setContentView(R.layout.activity_lock_screen);
        slidingMenu = (SlidingScrollView) findViewById(R.id.menu);
        slidingMenu.setOnOpenListener(this);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvSinger = (TextView) findViewById(R.id.tvSinger);
        portrait = (ImageView) findViewById(R.id.portrait);
        imgMode = (ImageView) findViewById(R.id.imgMode);
        imgPre = (ImageView) findViewById(R.id.imgPre);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        imgNext = (ImageView) findViewById(R.id.imgNext);

        imgMode.setOnClickListener(this);
        imgPre.setOnClickListener(this);
        imgPlay.setOnClickListener(this);
        imgNext.setOnClickListener(this);
        if (MusicPlayService.getMusics().size() == 0) {
            finish();
        }
        musics.addAll(MusicPlayService.getMusics());
        tvSinger.setText(musics.get(MusicPlayService.getCurrentPosition()).singer);
        tvTitle.setText(musics.get(MusicPlayService.getCurrentPosition()).title);
        setMode();
        setPortraitHeight();
        EventBus.getDefault().register(this);
    }


    private void init() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    private void setPortraitHeight() {
        int screenWidth = ScreenUtils.getScreenWidth(this);
        int margin = SizeUtils.dp2Px(this, 62);
        portrait.getLayoutParams().width = screenWidth - margin * 2;
        portrait.getLayoutParams().height = screenWidth - margin * 2;
    }

    private int currentPosition = -1;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEventBean eventBean) {

        if (eventBean.onPlaying) {//&& eventBean.isPrepared
            imgPlay.setImageResource(R.drawable.lock_play);
        } else {
            imgPlay.setImageResource(R.drawable.lock_pause);
        }

        if (currentPosition == -1 || eventBean.isSongChange) {//上一首下一首切换时
            tvTitle.setText(eventBean.musicName);
            getBitmap(eventBean.imgUrl);
        }

        if (!eventBean.isServiceExist) {
            musics.clear();
            musics.addAll(eventBean.list);
        }
        currentProgress = eventBean.currentProgress;
        currentPosition = eventBean.currentPosition;
        tvSinger.setText(eventBean.authorName);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String action) {
        if (ACTION.equalsIgnoreCase(action)) {
            L.d(TAG,"action="+action);
            finish();
        }
    }


    public void getBitmap(final String avatar) {
        if (TextUtils.isEmpty(avatar)) {
            portrait.setImageDrawable(null);
            return;
        }
        if (!avatar.startsWith("http")) {
            Uri uri = Uri.parse(avatar);
            loadUriBitmap(uri);
            return;
        }
        loadUrlBitmap(avatar);
    }

    private void loadUrlBitmap(String avatar) {
        L.d("loadUrlBitmap", "avatar---" + avatar);
        ImageLoaderManager.imageLoaderBitMap(avatar, new OnBitmapLoadListener() {
            @Override
            public void onLoadFailed() {
                if (LockScreenActivity.this.isFinishing()) {
                    return;
                }
                portrait.setImageDrawable(null);
            }

            @Override
            public void onResourceReady(GlideDrawable resource) {
                if (LockScreenActivity.this.isFinishing()) {
                    return;
                }
                portrait.setImageDrawable(resource);
            }
        });

    }

    private void loadUriBitmap(Uri uri) {
        ImageLoaderManager.imageLoaderBitMap(uri, new OnBitmapLoadListener() {
            @Override
            public void onLoadFailed() {
                if (LockScreenActivity.this.isFinishing()) {
                    return;
                }
                portrait.setImageDrawable(null);
            }

            @Override
            public void onResourceReady(GlideDrawable resource) {
                if (LockScreenActivity.this.isFinishing()) {
                    return;
                }
                portrait.setImageDrawable(resource);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ActivityHelper.getDefault().removeActivity(this);
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgMode:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.LOCK_PLAY, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.PLAYMODE);
                changeMode();
                PlayHelper.getInstance().notifyModeChanged();
                break;
            case R.id.imgPre:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.LOCK_PLAY, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.PREVIOUS);
                previous();
                break;
            case R.id.imgPlay:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.LOCK_PLAY, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.PLAY);
                play();
                break;
            case R.id.imgNext:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.LOCK_PLAY, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.NEXT);
                next();
                break;
        }
    }

    private void play() {
        if (!isServiceExist()) {
            initPlayer();
            return;
        }
        Intent i2 = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        i2.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.PAUSE_OPEN_FLAG);
        sendBroadcast(i2);
    }

    private void previous() {
        if (!isServiceExist()) {
            initPlayer();
            return;
        }
        Intent i1 = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        i1.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.PREVIOUS_FLAG);
        sendBroadcast(i1);
    }

    private void next() {
        if (!isServiceExist()) {
            initPlayer();
            return;
        }
        Intent i3 = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        i3.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.NEXT_FLAG);
        sendBroadcast(i3);
    }

    private void initPlayer() {
        L.d(TAG, "--" + musics.size());
        MusicPlayService.setMusics(musics);
        Intent intent = new Intent(this, MusicPlayService.class);
        MessageIntentBean intentBean = new MessageIntentBean();
        intentBean.onPlaying = true;
        intentBean.currentPosition = currentPosition;
        intentBean.currentProgress = currentProgress;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.IntentType.BUNDLE_KEY_FLAG, intentBean);
        intent.putExtras(bundle);
        startService(intent);
    }

    public boolean isServiceExist() {
        int size = MusicPlayService.getMusics().size();
        return (size == 0 ? false : true);
    }

    @Override
    public void onOpen() {
        finish();
    }

    private void changeMode() {
        switch (SharedPreferencesHelper.getPlayingType()) {
            case 0://默认的顺序
                SharedPreferencesHelper.setPlayingType(1);
                break;
            case 1://单曲
                SharedPreferencesHelper.setPlayingType(2);
                break;
            case 2://随机
                SharedPreferencesHelper.setPlayingType(0);
                break;
            default:
                break;
        }
        setMode();
    }

    public void setMode() {
        switch (SharedPreferencesHelper.getPlayingType()) {
            case 0://默认的顺序
                imgMode.setImageResource(R.drawable.lock_mode);
                break;
            case 1://单曲
                imgMode.setImageResource(R.drawable.lock_repeat);
                break;
            case 2://随机
                imgMode.setImageResource(R.drawable.lock_random);
                break;
            default:
                break;
        }
    }
}