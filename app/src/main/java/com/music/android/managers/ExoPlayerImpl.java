package com.music.android.managers;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SeekBar;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.music.android.MusicApp;
import com.music.android.listener.IPlayer;
import com.music.android.listener.OnPlayingListener;
import com.music.android.utils.L;

/**
 * Created by hui.lv on 2017/3/13.
 */

public class ExoPlayerImpl implements IPlayer, ExoPlayer.EventListener {
    private final String TAG = "ExoPlayerImpl";
    private SimpleExoPlayer exoPlayer;
    private DataSource.Factory dataSourceFactory;
    private ExtractorsFactory extractorsFactory;
    private MediaSource mediaSource;
    private OnPlayingListener listener;

    public ExoPlayerImpl(Context context, OnPlayingListener listener) {
        this.listener = listener;
        initPlayer(context);
    }

    private void initPlayer(Context context) {
        LoadControl localControl = new DefaultLoadControl();
        TrackSelector trackSelector = new DefaultTrackSelector();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, localControl);
        exoPlayer.addListener(this);
        dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getPackageName()), null);
        extractorsFactory = new DefaultExtractorsFactory();

//		OkHttpClient client = new OkHttpClient.Builder().build();
//		DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//		DataSource.Factory dataSourceFactory =new OkHttpDataSourceFactory(client,getDeviceModelName(),bandwidthMeter);
//		ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//		MediaSource mediaSource = new ExtractorMediaSource(data, dataSourceFactory, extractorsFactory, null, null);
    }

    /**
     * 测试数据
     * http://develop.mijack.cn:8080/FileSite/file/0
     * http://fs.web.kugou.com/9587e77d84d1ce954a3a4e1d0b52b89a/58c242f3/G089/M08/0A/13/mQ0DAFjArfCABm6aADWIFItF4-E130.mp3
     * file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/qq.mp3
     * <p>
     * 数据准备
     *
     * @param dataSource
     */
    @Override
    public void setDataSource(String dataSource) {
        if(!TextUtils.isEmpty(dataSource)) {
            Uri uri = Uri.parse(dataSource);
            L.d(TAG, dataSource);
            mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
            start();
        }
    }

    /**
     * 开始播放
     */
    @Override
    public void start() {
        if(exoPlayer!=null){
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        if(exoPlayer!=null){
            exoPlayer.setPlayWhenReady(false);
        }
    }

    /**
     * 暂停之后重新开始
     */
    @Override
    public void restart() {
        if(exoPlayer!=null){
            exoPlayer.setPlayWhenReady(true);
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
        if(exoPlayer!=null){
            exoPlayer.seekTo(exoPlayer.getCurrentWindowIndex(), positionValue(progress));
        }
    }


    /**
     * 停止
     */
    @Override
    public void stop() {
        if(exoPlayer!=null){
            exoPlayer.stop();
        }
    }

    /**
     * 释放资源
     */
    @Override
    public void release() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
            dataSourceFactory = null;
            extractorsFactory = null;
        }
    }


    /**
     * 播放上一首
     *
     * @param path
     */
    @Override
    public void previous(String path) {
        stop();
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

    @Override
    public long getDuration() {
        if(exoPlayer!=null){
            return exoPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 获得当前播放进度
     *
     * @return
     */
    @Override
    public long getCurrentProgress() {
        if(exoPlayer!=null){
            return exoPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED && listener != null) {
            listener.onFinish();
        } else if (playWhenReady && playbackState == ExoPlayer.STATE_READY&& listener != null) {
            listener.onStart();
        }
        L.i(TAG, playWhenReady + "----" + playbackState);
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        if (listener != null) {
            listener.onPlayerError(11);
        }

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    private long positionValue(float progress) {
        long duration = exoPlayer == null ? C.TIME_UNSET : exoPlayer.getDuration();
        return duration == C.TIME_UNSET ? 0 : (long) (duration * progress);
    }
}
