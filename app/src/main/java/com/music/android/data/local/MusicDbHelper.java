package com.music.android.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by liuyun on 17/3/10.
 */

public class MusicDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "Musics.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_TABLE_PLAYLIST_JUNCTION =
            "CREATE TABLE IF NOT EXISTS "
                    + MusicPersistenceContract.PlayListEntry.JUNCTION_TABLE_NAME + "("
                    + MusicPersistenceContract.PlayListJunctionColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + MusicPersistenceContract.PlayListJunctionColumns.COLUMN_PLAYLIST_ID + TEXT_TYPE + COMMA_SEP
                    + MusicPersistenceContract.PlayListJunctionColumns.COLUMN_PLAYLIST_NAME_ID + TEXT_TYPE + " )";

    private static final String SQL_CREATE_TABLE_PLAYLIST =
            "CREATE TABLE IF NOT EXISTS "
                    + MusicPersistenceContract.PlayListEntry.TABLE_NAME + "("
                    + MusicPersistenceContract.PlayListNameColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + MusicPersistenceContract.PlayListNameColumns.COLUMN_PLAYLIST_NAME + TEXT_TYPE + " )";


    /**
     * 创建喜爱的歌曲表
     */
    private static final String SQL_CREATE_MUSIC =
            "CREATE TABLE " + MusicPersistenceContract.FavoriteEntry.TABLE_NAME + " (" +
                    MusicPersistenceContract.FavoriteEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
                    MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_DURATION + TEXT_TYPE + COMMA_SEP +
                    MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_SINGER + TEXT_TYPE + COMMA_SEP +
                    MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_FAVORITE + TEXT_TYPE + COMMA_SEP +
                    MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_IMAGE_PATH + TEXT_TYPE + COMMA_SEP +
                    MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_PATH + TEXT_TYPE +
                    " )";

    /**
     * 创建最近收听的歌曲表
     */
    private static final String SQL_CREATE_RECENTLY =
            "CREATE TABLE " + MusicPersistenceContract.RecentlyEntry.TABLE_NAME + " (" +
                    MusicPersistenceContract.RecentlyEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
                    MusicPersistenceContract.RecentlyEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    MusicPersistenceContract.RecentlyEntry.COLUMN_NAME_DURATION + TEXT_TYPE + COMMA_SEP +
                    MusicPersistenceContract.RecentlyEntry.COLUMN_NAME_SINGER + TEXT_TYPE + COMMA_SEP +
                    MusicPersistenceContract.RecentlyEntry.COLUMN_NAME_FAVORITE + TEXT_TYPE + COMMA_SEP +
                    MusicPersistenceContract.FavoriteEntry.COLUMN_NAME_IMAGE_PATH + TEXT_TYPE + COMMA_SEP +
                    MusicPersistenceContract.RecentlyEntry.COLUMN_NAME_PATH + TEXT_TYPE +
                    " )";
    /**
     * 创建歌单表
     * String sql="create table songMenu(_id integer primary key autoincrement,name text)";
     */
    private final String SQL_SONG_MENU =
            "create table " + SongMenuHelper.TABLE_NAME + "("
                    + SongMenuHelper._ID + " integer primary key autoincrement, "
                    + SongMenuHelper.SONG_MENU_NAME + TEXT_TYPE + COMMA_SEP
                    + SongMenuHelper.SONG_NAME + TEXT_TYPE + COMMA_SEP
                    + SongMenuHelper.ARTWORK_URL + TEXT_TYPE + COMMA_SEP
                    + SongMenuHelper.SINGER + TEXT_TYPE + COMMA_SEP
                    + SongMenuHelper.CREATE_TIME + TEXT_TYPE + COMMA_SEP
                    + SongMenuHelper.PATH + TEXT_TYPE + " )";
    /**
     * 创建最喜欢歌曲表
     * String sql="create table FavoriteSong(_id integer primary key autoincrement,name text)";
     */
    private final String SQL_SONG_FAVORITE =
            "create table " + FavoriteSongHelper.TABLE_NAME + "("
                    + FavoriteSongHelper._ID + " integer primary key autoincrement, "
                    + FavoriteSongHelper.SONG_NAME + TEXT_TYPE + COMMA_SEP
                    + FavoriteSongHelper.LIKE_COUNT + TEXT_TYPE + COMMA_SEP
                    + FavoriteSongHelper.SINGER + TEXT_TYPE + COMMA_SEP
                    + FavoriteSongHelper.CREATE_TIME + TEXT_TYPE + COMMA_SEP
                    + FavoriteSongHelper.ARTWORK_URL + TEXT_TYPE + COMMA_SEP
                    + FavoriteSongHelper.PATH + TEXT_TYPE + " )";

    /**
     * 创建播放历史
     * String sql="create table HistorySong(_id integer primary key autoincrement,name text)";
     */
    private final String SQL_SONG_HISTORY =
            "create table " + HistorySongHelper.TABLE_NAME + "("
                    + HistorySongHelper._ID + " integer primary key autoincrement, "
                    + HistorySongHelper.SONG_NAME + TEXT_TYPE + COMMA_SEP
                    + HistorySongHelper.LIKE_COUNT + TEXT_TYPE + COMMA_SEP
                    + HistorySongHelper.SINGER + TEXT_TYPE + COMMA_SEP
                    + HistorySongHelper.PLAYING + TEXT_TYPE + COMMA_SEP
                    + HistorySongHelper.PROGRESS + TEXT_TYPE + COMMA_SEP
                    + HistorySongHelper.DURATION + TEXT_TYPE + COMMA_SEP
                    + HistorySongHelper.ARTWORK_URL + TEXT_TYPE + COMMA_SEP
                    + HistorySongHelper.PATH + TEXT_TYPE + " )";

    /**
     * 创建搜索纪录
     */
    private final String SQL_SEARCH_HISTORY =
            "create table " + MusicPersistenceContract.SearchHistory.TABLE_NAME + "("
                    + MusicPersistenceContract.SearchHistory._ID + " integer primary key autoincrement, "
                    + MusicPersistenceContract.SearchHistory.COLUMN_NAME_KEYWORD + TEXT_TYPE + " )";


    public MusicDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_SONG_MENU);
        db.execSQL(SQL_CREATE_MUSIC);
        db.execSQL(SQL_CREATE_RECENTLY);
        db.execSQL(SQL_SONG_FAVORITE);
        db.execSQL(SQL_SONG_HISTORY);
        db.execSQL(SQL_SEARCH_HISTORY);
        db.execSQL(SQL_CREATE_TABLE_PLAYLIST_JUNCTION);
        db.execSQL(SQL_CREATE_TABLE_PLAYLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == DATABASE_VERSION) {
            try {
                db.execSQL(SQL_CREATE_TABLE_PLAYLIST_JUNCTION);
                db.execSQL(SQL_CREATE_TABLE_PLAYLIST);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
