package com.music.android.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.music.android.MusicApp;
import com.music.android.bean.MusicInfoBean;

import java.util.ArrayList;

/**
 * Created by hui.lv on 2017/3/8.
 */
public class LocalMusicQueryHelper {
    private static String TAG="LocalMusicQueryHelper";
    private static Uri URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;


    public static String[] COLUMNS = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,

    };

    public static ArrayList<MusicInfoBean> queryMediaByAlbum(Context context) {
        ArrayList<MusicInfoBean> mediaList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(URI, COLUMNS, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                MusicInfoBean object = new MusicInfoBean();
                object.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                object.singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                object.duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                object.path= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                L.d(TAG,object);
                mediaList.add(object);
            }
        } finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return mediaList;
    }

    private static String[] ALBUM = new String[]{
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM_ART,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.ARTIST
    };

    public static void getAlbumInfo(Context context, long albumId) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, ALBUM, "_id =" + String.valueOf(albumId), null, null);
        while (cursor.moveToNext()) {
            String string = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));

        }
    }

    public static void updateName(Context context,String updateName,String path) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.TITLE, updateName);
         resolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values, MediaStore.Audio.Media.DATA + "=?", new String[]{path});

    }

    public static void updateSinger(Context context,String updateName,String path) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.ARTIST, updateName);
        resolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values, MediaStore.Audio.Media.DATA + "=?", new String[]{path});

    }


    public static int getAllLocalSongs() {
        SharedPreferencesHelper.getMusicFilter();
        Cursor cursor = MusicApp.context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, COLUMNS,
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

    private static String getFilter() {
        return SharedPreferencesHelper.getMusicFilter() ? "60000" : "0";
    }

}
