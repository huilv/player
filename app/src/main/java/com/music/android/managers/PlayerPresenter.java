package com.music.android.managers;

import android.content.Context;

import com.music.android.listener.IPlayer;
import com.music.android.listener.OnPlayingListener;


/**
 * 该类的主要功能是实现解耦，当播放器切换时，只需要重写IPlayer的实现类
 * player = new XXImpl(context, listener);
 * Created by hui.lv on 2017/3/13.
 */

public class PlayerPresenter {

    private IPlayer player;

    public PlayerPresenter(Context context, OnPlayingListener listener) {
        player = new ExoPlayerImpl(context, listener);
        //  player = new MediaPlayerImpl(context, listener);
    }

    public void setDataSource(String dataSource) {
        if(player!=null){
            player.setDataSource(dataSource);
        }
    }

    public void reStart() {
        if(player!=null){
            player.restart();
        }
    }

    public void pause() {
        if(player!=null){
            player.pause();
        }
    }

    public void stop() {
        if(player!=null){
            player.stop();
        }
    }

    public void seekTo(float progress) {
        if(player!=null){
            player.seekTo(progress);
        }
    }

    public void release() {
        if(player!=null){
            player.release();
        }
    }

    public void previous(String path) {
        if(player!=null){
            player.previous(path);
        }
    }

    public void next(String path) {
        if(player!=null){
            player.next(path);
        }
    }

    public long getCurrentProgress() {
        if(player!=null){
            return player.getCurrentProgress();
        }
        return 0;
    }

    public long getDuration() {
        if(player!=null){
            return player.getDuration();
        }
        return 0;
    }
}
