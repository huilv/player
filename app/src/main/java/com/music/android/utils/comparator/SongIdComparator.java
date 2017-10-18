package com.music.android.utils.comparator;

import com.music.android.bean.MusicInfoBean;

import java.util.Comparator;

/**
 * Created by liuyun on 17/3/30.
 */

public class SongIdComparator implements Comparator<MusicInfoBean> {
    @Override
    public int compare(MusicInfoBean musicInfoBean1, MusicInfoBean musicInfoBean2) {
        if (musicInfoBean1.songId > musicInfoBean2.songId) {
            return 1;
        } else if (musicInfoBean1.songId < musicInfoBean2.songId) {
            return -1;
        } else {
            return 0;
        }
    }
}
