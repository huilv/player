package com.music.android.utils;

/**
 * Created by hui.lv on 17/4/26.
 */
public class AnalyticsConstants {

    public static String PLAYBANNER = "playbanner";
    public static String PLAYPAGE = "playpage";
    public static String MENU = "menu";
    public static String SONGLIST = "songlist";
    public static String NOTIFICATIONPLAY = "notificationplay";
    public static String AMOUNTPLAY = "amountplay";
    public static String LOCK_PLAY = "lock_play";
    public static String PLAYLIST = "playlist";
    public static String MY_LIBRARY = "my_library";

    public static class Action {
        public static String CLICK = "click";
        public static String CLICK_SLEEPTIME = "click_sleeptime";
        public static String CLICK_RATEUS = "click_rateus";
        public static String CLICK_SHARE = "click_share";
        public static String CLICK_SETTING = "click_setting";

        public static String CLICK_SONGS = "click_songs";
        public static String CLICK_PLAYMODE = "click_playmode";

        public static String SHOW = "show";
        public static String ONLINE_PLAY = "online_play";
        public static String LOCAL_PLAY = "local_play";

        public static String CLICK_EMENU = "click_emenu";
        public static String CLICK_MENU = "click_menu";
    }

    public static class Value {
        public static String TOPLAYPAGE = "to_palypage";
        public static String SONGLIST = "songlist";
        public static String FAVORITES = "favorites";
        public static String CANCEL_FAVORITES = "cancel_favorites";
        public static String PLAYMODE = "playmode";
        public static String ADDPLAYLIST = "addplaylist";
        public static String PREVIOUS = "previous";
        public static String NEXT = "next";
        public static String PLAY = "play";
        public static String DELETE = "delete";
        public static String CONFIRM = "confirm";
        public static String CANCEL = "cancel";

        public static String CLICK_IST_OFF = "click_ist_off";
        public static String CLICK_IST_ON = "click_ist_on";
        public static String CLICK_UPDATE = "click_update";
        public static String CLICK_FEEDBACK = "click_feedback";
        public static String CLICK_PRIVACY = "click_privacy";

        public static String RENAME = "rename";

    }

    public static String CATEGORY_SEARCH = "search";

    public static String CATEGORY_PLAYLIST = "playlist";

    public static String CATEGORY_EMENU = "emenu";

    public static String CATEGORY_SEQUENCE = "sequence";

    public static String CATEGORY_EDIT = "edit";

    public static String CATEGORY_SHUFFLE_PLAY = "shuffleplay";

    public static String CATEGORY_STREAM = "stream";

    public static String CATEGORY_MY_LIBRARY = "my library";

    public static String CATEGORY_SONG_LIST = "songlist";

    public static class Search {

        public static String ACTION_ENTER = "enter";
        public static String ACTION_CLICK = "click";

        public static class Enter {
            public static String VALUE_IN_STREAM = "in_stream";      //在stream页面，点击搜索时发送
            public static String VALUE_IN_MYLIBRARY = "in_mylibrary";//在my library页面，点击搜索时发送
            public static String VALUE_IN_MYSONGS = "in_mysongs";    //在my songs页面，点击搜索时发送
        }

        public static class Click {
            public static String VALUE_STREAM = "stream";            //点击搜索在线音乐时发送
            public static String VALUE_MYLIBRARY = "mylibrary";      //点击搜索本地音乐时发送
        }
    }

    public static class PlayList {

        public static String ACTION_CREATION = "creation";

        public static class Creation {
            public static String VALUE_IN_MYLIBRARY = "in_mylibrary"; //在my library页面创建歌单时发送
            public static String VALUE_IN_PLAYPAGE = "in_playpage";   //在播放详情页创建歌单时发送
            public static String VALUE_IN_MYSONGS = "in_mysongs";     //在my songs（包括my songs，Aritist，folder）内创建歌单时发送
            public static String VALUE_IN_FAVORITES = "in_favorites"; //在favorites内创建歌单时发送
            public static String VALUE_IN_RECENTLY = "in_recently";   //在Recently play内创建歌单
            public static String VALUE_IN_STREAM = "in_stream";       //在Stream上的歌曲列表内创建歌单
            public static String VALUE_IN_PLAYLIST = "in_playlist";   //在歌单内创建歌单时发送
        }

    }

    public static class EMenu {
        public static String ACTION_IN_MYSONGS = "in_mysongs";
        public static String ACTION_IN_FAVORITES = "in_favorites";
        public static String ACTION_IN_RECENT = "in_recent";
        public static String ACTION_IN_PLAYLIST = "in_playlist";
        public static String ACTION_IN_STREAM = "in_stream";

        public static class InMysongs {
            public static String VALUE_CLICK_ADDNEXTSONG = "click_addnextsong";//在my songs列表的扩展菜单处点击add to the next song时发送
            public static String VALUE_CLICK_DETAIL = "click_detail";          //在my songs列表的扩展菜单处点击song detail时发送
            public static String VALUE_CLICK_DELETE = "click_delete";          //在my songs列表的扩展菜单处点击delete时发送
            public static String VALUE_CLICK_ADDPLAYLIST = "click_addplaylist";//在my songs列表的扩展菜单处点击add to playlist时发送
        }

        public static class InFavorites {
            public static String VALUE_CLICK_ADDNEXTSONG = "click_addnextsong";//在favorites列表的扩展菜单处点击add to the next song时发送
            public static String VALUE_CLICK_DELETE = "click_delete";          //在favorites列表的扩展菜单处点击delete时发送
            public static String VALUE_CLICK_ADDPLAYLIST = "click_addplaylist";//在favorites列表的扩展菜单处点击add to playlist时发送
        }

        public static class InRecent {
            public static String VALUE_CLICK_ADDNEXTSONG = "click_addnextsong";//在Recently Play列表的扩展菜单处点击add to the next song时发送
            public static String VALUE_CLICK_DELETE = "click_delete";          //在Recently Play列表的扩展菜单处点击delete时发送
            public static String VALUE_CLICK_ADDPLAYLIST = "click_addplaylist";//在Recently Play列表的扩展菜单处点击add to playlist时发送
        }

        public static class InPlaylist {
            public static String VALUE_CLICK_ADDNEXTSONG = "click_addnextsong";//在Playlist列表的扩展菜单处点击add to the next song时发送
            public static String VALUE_CLICK_DELETE = "click_delete";          //在Playlist列表的扩展菜单处点击delete时发送
            public static String VALUE_CLICK_ADDPLAYLIST = "click_addplaylist";//在Playlist列表的扩展菜单处点击add to playlist时发送
        }

        public static class InStream {
            public static String VALUE_CLICK_ADDNEXTSONG = "click_addnextsong";//在stream页面的扩展菜单处点击add to the next song时发送
            public static String VALUE_CLICK_ADDPLAYLIST = "click_addplaylist";//在stream页面的扩展菜单处点击add to playlist时发送
        }
    }

    public static class Sequence {
        public static String ACTION_IN_MYSONGS = "in_mysongs";
        public static String ACTION_IN_FAVORITES = "in_favorites";
        public static String ACTION_IN_RECENT = "in_recent";
        public static String ACTION_IN_PLAYLIST = "in_playlist";

        public static class InMysongs {
            public static String VALUE_CLICK_LETTER = "click_letter";          //在my songs列表点击字母排序时发送
            public static String VALUE_CLICK_TIME = "click_time";              //在my songs列表点击时间排序时发送
        }

        public static class InFavorites {
            public static String VALUE_CLICK_LETTER = "click_letter";          //在favorite列表点击字母排序时发送
            public static String VALUE_CLICK_TIME = "click_time";              //在favorite列表点击时间排序时发送
        }

        public static class InRecent {
            public static String VALUE_CLICK_LETTER = "click_letter";          //在Recently Play列表点击字母排序时发送
            public static String VALUE_CLICK_TIME = "click_time";              //在Recently Play列表点击时间排序时发送
        }

        public static class InPlaylist {
            public static String VALUE_CLICK_LETTER = "click_letter";          //在Playlist列表点击字母排序时发送
            public static String VALUE_CLICK_TIME = "click_time";              //在Playlist列表点击时间排序时发送
        }
    }

    public static class Edit {
        public static String ACTION_IN_MYSONGS = "in_mysongs";                  //在my songs列表点击编辑时发送
        public static String ACTION_IN_FAVORITES = "in_favorites";              //在favorites列表点击编辑时发送
        public static String ACTION_IN_RECENT = "in_recent";                    //在Recently Play列表点击编辑时发送
        public static String ACTION_IN_PLAYLIST = "in_playlist";                //在Playlist列表点击编辑时发送
    }

    public static class Shuffleplay {
        public static String ACTION_IN_MYSONGS = "in_mysongs";                  //在my songs列表点击shuffle play时发送
        public static String ACTION_IN_FAVORITES = "in_favorites";              //在favorites列表点击shuffle play时发送
        public static String ACTION_IN_RECENT = "in_recent";                    //在Recently Play列表点击shuffle play时发送
        public static String ACTION_IN_PLAYLIST = "in_playlist";                //在Playlist列表点击shuffle play时发送
        public static String ACTION_IN_STREAM = "in_stream";                    //在stream页面的列表点击shuffle play时发送
    }

    public static class Stream {
        public static String ACTION_CLICK_NEWHOT = "click_newhot";              //进入new&hot页面时发送
        public static String ACTION_CLICK_RANK = "click_rank";                  //进入rank页面时发送，并附带进入的rank的具体名称
        public static String ACTION_CLICK_GENRES = "click_genres";              //进入genres页面时发送，并附带进入的genres的具体名称
        public static String ACTION_CLICK_AUDIO = "click_audio";                //进入audio页面时发送，并附带进入的audio的具体名称
    }

    public static class MyLibrary {
        public static String ACTION_CLICK_MYSONGS = "click_mysongs";
        public static String ACTION_CLICK_FAVORITES = "click_favorites";        //进入favorites页面时发送
        public static String ACTION_CLICK_RECENT = "click_recent";              //进入Recently Play页面时发送
        public static String ACTION_CLICK_PLAYLIST = "click_playlist";          //进入Playlist页面时发送
        public static String ACTION_CLICK_MENU = "click_menu";                  //进入抽屉菜单页面时发送

        public static class ClickMySongs {
            public static String VALUE_CLICK_MYSONGS = "click_mysongs";          //进入my songs页面时发送
            public static String VALUE_CLICK_ARTIST = "click_artist";            //进入artist页面时发送
            public static String VALUE_CLICK_FOLDER = "click_folder";            //进入folder页面时发送
        }
    }

    public static class SongList {
        public static String ACTION_CLICK_SONGS = "click_songs";                //在播放列表中点击歌曲时发送
        public static String ACTION_CLICK_PLAYMODE = "click_playmode";          //在播放列表中点击播放模式时发送
    }

    public static class Error {
        public static String ACTION_SHOW = "show";                              //服务器异常，显示错误提示时发送
        public static String ACTION_CLICK_REFRESH = "click_refresh";            //在服务器异常页面点击刷新时发送
    }

}
