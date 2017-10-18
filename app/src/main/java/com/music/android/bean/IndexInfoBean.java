package com.music.android.bean;

import java.util.List;

/**
 * Created by liuyun on 17/3/20.
 */

public class IndexInfoBean extends IndexBaseBean {


    public List<DataBean> data;

    public static class DataBean {

        public String module_name;
        public String type;
        public List<MusicInfoBean> list;


    }
}
