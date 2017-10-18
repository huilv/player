package com.music.android.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hui.lv on 2017/3/13.
 */

public class MessageEventBean implements Serializable {

    /**
     *  service 是否销毁
     */
    public boolean isServiceExist;
    /**
     *  是否切换歌曲
     */
    public boolean isSongChange;
    /**
     * 当前播放进度
     */
    public long currentProgress;
    /**
     * 总时间
     */
    public long duration;
    /**
     *  false 暂停 ; true 正在播放
     */
    public boolean onPlaying;
    /**
     *  音乐名字
     */
    public String musicName;
    /**
     * 歌手名字
     */
    public String authorName;
    /**
     * 点赞人数
     */
    public String likeCount;
    /**
     * 当前播放位置
     */
    public int currentPosition;
    /**
     * 图片
     */
    public String imgUrl;
    /**
     * 歌曲地址
     */
    public String path;

    /**
     * 歌曲是否开始播放
     */
    public boolean isPrepared;

    public List<MusicInfoBean> list=new ArrayList<>();

}
