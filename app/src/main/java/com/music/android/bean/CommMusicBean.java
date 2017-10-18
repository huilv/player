package com.music.android.bean;

import com.music.android.base.BaseBean;

import java.io.Serializable;

/**
 * Created by liuyun on 17/3/20.
 */

public class CommMusicBean extends BaseBean implements Serializable {
    public int songId;
    public String title;
    public String newTitle;
    public String artwork_url;
}

