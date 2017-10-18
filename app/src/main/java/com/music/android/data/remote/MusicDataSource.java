package com.music.android.data.remote;

import com.music.android.bean.MusicInfoBean;
import com.music.android.bean.PlaylistJunctionBean;
import com.music.android.bean.PlaylistNameBean;
import com.music.android.bean.SongMenuBean;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Flowable;
import mobi.android.adlibrary.internal.ad.bean.Flow;

/**
 * Created by liuyun on 17/3/10.
 */

public interface MusicDataSource {

    interface LoadMusicCallback {
        void onMusicsLoaded(List<MusicInfoBean> musics);

        void onDataNotAvailable();
    }

    void getMusics(@NonNull String table_name, @NonNull LoadMusicCallback callback);

    List<MusicInfoBean> getMusics(@NonNull String table_name);

    int getMusicCount(@NonNull String table_name);

    void saveRecentlyMusic(@NonNull MusicInfoBean musicInfoBean);

    List<MusicInfoBean> getRecentlyMusics();

    void deleteRecentlyMusic(MusicInfoBean musicInfoBean);

    boolean isPlaylistNameExist(String name);

    long insertPlaylistName(String name);

    long insertPlaylistJunction(String nameId, String musicId);

    long insertPlaylistMusic(MusicInfoBean musicInfoBean);

    long insertPlaylistNameAndMusic(PlaylistNameBean playlistNameBean, MusicInfoBean musicInfoBean);

    long insertPlaylistNameAndMusic(SongMenuBean songMenuBean);

    long countPlaylist(String nameId);

    long getPlaylistNameId(String playlistName);

    int updatePlaylistName(String name, String nameId);

    int deletePlayListName(String id);

    int deletePlayListJunction(String id);

    int deletePlayListJunction(String nameId, String musicId);

    Flowable<List<PlaylistNameBean>> getPlaylistNames();

    Flowable<List<MusicInfoBean>> getPlaylistMusics(String nameId);

    Flowable<List<PlaylistJunctionBean>> getPlaylistJunctions();

}
