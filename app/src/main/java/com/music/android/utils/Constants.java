package com.music.android.utils;

/**
 * Created by Administrator on 2017/3/13.
 */

public class Constants {
    /**
     * 关于音乐播放的广播
     */
    public class BroadcastConstants {
        //notification的发出标记 用于notification 的发出和取消
        public final static int NOTIFICATION_FLAG = 11;
        //广播action标记
        public final static String NOTIFICATION_BROADCAST_FLAG = "music come";
        //广播传递数据的key  对应的value= 0 1 2 3 4 5
        public final static String NOTIFICATION_BROADCAST_VALUE = "value";
        //歌曲切换来自通知栏
        public final static String NOTIFICATION_COME_KEY = "come";
        public final static String NOTIFICATION_COME_VALUE = "NotificationHelper";
        // 打开播放界面的标记
        public final static int ACTIVITY_OPEN_FLAG = 0;
        //上一首的标记
        public final static int PREVIOUS_FLAG = 1;
        //下一首的标记
        public final static int NEXT_FLAG = 2;
        //消失的标记
        public final static int DISMISS_FLAG = 3;
        //暂停或者播放的标记
        public final static int PAUSE_OPEN_FLAG = 4;
        //快进的标志
        public final static int SEEK_FLAG = 5;
        //开始的标记
        public final static int START_FLAG = 6;
        //歌曲更改的标志
        public final static int SONG_UPDATE_FLAG = 7;
        //handler 开始
        public final static int HANDLER_FLAG = 8;
        //删除歌曲
        public final static int DELETE_FLAG = 9;
        //删除歌曲列表
        public final static int DELETE_LIST_FLAG = 10;
        //页面跳转
        public final static int GO_FLAG = 11;
        //耳机事件
        public final static int HEADSET_FLAG = 12;
        //暂停或者播放的标记
        public final static int MODE_CHANGE_FLAG = 13;
        //删除歌曲路径
        public final static String DELETE_PATH = "path";
        //快进 或者播放  传递过来的百分数对应的key
        public final static String SEEK_OR_OPEN = "seekOpen";
        //歌曲修改名字或者作者的key
        public final static String SONGDETAILDIALOG = "detail";
        //Notification的跳转
        public final static String NOTIFICATION_GO_FLAG = "go";
        //耳机事件
        public final static String HEADSET_KEY = "headset";

    }

    /**
     * 关于音乐播放模式
     */
    public class PlayType {
        //歌曲过滤标记
        public final static String PLAY_TYPE_FLAG = "play type";
        //顺序
        public final static int ORDER_FLAG = 0;
        //单曲循环
        public final static int LOOP_FLAG = 1;
        //随机
        public final static int RANDOM_FLAG = 2;
        //60秒歌曲过滤标记
        public final static String MUSIC_FILTER_FLAG = "filter";
        //歌曲休眠标志
        public final static String MUSIC_SLEEP_FLAG = "sleep";
        //应用退出保存的信息
        public final static String APP_OVER_FLAG = "over";
        //应用退出保存的信息
        public final static String APP_BACK = "back";

        //应用退出保存的信息
        public final static String VERSIONCODE = "code";
    }

    /**
     * 关于intent传递对象
     */
    public class IntentType {
        //intent 对应的key
        public final static String INTENT_KEY_FLAG = "IntentType";
        //Bundle 对应的key
        public final static String BUNDLE_KEY_FLAG = "BundleType";

    }

    /**
     * 关于EventBus点击
     */
    public class EventBusType {

        //创建新的歌单
        public final static int CREATE_FLAG = 0;
        //加入歌单
        public final static int ADD_FLAG = 1;

    }

    /**
     * mediaplayer code值
     */
    public class Code {
        //网络错误
        public final static int NETWORK_ERROR = 1;
        // 文件找不到错误
        public final static int FILE_ERROR = 38;

    }

    /**
     * 其他
     */
    public class MusicOthers {
        //电话来的action标记
        public final static String CALL_PHONE = "MusicOthers.call.phone";
        //电话的 intent key
        public final static String CALL_PHONE_INTENT = "key";
        //电话挂断
        public final static int CALL_PHONE_IDLE = 0;
        // 打电话
        public final static int CALL_PHONE_GOING = 1;

        //UpdateMenuDialog  bundle 对应的key
        public final static String MUSICSDIALOG = "UpdateMenuDialog";

        public final static String NEWPLAYLISTDIALOG = "CreateMenuDialog";
        //歌单名字
        public final static String MENU_NAME = "menuName";
        // 所有的歌单名字
        public final static String ALL_MENU_NAMES = "names";
        //SongDetailDialog bundle 对应的bean
        public final static String SONGDETAILDIALOG = "SongDetailDialog";

        public final static String PLAY_LIST_NAME_ID = "playlistNameId";
    }

    public static class EventBusConstants {
        public final static int EVENT_UPDATE_UI = 1;
        public final static int EVENT_INFO_UPDATE = 2;
        public final static int EVENT_DURATION_CHANGE = 3;
        public final static int EVENT_PLAY_LIST_UPDATE = 4;
    }

    public static class FlagConstants {
        public final static String FLAG_ALL = "all";
        public final static String FLAG_FAVORITES = "Favorites";
        public final static String FLAG_RECENTLY_PLAY = "RecentlyPlay";
        public final static String FLAG_PLAY_LIST = "playList";
        public final static String FLAG_HOT = "hot";
    }

    public class LoginType {
        public final static int TYPE_FACEBOOK = 0;
        public final static int TYPE_GOOGLE = 1;
    }
}
