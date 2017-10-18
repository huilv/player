package com.music.android.listener;
/**
 * Created by hui.lv on 2017/3/13.
 */

public interface OnPlayingListener {
    /**
     *  播放错误
     * @param what
     */
    void onPlayerError(int  what);

    /**
     * 开始播放
     */
    void onStart();

    /**
     * 播放结束
     */
    void onFinish();
}
