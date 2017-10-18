package com.music.android.ui.adapter;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.bean.DialogBundleBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.bean.PlaylistNameBean;
import com.music.android.data.local.FavoriteSongHelper;
import com.music.android.data.local.MusicLocalDataSource;
import com.music.android.listener.OnMenuSuccessListener;
import com.music.android.listener.OnMenuUpdateListener;
import com.music.android.managers.AnalyticsManager;
import com.music.android.managers.LocalMusicImpl;
import com.music.android.ui.dialog.CreateMenuDialog;
import com.music.android.ui.dialog.UpdateMenuDialog;
import com.music.android.ui.mvp.main.MainActivity;
import com.music.android.utils.Constants;
import com.music.android.utils.L;
import com.music.android.utils.LocalMusicUtils;
import com.music.android.utils.PermissionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * Created by liuyun on 17/3/22.
 */

public class PlayList2Adapter extends BaseAdapter<RecyclerView.ViewHolder, PlaylistNameBean> {

    private static int TYPE_HEADER = 1;

    private static int TYPE_COMMON = 2;

    private static int TYPE_CREATE = 3;

    private FragmentManager mFragmentManager;

    public PlayList2Adapter(Context context, FragmentManager mFragmentManager) {
        super(context);
        this.mFragmentManager = mFragmentManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        if (viewType == TYPE_HEADER) {
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.favorites_relativeLayout.setOnClickListener(new AddFragmentClickListener("favoritesFragment"));
            headerViewHolder.my_song_relativeLayout.setOnClickListener(new AddFragmentClickListener("mySongFragment"));
            headerViewHolder.recently_play_relativeLayout.setOnClickListener(new AddFragmentClickListener("recentlyPlayFragment"));
            headerViewHolder.favorites_count_TextView.setText("");
            setMySongsCount(headerViewHolder.my_songs_count_TextView);
            setFavoritesCount(headerViewHolder.favorites_count_TextView);
        } else if (viewType == TYPE_COMMON) {
            final PlayListViewHolder playListViewHolder = (PlayListViewHolder) holder;
            final PlaylistNameBean playlistNameBean = data.get(position);
            long count = playlistNameBean.count;
            if (count == 0 || count == 1) {
                playListViewHolder.play_list_count_TextView.setText(count + " Song");
            } else {
                playListViewHolder.play_list_count_TextView.setText(String.valueOf(count) + " Songs");
            }

            playListViewHolder.play_list_name_TextView.setText(playlistNameBean.name);
            playListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnAddFragmentListener.onAddFragment("playListFragment", playlistNameBean);
                }
            });
            playListViewHolder.setting_LinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateMenu(playlistNameBean.name, String.valueOf(playlistNameBean.nameId));
                }
            });
        } else if (viewType == TYPE_CREATE) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.MusicOthers.NEWPLAYLISTDIALOG, new DialogBundleBean());
                    CreateMenuDialog newPlaylistDialog = new CreateMenuDialog();
                    newPlaylistDialog.setArguments(bundle);
                    newPlaylistDialog.setOnMenuCreateSuccessListener(new OnMenuSuccessListener() {
                        @Override
                        public void onSuccess() {
                            updateUI();
                        }
                    });
                    newPlaylistDialog.show(mFragmentManager, "dialog");
                }
            });
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(mLayoutInflater.inflate(R.layout.layout_my_library_header, parent, false));
        } else if (viewType == TYPE_COMMON) {
            return new PlayListViewHolder(mLayoutInflater.inflate(R.layout.layout_playlist_item, parent, false));
        } else if (viewType == TYPE_CREATE) {
            return new CreateViewHolder(mLayoutInflater.inflate(R.layout.layout_playlist_create_item, parent, false));
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == data.size() - 1) {
            return TYPE_CREATE;
        } else {
            return TYPE_COMMON;

        }
    }

    private void updateMenu(String oldName, String nameId) {
        UpdateMenuDialog dialog = new UpdateMenuDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MusicOthers.MENU_NAME, oldName);
        bundle.putString(Constants.MusicOthers.PLAY_LIST_NAME_ID, nameId);
        dialog.setArguments(bundle);
        dialog.setOnMenuUpdateListener(new OnMenuUpdateListener() {
            @Override
            public void onMenuNameUpdateSuccess(String oldName, String newName) {
                updateUI();
                AnalyticsManager.getInstance().playlistCreation(0);
            }

            @Override
            public void onMenuDeleteSuccess(String name) {
                updateUI();
            }

            @Override
            public void onFailed() {

            }
        });
        dialog.show(((MainActivity) mContext).getSupportFragmentManager(), UpdateMenuDialog.class.getSimpleName());
    }

    public void updateUI() {
        L.d("MyLib", " updateUI : ");
        MusicLocalDataSource.getInstance(MusicApp.context).getPlaylistNames().subscribe(new Consumer<List<PlaylistNameBean>>() {
            @Override
            public void accept(List<PlaylistNameBean> playlistNameBeen) throws Exception {
                L.d("MyLib", " playlistNameBeen size : " + playlistNameBeen.size());
                Collections.reverse(playlistNameBeen);
                playlistNameBeen.add(0, new PlaylistNameBean());
                playlistNameBeen.add(playlistNameBeen.size(), new PlaylistNameBean());
                setData(playlistNameBeen);
            }
        });
    }

    public void setFavoritesCount(TextView textView) {
        int count = FavoriteSongHelper.getDefault(mContext).query().size();
        final String songs;
        if (count > 1) {
            songs = " Songs";
        } else {
            songs = " Song";
        }
        textView.setText(count + songs);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setMySongsCount(final TextView textView) {
        if (PermissionUtils.checkSelfPermission((MainActivity) mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            LocalMusicImpl<MusicInfoBean> mLocalMusicImpl = new LocalMusicImpl<MusicInfoBean>() {

                @Override
                protected void buildMapData(Cursor cursor, Map<String, MusicInfoBean> commonBeanMap) {

                }

                @Override
                protected MusicInfoBean buildData(Cursor cursor) {
                    return LocalMusicUtils.getAllSong(cursor);

                }

                @Override
                public void getResult(List<MusicInfoBean> list) {
                    final String songs;
                    if (list.size() > 1) {
                        songs = " Songs";
                    } else {
                        songs = " Song";
                    }
                    textView.setText(list.size() + songs);
                }
            };
            mLocalMusicImpl.getLocalSongs();
        }
    }

    private static class PlayListViewHolder extends RecyclerView.ViewHolder {

        TextView play_list_name_TextView, play_list_count_TextView;

        LinearLayout setting_LinearLayout;

        PlayListViewHolder(View itemView) {
            super(itemView);
            play_list_name_TextView = (TextView) itemView.findViewById(R.id.play_list_name_TextView);
            play_list_count_TextView = (TextView) itemView.findViewById(R.id.play_list_count_TextView);
            setting_LinearLayout = (LinearLayout) itemView.findViewById(R.id.setting_LinearLayout);
        }
    }

    private static class CreateViewHolder extends RecyclerView.ViewHolder {

        CreateViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout my_song_relativeLayout, favorites_relativeLayout, recently_play_relativeLayout;

        TextView my_songs_count_TextView, favorites_count_TextView;

        HeaderViewHolder(View itemView) {
            super(itemView);
            my_song_relativeLayout = (RelativeLayout) itemView.findViewById(R.id.my_song_relativeLayout);
            favorites_relativeLayout = (RelativeLayout) itemView.findViewById(R.id.favorites_relativeLayout);
            recently_play_relativeLayout = (RelativeLayout) itemView.findViewById(R.id.recently_play_relativeLayout);
            my_songs_count_TextView = (TextView) itemView.findViewById(R.id.my_songs_count_TextView);
            favorites_count_TextView = (TextView) itemView.findViewById(R.id.favorites_count_TextView);
        }
    }

    private class AddFragmentClickListener implements View.OnClickListener {

        private String tag = "";

        AddFragmentClickListener(String tag) {
            this.tag = tag;
        }

        @Override
        public void onClick(View v) {
            mOnAddFragmentListener.onAddFragment(tag, null);
        }
    }



}
