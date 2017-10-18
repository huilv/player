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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.music.android.R;
import com.music.android.bean.MessageEventBean;
import com.music.android.bean.MessageIntentBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.listener.OnBitmapLoadListener;
import com.music.android.listener.OnOpenListener;
import com.music.android.managers.AbsADManager;
import com.music.android.managers.Constant;
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
import com.music.android.utils.VarUtils;

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

public class AdLockScreenActivity extends Activity implements OnOpenListener, View.OnClickListener {
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
    private LinearLayout tvParent;
    private Timer timer = new Timer();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2222) {

                String time = TimeHelper.getTime(AdLockScreenActivity.this);
                int data = TimeHelper.getData(AdLockScreenActivity.this);
                String week = TimeHelper.getWeek(AdLockScreenActivity.this);
                String month = TimeHelper.getMonth(AdLockScreenActivity.this);
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
        setContentView(R.layout.activity_adlock_screen);
        slidingMenu = (SlidingScrollView) findViewById(R.id.menu);
        slidingMenu.setOnOpenListener(this);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvSinger = (TextView) findViewById(R.id.tvSinger);

        imgMode = (ImageView) findViewById(R.id.imgMode);
        imgPre = (ImageView) findViewById(R.id.imgPre);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        imgNext = (ImageView) findViewById(R.id.imgNext);

        imgMode.setOnClickListener(this);
        imgPre.setOnClickListener(this);
        imgPlay.setOnClickListener(this);
        imgNext.setOnClickListener(this);
        musics.addAll(MusicPlayService.getMusics());
        if (musics.size() == 0|| VarUtils.currentPosition>=VarUtils.list.size()||VarUtils.currentPosition==-1) {
            finish();
            return;
        }
        tvSinger.setText(musics.get(MusicPlayService.getCurrentPosition()).singer);
        tvTitle.setText(musics.get(MusicPlayService.getCurrentPosition()).title);
        intAdView();
        EventBus.getDefault().register(this);
    }

    private void intAdView() {
        tvParent = (LinearLayout) findViewById(R.id.tv_parent);
        initAdData();
    }

    private void initAdData() {
        new AbsADManager(Constant.SlotIdConstant.LOCK_SCREEN_AD, R.layout.layout_ad, "1") {
            @Override
            public void onViewLoaded(View view) {
                if (view != null && tvParent.getChildCount() < 1) {
                    tvParent.setVisibility(View.VISIBLE);
                    tvParent.removeAllViews();
                    tvParent.addView(view);
                }
            }
        };
    }


    private void init() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }


    private int currentPosition = -1;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEventBean eventBean) {

        if (eventBean.onPlaying) {//&& eventBean.isPrepared
            imgPlay.setImageResource(R.drawable.ad_play_selector);
        } else {
            imgPlay.setImageResource(R.drawable.ad_pause_selector);
        }

        if (currentPosition == -1 || eventBean.isSongChange) {//上一首下一首切换时
            tvTitle.setText(eventBean.musicName);
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
            L.d(TAG, "action=" + action);
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ActivityHelper.getDefault().removeActivity(this);
        if (timer != null) {
            timer.cancel();
        }
        if (handler != null) {
            handler.removeMessages(2222);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgMode:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.LOCK_PLAY, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.PLAYMODE);
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

    @Override
    public void onBackPressed() {

    }
}