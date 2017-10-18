package com.music.android.listener;

/**
 * Created by hui.lv on 2017/3/13.
 */

public interface IPlayer {
    /**
     * 设置资源文件
     * @param dataSource
     */
    void setDataSource(String dataSource);

    /**
     * 开始播放
     */
    void start();

    /**
     * 暂停
     */
    void pause();

    /**
     * 暂停之后 重新开始
     */
    void restart();

    /**
     * 拖动
     * @param progress
     */
    void seekTo(float progress);

    /**
     * 停止
     */
    void stop();

    /**
     * 释放资源
     */
    void release();

    /**
     * 获得当前播放进度
     * @return
     */
    long getCurrentProgress();

    /**
     * 获得当前位置
     * @param position
     */
    void setCurrentPosition(long position);

    /**
     * 歌曲的总时间
     * @return
     */
    long getDuration();

    /**
     * 上一首
     * @param path
     */
    void previous(String path);

    /**
     * 下一首
     * @param path
     */
    void next(String path);
}
