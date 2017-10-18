package com.music.android.listener;

import com.music.android.bean.MusicInfoBean;

/**
 * Created by hui.lv on 2017/3/31.
 */

public interface OnSongUpdateListener {
    /**
     * 歌单名字修改成功
     * @param
     */
    void onSongNameUpdateSuccess(MusicInfoBean bean);

    /**
     * 歌单删除成功
     * @param
     */
    void onSongArtistUpdateSuccess(MusicInfoBean bean);


}
