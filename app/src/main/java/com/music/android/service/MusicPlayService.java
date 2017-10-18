package com.music.android.service;

import android.app.Notification;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.base.BaseService;
import com.music.android.bean.MessageEventBean;
import com.music.android.bean.MessageIntentBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.bean.PathBean;
import com.music.android.data.local.MusicLocalDataSource;
import com.music.android.listener.OnBitmapListener;
import com.music.android.listener.OnPlayingListener;
import com.music.android.managers.ImageLoaderManager;
import com.music.android.managers.PlayerPresenter;
import com.music.android.ui.mvp.audio.AdLockScreenActivity;
import com.music.android.utils.ActivityHelper;
import com.music.android.utils.AnalyticsConstants;
import com.music.android.utils.AnalyticsUtils;
import com.music.android.utils.Constants;
import com.music.android.utils.L;
import com.music.android.utils.NetWorkUtils;
import com.music.android.utils.NotificationHelper;
import com.music.android.utils.SharedPreferencesHelper;
import com.music.android.utils.ToastUtil;
import com.music.android.utils.VarUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.List;


/**
 * * Created by hui.lv on 2017/3/8.
 */

public class MusicPlayService extends BaseService {
    private final String TAG = "MusicPlayService";
    private MusicPlayingBroadcastReceiver broadcast;
    public static PlayerPresenter playerPresenter;

    private MessageEventBean bean;
    /**
     * 短时间里发送大量的handler，handler 内部存在堵塞的情况，
     * 同理 EventBus内部的实现用的handler，也存在堵塞的情况
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        private long time;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VarUtils.HANDLER_PLAYING:
                    long timeMillis = System.currentTimeMillis();
                    if (timeMillis - time >= VarUtils.TIME_DELAY + 50) {
                        L.d(" ", "----" + System.currentTimeMillis());
                        time = timeMillis;
                        sendEventBusMessage();
                    }
                    break;
                case VarUtils.SLEEP_OVER:
                    isPause();
                    getBitmapForNotification(VarUtils.list.get(VarUtils.currentPosition).path);
                    break;
                case VarUtils.HANDLER_SONG_CHANGE:
                    if (VarUtils.fastPosition == -1) {
                        return;
                    }
                    L.d("HANDLER_SONG_CHANGE", "----" + System.currentTimeMillis());
                    VarUtils.fastPosition = -1;
                    startForeground();
                    break;
                case VarUtils.HANDLER_PAUSE://暂停或者播放
                    if (VarUtils.fastPause == -1) {
                        return;
                    }
                    L.d("HANDLER_PAUSE", "----" + System.currentTimeMillis());
                    VarUtils.fastPause = -1;
                    getBitmapForNotification(VarUtils.list.get(VarUtils.currentPosition).artwork_url);
                    break;
                default:
                    break;
            }

        }
    };


    private void sendEventBusMessage() {
        if(mHandler==null||VarUtils.list.size()==0||VarUtils.currentPosition>=VarUtils.list.size()||VarUtils.currentPosition==-1){
            stopSelf();
            return;
        }
        if (mHandler != null&&playerPresenter!=null) {
            bean = VarUtils.getMessageBean(playerPresenter, true, VarUtils.list.get(VarUtils.currentPosition));
            isNetwork();
            if (bean.currentProgress > 0 && bean.duration < bean.currentProgress) {
                onFinished();
            } else {
                EventBus.getDefault().post(bean);
            }
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.d(TAG, "onStartCommand");
        AnalyticsUtils.vMusicClick(AnalyticsConstants.NOTIFICATIONPLAY, AnalyticsConstants.Action.SHOW, "");
        init(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void init(Intent intent) {
        register();
        if(intent==null||intent.getSerializableExtra(Constants.IntentType.BUNDLE_KEY_FLAG)==null){
            stopSelf();
            return;
        }
        if (VarUtils.list.size() == 0||VarUtils.currentPosition==-1||VarUtils.currentPosition>VarUtils.list.size()) {
            stopSelf();
            return;
        }
        initData(intent);
        initPlayer();
        startForeground();
        saveRecentlyMusic();
        new FastForwardThread().start();
    }

    private void initData(Intent intent) {
        if(playerPresenter==null){
            playerPresenter = new PlayerPresenter(this, new MusicOnPlayingListener());
        }
        Serializable extra = intent.getSerializableExtra(Constants.IntentType.BUNDLE_KEY_FLAG);
        if (extra != null) {
            MessageIntentBean bean = (MessageIntentBean) extra;
            VarUtils.onPlaying = bean.onPlaying;
            L.d(TAG, bean.onNext + "onPlaying=" + VarUtils.onPlaying);
            VarUtils.currentPosition = bean.currentPosition;
            VarUtils.currentProgress = bean.currentProgress;
            VarUtils.LOOP = true;
            VarUtils.isSongChange = true;
            VarUtils.onNext(bean.onNext);
            return;
        }
        if (VarUtils.currentPosition < 0 || VarUtils.list.size() <= 0) {//防止初始化数据出错
            stopSelf();
            return;
        }
        throw new RuntimeException("MessageIntentBean can not be null");
    }

    private void register() {
        broadcast = new MusicPlayingBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Constants.MusicOthers.CALL_PHONE);
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(broadcast, filter);
    }


    private void initPlayer() {
        L.d(TAG, VarUtils.currentProgress + "---" + VarUtils.list.size() + "--size--" + VarUtils.currentPosition);
        if (VarUtils.list.size() > 0 && VarUtils.currentPosition >= 0&&playerPresenter!=null) {
            if(VarUtils.currentPosition>=VarUtils.list.size()){
                VarUtils.currentPosition=0;
            }
            playerPresenter.setDataSource(VarUtils.list.get(VarUtils.currentPosition).path);
            return;
        }
//        throw new RuntimeException("Player Data has not init!!!");
//        if (!VarUtils.list.get(VarUtils.currentPosition).path.startsWith("http")) {
//            if (VarUtils.currentProgress != 0 && playerPresenter.getDuration() != 0) {
//                float progress = SizeUtils.div(VarUtils.currentProgress, playerPresenter.getDuration());
//                playerPresenter.seekTo(progress);
//            }
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.d(TAG, "onDestroy");
        clearData();
    }


    public static int getCurrentPosition() {
        if(VarUtils.currentPosition>=VarUtils.list.size()||VarUtils.currentPosition==-1){
            VarUtils.currentPosition=0;
        }
        return VarUtils.currentPosition;
    }

    public static MusicInfoBean getCurrentMusic() {
        if (VarUtils.currentPosition >= VarUtils.list.size()) {
            return null;
        }
        return VarUtils.list.get(VarUtils.currentPosition);
    }

    public static boolean getCurrentState() {
        return VarUtils.onPlaying;
    }

    public static long getDuration() {
        return VarUtils.duration;
    }

    public static long getCurrentProgress() {
        return VarUtils.currentProgress;
    }

    public static boolean getRingState() {
        return VarUtils.onRingUp;
    }


    public static void sortMusics(List<MusicInfoBean> list, int position) {
        list.clear();
        VarUtils.currentPosition = position;
        list.addAll(list);
    }


    public static boolean isPrepared() {
        return VarUtils.onPrepared;
    }

    public static void insertNextMusic(MusicInfoBean bean) {
        VarUtils.list.add(VarUtils.currentPosition + 1, bean);
    }


    public static void setMusics(List<MusicInfoBean> musics) {
        if(musics==null||musics.size()==0){
            return;
        }
        VarUtils.list.clear();
        VarUtils.list.addAll(musics);
        VarUtils.changeMode();
    }

    public static void resetMusics(int position, List<MusicInfoBean> musics) {
        if (position < 0 || position >= musics.size()) {
            return;
        }
        long millis = System.currentTimeMillis();
        if (millis - VarUtils.currentTime < VarUtils.TIME_DELAY * 2) {
            return;
        }
        if (VarUtils.currentPosition < 0 || VarUtils.currentPosition >= VarUtils.list.size()) {
            return;
        }
        // 歌单切换，但是歌曲不切换,保留当前状态
        if (VarUtils.currentPosition < VarUtils.list.size() && musics.get(position).path.equalsIgnoreCase(getCurrentMusic().path)) {
            VarUtils.setData(position, musics);
            return;
        }
        VarUtils.setData(millis, position, musics);
        if(playerPresenter!=null){
            playerPresenter.setDataSource(VarUtils.list.get(VarUtils.currentPosition).path);
            MusicLocalDataSource.getInstance(MusicApp.context).saveRecentlyMusic(VarUtils.list.get(VarUtils.currentPosition));
        }
    }

    /**
     * 显示的音乐列表
     *
     * @return
     */
    public static List<MusicInfoBean> getMusics() {
        return VarUtils.list;
    }

    /**
     * 播放的音乐列表
     *
     * @return
     */
    public static List<MusicInfoBean> getPlayMusics() {
        return VarUtils.counts;
    }

    private void onMusicPlayingEvent(Intent intent) {
        int intExtra = intent.getIntExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, -1);
        L.d(TAG, "---intExtra--" + intExtra);
        String extra = intent.getStringExtra(Constants.BroadcastConstants.NOTIFICATION_COME_KEY);
        switch (intExtra) {
            case 1://上一首
                if (!TextUtils.isEmpty(extra)) {
                    L.d(TAG, "---extra=" + extra);
                    AnalyticsUtils.vMusicClick(AnalyticsConstants.NOTIFICATIONPLAY, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.PREVIOUS);
                }
                previous();
                break;
            case 2://下一首
                if (!TextUtils.isEmpty(extra)) {
                    AnalyticsUtils.vMusicClick(AnalyticsConstants.NOTIFICATIONPLAY, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.NEXT);
                }
                next();
                break;
            case 3://消失
                AnalyticsUtils.vMusicClick(AnalyticsConstants.NOTIFICATIONPLAY, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.DELETE);
                stopSelf();
                break;
            case 4://暂停或者播放
                if (!TextUtils.isEmpty(extra)) {
                    AnalyticsUtils.vMusicClick(AnalyticsConstants.NOTIFICATIONPLAY, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.PLAY);
                }
                isPause();
                break;
            case 5://音乐快进
                seekTo(intent);
                break;
            case 7://音乐信息修改
                updateMusicDetail(intent);
                break;
            case 9://删除歌曲
                Log.d("onMusicPlayingEvent", "onMusicPlayingEvent");
                String path = intent.getStringExtra(Constants.BroadcastConstants.DELETE_PATH);
                deleteSong(path);
                saveRecentlyMusic();
                VarUtils.changeMode();
                break;
            case 10://删除歌曲列表
                Log.d("onMusicPlayingEvent", "10");
                deleteSongList(intent);
                saveRecentlyMusic();
                VarUtils.changeMode();
                break;
            case 6:// 作废
                VarUtils.isSongChange = true;
                startMusic(intent);
                break;
            case 8://作废
                startForeground();
                break;
            case 11://页面跳转 作废
                Log.d("onMusicPlayingEvent", "12");
                break;
            case 12://耳机事件,作废
                break;
            case 13://播放模式改变
                Log.d("onMusicPlayingEvent", "13");
                VarUtils.changeMode();
                break;
            default:
                break;

        }
    }

    private void headsetEvent() {
        if (VarUtils.onPlaying) {
            VarUtils.onPlaying = false;
            VarUtils.fastPause = 1;
            if (VarUtils.onPrepared&&playerPresenter!=null) {
                playerPresenter.pause();
            }
        }
    }

    private void saveRecentlyMusic() {
        if(VarUtils.currentPosition==-1||VarUtils.list.size()==0||VarUtils.currentPosition>=VarUtils.list.size()){
            stopSelf();
            return;
        }

        MusicLocalDataSource.getInstance(MusicApp.context).saveRecentlyMusic(VarUtils.list.get(VarUtils.currentPosition));
    }

    private void deleteSongList(Intent intent) {
        Parcelable[] parcelables = intent.getParcelableArrayExtra(Constants.BroadcastConstants.DELETE_PATH);
        for (int i = 0; i < parcelables.length; i++) {
            PathBean bean = (PathBean) parcelables[i];
            deleteSong(bean.path);
        }
    }

    private void deleteSong(String path) {
        L.d("deleteSong", "path=" + path);
        if (VarUtils.list.size() == 0) {
            return;
        }
        if (VarUtils.list.size() == 1 && VarUtils.list.get(0).path.equalsIgnoreCase(path)) {
            stopSelf();
            return;
        }
        int deletePosition = -1;
        for (int i = 0; i < VarUtils.list.size(); i++) {
            if (VarUtils.list.get(i).path.equalsIgnoreCase(path)) {
                deletePosition = i;
                break;
            }
        }
        if (deletePosition == -1) {
            return;
        }
        VarUtils.list.remove(deletePosition);
        if (deletePosition == VarUtils.currentPosition) {
            VarUtils.currentPosition = (VarUtils.currentPosition >= VarUtils.list.size() ? 0 : VarUtils.currentPosition);
            startPlay();
        } else if (deletePosition > VarUtils.currentPosition) {

        } else {
            VarUtils.currentPosition = (VarUtils.currentPosition == 0 ? 0 : --VarUtils.currentPosition);
            L.d("deleteSong", VarUtils.currentPosition + "");
        }

    }

    private void updateMusicDetail(Intent intent) {
        MusicInfoBean bean = (MusicInfoBean) intent.getSerializableExtra(Constants.BroadcastConstants.SONGDETAILDIALOG);
        if (!TextUtils.isEmpty(bean.newSinger)) {
            VarUtils.updateSinger(bean);
        } else {
            VarUtils.updateTitle(bean);
        }
        EventBus.getDefault().post(VarUtils.getMessageBean(playerPresenter, true, VarUtils.list.get(VarUtils.currentPosition)));
    }

    private void startMusic(Intent intent) {
        if(playerPresenter!=null){
            VarUtils.currentPosition = intent.getIntExtra(Constants.BroadcastConstants.SEEK_OR_OPEN, 0);
            saveRecentlyMusic();
            playerPresenter.setDataSource(VarUtils.list.get(VarUtils.currentPosition).path);
            if (!VarUtils.onPlaying) {
                VarUtils.onPlaying = true;
            }
        }
    }


    private void seekTo(Intent intent) {
        float longExtra = intent.getFloatExtra(Constants.BroadcastConstants.SEEK_OR_OPEN, 0);
        playerPresenter.seekTo(longExtra);
    }

    private void isPause() {
        long millis = System.currentTimeMillis();
        if (millis - VarUtils.currentTime < VarUtils.TIME_DELAY * 3) {
            return;
        }
        VarUtils.isPause(millis);
        if (VarUtils.onPrepared) {
            if (VarUtils.onPlaying) {
                playerPresenter.reStart();
                return;
            }
            playerPresenter.pause();
        } else {
            Log.d("isPause", "isPause");
            if (VarUtils.onError) {//出错之后重新开始
                Log.d("isPause", "onError");
                playerPresenter.setDataSource(VarUtils.list.get(VarUtils.currentPosition).path);
                VarUtils.onError = false;
                return;
            }
        }
    }


    private void next() {
        long millis = System.currentTimeMillis();
        if (millis - VarUtils.currentTime < VarUtils.TIME_DELAY * 3) {
            return;
        }
        L.d("next", "next=");
        VarUtils.next(millis);
        playerPresenter.next(VarUtils.list.get(VarUtils.currentPosition).path);
        saveRecentlyMusic();
    }

    private void previous() {
        long timeMillis = System.currentTimeMillis();
        if (timeMillis - VarUtils.currentTime < VarUtils.TIME_DELAY * 3) {
            return;
        }
        VarUtils.previous(timeMillis);
        saveRecentlyMusic();
        playerPresenter.previous(VarUtils.list.get(VarUtils.currentPosition).path);
    }


    private void showLockScreenMusic() {
        L.d("showLockScreenMusic");
        try {
            Intent intent = new Intent(MusicPlayService.this, AdLockScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (ActivityNotFoundException e){

        }

    }

    public void isNetwork() {
        if (!VarUtils.onError && !NetWorkUtils.isNetworkConnected() && bean.path.startsWith("http")&&bean.onPlaying) {// 没网络 网络歌曲
            L.d("MusicOnPlayingListener", "isNetwork");
            VarUtils.onPlaying = false;
            if (!VarUtils.onPrepared) {
                VarUtils.onError = true;
            }
            bean.onPlaying = false;
            playerPresenter.pause();
            getBitmapForNotification(VarUtils.list.get(VarUtils.currentPosition).artwork_url);
            ToastUtil.showToast(MusicPlayService.this, getResources().getString(R.string.network_error));
        }
    }


    private class MusicPlayingBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                headsetEvent();
                return;
            }
            if (Intent.ACTION_SCREEN_OFF.equalsIgnoreCase(intent.getAction())&&!ActivityHelper.getDefault().isActivityExist(AdLockScreenActivity.class)) {
                AnalyticsUtils.vMusicClick(AnalyticsConstants.LOCK_PLAY, AnalyticsConstants.Action.SHOW, "");
                showLockScreenMusic();
                return;
            }
            if (Constants.MusicOthers.CALL_PHONE.equalsIgnoreCase(intent.getAction())) {
                onPhonePlayingEvent(intent);
                return;
            }
            onMusicPlayingEvent(intent);
        }
    }

    private void onPhonePlayingEvent(Intent intent) {// 0 1 1 0
        int value = intent.getIntExtra(Constants.MusicOthers.CALL_PHONE_INTENT, 0);
        L.d(TAG, "onPhonePlayingEvent---value=" + value);
        if (value == Constants.MusicOthers.CALL_PHONE_GOING) {
            if (!VarUtils.onPlaying && VarUtils.onPlaying) {
                playerPresenter.pause();
                startForeground();
            }
            VarUtils.onRingUp = true;
            return;
        }
        if (VarUtils.onRingUp && VarUtils.onPlaying) {
            startForeground();
            playerPresenter.reStart();
        }
        VarUtils.onRingUp = false;
    }

    private class MusicOnPlayingListener implements OnPlayingListener {
        private String path;

        @Override
        public void onPlayerError(int what) {
            L.d("MusicOnPlayingListener", "WHAT=" + what);
            VarUtils.onPlaying = false;
            VarUtils.onError = true;
            if (!getCurrentMusic().path.equalsIgnoreCase(path)) {
//                TipToast.makeText(MusicPlayService.this, getResources().getString(R.string.file_error), Toast.LENGTH_SHORT).show();
                path = getCurrentMusic().path;
            }
        }

        @Override
        public void onStart() {
            L.d("MusicOnPlayingListener", "onStart==" + VarUtils.onPlaying);
            VarUtils.onPrepared = true;
            if (!VarUtils.onPlaying && playerPresenter != null) {
                playerPresenter.pause();
            }
        }

        @Override
        public void onFinish() { //歌曲自动播放完毕
            L.i("MusicPlayService", "onFinish");
            onFinished();
        }
    }

    private void onFinished() {
        if (VarUtils.list.size() == 0) {
            stopSelf();
            return;
        }
        if (VarUtils.list.size() == 1) {
            playerPresenter.previous(VarUtils.list.get(0).path);
            return;
        }
        if (SharedPreferencesHelper.getPlayingType() == Constants.PlayType.RANDOM_FLAG) {// 随机
            VarUtils.setNextPosition();
        } else if (SharedPreferencesHelper.getPlayingType() == Constants.PlayType.LOOP_FLAG) {//循环
        } else {//顺序
            VarUtils.setNextPosition();
        }
        startPlay();
    }

    private void startPlay() {
        VarUtils.isSongChange = true;
        VarUtils.onPrepared = false;
        playerPresenter.previous(VarUtils.list.get(VarUtils.currentPosition).path);
        startForeground();
    }

    /**
     * 该类是 一.处理用户快速快进 快退造成的页面，notification频繁刷新导致的卡顿情况，运行在子线程  防止堵塞ui线程
     * 二.闹钟定时开关
     * 三.发送handler
     */
    private class FastForwardThread extends Thread {
        private final String TAG = "FastForwardThread";

        @Override
        public void run() {
            while (VarUtils.LOOP) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(VarUtils.HANDLER_PLAYING);
                    checkFast();
                    checkPause();
                    checkClock();
                    SystemClock.sleep(VarUtils.TIME_DELAY + 50);
                }
            }
        }

        private void checkPause() {
            if (VarUtils.fastPause != -1) {
                mHandler.sendEmptyMessage(VarUtils.HANDLER_PAUSE);
            }
        }

        private void checkFast() {
            if (VarUtils.fastPosition != -1) {
                mHandler.sendEmptyMessage(VarUtils.HANDLER_SONG_CHANGE);
            }
        }

        private void checkClock() {
            long sleepTime = SharedPreferencesHelper.getSleepTime();
            if (sleepTime != 0) {
                long time = System.currentTimeMillis() - sleepTime;
                if (time >= 0 && time < 3000 && VarUtils.onPlaying) {
                    mHandler.sendEmptyMessage(VarUtils.SLEEP_OVER);
                    SharedPreferencesHelper.setSleepTime(0);
                } else if (time > 3000) {//用户没有听歌，此时闹钟过期
                    SharedPreferencesHelper.setSleepTime(0);
                }
            }
        }
    }


    /**
     * 该方法涉及到 图片的异步加载 以及 暂停播放的更新 以及 歌曲名字提示
     * String path = "http://cdn.ylive.info/images/201703/103/0af1bfa32bb7387986b3220cd7801e18_1080x1080.jpg";
     */
    private void startForeground() {
        if (VarUtils.list.size() == 0||VarUtils.currentPosition==-1) {
            stopSelf();
            return;
        }
        String path = VarUtils.list.get(VarUtils.currentPosition).artwork_url;
        if (TextUtils.isEmpty(path)) {
            optimizeNotification();
            return;
        }
        if (!path.startsWith("http")) {
            Uri uri = Uri.parse(path);
            loadUriBitmap(uri, true);
            return;
        }
        //网络情况
        optimizeNotification();
        setHeadBitmap(path);
    }


    /**
     * 适用于暂停 不在出现文字提示
     *
     * @param path
     */
    private void getBitmapForNotification(String path) {
        setHeadBitmap(path);
    }

    /**
     * 出现文字提醒，不带图片
     */
    private void optimizeNotification() {
        Notification notification = NotificationHelper.getInstance().getNotification(VarUtils.list.get(VarUtils.currentPosition));
        startForeground(Constants.BroadcastConstants.NOTIFICATION_FLAG, notification);
    }

    /**
     * 出现文字提醒，带图片
     */
    private void optimizeNotification(Bitmap bitmap) {
        Notification notification = NotificationHelper.getInstance().getNotification(bitmap, VarUtils.list.get(VarUtils.currentPosition));
        startForeground(Constants.BroadcastConstants.NOTIFICATION_FLAG, notification);
    }

    /**
     * 适用于暂停 不在出现文字提示
     *
     * @param bitmap
     */
    private void startNotification(Bitmap bitmap) {
        Notification notification = NotificationHelper.getInstance().changeNotification(bitmap, VarUtils.list.get(VarUtils.currentPosition));
        startForeground(Constants.BroadcastConstants.NOTIFICATION_FLAG, notification);
    }

    private void setHeadBitmap(final String avatar) {
        if (TextUtils.isEmpty(avatar)) {
            startNotification(null);
            return;
        }
        if (!avatar.startsWith("http")) {
            Uri uri = Uri.parse(avatar);
            loadUriBitmap(uri, false);
            return;
        }
        loadUrlBitmap(avatar);
    }

    /**
     * 网络
     *
     * @param avatar
     */
    private void loadUrlBitmap(final String avatar) {

        ImageLoaderManager.imageLoaderBitMap(avatar, new OnBitmapListener() {
            @Override
            public void onLoadFailed() {
                if (mHandler == null || !avatar.equalsIgnoreCase(VarUtils.list.get(VarUtils.currentPosition).artwork_url)) {
                    return;
                }
                startNotification(null);
            }

            @Override
            public void onResourceReady(Bitmap resource) {
                if (mHandler == null || !avatar.equalsIgnoreCase(VarUtils.list.get(VarUtils.currentPosition).artwork_url)) {
                    return;
                }
                startNotification(resource);
            }
        });

    }

    /**
     * @param uri  本地
     * @param flag 是否进行文字提示
     */
    private void loadUriBitmap(Uri uri, final boolean flag) {
        final String avatar = uri.toString();
        ImageLoaderManager.imageLoaderBitMap(uri, new OnBitmapListener() {
            @Override
            public void onLoadFailed() {
                if (mHandler == null || !avatar.equalsIgnoreCase(VarUtils.list.get(VarUtils.currentPosition).artwork_url)) {
                    return;
                }
                if (flag) {
                    optimizeNotification();
                    return;
                }
                startNotification(null);
            }

            @Override
            public void onResourceReady(Bitmap resource) {
                if (mHandler == null || !avatar.equalsIgnoreCase(VarUtils.list.get(VarUtils.currentPosition).artwork_url)) {
                    return;
                }
                if (flag) {
                    optimizeNotification(resource);
                    return;
                }
                startNotification(resource);
            }
        });

    }

    private void clearData() {
        L.d("clearData", "clearData");
        unregisterReceiver(broadcast);
        if(playerPresenter==null||VarUtils.currentPosition==-1||VarUtils.list.size()==0||VarUtils.currentPosition>=VarUtils.list.size()){return;}
        VarUtils.saveData();
        EventBus.getDefault().post(VarUtils.getMessageBean(playerPresenter, false, VarUtils.list.get(VarUtils.currentPosition)));
        playerPresenter.release();
        VarUtils.clear();
        mHandler = null;
        bean = null;
        playerPresenter = null;
    }

}
