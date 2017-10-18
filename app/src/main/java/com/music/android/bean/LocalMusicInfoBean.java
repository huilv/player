package com.music.android.bean;

/**
 * Created by liuyun on 17/3/9.
 */

public class LocalMusicInfoBean extends CommMusicBean {

    public long duration;
    public String singer;
    public String newSinger;
    public String path;
    public String menuName;
    public long size;
    public long createTime;//歌单创建时间
    public int collection = 0;
    public boolean isChecked = false;
    public boolean isPlaying = false;
    public long currentProgress;
    LocalMusicInfoBean() {

    }

    LocalMusicInfoBean(String title, int duration, String singer, int collection, String path, String artwork_url) {
        super();
        this.title = title;
        this.duration = duration;
        this.singer = singer;
        this.collection = collection;
        this.path = path;
        this.artwork_url = artwork_url;
    }


    @Override
    public String toString() {
        return "LocalMusicInfoBean{" +
                "duration=" + duration +
                ", singer='" + singer + '\'' +
                ", path='" + path + '\'' +
                ", menuName='" + menuName + '\'' +
                ", createTime=" + createTime +
                ", collection=" + collection +
                ", isChecked=" + isChecked +
                ", isPlaying=" + isPlaying +
                '}';
    }
}
