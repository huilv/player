package com.music.android.data.local;

import android.provider.BaseColumns;

/**
 * Created by liuyun on 17/3/10.
 */

public class MusicPersistenceContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public MusicPersistenceContract() {

    }

    /* Inner class that defines the table contents */
    public static abstract class FavoriteEntry extends BaseMusicColumns {
        public static final String TABLE_NAME = "music";

    }

    public static abstract class RecentlyEntry extends BaseMusicColumns {
        public static final String TABLE_NAME = "recently";

    }

    public static abstract class PlayListEntry extends BaseMusicColumns {
        public static final String TABLE_NAME = "playlistName";

        public static final String JUNCTION_TABLE_NAME = "junction";
    }

    public static abstract class SearchHistory implements BaseColumns {

        public static final String TABLE_NAME = "searchHistory";

        public static final String COLUMN_NAME_KEYWORD = "keyWord";
    }

    public static class BaseMusicColumns implements BaseColumns {
        public static final String COLUMN_NAME_TITLE = "title";//歌曲介绍
        public static final String COLUMN_NAME_DURATION = "duration";//歌曲时长
        public static final String COLUMN_NAME_SINGER = "singer";//歌手
        public static final String COLUMN_NAME_FAVORITE = "favorite";//收藏的歌曲包括本地歌曲和在线歌曲
        public static final String COLUMN_NAME_PATH = "path";
        public static final String COLUMN_NAME_IMAGE_PATH = "imagePath";
    }

    public static class PlayListNameColumns implements BaseColumns {
        public static final String COLUMN_PLAYLIST_NAME = "name";
    }

    public static class PlayListJunctionColumns implements BaseColumns {
        public static final String COLUMN_PLAYLIST_ID = "music_id";
        public static final String COLUMN_PLAYLIST_NAME_ID = "playlist_name_id";
    }

}
