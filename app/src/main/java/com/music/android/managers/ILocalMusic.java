package com.music.android.managers;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by liuyun on 17/3/16.
 */

public interface ILocalMusic<T> {

    void getLocalSongs();

    void getLocalArtistsOrFord();

    void getMusicByArtistsName(String name);

    Flowable<List<T>> getAllSongs();

    Flowable<List<T>> getArtistsOrFord();

    Flowable<List<T>> getArtistsByName(String name);

}
