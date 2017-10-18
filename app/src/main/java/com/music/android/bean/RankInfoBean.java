package com.music.android.bean;

import java.util.List;

/**
 * Created by liuyun on 17/3/21.
 */

public class RankInfoBean extends IndexBaseBean {

    public List<DataBean> data;

    public static class DataBean extends MusicInfoBean {
        public List<MusicInfoBean> list;
    }
}
