package com.music.android.utils.comparator;

import com.music.android.bean.MusicInfoBean;

import java.util.Comparator;

/**
 * Created by liuyun on 17/3/30.
 */

public class TimeComparator implements Comparator<MusicInfoBean> {
    @Override
    public int compare(MusicInfoBean musicInfoBean1, MusicInfoBean musicInfoBean2) {
        if (musicInfoBean1.createTime > musicInfoBean2.createTime) {
            return 1;
        } else if (musicInfoBean1.createTime < musicInfoBean2.createTime) {
            return -1;
        } else {
            return 0;
        }
    }
}
