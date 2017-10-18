package com.music.android.managers;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.music.android.MusicApp;
import com.music.android.listener.IPlayer;
import com.music.android.listener.OnPlayingListener;
import com.music.android.utils.Constants;
import com.music.android.utils.HttpHelper;
import com.music.android.utils.L;

/**
 * Created by hui.lv on 2017/3/16.
 */

public class MediaPlayerImpl implements IPlayer, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    private final String TAG = "MediaPlayerImpl";
    private MediaPlayer mediaPlayer;
    private OnPlayingListener listener;

    public MediaPlayerImpl(Context context, OnPlayingListener listener) {
        this.listener = listener;
        initPlayer();
    }

    private void initPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }
    /**
     * file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/qq.mp3
     * http://abv.cn/music/光辉岁月.mp3
     *
     * @param dataSource
     */
    @Override
    public void setDataSource(String dataSource) {
        L.d(TAG, "---" + dataSource);
        if (mediaPlayer == null) {
            initPlayer();
        }
        if (dataSource.startsWith("http")) {
         startNetWorkMusic(dataSource);
        } else {
            mediaPlayer.reset();
            startLocalMusic(dataSource);
        }

    }

    private void startNetWorkMusic(String dataSource) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(dataSource);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            release();
        }
    }

    private void startLocalMusic(String dataSource) {
        try {
            mediaPlayer.setDataSource(dataSource);
            mediaPlayer.prepare();
        } catch (Exception e) {
            release();
        }

    }


    /**
     * 开始播放
     */
    @Override
    public void start() {
//       mediaPlayer.start();
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        L.d(TAG, "pause");
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    /**
     * 暂停之后重新开始
     */
    @Override
    public void restart() {
        L.d(TAG, "restart");
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    /**
     * 快进或者快退
     *
     * @param progress 百分数
     */
    @Override
    public void seekTo(float progress) {
        L.d(TAG, "progress=" + progress);
        if (mediaPlayer != null) {
            mediaPlayer.seekTo((int) (mediaPlayer.getDuration() * progress));
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        L.d(TAG, "stop");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * 释放资源
     */
    @Override
    public void release() {
        L.d(TAG, "release");
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception E) {
        }

    }

    @Override
    public long getDuration() {
        L.d(TAG, "getDuration---");
        long duration = 0;
        if (mediaPlayer != null) {
            duration = mediaPlayer.getDuration();
        }
        return duration;
    }

    /**
     * 播放上一首
     *
     * @param path
     */
    @Override
    public void previous(String path) {
        L.d(TAG, "previous");
        setDataSource(path);
    }

    /**
     * 播放下一首
     *
     * @param path
     */
    @Override
    public void next(String path) {
        previous(path);
    }

    @Override
    public void setCurrentPosition(long position) {

    }

    /**
     * 获得当前播放进度
     *
     * @return
     */
    @Override
    public long getCurrentProgress() {
        L.d(TAG, "getCurrentProgress");
        long progress = 0;
        if (mediaPlayer != null) {
            progress = mediaPlayer.getCurrentPosition();
        }
        return progress;
    }

    /**
     * 播放完成
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        L.i(TAG, "--onCompletion--");
        if (!HttpHelper.isNetworkAvailable(MusicApp.context)) {
            if (listener != null) {
                listener.onPlayerError(Constants.Code.NETWORK_ERROR);
            }
            return;
        }
        if (listener != null) {
            listener.onFinish();
        }
    }

    /**
     * 播放错误
     *
     * @param mp
     * @param what  1 网络连接错误 -38 文件找不到
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        L.d(TAG, "onError=" + what);
        if (listener != null) {
            listener.onPlayerError(what);
        }
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        L.d(TAG, "onPrepared");
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (listener != null) {
            listener.onStart();
        }
    }

}
