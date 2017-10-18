package com.music.android.utils.comparator;

import com.music.android.bean.MusicInfoBean;
import com.music.android.utils.PinyinUtil;

import java.util.Comparator;

/**
 * Created by liuyun on 17/3/30.
 */

public class AZComparator implements Comparator<MusicInfoBean> {
    @Override
    public int compare(MusicInfoBean musicInfoBean1, MusicInfoBean musicInfoBean2) {
        String spell1 = PinyinUtil.getPingYin(musicInfoBean1.title);
        String spell2 = PinyinUtil.getPingYin(musicInfoBean2.title);
        if (spell1.compareTo(spell2) > 0) {
            return 1;
        } else {
            return -1;
        }
    }
}
