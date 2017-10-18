package com.music.android.listener;
/**
 * Created by hui.lv on 2017/3/22.
 */

public interface OnMenuDeleteListener {
    /**
     * 歌单删除成功
     */
    void onSuccess(String name);
    /**
     * 歌单删除失败
     */
    void onFailed();
}
