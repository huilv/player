package com.music.android.utils;

import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.music.android.MusicApp;
import com.music.android.bean.CommonBean;
import com.music.android.bean.MusicInfoBean;

import java.util.Map;

/**
 * Created by liuyun on 17/3/16.
 */

public class LocalMusicUtils {

    public static MusicInfoBean getAllSong(Cursor cursor) {
        MusicInfoBean infoBean = new MusicInfoBean();
        infoBean.songId = cursor.getInt(0);//歌曲id
        infoBean.title = cursor.getString(1); // 歌曲名
        infoBean.duration = cursor.getInt(2);// 时长
        infoBean.singer = cursor.getString(3); // 歌手名
        infoBean.path = cursor.getString(4); // 文件路径
        infoBean.artwork_url = "content://media/external/audio/media/" + infoBean.songId + "/albumart";
        infoBean.size = cursor.getLong(5);
        return infoBean;
    }

    public static MusicInfoBean getAllSong(Cursor cursor, String name) {
        MusicInfoBean infoBean = new MusicInfoBean();
        infoBean.songId = cursor.getInt(0);//歌曲id
        infoBean.title = cursor.getString(1);  // 歌曲名
        infoBean.duration = cursor.getInt(2);  // 时长
        infoBean.singer = cursor.getString(3); // 歌手名
        infoBean.path = cursor.getString(4);   // 文件路径
        infoBean.artwork_url = "content://media/external/audio/media/" + infoBean.songId + "/albumart";
        infoBean.size = cursor.getLong(5);
        if (TextUtils.isEmpty(infoBean.singer) || !infoBean.singer.equals(name)) {
            return null;
        }
        return infoBean;
    }

    public static MusicInfoBean getAllSongByName(Cursor cursor) {
        MusicInfoBean infoBean = new MusicInfoBean();
        infoBean.songId = cursor.getInt(0);   //歌曲id
        infoBean.title = cursor.getString(1); // 歌曲名
        infoBean.duration = cursor.getInt(2); // 时长
        infoBean.singer = cursor.getString(3);// 歌手名
        infoBean.path = cursor.getString(4);  // 文件路径
        infoBean.artwork_url = "content://media/external/audio/media/" + infoBean.songId + "/albumart";
        infoBean.size = cursor.getLong(5);
        return infoBean;
    }

    public static MusicInfoBean getAllSongByFord(Cursor cursor, String ford) {
        MusicInfoBean infoBean = new MusicInfoBean();
        String songId = cursor.getString(0);   //歌曲id
        infoBean.title = cursor.getString(1);  //歌曲名
        infoBean.duration = cursor.getInt(2);  //时长
        infoBean.singer = cursor.getString(3); //歌手名
        infoBean.path = cursor.getString(4);   //文件路径
        infoBean.artwork_url = "content://media/external/audio/media/" + songId + "/albumart";
        infoBean.size = cursor.getLong(5);

        if (infoBean.path == null || infoBean.path.indexOf(ford) <= 0) {
            return null;
        }
        return infoBean;
    }

    public static void getArtists(Cursor cursor, Map<String, CommonBean> commonBeanMap) {
        CommonBean infoBean = new CommonBean();
        infoBean.singer = cursor.getString(2);// 歌手名
        if (commonBeanMap.get(infoBean.singer) != null) {
            CommonBean commonBean = commonBeanMap.get(infoBean.singer);
            commonBean.count++;
        } else {
            commonBeanMap.put(infoBean.singer, infoBean);
        }

    }

    public static void getFord(Cursor cursor, Map<String, CommonBean> commonBeanMap) {
        if (!TextUtils.isEmpty(cursor.getString(3))) {
            CommonBean infoBean = new CommonBean();
            infoBean.folder = getFileName(cursor.getString(3));// 文件地址
            if (commonBeanMap.get(infoBean.folder) != null) {
                CommonBean commonBean = commonBeanMap.get(infoBean.folder);
                commonBean.count++;
            } else {
                commonBeanMap.put(infoBean.folder, infoBean);
            }
        }
    }

    private static String getFileName(String url) {
        String[] files = url.split("/");
        return files[files.length - 2];
    }


}
