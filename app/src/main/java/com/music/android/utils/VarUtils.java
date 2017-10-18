package com.music.android.utils;

import com.music.android.bean.MessageEventBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.managers.PlayerPresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hui.lv on 2017/3/13.
 */

public class VarUtils {
    public static final int HANDLER_SONG_CHANGE = 886688;
    public static final int HANDLER_PAUSE = 686868;
    public static final int HANDLER_PLAYING = 668866;
    public static final int SLEEP_OVER = 88588;
    private static final String TAG = "VarUtils";

    public static boolean onPlaying = false; //标记当前是否在播放
    public static boolean onPrepared = false;//标记当前播放器是否准备好，可以开始播放
    public static boolean onRingUp = false;//标记当前是否在打电话
    public static final int TIME_DELAY = 100;
    public static int currentPosition = 0;
    public static long currentProgress = 0;
    public static boolean isSongChange = false;
    public static long duration = 0;
    public static int likeCount = -1;
    public static int fastPosition = -1;
    public static boolean LOOP = true;
    public static long currentTime;
    public static int fastPause = -1;
    public static boolean onError = false;
    public static List<MusicInfoBean> list = new ArrayList();
    public static List<MusicInfoBean> counts = new ArrayList();

    public static MessageEventBean getMessageBean(PlayerPresenter playerPresenter, boolean isServiceExist, MusicInfoBean infoBean) {

        MessageEventBean bean = new MessageEventBean();
        if (VarUtils.onPrepared) {
            bean.duration = playerPresenter.getDuration();
            bean.currentProgress = playerPresenter.getCurrentProgress();
            VarUtils.currentProgress = playerPresenter.getCurrentProgress();
            VarUtils.duration = playerPresenter.getDuration();
        } else {
            bean.duration = 0;
            bean.currentProgress = 0;
            VarUtils.currentProgress = 0;
            VarUtils.duration = 0;
        }
        bean.isSongChange = VarUtils.isSongChange;
        if (VarUtils.isSongChange) {
            VarUtils.isSongChange = !VarUtils.isSongChange;
        }
        bean.authorName = infoBean.singer;
        bean.currentPosition = VarUtils.currentPosition;
        bean.onPlaying = VarUtils.onPlaying;
        bean.isPrepared = VarUtils.onPrepared;
        if (VarUtils.onRingUp) {
            bean.onPlaying = false;
        }
//        if (!onPrepared) {
//            bean.onPlaying = false;
//        }
        bean.musicName = infoBean.title;
        if (!isServiceExist) {
            bean.list.addAll(VarUtils.list);
        }
        bean.isServiceExist = isServiceExist;
        if (!isServiceExist) {
            bean.onPlaying = false;
        }
        bean.imgUrl = infoBean.artwork_url;

        bean.likeCount = "" + infoBean.likes_count;
        bean.path = infoBean.path;
        VarUtils.likeCount = infoBean.favoritings_count;
        return bean;
    }


    public static void changeMode() {
        switch (SharedPreferencesHelper.getPlayingType()) {
            case 0://默认的顺序
                L.d("VarUtils", "0");
                counts.clear();
                counts.addAll(list);
                break;
            case 1://单曲
                L.d("VarUtils", "1");
                counts.clear();
                counts.addAll(list);
                break;
            case 2://随机  需要打散
                L.d("VarUtils", "2");
                shuffle();
                break;
            default:
                break;
        }
    }

    public static void shuffle() {
        L.d("VarUtils", "shuffle=");
        counts.clear();
        List<MusicInfoBean> musics = new ArrayList<>();
        musics.addAll(list);
        Collections.shuffle(musics);
        counts.addAll(musics);
        musics = null;
    }

    public static void setPreviousPosition() {
        L.d("VarUtils", "setPreviousPosition=");
        try {
            int indexOf = counts.indexOf(list.get(currentPosition));
            int position = (indexOf == 0 ? list.size() - 1 : indexOf - 1);
            currentPosition = list.indexOf(counts.get(position));
        }catch (Exception E){
            currentPosition=0;
        }

    }

    public static void setNextPosition() {
        try {
            int indexOf = counts.indexOf(list.get(currentPosition));
            int position = (indexOf == list.size() - 1 ? 0 : indexOf + 1);
            currentPosition = list.indexOf(counts.get(position));
        }catch (Exception e){
            currentPosition=0;
        }


    }

    public static void onNext(int onNext) {
        if (onNext == 1) {
            setPreviousPosition();
        } else if (onNext == 2) {
            setNextPosition();
        }
    }


    public static void setData(int position, List<MusicInfoBean> musics) {
        list.clear();
        list.addAll(musics);
        currentPosition = position;
        changeMode();
    }

    public static void setData(long millis, int position, List<MusicInfoBean> musics) {
        currentTime = millis;
        list.clear();
        list.addAll(musics);
        currentPosition = position;
        onPrepared = false;
        onPlaying = true;
        isSongChange = true;
        onError=false;
        fastPosition = VarUtils.currentPosition;
        changeMode();
    }

    public static void clear() {
        L.d(TAG,"clear");
        list.clear();
        counts.clear();
        onPlaying = false;
        onRingUp = false;
        onPrepared = false;
        currentPosition = 0;
        currentProgress = 0;
        isSongChange = false;
        duration = 0;
        fastPosition = -1;
        LOOP = false;
        fastPause = -1;
        currentTime = 0;
        onError=false;
    }

    public static void previous(long timeMillis) {
        currentTime = timeMillis;
        isSongChange = true;
        onPrepared = false;
        onPlaying = true;
        onError=false;
        setPreviousPosition();
        fastPosition = currentPosition;
    }

    public static void next(long millis) {
        currentTime = millis;
        isSongChange = true;
        onPrepared = false;
        onPlaying = true;
        onError=false;
        setNextPosition();
        fastPosition = currentPosition;
    }

    public static void updateSinger(MusicInfoBean bean) {
        String path = bean.path;
        for (MusicInfoBean infoBean : VarUtils.list) {
            if (infoBean.path.equalsIgnoreCase(path)) {
                infoBean.singer = bean.newSinger;
                return;
            }
        }
    }


    public static void updateTitle(MusicInfoBean bean) {
        String path = bean.path;
        for (MusicInfoBean infoBean : VarUtils.list) {
            if (infoBean.path.equalsIgnoreCase(path)) {
                infoBean.title = bean.newTitle;
                return;
            }
        }
    }

    public static void isPause(long millis) {
        currentTime = millis;
        fastPause = 1;
        onPlaying = !onPlaying;
    }

    public static void saveData() {

        MusicInfoBean bean = list.get(currentPosition);
        bean.currentProgress = currentProgress;
        bean.duration = duration;
        SharedPreferencesHelper.setMusicInfoBean(bean);
    }


}
