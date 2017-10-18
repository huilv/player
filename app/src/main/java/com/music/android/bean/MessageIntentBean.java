package com.music.android.bean;

import java.io.Serializable;

/**
 * Created by hui.lv on 2017/3/13.
 */

public class MessageIntentBean implements Serializable{

    /**
     * 当前播放位置
     */
    public int currentPosition;

    /**
     * 当前播放进度
     */
    public long currentProgress;

    /**
     * 当前是否播放
     */
    public  boolean onPlaying;

    /**
     *  1 上一首 2 下一首
     */
    public  int onNext;

}
