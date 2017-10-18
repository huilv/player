package com.music.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.io.Serializable;

/**
 * Created by liuyun on 17/3/3.
 */

public class MusicInfoBean extends LocalMusicInfoBean {

    public int id;
    public String uri;
    public UserBean user;
    public String stream_url;
    public int playback_count;
    public int favoritings_count;
    public int likes_count;
    public String skey = "";

    public MusicInfoBean() {

    }

    public MusicInfoBean(String title, int duration, String singer, int collection, String path, String artwork_url) {
        super(title, duration, singer, collection, path, artwork_url);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result * 31 + path.hashCode();
        return result;

    }

    @Override
    public boolean equals(Object obj) { //根据path 和 singer 同时判定是否同一个对象
        boolean flag = false;
        if (obj != null && obj instanceof MusicInfoBean) {
            MusicInfoBean bean = (MusicInfoBean) obj;
            flag = bean.path.equals(this.path);
            if (flag) {
                if ((singer == null && bean.singer != null) || (singer != null && bean.singer == null)) {
                    flag = false;
                }
                if (singer != null && bean.singer != null) {
                    flag = singer.equals(bean.singer);
                }
            }

        }
        return flag;
    }

    @Override
    public String toString() {
        return "MusicInfoBean{" +
                "id=" + id +
                ", uri='" + uri + '\'' +
                ", user=" + user +
                ", stream_url='" + stream_url + '\'' +
                ", playback_count=" + playback_count +
                ", favoritings_count=" + favoritings_count +
                ", likes_count=" + likes_count +
                '}';
    }
}
