package com.music.android.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.music.android.MusicApp;
import com.music.android.bean.MusicInfoBean;
import com.music.android.ui.adapter.BaseAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by liuyun on 17/4/12.
 */

public class PlayUtils {

    public static int count = -1;

    public static void randomPlay(BaseAdapter adapter, List<MusicInfoBean> data) {
        count = getRandom(1, data.size() - 1);
        scrollToPlayItem(adapter, data, count);
    }

    public static void play(BaseAdapter adapter, List<MusicInfoBean> data, int position) {
        scrollToPlayItem(adapter, data, position);
    }

    public static void play(MusicInfoBean musicInfoBean) {
        if (PlayHelper.getInstance().isServiceExist()) {
            return;
        }
        List<MusicInfoBean> list = new ArrayList<>();
        list.add(musicInfoBean);
        PlayHelper.getInstance().changePlayList(MusicApp.context, list, 0, (int) musicInfoBean.currentProgress);
    }

    public static void randomPlay(List<MusicInfoBean> data) {
        count = getRandom(0, data.size() - 1);
        scrollToPlayItem(data, count);
    }

    private static void scrollToPlayItem(List<MusicInfoBean> data, int position) {
        for (int i = 0; i < data.size(); i++) {
            MusicInfoBean musicInfoBean = data.get(i);
            if (position == i) {
                musicInfoBean.isPlaying = true;
            } else {
                musicInfoBean.isPlaying = false;
            }
        }
        play(data, position - 1);
    }

    private static void scrollToPlayItem(BaseAdapter adapter, List<MusicInfoBean> data, int position) {
        play(data.subList(1, data.size()), position - 1);
    }

    public static void play(@NonNull List<MusicInfoBean> list, @NonNull int position) {
        PlayHelper.getInstance().changePlayList(MusicApp.context, list, position == -1 ? 0 : position);
    }

    public static void startPlay(List<MusicInfoBean> list, int position) {
        PlayHelper.getInstance().startPlayList(MusicApp.context, list, position, 0);
    }

    private static int oldPosition;

    private static int getRandom(int min, int max) {
        Log.d("PlayUtils", " min : " + min);
        Log.d("PlayUtils", " max : " + max);
        if (min == max) {
            return min;
        }
        Random random = new Random();
        int position = random.nextInt(max) % (max - min + 1) + min;
        if (position == oldPosition) {
            if (position > 0) {
                position--;
            } else {
                position++;
            }
        }
        oldPosition = position;
        return position;
    }

}
