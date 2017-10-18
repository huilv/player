package com.music.android.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.music.android.bean.MusicInfoBean;
import com.music.android.bean.PlaylistJunctionBean;
import com.music.android.bean.PlaylistNameBean;
import com.music.android.bean.SongMenuBean;
import com.music.android.data.remote.MusicDataSource;
import com.music.android.utils.AnalyticsConstants;
import com.music.android.utils.AnalyticsUtils;
import com.music.android.utils.L;
import com.music.android.utils.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;
import static com.music.android.data.local.SongMenuHelper.ARTWORK_URL;
import static com.music.android.data.local.SongMenuHelper.PATH;
import static com.music.android.data.local.SongMenuHelper.SINGER;
import static com.music.android.data.local.SongMenuHelper.SONG_NAME;
import static com.music.android.data.local.SongMenuHelper.TABLE_NAME;
import static com.music.android.data.local.SongMenuHelper._ID;

/**
 * Created by liuyun on 17/3/10.
 */

public class MusicLocalDataSource implements MusicDataSource {

    String[] projection = {
            MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_TITLE,
            MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_DURATION,
            MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_SINGER,
            MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_FAVORITE,
            MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_PATH,
            MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_IMAGE_PATH
    };


    private static MusicLocalDataSource INSTANCE;

    private MusicDbHelper mDbHelper;

    // Prevent direct instantiation.
    private MusicLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new MusicDbHelper(context);
    }

    public static MusicLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MusicLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void getMusics(@NonNull String tableName, @NonNull LoadMusicCallback callback) {
        List<MusicInfoBean> musics = getMusics(tableName);
        if (musics.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onMusicsLoaded(getMusics(tableName));
        }
    }

    @Override
    public List<MusicInfoBean> getMusics(@NonNull String table_name) {
        List<MusicInfoBean> musics = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.query(table_name, projection, null, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                musics.add(getData(c));
            }
        }
        if (c != null) {
            c.close();
        }
        db.close();
        return musics;
    }

    @Override
    public int getMusicCount(@NonNull String tableName) {
        List<MusicInfoBean> musics = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.query(tableName, projection, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                musics.add(getData(c));
            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        return musics.size();
    }

    @Override
    public void saveRecentlyMusic(@NonNull MusicInfoBean musicInfoBean) {
        if(musicInfoBean==null||TextUtils.isEmpty(musicInfoBean.path)){
            return;
        }
        if (musicInfoBean.path.startsWith("http")) {
            AnalyticsUtils.vMusicClick(AnalyticsConstants.AMOUNTPLAY, AnalyticsConstants.Action.ONLINE_PLAY, "");
        } else {
            AnalyticsUtils.vMusicClick(AnalyticsConstants.AMOUNTPLAY, AnalyticsConstants.Action.LOCAL_PLAY, "");
        }
        saveHistory(musicInfoBean);
        saveMusic(musicInfoBean);
    }


    private void saveHistory(MusicInfoBean bean) {
        SharedPreferencesHelper.setMusicInfoBean(bean);
    }

    private void saveMusic(MusicInfoBean musicInfoBean) {
        checkNotNull(musicInfoBean);
        int count = getMusicCount(MusicPersistenceContract.RecentlyEntry.TABLE_NAME);
        int i = deleteData(musicInfoBean.path);
        if (count >= 50) {
            if (i == 0) {
                List<MusicInfoBean> musics = getMusics(MusicPersistenceContract.RecentlyEntry.TABLE_NAME);
                deleteData(musics.get(0).path);
            }
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.insert(MusicPersistenceContract.RecentlyEntry.TABLE_NAME, null, setData(musicInfoBean));
        db.close();
    }


    @Override
    public List<MusicInfoBean> getRecentlyMusics() {
        return getMusics(MusicPersistenceContract.RecentlyEntry.TABLE_NAME);
    }

    @Override
    public void deleteRecentlyMusic(MusicInfoBean musicInfoBean) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        deleteData(musicInfoBean.path);
        db.close();
    }

    @Override
    public boolean isPlaylistNameExist(String name) {
        String[] args = {name};

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql = "select count(*) from " + MusicPersistenceContract.PlayListEntry.TABLE_NAME +
                " where " + MusicPersistenceContract.PlayListNameColumns.COLUMN_PLAYLIST_NAME + " =? ";
        Cursor cursor = db.rawQuery(sql, args);
        long count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                count = cursor.getLong(0);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return count > 0;
    }

    @Override
    public long insertPlaylistName(String name) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MusicPersistenceContract.PlayListNameColumns.COLUMN_PLAYLIST_NAME, name);
        return database.insert(MusicPersistenceContract.PlayListEntry.TABLE_NAME, null, contentValues);
    }

    @Override
    public long insertPlaylistJunction(String nameId, String musicId) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MusicPersistenceContract.PlayListJunctionColumns.COLUMN_PLAYLIST_ID, musicId);
        contentValues.put(MusicPersistenceContract.PlayListJunctionColumns.COLUMN_PLAYLIST_NAME_ID, nameId);
        return database.insert(MusicPersistenceContract.PlayListEntry.JUNCTION_TABLE_NAME, null, contentValues);
    }

    @Override
    public long insertPlaylistMusic(MusicInfoBean musicInfoBean) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SONG_NAME, musicInfoBean.title);
        values.put(SINGER, musicInfoBean.singer);
        values.put(PATH, musicInfoBean.path);
        values.put(ARTWORK_URL, musicInfoBean.artwork_url);
        return database.insert(TABLE_NAME, null, values);
    }

    @Override
    public long insertPlaylistNameAndMusic(PlaylistNameBean playlistNameBean, MusicInfoBean musicInfoBean) {

        long musicId = getMusicIdByPath(musicInfoBean);

        long nameId = playlistNameBean.nameId;

        if (isHasPlayListJunction(String.valueOf(musicId), String.valueOf(nameId))) {
            return -1;
        }

        if (musicId <= 0) {
            musicId = insertPlaylistMusic(musicInfoBean);
        }

        if (nameId < 0) {
            nameId = insertPlaylistName(playlistNameBean.name);
        }

        if (musicId > 0 && nameId > 0) {
            return insertPlaylistJunction(String.valueOf(nameId), String.valueOf(musicId));
        } else {
            return -1;
        }

    }

    @Override
    public long insertPlaylistNameAndMusic(SongMenuBean songMenuBean) {
        if (songMenuBean.path == null || songMenuBean.path.equals("")) {
            return insertPlaylistName(songMenuBean.menuName);
        } else {
            long nameId;
            if (!isPlaylistNameExist(songMenuBean.menuName)) {
                nameId = insertPlaylistName(songMenuBean.menuName);
            } else {
                nameId = getPlaylistNameId(songMenuBean.menuName);
            }
            long musicId = songMenuBean.id;
            return insertPlaylistJunction(String.valueOf(nameId), String.valueOf(musicId));

        }
    }

    @Override
    public long countPlaylist(String nameId) {
        String sql = "select count(*) from SongMenu left join junction on junction.music_id = SongMenu._id where junction.playlist_name_id = ?";

        String[] selectArgs = {nameId};
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectArgs);
        long count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                count = cursor.getLong(0);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return count;
    }

    @Override
    public long getPlaylistNameId(String playlistName) {
        String[] args = {playlistName};

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql = "select _id from " + MusicPersistenceContract.PlayListEntry.TABLE_NAME +
                " where " + MusicPersistenceContract.PlayListNameColumns.COLUMN_PLAYLIST_NAME + " =? ";
        Cursor cursor = db.rawQuery(sql, args);
        long nameId = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                nameId = cursor.getLong(0);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return nameId;
    }

    @Override
    public int updatePlaylistName(String name, String nameId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MusicPersistenceContract.PlayListNameColumns.COLUMN_PLAYLIST_NAME, name);
        String whereClause = BaseColumns._ID + "=?";
        String[] selectArgs = {nameId};
        return db.update(MusicPersistenceContract.PlayListEntry.TABLE_NAME, values, whereClause, selectArgs);
    }

    @Override
    public int deletePlayListName(String nameId) {
        String selection = BaseColumns._ID + " =?";
        String[] selectionArgs = {nameId};
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        return db.delete(MusicPersistenceContract.PlayListEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public int deletePlayListJunction(String nameId) {
        String selection = MusicPersistenceContract.PlayListJunctionColumns.COLUMN_PLAYLIST_NAME_ID + " =?";
        String[] selectionArgs = {nameId};
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        return db.delete(MusicPersistenceContract.PlayListEntry.JUNCTION_TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public int deletePlayListJunction(String nameId, String musicId) {
        String selection =
                MusicPersistenceContract.PlayListJunctionColumns.COLUMN_PLAYLIST_NAME_ID + " =? AND " +
                        MusicPersistenceContract.PlayListJunctionColumns._ID + " =?";
        String[] selectionArgs = {nameId, musicId};
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        return db.delete(MusicPersistenceContract.PlayListEntry.JUNCTION_TABLE_NAME, selection, selectionArgs);
    }

    private boolean isHasPlayListJunction(String musicId, String nameId) {

        String[] args = {musicId, nameId};

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql = "select count(*) from " +
                MusicPersistenceContract.PlayListEntry.JUNCTION_TABLE_NAME + " where " +
                MusicPersistenceContract.PlayListJunctionColumns.COLUMN_PLAYLIST_ID + " =? and " +
                MusicPersistenceContract.PlayListJunctionColumns.COLUMN_PLAYLIST_NAME_ID + " =?";
        Cursor cursor = db.rawQuery(sql, args);
        long count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                count = cursor.getLong(0);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return count > 0;
    }

    private int getMusicIdByPath(MusicInfoBean musicInfoBean) {
        String[] args = {musicInfoBean.path};

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql = "select SongMenu._id from SongMenu where " + MusicPersistenceContract.BaseMusicColumns.COLUMN_NAME_PATH + " =? ";
        Cursor cursor = db.rawQuery(sql, args);
        int id = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return id;
    }

    @Override
    public Flowable<List<PlaylistNameBean>> getPlaylistNames() {

        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        Cursor cursor = database.query(MusicPersistenceContract.PlayListEntry.TABLE_NAME, null, null, null, null, null, null);
        List<PlaylistNameBean> list = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                PlaylistNameBean bean = new PlaylistNameBean();
                bean.name = cursor.getString(cursor.getColumnIndex(MusicPersistenceContract.PlayListNameColumns.COLUMN_PLAYLIST_NAME));
                bean.nameId = cursor.getInt(cursor.getColumnIndex(MusicPersistenceContract.PlayListNameColumns._ID));
                bean.count = countPlaylist(String.valueOf(bean.nameId));
                list.add(bean);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return Flowable.just(list);
    }

    @Override
    public Flowable<List<MusicInfoBean>> getPlaylistMusics(String nameId) {
        String sql = "select * from SongMenu left join junction on junction.music_id = SongMenu._id where junction.playlist_name_id = ?";

        String[] selectArgs = {nameId};
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectArgs);
        List<MusicInfoBean> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                MusicInfoBean bean = new MusicInfoBean();
                bean.id = cursor.getInt(cursor.getColumnIndex(SongMenuHelper._ID));
                bean.title = cursor.getString(cursor.getColumnIndex(SONG_NAME));
                bean.singer = cursor.getString(cursor.getColumnIndex(SINGER));
                bean.path = cursor.getString(cursor.getColumnIndex(PATH));
                bean.artwork_url = cursor.getString(cursor.getColumnIndex(ARTWORK_URL));
                Log.d("MusicLocalDataSource","artwork_url="+bean.artwork_url);
                list.add(bean);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return Flowable.just(list);
    }

    @Override
    public Flowable<List<PlaylistJunctionBean>> getPlaylistJunctions() {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        Cursor cursor = database.query(MusicPersistenceContract.PlayListEntry.JUNCTION_TABLE_NAME, null, null, null, null, null, null);
        List<PlaylistJunctionBean> list = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                PlaylistJunctionBean bean = new PlaylistJunctionBean();
                bean.junctionId = cursor.getInt(cursor.getColumnIndex(MusicPersistenceContract.PlayListJunctionColumns._ID));
                bean.musicId = cursor.getInt(cursor.getColumnIndex(MusicPersistenceContract.PlayListJunctionColumns.COLUMN_PLAYLIST_NAME_ID));
                bean.nameId = cursor.getInt(cursor.getColumnIndex(MusicPersistenceContract.PlayListJunctionColumns.COLUMN_PLAYLIST_ID));
                list.add(bean);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return Flowable.just(list);
    }

    private MusicInfoBean getData(Cursor c) {
        String title = c.getString(c.getColumnIndexOrThrow(MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_TITLE));
        int duration = c.getInt(c.getColumnIndexOrThrow(MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_DURATION));
        String singer =
                c.getString(c.getColumnIndexOrThrow(MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_SINGER));
        int favorite =
                c.getInt(c.getColumnIndexOrThrow(MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_FAVORITE));
        String path =
                c.getString(c.getColumnIndexOrThrow(MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_PATH));
        String imagePath =
                c.getString(c.getColumnIndexOrThrow(MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_IMAGE_PATH));
        return new MusicInfoBean(title, duration, singer, favorite, path, imagePath);
    }

    private ContentValues setData(MusicInfoBean musicInfoBean) {
        ContentValues values = new ContentValues();
        values.put(MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_TITLE, musicInfoBean.title);
        values.put(MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_DURATION, musicInfoBean.duration);
        values.put(MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_SINGER, musicInfoBean.user == null ? musicInfoBean.singer : musicInfoBean.user.username);
        values.put(MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_FAVORITE, musicInfoBean.collection);
        values.put(MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_PATH, musicInfoBean.path);
        values.put(MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_IMAGE_PATH, musicInfoBean.artwork_url);
        return values;
    }

    private int deleteData(String path) {
        String selection = MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_PATH + " LIKE ?";
        String[] selectionArgs = {path};
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        return db.delete(MusicPersistenceContract.RecentlyEntry.TABLE_NAME, selection, selectionArgs);
    }

    public List<String> querySearchHistory() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                MusicPersistenceContract.SearchHistory.TABLE_NAME, new String[]{MusicPersistenceContract.SearchHistory.COLUMN_NAME_KEYWORD}, null, null, null, null, "_id desc");
        List<String> strings = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String keyWord = cursor.getString(0);
                strings.add(keyWord);

            } while (cursor.moveToNext());
        }
        return strings;
    }

    public void saveSearchHistory(@NonNull String keyWord) {
        deleteHistoryKeyWord(keyWord);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MusicPersistenceContract.SearchHistory.COLUMN_NAME_KEYWORD, keyWord);
        db.insert(MusicPersistenceContract.SearchHistory.TABLE_NAME, null, values);
        db.close();
    }

    public int deleteHistoryKeyWord(@NonNull String keyWord) {
        String selection = MusicPersistenceContract.SearchHistory.COLUMN_NAME_KEYWORD + " LIKE ?";
        String[] selectionArgs = {keyWord};
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int i = db.delete(MusicPersistenceContract.SearchHistory.TABLE_NAME, selection, selectionArgs);
        db.close();
        return i;
    }

    public void removeAllKeyWord() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(MusicPersistenceContract.SearchHistory.TABLE_NAME, null, null);
        db.close();
    }

}
