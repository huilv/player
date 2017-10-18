package com.music.android.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.music.android.bean.MusicInfoBean;
import com.music.android.bean.SongMenuBean;
import com.music.android.utils.L;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import io.reactivex.Flowable;

/**
 * Created by hui.lv on 2017/3/20.
 */

public class SongMenuHelper {
    private final String TAG = "SongMenuHelper";
    private final SQLiteDatabase database;
    public final static String TABLE_NAME = "SongMenu"; //表的名字
    public final static String _ID = "_id";
    public final static String SONG_MENU_NAME = "menuName"; //歌单名字
    public final static String SONG_NAME = "songName"; //歌曲名字
    public static final String SINGER = "singer"; //歌手
    public static final String PATH = "path";//路径
    public static final String ARTWORK_URL = "artwork_url";//图片路径
    public static final String CREATE_TIME = "time";//路径
    private static SongMenuHelper helper;

    private SongMenuHelper(Context context) {
        MusicDbHelper dbHelper = new MusicDbHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public static SongMenuHelper getDefault(Context context) {
        if (helper == null) {
            synchronized (SongMenuHelper.class) {
                helper = new SongMenuHelper(context);
            }
        }
        return helper;
    }

    /**
     * 该情况是歌单不存在 插入的同时创建歌单
     *
     * @param bean 歌曲对象
     * @return true 歌单已经存在   false 歌单不存在
     */
    public boolean insert(MusicInfoBean bean) {
        if (-1 != isMenuExist(bean.menuName)) {
            L.d(TAG, "insert---");
            return true;
        }
        L.d(TAG, "insert");
        ContentValues values = new ContentValues();
        values.put(SONG_MENU_NAME, bean.menuName);
        values.put(CREATE_TIME, System.currentTimeMillis());
        values.put(SONG_NAME, bean.title);
        values.put(SINGER, bean.singer);
        values.put(PATH, bean.path);
        values.put(ARTWORK_URL, bean.artwork_url);
        database.insert(TABLE_NAME, null, values);
        return false;
    }

    /**
     * 该情况是歌单已经存在   的情况下插入
     *
     * @param bean 歌曲对象
     * @return true 已经插入    false 可以插入
     */
    public boolean insertSong(MusicInfoBean bean) {
        long menuSongExist = isMenuSongExist(bean);
        if (0 == menuSongExist) {
            L.d(TAG, "menuSongExist");
            return true;
        }
        L.d(TAG, "insertSong");
        ContentValues values = new ContentValues();
        values.put(SONG_MENU_NAME, bean.menuName);
        values.put(SONG_NAME, bean.title);
        values.put(CREATE_TIME, menuSongExist);
        values.put(SINGER, bean.singer);
        values.put(PATH, bean.path);
        values.put(ARTWORK_URL, bean.artwork_url);
        database.insert(TABLE_NAME, null, values);
        return false;
    }

    /**
     * 插入歌单
     *
     * @param menuName 歌单名字
     * @return true 表示歌单已经存在
     * false 表示可以插入
     */
    public boolean insertMenu(String menuName) {
        if (-1 == isMenuExist(menuName)) {
            return true;
        }
        ContentValues values = new ContentValues();
        values.put(SONG_MENU_NAME, menuName);
        values.put(CREATE_TIME, "" + System.currentTimeMillis());
        database.insert(TABLE_NAME, null, values);
        return false;
    }


    /**
     * 查询歌单下的所有歌曲
     *
     * @param menuName
     */
    public ArrayList<MusicInfoBean> query(String menuName) {
        ArrayList<MusicInfoBean> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME, null, SONG_MENU_NAME + "=?", new String[]{menuName}, null, null, null);
        try {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                MusicInfoBean bean = new MusicInfoBean();
                bean.menuName = cursor.getString(cursor.getColumnIndex(SONG_MENU_NAME));
                bean.title = cursor.getString(cursor.getColumnIndex(SONG_NAME));
                bean.singer = cursor.getString(cursor.getColumnIndex(SINGER));
                bean.path = cursor.getString(cursor.getColumnIndex(PATH));
                String time = cursor.getString(cursor.getColumnIndex(CREATE_TIME));
                bean.createTime = Long.parseLong(time);
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
     * 查询出所有的歌曲 按照歌单进行分类 并且进行排序
     *
     * @return
     */
    public Flowable<ArrayList<SongMenuBean>> queryAllSortSongs() {
        ArrayList<SongMenuBean> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    SongMenuBean menuBean = new SongMenuBean();
                    menuBean.menuName = cursor.getString(cursor.getColumnIndex(SONG_MENU_NAME));
                    menuBean.id = cursor.getInt(cursor.getColumnIndex(_ID));
                    menuBean.menuCreateTime = Long.parseLong(cursor.getString(cursor.getColumnIndex(CREATE_TIME)));
                    menuBean.title = cursor.getString(cursor.getColumnIndex(SONG_NAME));
                    menuBean.singer = cursor.getString(cursor.getColumnIndex(SINGER));
                    menuBean.path = cursor.getString(cursor.getColumnIndex(PATH));
                    menuBean.artwork_url = cursor.getString(cursor.getColumnIndex(ARTWORK_URL));
                    menuBean.createTime = Long.parseLong(cursor.getString(cursor.getColumnIndex(CREATE_TIME)));
                    list.add(menuBean);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Flowable.just(list);

    }

    /**
     * 删除歌曲
     *
     * @param path
     */
    public boolean deleteSong(String path) {
        database.delete(TABLE_NAME, PATH + "=?", new String[]{path});
        return true;
    }

    /**
     * 删除 菜单
     *
     * @param songMenuName
     */
    public boolean deleteMenu(String songMenuName) {
        database.delete(TABLE_NAME, SONG_MENU_NAME + "=?", new String[]{songMenuName});
        return true;
    }

    /**
     * 将歌曲从菜单里面删除
     *
     * @param bean
     */
    public void deleteSongFromMenu(MusicInfoBean bean) {
        database.delete(TABLE_NAME, SONG_MENU_NAME + "=? and " + PATH + "=?", new String[]{bean.menuName, bean.path});
    }

    public void deleteAll() {
        database.delete(TABLE_NAME, null, null);
    }

    /**
     * 判定菜单是否存在
     *
     * @param menuName 菜单名字
     * @return -1 表示歌单不存在 其他表示存在
     */
    public long isMenuExist(String menuName) {
        long menuCreateTime = -1;
        L.d(TAG, "" + menuName);
        Cursor cursor = database.query(TABLE_NAME, null, SONG_MENU_NAME + "=?", new String[]{menuName}, null, null, null);
        try {
            while (cursor.moveToNext()) {

                String cursorString = cursor.getString(cursor.getColumnIndex(SONG_MENU_NAME));
                if (cursorString.equalsIgnoreCase(menuName)) {
                    String time = cursor.getString(cursor.getColumnIndex(CREATE_TIME));
                    menuCreateTime = Long.parseLong(time);
                    break;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return menuCreateTime;
    }

    /**
     * 判定该菜单下该歌曲是否存在
     *
     * @param bean
     * @return -1  表示不存在 0  表示存在   其他表示表的创建时间
     */
    public long isMenuSongExist(MusicInfoBean bean) {
        long menuCreateTime = -1;
        L.d(TAG, "PATH=" + bean.path);
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                String menuName = cursor.getString(cursor.getColumnIndex(SONG_MENU_NAME));
                String path = cursor.getString(cursor.getColumnIndex(PATH));
                L.d(TAG, "PATH=" + path);
                if (bean.menuName.equalsIgnoreCase(menuName)) {
                    L.d(TAG, "menuName");
                    String time = cursor.getString(cursor.getColumnIndex(CREATE_TIME));
                    menuCreateTime = Long.parseLong(time);
                    if (bean.path.equalsIgnoreCase(path)) {
                        menuCreateTime = 0;
                        L.d(TAG, "isMenuSongExist");
                        break;
                    }
                }

            }
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return menuCreateTime;
    }

    public Boolean update(String oldName, String newName) {
        L.d(TAG, oldName + "----" + newName);
        ContentValues contentValues = new ContentValues();
        contentValues.put(SONG_MENU_NAME, newName);
        int updateCount = database.update(TABLE_NAME, contentValues, SONG_MENU_NAME + "=?", new String[]{oldName});
        return (updateCount > 0);
    }

//    private class MusicComparator implements Comparator<SongMenuBean> {
//        private final String TAG = "MusicComparator";
//
//        @Override
//        public int compare(SongMenuBean o1, SongMenuBean o2) {
//
//            if (o1.list.size() != o2.list.size()) {
//                L.d(TAG, "MusicComparator");
//                return (o2.list.size() - o1.list.size());
//            }
//            return (int) (o1.menuCreateTime - o2.menuCreateTime);
//        }
//    }

//    private void optimizeList(ArrayList<SongMenuBean> list) {
//        for (int i = 0; i < list.size(); i++) {
//            SongMenuBean menuBean = list.get(i);
//            ArrayList<MusicInfoBean> beans = menuBean.list;
//            if (beans.size() > 1) {
//                Iterator<MusicInfoBean> iterator = beans.iterator();
//                while (iterator.hasNext()) {
//                    MusicInfoBean next = iterator.next();
//                    if (TextUtils.isEmpty(next.path)) {
//                        iterator.remove();
//                        break;
//                    }
//                }
//            }
//        }
//    }
}
