package com.music.android.managers;

import com.music.android.utils.AnalyticsConstants;
import com.music.android.utils.AnalyticsUtils;
import com.music.android.utils.L;

/**
 * Created by liuyun on 17/5/15.
 */

public class AnalyticsManager {
    private static final AnalyticsManager ourInstance = new AnalyticsManager();

    public static AnalyticsManager getInstance() {
        return ourInstance;
    }

    private AnalyticsManager() {
    }

    public synchronized void searchEnter(int position) {
        String value = null;
        switch (position) {
            case 0:
                value = AnalyticsConstants.Search.Enter.VALUE_IN_STREAM;
                break;
            case 1:
                value = AnalyticsConstants.Search.Enter.VALUE_IN_MYLIBRARY;
                break;
            case 2:
                value = AnalyticsConstants.Search.Enter.VALUE_IN_MYSONGS;
                break;
        }
        AnalyticsUtils.vMusicClick(AnalyticsConstants.CATEGORY_SEARCH, AnalyticsConstants.Search.ACTION_ENTER, value);
    }

    public synchronized void searchMusic(int position) {
        String value = null;
        switch (position) {
            case 0:
                value = AnalyticsConstants.Search.Click.VALUE_STREAM;
                break;
            case 1:
                value = AnalyticsConstants.Search.Click.VALUE_MYLIBRARY;
                break;
        }
        AnalyticsUtils.vMusicClick(AnalyticsConstants.CATEGORY_SEARCH, AnalyticsConstants.Search.ACTION_CLICK, value);
    }

    public synchronized void playlistCreation(int position) {
        String value = null;
        switch (position) {
            case 0:
                value = AnalyticsConstants.PlayList.Creation.VALUE_IN_MYLIBRARY;
                break;
            case 1:
                value = AnalyticsConstants.PlayList.Creation.VALUE_IN_PLAYPAGE;
                break;
            case 2:
                value = AnalyticsConstants.PlayList.Creation.VALUE_IN_MYSONGS;
                break;
            case 3:
                value = AnalyticsConstants.PlayList.Creation.VALUE_IN_FAVORITES;
                break;
            case 4:
                value = AnalyticsConstants.PlayList.Creation.VALUE_IN_RECENTLY;
                break;
            case 5:
                value = AnalyticsConstants.PlayList.Creation.VALUE_IN_STREAM;
                break;
            case 6:
                value = AnalyticsConstants.PlayList.Creation.VALUE_IN_PLAYLIST;
                break;

        }
        AnalyticsUtils.vMusicClick(AnalyticsConstants.CATEGORY_PLAYLIST, AnalyticsConstants.PlayList.ACTION_CREATION, value);
    }

    public synchronized void emenuMySongs(int position) {
        String value = null;
        switch (position) {
            case 0:
                value = AnalyticsConstants.EMenu.InMysongs.VALUE_CLICK_ADDNEXTSONG;
                break;
            case 1:
                value = AnalyticsConstants.EMenu.InMysongs.VALUE_CLICK_DETAIL;
                break;
            case 2:
                value = AnalyticsConstants.EMenu.InMysongs.VALUE_CLICK_DELETE;
                break;
            case 3:
                value = AnalyticsConstants.EMenu.InMysongs.VALUE_CLICK_ADDPLAYLIST;
                break;
        }
        AnalyticsUtils.vMusicClick(AnalyticsConstants.CATEGORY_EMENU, AnalyticsConstants.EMenu.ACTION_IN_MYSONGS, value);
    }

    public synchronized void emenuFavorite(int position) {
        String value = null;
        switch (position) {
            case 0:
                value = AnalyticsConstants.EMenu.InFavorites.VALUE_CLICK_ADDNEXTSONG;
                break;
            case 2:
                value = AnalyticsConstants.EMenu.InFavorites.VALUE_CLICK_DELETE;
                break;
            case 3:
                value = AnalyticsConstants.EMenu.InFavorites.VALUE_CLICK_ADDPLAYLIST;
                break;
        }
        AnalyticsUtils.vMusicClick(AnalyticsConstants.CATEGORY_EMENU, AnalyticsConstants.EMenu.ACTION_IN_FAVORITES, value);
    }

    public synchronized void emenuRecent(int position) {
        String value = null;
        switch (position) {
            case 0:
                value = AnalyticsConstants.EMenu.InRecent.VALUE_CLICK_ADDNEXTSONG;
                break;
            case 2:
                value = AnalyticsConstants.EMenu.InRecent.VALUE_CLICK_DELETE;
                break;
            case 3:
                value = AnalyticsConstants.EMenu.InRecent.VALUE_CLICK_ADDPLAYLIST;
                break;
        }
        AnalyticsUtils.vMusicClick(AnalyticsConstants.CATEGORY_EMENU, AnalyticsConstants.EMenu.ACTION_IN_RECENT, value);
    }

    public synchronized void emenuPlaylist(int position) {
        String value = null;
        switch (position) {
            case 0:
                value = AnalyticsConstants.EMenu.InPlaylist.VALUE_CLICK_ADDNEXTSONG;
                break;
            case 2:
                value = AnalyticsConstants.EMenu.InPlaylist.VALUE_CLICK_DELETE;
                break;
            case 3:
                value = AnalyticsConstants.EMenu.InPlaylist.VALUE_CLICK_ADDPLAYLIST;
                break;
        }
        AnalyticsUtils.vMusicClick(AnalyticsConstants.CATEGORY_EMENU, AnalyticsConstants.EMenu.ACTION_IN_PLAYLIST, value);
    }

    public synchronized void emenuStream(int position) {
        String value = null;
        switch (position) {
            case 0:
                value = AnalyticsConstants.EMenu.InStream.VALUE_CLICK_ADDNEXTSONG;
                break;
            case 3:
                value = AnalyticsConstants.EMenu.InStream.VALUE_CLICK_ADDPLAYLIST;
                break;
        }
        AnalyticsUtils.vMusicClick(AnalyticsConstants.CATEGORY_EMENU, AnalyticsConstants.EMenu.ACTION_IN_STREAM, value);
    }

    public synchronized void sequence(String action, int position) {
        String value = null;
        switch (position) {
            case 0:
                value = AnalyticsConstants.Sequence.InMysongs.VALUE_CLICK_LETTER;
                break;
            case 1:
                value = AnalyticsConstants.Sequence.InMysongs.VALUE_CLICK_TIME;
                break;
        }
        AnalyticsUtils.vMusicClick(AnalyticsConstants.CATEGORY_SEQUENCE, action, value);
    }

    public synchronized void edite(int position) {
        String action = null;
        switch (position) {
            case 0:
                action = AnalyticsConstants.Edit.ACTION_IN_MYSONGS;
                break;
            case 1:
                action = AnalyticsConstants.Edit.ACTION_IN_FAVORITES;
                break;
            case 2:
                action = AnalyticsConstants.Edit.ACTION_IN_RECENT;
                break;
            case 3:
                action = AnalyticsConstants.Edit.ACTION_IN_PLAYLIST;
                break;
        }
        AnalyticsUtils.vMusicClick(AnalyticsConstants.CATEGORY_EDIT, action, null);
    }

    public synchronized void shuffleplay(int position) {
        String action = null;
        switch (position) {
            case 0:
                action = AnalyticsConstants.Shuffleplay.ACTION_IN_MYSONGS;
                break;
            case 1:
                action = AnalyticsConstants.Shuffleplay.ACTION_IN_FAVORITES;
                break;
            case 2:
                action = AnalyticsConstants.Shuffleplay.ACTION_IN_RECENT;
                break;
            case 3:
                action = AnalyticsConstants.Shuffleplay.ACTION_IN_PLAYLIST;
                break;
            case 4:
                action = AnalyticsConstants.Shuffleplay.ACTION_IN_STREAM;
                break;
        }
        AnalyticsUtils.vMusicClick(AnalyticsConstants.CATEGORY_SHUFFLE_PLAY, action, null);
    }

    public synchronized void stream(int position, String nameStr) {
        String action = null;
        switch (position) {
            case 0:
                action = AnalyticsConstants.Stream.ACTION_CLICK_NEWHOT;
                break;
            case 1:
                action = AnalyticsConstants.Stream.ACTION_CLICK_RANK;
                break;
            case 2:
                action = AnalyticsConstants.Stream.ACTION_CLICK_GENRES;
                break;
            case 3:
                action = AnalyticsConstants.Stream.ACTION_CLICK_AUDIO;
                break;
        }
        String str = nameStr.trim();
        AnalyticsUtils.vMusicClick(AnalyticsConstants.CATEGORY_STREAM, action, nameStr);
    }

    public synchronized void myLibraryMySongs(int position) {
        String name = null;
        switch (position) {
            case 0:
                name = AnalyticsConstants.MyLibrary.ClickMySongs.VALUE_CLICK_MYSONGS;
                break;
            case 1:
                name = AnalyticsConstants.MyLibrary.ClickMySongs.VALUE_CLICK_ARTIST;
                break;
            case 2:
                name = AnalyticsConstants.MyLibrary.ClickMySongs.VALUE_CLICK_FOLDER;
                break;
        }
        AnalyticsUtils.vMusicClick(AnalyticsConstants.CATEGORY_MY_LIBRARY, AnalyticsConstants.MyLibrary.ACTION_CLICK_MYSONGS, name);
    }

    public synchronized void myLibrary(int position) {
        String action = null;
        switch (position) {
            case 0:
                action = AnalyticsConstants.MyLibrary.ACTION_CLICK_FAVORITES;
                break;
            case 1:
                action = AnalyticsConstants.MyLibrary.ACTION_CLICK_RECENT;
                break;
            case 2:
                action = AnalyticsConstants.MyLibrary.ACTION_CLICK_PLAYLIST;
                break;
            case 3:
                action = AnalyticsConstants.MyLibrary.ACTION_CLICK_MENU;
                break;
        }
        AnalyticsUtils.vMusicClick(AnalyticsConstants.CATEGORY_MY_LIBRARY, action, null);
    }

    public synchronized void error(int position) {
        String action = null;
        switch (position) {
            case 0:
                action = AnalyticsConstants.Error.ACTION_SHOW;
                break;
            case 1:
                action = AnalyticsConstants.Error.ACTION_CLICK_REFRESH;
                break;
        }
        AnalyticsUtils.vMusicClick(AnalyticsConstants.CATEGORY_SONG_LIST, action, null);
    }

}
