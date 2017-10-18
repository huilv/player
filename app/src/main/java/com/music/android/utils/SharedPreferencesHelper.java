package com.music.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.music.android.MusicApp;
import com.music.android.bean.MusicInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hui.lv on 2017/3/8.
 */

public class SharedPreferencesHelper {

    /**
     * SharedPreferences name
     */
    public static final String NAME = "music";
    /**
     * SharedPreferences name
     */
    private static final String BIG_NAME = "musics";

    private static final String CLIENT_ID = "client_id";

    private static final String FIRST_UPDATE = "first_update";

    private static final String SHOW_PERMISSION_DIALOG = "show_permission_dialog";

    private static final String SHOW_AD_MAX_COUNT = "show_ad_max_count";

    private static final String SHOW_AD_CURRENT_TIME = "show_ad_current_time";
    private static final String LOCAL_MUSIC_SIZE = "localMusicSize";

    public static int getPlayingType() {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return preferences.getInt(Constants.PlayType.PLAY_TYPE_FLAG, Constants.PlayType.ORDER_FLAG);
    }

    /**
     * 设置播放模式
     *
     * @param type
     */
    public static void setPlayingType(int type) {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        preferences.edit().putInt(Constants.PlayType.PLAY_TYPE_FLAG, type).apply();
    }

    /**
     * 获取歌曲是否过滤
     */
    public static boolean getMusicFilter() {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(Constants.PlayType.MUSIC_FILTER_FLAG, true);
    }

    /**
     * 设置60秒以下歌曲是否过滤
     *
     * @param flag
     */
    public static void setMusicFilter(boolean flag) {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(Constants.PlayType.MUSIC_FILTER_FLAG, flag).apply();
    }


    /**
     * 设置睡眠时间
     *
     * @param time
     */
    public static void setSleepTime(long time) {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        preferences.edit().putLong(Constants.PlayType.MUSIC_SLEEP_FLAG, time).apply();
    }

    /**
     * 获取睡眠时间
     *
     * @return
     */
    public static long getSleepTime() {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return preferences.getLong(Constants.PlayType.MUSIC_SLEEP_FLAG, 0);
    }

    /**
     * 播放结束 保存当前播放信息
     *
     * @param value
     */
    public static void setMusicInfoBean(MusicInfoBean value) {
        Gson gson = new Gson();
        String json = gson.toJson(value);
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(BIG_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(Constants.PlayType.APP_OVER_FLAG, json).apply();
    }

    /**
     * 获得播放结束保存信息
     *
     * @return
     */
    public static MusicInfoBean getMusicInfoBean() {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(BIG_NAME, Context.MODE_PRIVATE);
        String string = preferences.getString(Constants.PlayType.APP_OVER_FLAG, "");
        Gson gson = new Gson();
        MusicInfoBean bean = gson.fromJson(string, MusicInfoBean.class);
        return bean;
    }


    /**
     * 获得服务器最新版本号版本号
     *
     * @return
     */
    public static String getVersionCode() {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return preferences.getString(Constants.PlayType.VERSIONCODE, SizeUtils.getVersionCode(MusicApp.context));
    }

    /**
     * 设置当前版本号
     *
     * @param code
     */
    public static void setVersionCode(String code) {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(Constants.PlayType.VERSIONCODE, code).apply();
    }

    /**
     * 保存ClientId
     *
     * @param clientId
     */

    public static void setClientId(String clientId) {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(CLIENT_ID, Context.MODE_PRIVATE);
        preferences.edit().putString(CLIENT_ID, clientId).apply();
    }

    /**
     * 查找ClientId
     *
     * @return String
     */
    public static String getClientId() {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(CLIENT_ID, Context.MODE_PRIVATE);
        return preferences.getString(CLIENT_ID, null);
    }

    public static void setFirstUpdate() {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(FIRST_UPDATE, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(FIRST_UPDATE, false).apply();
    }

    public static boolean isFirstUpdate() {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(FIRST_UPDATE, Context.MODE_PRIVATE);
        return preferences.getBoolean(FIRST_UPDATE, true);
    }

    /**
     * 设置睡眠时间
     *
     * @param time
     */
    public static void setPermissionShowTime(long time) {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(SHOW_PERMISSION_DIALOG, Context.MODE_PRIVATE);
        preferences.edit().putLong(SHOW_PERMISSION_DIALOG, time).apply();
    }

    /**
     * 获取睡眠时间
     *
     * @return
     */
    public static long getPermissionShowTime() {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(SHOW_PERMISSION_DIALOG, Context.MODE_PRIVATE);
        return preferences.getLong(SHOW_PERMISSION_DIALOG, 0);
    }

    /**
     * 设置当前显示的广告次数
     *
     * @param count
     */
    public static void setShowAdCurrentCount(int count) {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(SHOW_AD_MAX_COUNT, Context.MODE_PRIVATE);
        preferences.edit().putInt(SHOW_AD_MAX_COUNT, count).apply();
    }

    /**
     * 获取当前的广告显示次数
     *
     * @return
     */
    public static int getPShowAdCurrentCount() {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(SHOW_AD_MAX_COUNT, Context.MODE_PRIVATE);
        return preferences.getInt(SHOW_AD_MAX_COUNT, 1);
    }

    /**
     * 设置睡眠时间
     *
     * @param time
     */
    public static void setAdShowCurrentTime(long time) {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(SHOW_AD_CURRENT_TIME, Context.MODE_PRIVATE);
        preferences.edit().putLong(SHOW_AD_CURRENT_TIME, time).apply();
    }

    /**
     * 获取睡眠时间
     *
     * @return
     */
    public static long getADShowCurrentTime() {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(SHOW_AD_CURRENT_TIME, Context.MODE_PRIVATE);
        return preferences.getLong(SHOW_AD_CURRENT_TIME, 0);
    }

    /**
     * 保存上一次本地歌曲的数量
     * @param localMusicSize
     */
    public static void setLocalMusicSize(int localMusicSize) {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        preferences.edit().putInt(LOCAL_MUSIC_SIZE, localMusicSize).apply();
    }

    /**
     * 获得上一次本地歌曲数量
     * @return
     */
    public static int getLocalMusicSize( ) {
        SharedPreferences preferences = MusicApp.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return preferences.getInt(LOCAL_MUSIC_SIZE, 0);
    }
}
