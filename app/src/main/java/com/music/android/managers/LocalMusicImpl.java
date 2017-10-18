package com.music.android.managers;

import android.database.Cursor;
import android.provider.MediaStore;

import com.music.android.MusicApp;
import com.music.android.utils.SharedPreferencesHelper;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuyun on 17/3/16.
 */

public abstract class LocalMusicImpl<T> implements ILocalMusic {

    private String[] entryArray = new String[]{MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE};

    @Override
    public void getLocalSongs() {
        getAllSongs()

                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getSubscriber());
    }

    @Override
    public void getLocalArtistsOrFord() {
        getArtistsOrFord()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getSubscriber());
    }

    @Override
    public void getMusicByArtistsName(String name) {

        getArtistsByName(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getSubscriber());
    }

    private Subscriber<List<T>> getSubscriber() {
        return new Subscriber<List<T>>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(List<T> list) {
                getResult(list);
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        };
    }

    @Override
    public Flowable<List<T>> getAllSongs() {
        SharedPreferencesHelper.getMusicFilter();
        Cursor cursor = MusicApp.context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, entryArray,
                MediaStore.Audio.Media.DURATION + ">?",
                new String[]{getFilter()}, null);
        final List<T> infoBeanList = new ArrayList<>();
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        T data = buildData(cursor);
                        if (data != null) {
                            infoBeanList.add(data);
                        }
                    } while (cursor.moveToNext());
                }
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return Flowable.just(infoBeanList);
    }


    public int getAllLocalSongs() {
        SharedPreferencesHelper.getMusicFilter();
        Cursor cursor = MusicApp.context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, entryArray,
                MediaStore.Audio.Media.DURATION + ">?",
                new String[]{getFilter()}, null);
        int count = 0;
        try {
            if (cursor != null) {
                count = cursor.getCount();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return count;
    }


    @Override
    public Flowable<List<T>> getArtistsOrFord() {
        Cursor cursor = MusicApp.context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DATA},
                MediaStore.Audio.Media.DURATION + ">?",
                new String[]{getFilter()}, null);
        Map<String, T> commonBeanMap = new HashMap<>();
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        buildMapData(cursor, commonBeanMap);
                    } while (cursor.moveToNext());
                }
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }

        List<T> mapValuesList = new ArrayList<>(commonBeanMap.values());
        return Flowable.just(mapValuesList);
    }

    @Override
    public Flowable<List<T>> getArtistsByName(String name) {
        Cursor cursor = MusicApp.context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, entryArray,
                MediaStore.Audio.Media.DURATION + ">? and " + MediaStore.Audio.Media.TITLE + " LIKE ? ",
                new String[]{getFilter(), "%" + name + "%"}, null);
        List<T> infoBeanList = new ArrayList<>();
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        T data = buildData(cursor);
                        if (data != null) {
                            infoBeanList.add(data);
                        }
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return Flowable.just(infoBeanList);
    }

    private String getFilter() {
        return SharedPreferencesHelper.getMusicFilter() ? "60000" : "0";
    }

    protected abstract void buildMapData(Cursor cursor, Map<String, T> commonBeanMap);

    protected abstract T buildData(Cursor cursor);

    protected abstract void getResult(List<T> list);

}
