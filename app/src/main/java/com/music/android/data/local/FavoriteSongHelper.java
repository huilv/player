package com.music.android.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.music.android.bean.MusicInfoBean;
import com.music.android.bean.SongMenuBean;
import com.music.android.utils.L;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by hui.lv on 2017/3/20.
 */

public class FavoriteSongHelper {
    private final String TAG = "FavoriteSongHelper";
    private final SQLiteDatabase database;
    public final static String TABLE_NAME = "FavoriteSong"; //表的名字
    public final static String _ID = "_id";
    public final static String SONG_NAME = "songName"; //歌曲名字
    public static final String SINGER = "singer"; //歌手
    public static final String PATH = "path";//路径
    public static final String LIKE_COUNT = "count";//喜欢数量
    public static final String CREATE_TIME = "time";//创建时间
    public static final String ARTWORK_URL = "artwork_url";//图片路径
    private static FavoriteSongHelper helper;

    private FavoriteSongHelper(Context context) {
        MusicDbHelper dbHelper = new MusicDbHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public static FavoriteSongHelper getDefault(Context context) {
        if (helper == null) {
            synchronized (FavoriteSongHelper.class) {
                helper = new FavoriteSongHelper(context);
            }
        }
        return helper;
    }

    /**
     * 插入
     *
     * @param bean
     * @return
     */
    public boolean insert(MusicInfoBean bean) {
        L.d(TAG, "insert");
        if(bean!=null){
            ContentValues values = new ContentValues();
            values.put(CREATE_TIME, System.currentTimeMillis());
            values.put(SONG_NAME, bean.title);
            values.put(LIKE_COUNT, bean.likes_count);
            values.put(SINGER, bean.singer);
            values.put(PATH, bean.path);
            values.put(ARTWORK_URL, bean.artwork_url);
            database.insert(TABLE_NAME, null, values);
        }
        return false;
    }


    /**
     * 查询所有的歌曲
     *
     * @return
     */
    public ArrayList<MusicInfoBean> query() {
        ArrayList<MusicInfoBean> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, "time desc");
        try {
//            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                MusicInfoBean bean = new MusicInfoBean();
                bean.title = cursor.getString(cursor.getColumnIndex(SONG_NAME));
                bean.singer = cursor.getString(cursor.getColumnIndex(SINGER));
                bean.path = cursor.getString(cursor.getColumnIndex(PATH));
                String count = cursor.getString(cursor.getColumnIndex(LIKE_COUNT));
                bean.likes_count = Integer.parseInt(count);
                String time = cursor.getString(cursor.getColumnIndex(CREATE_TIME));
                bean.createTime = Long.parseLong(time);
                bean.artwork_url=cursor.getString(cursor.getColumnIndex(ARTWORK_URL));
                list.add(bean);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
//        Collections.sort(list, new MusicComparator());
        L.d(TAG,"SIZE==="+list.size());
        return list;
    }


    /**
     * 删除最喜欢
     *
     * @param path
     * @return
     */
    public boolean delete(String path) {
        database.delete(TABLE_NAME, PATH + "=?", new String[]{path});
        return true;
    }


    /**
     * 歌曲是否存在
     *
     * @param path
     * @return
     */
    public boolean isSongExist(String path) {

        Cursor cursor = database.query(TABLE_NAME, null, PATH + "=?", new String[]{path}, null, null, null);
        int count = 0;
        try {
            count = cursor.getCount();
            L.d(TAG, "---count---" + count);
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return (count == 0 ? false : true);
    }


    private class MusicComparator implements Comparator<MusicInfoBean> {
        private final String TAG = "MusicComparator";

        @Override
        public int compare(MusicInfoBean o1, MusicInfoBean o2) {
            return (int) (o2.createTime - o1.createTime);
        }
    }

}
