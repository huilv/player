package com.music.android.bean;

import java.util.List;

/**
 * Created by liuyun on 17/3/21.
 */

public class RankListBean extends IndexBaseBean {

    public DataBean data;

    public static class DataBean {

        public String module_name;
        public String module_logo;
        public String module_desc;
        public int count;
        public List<MusicInfoBean> list;

    }
}
