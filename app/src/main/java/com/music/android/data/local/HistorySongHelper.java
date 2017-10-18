package com.music.android.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.music.android.bean.MusicInfoBean;
import com.music.android.utils.L;
import com.music.android.utils.LocalMusicQueryHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by hui.lv on 2017/3/20.
 */

public class HistorySongHelper {
    private final String TAG = "HistorySongHelper";
    private final SQLiteDatabase database;
    public final static String TABLE_NAME = "HistorySong"; //表的名字
    public final static String _ID = "_id";
    public final static String SONG_NAME = "songName"; //歌曲名字
    public static final String SINGER = "singer"; //歌手
    public static final String PATH = "path";//路径
    public static final String LIKE_COUNT = "count";//喜欢数量
    public static final String PLAYING = "play";//当前播放位置
    public static final String PROGRESS = "progress";//播放位置
    public static final String DURATION = "duration";//时间
    public static final String ARTWORK_URL = "artwork_url";//图片路径
    private static HistorySongHelper helper;

    private HistorySongHelper(Context context) {
        MusicDbHelper dbHelper = new MusicDbHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public static HistorySongHelper getDefault(Context context) {
        if (helper == null) {
            helper = new HistorySongHelper(context);

        }
        return helper;
    }

    /**
     * 插入
     *
     * @return
     */
    public void insertList(List<MusicInfoBean> list, int position, long progress, long duration) {
        delete();
        for (int i = 0; i < list.size(); i++) {
            MusicInfoBean bean = list.get(i);
            bean.isPlaying = false;
            bean.currentProgress = 0;
            bean.duration = 0;
            if (position == i) {
                bean.isPlaying = true;
                bean.currentProgress = progress;
                bean.duration = duration;
            }
            insert(bean);
        }
    }

    private void insert(MusicInfoBean bean) {
        ContentValues values = new ContentValues();
        values.put(SONG_NAME, bean.title);
        values.put(LIKE_COUNT, bean.likes_count);
        values.put(SINGER, bean.singer);
        values.put(PATH, bean.path);
        values.put(PLAYING, "" + bean.isPlaying);
        values.put(PROGRESS, bean.currentProgress);
        values.put(DURATION, bean.duration);
        values.put(ARTWORK_URL, bean.artwork_url);
        database.insert(TABLE_NAME, null, values);
    }


    /**
     * 查询所有的歌曲
     *
     * @return
     */
    public ArrayList<MusicInfoBean> query() {
        ArrayList<MusicInfoBean> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                MusicInfoBean bean = new MusicInfoBean();
                bean.title = cursor.getString(cursor.getColumnIndex(SONG_NAME));
                bean.singer = cursor.getString(cursor.getColumnIndex(SINGER));
                bean.path = cursor.getString(cursor.getColumnIndex(PATH));
                bean.likes_count = Integer.parseInt(cursor.getString(cursor.getColumnIndex(LIKE_COUNT)));
                bean.artwork_url = cursor.getString(cursor.getColumnIndex(ARTWORK_URL));
                bean.isPlaying = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(PLAYING)));
                bean.currentProgress = Long.parseLong(cursor.getString(cursor.getColumnIndex(PROGRESS)));
                bean.duration = Long.parseLong(cursor.getString(cursor.getColumnIndex(DURATION)));
                L.d(TAG, "---" + bean.isPlaying + "--" + bean.currentProgress + "---" + bean.duration);
                list.add(bean);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return list;
    }


    /**
     * 删除最喜欢
     *
     * @return
     */
    public boolean delete() {
        database.delete(TABLE_NAME, null, null);
        return true;
    }
    public boolean delete(String path) {
        database.delete(TABLE_NAME, PATH + "=?", new String[]{path});
        return true;
    }
    /**
     * 修改
     *
     * @return
     */
    public void update(String path) {
        update();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLAYING, "" + true);
        database.update(TABLE_NAME, contentValues, PATH + "=?", new String[]{path});
    }

    public void update() {
        ContentValues values = new ContentValues();
        values.put(PLAYING, "" + false);
        database.update(TABLE_NAME, values, PLAYING + "=?", new String[]{"true"});

    }
}
