package com.music.android.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.music.android.MusicApp;
import com.music.android.bean.MessageIntentBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.bean.PathBean;
import com.music.android.service.MusicPlayService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hui.lv on 2017/3/24.
 */

public class PlayHelper {
    private static PlayHelper helper;

    private PlayHelper() {
    }

    public static PlayHelper getInstance() {
        if (helper == null) {
            synchronized (PlayHelper.class) {
                helper = new PlayHelper();
            }
        }
        return helper;
    }

    /**
     * service 是否存在
     *
     * @return
     */
    public boolean isServiceExist() {
        int size = getPlayMusics().size();
        return (size == 0 ? false : true);
    }

    /**
     * 获得播放列表
     *
     * @return
     */
    public List<MusicInfoBean> getPlayMusics() {
        List<MusicInfoBean> musics = MusicPlayService.getMusics();
        return musics;
    }

    /**
     * 当前播放
     *
     * @return
     */
    public int getCurrentPosition() {
        int position = MusicPlayService.getCurrentPosition();
        return position;
    }

    /**
     * 获得当前播放歌曲
     *
     * @return
     */
    public MusicInfoBean getCurrentMusic() {
        MusicInfoBean music = MusicPlayService.getCurrentMusic();

        return music;
    }

    /**
     * 获得当前是否缓冲完毕
     *
     * @return
     */
    public boolean getPreparedState() {
        boolean prepared = MusicPlayService.isPrepared();
        return prepared;
    }


    /**
     * 获得当前播放状态
     *
     * @return
     */
    public boolean getCurrentState() {
        boolean state = MusicPlayService.getCurrentState();
        return state;
    }

    /**
     * 获得当前状态 是否在打电话
     *
     * @return
     */
    public boolean getRingState() {
        boolean state = MusicPlayService.getRingState();
        return state;
    }

    public void sortMusics(List<MusicInfoBean> list, int currentPosition) {
        MusicPlayService.sortMusics(list, currentPosition);
    }

    /**
     * playservice 已经存在 即正在播放或者暂停状态
     *
     * @param context
     */
    public void startActivityWithServiceExist(Context context) {
//        context.startActivity(new Intent(context, PlayingActivity.class));
    }

    /**
     * 进入播放列表 当service销毁
     *
     * @param context
     * @param list
     * @param position
     * @param progress
     */
    public void startActivity(Context context, List<MusicInfoBean> list, int position, int progress) {

        MusicPlayService.setMusics(list);
        Intent intent = new Intent(context, MusicPlayService.class);
        MessageIntentBean intentBean = new MessageIntentBean();
        intentBean.onPlaying = true;
        intentBean.currentPosition = position;
        intentBean.currentProgress = progress;
        intent.putExtra(Constants.IntentType.BUNDLE_KEY_FLAG, intentBean);
        context.startService(intent);
//        context.startActivity(new Intent(context, PlayingActivity.class));
    }


    /**
     * 不进入播放界面
     *
     * @param context
     * @param list
     * @param position 指定的播放位置
     */
    public void changePlayList(Context context, List<MusicInfoBean> list, int position) {
        if (isServiceExist()) {
            MusicPlayService.resetMusics(position, list);
            return;
        }
        MusicPlayService.setMusics(list);
        Intent intent = new Intent(context, MusicPlayService.class);
        MessageIntentBean intentBean = new MessageIntentBean();
        intentBean.onPlaying = true;
        intentBean.currentPosition = position;
        intentBean.currentProgress = 0;
        intent.putExtra(Constants.IntentType.BUNDLE_KEY_FLAG, intentBean);
        context.startService(intent);
    }

    /**
     * 不进入播放界面
     *
     * @param context
     * @param list
     * @param position 指定的播放位置
     */
    public void changePlayList(Context context, List<MusicInfoBean> list, int position, int progress) {
        if (isServiceExist()) {
            MusicPlayService.resetMusics(position, list);
            return;
        }
        MusicPlayService.setMusics(list);
        Intent intent = new Intent(context, MusicPlayService.class);
        MessageIntentBean intentBean = new MessageIntentBean();
        intentBean.onPlaying = true;
        intentBean.currentPosition = position;
        intentBean.currentProgress = progress;
        intent.putExtra(Constants.IntentType.BUNDLE_KEY_FLAG, intentBean);
        context.startService(intent);
    }

    /**
     * 第一次进入MainActivity页面
     */
    public void startPlayList(Context context, List<MusicInfoBean> list, int position, int progress) {
        MusicPlayService.setMusics(list);
        Intent intent = new Intent(context, MusicPlayService.class);
        MessageIntentBean intentBean = new MessageIntentBean();
        intentBean.onPlaying = false;
        intentBean.currentPosition = position;
        intentBean.currentProgress = progress;
        intent.putExtra(Constants.IntentType.BUNDLE_KEY_FLAG, intentBean);
        context.startService(intent);
    }

    /**
     * 当service 不存在时，添加播放列表 或者添加下一首歌
     *
     * @param context
     * @param bean
     */
    public void startPlayList(Context context, MusicInfoBean bean) {
        List<MusicInfoBean> list = new ArrayList<>();
        list.add(bean);
        MusicPlayService.setMusics(list);
        Intent intent = new Intent(context, MusicPlayService.class);
        MessageIntentBean intentBean = new MessageIntentBean();
        intentBean.onPlaying = true;
        intentBean.currentPosition = 0;
        intentBean.currentProgress = 0;
        intent.putExtra(Constants.IntentType.BUNDLE_KEY_FLAG, intentBean);
        context.startService(intent);
    }

    /**
     * 删除歌曲
     *
     * @param path
     */
    public void deleteSong(String path) {
        Log.d("onMusicPlayingEvent", "deleteSong");
        Intent intent = new Intent();
        intent.setAction(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        intent.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.DELETE_FLAG);
        intent.putExtra(Constants.BroadcastConstants.DELETE_PATH, path);
        MusicApp.context.sendBroadcast(intent);
    }

    /**
     * 删除多个歌曲
     *
     * @param list
     */
    public void deleteSongList(List<MusicInfoBean> list) {
        Log.d("onMusicPlayingEvent", "deleteSongList");
        Parcelable[] musics = new PathBean[list.size()];
        for (int i = 0; i < list.size(); i++) {
            PathBean pathBean = new PathBean(list.get(i).path);
            musics[i] = pathBean;
        }
        Intent intent = new Intent();
        intent.setAction(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        intent.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.DELETE_LIST_FLAG);
        intent.putExtra(Constants.BroadcastConstants.DELETE_PATH, musics);
        MusicApp.context.sendBroadcast(intent);
    }

    public void notifyModeChanged() {
        Intent i4 = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        i4.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.MODE_CHANGE_FLAG);
        MusicApp.context.sendBroadcast(i4);
    }

    public boolean checkServices() {
        try {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            int resultCode = apiAvailability.isGooglePlayServicesAvailable(MusicApp.context);
            if (resultCode != ConnectionResult.SUCCESS) {
                return false;
            }
            return true;
        } catch (Exception e) {
        }
        return false;
    }


}
