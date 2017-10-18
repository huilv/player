package com.music.android.ui.widgets.playinglayout;

import android.content.Context;

import com.music.android.MusicApp;
import com.music.android.R;

/**
 * Created by CTer on 17/4/27.
 */

public class ViewTag {

    public static final String X = "_x";
    public static final String Y = "_y";
    public static final String W = "_w";
    public static final String H = "_h";
    public static final String S = "_s";

    public static final String MUSIC_IMAGE = MusicApp.context.getString(R.string.tag_music_image);
    public static final String MUSIC_BG = MusicApp.context.getString(R.string.tag_music_bg);
    public static final String CD_BG = MusicApp.context.getString(R.string.tag_cd_bg);
    public static final String PLAY_NEXT = MusicApp.context.getString(R.string.tag_play_next);
    public static final String PLAY_PAUSE = MusicApp.context.getString(R.string.tag_play_pause);
    public static final String ADD_TO_PLAY_LIST = MusicApp.context.getString(R.string.tag_add_to_play_list);
    public static final String PLAY_PREV = MusicApp.context.getString(R.string.tag_play_prev);
    public static final String PLAY_MODE = MusicApp.context.getString(R.string.tag_play_mode);
    public static final String TITLE = MusicApp.context.getString(R.string.tag_title);
    public static final String SINGER = MusicApp.context.getString(R.string.tag_singer);


    public static String[] ALLVIEWTAG = {MUSIC_IMAGE, MUSIC_BG, PLAY_NEXT, PLAY_PAUSE, ADD_TO_PLAY_LIST,
            PLAY_PREV, PLAY_MODE, TITLE, SINGER, CD_BG};
}
