package com.music.android.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.bean.DialogBundleBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.bean.PlaylistNameBean;
import com.music.android.bean.SongMenuBean;
import com.music.android.data.local.MusicLocalDataSource;
import com.music.android.data.local.SongMenuHelper;
import com.music.android.ui.dialog.CreateMenuDialog;
import com.music.android.ui.dialog.PlaylistDialog;
import com.music.android.ui.widgets.TipToast;
import com.music.android.utils.Constants;
import com.music.android.utils.L;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hui.lv on 2017/3/20.
 */

public class PlayListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = "PlayListAdapter";
    private List<PlaylistNameBean> list = new ArrayList<>();
    private int HEAD_TYPE = -1;
    private PlaylistDialog dialog;
    private DialogBundleBean bundleBean;

    public PlayListAdapter(PlaylistDialog dialog, DialogBundleBean bundleBean) {
        this.dialog = dialog;
        this.bundleBean = bundleBean;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == HEAD_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_playlist_head, null, false);
            holder = new HeadHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_playlist, null, false);
            holder = new PlayListHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == list.size()) {
            ((HeadHolder) holder).view.setOnClickListener(new PlayListOnClickListener(position));
            return;
        }
        PlaylistNameBean playlistNameBean = list.get(position);
        PlayListHolder playListHolder = (PlayListHolder) holder;
        playListHolder.tv.setText(playlistNameBean.name);
        playListHolder.item.setOnClickListener(new PlayListOnClickListener(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == list.size()) {
            return HEAD_TYPE;
        }
        return super.getItemViewType(position);
    }

    public void setData(List<PlaylistNameBean> musics) {
        list.clear();
        list.addAll(musics);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (list.size() + 1);
    }

    private class PlayListHolder extends RecyclerView.ViewHolder {

        public TextView tv;
        public ImageView img;
        public View item;

        public PlayListHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            tv = (TextView) itemView.findViewById(R.id.tv);
            item = itemView.findViewById(R.id.item);
        }
    }

    private class HeadHolder extends RecyclerView.ViewHolder {

        public View view;

        public HeadHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.itemHead);
        }
    }

    private class PlayListOnClickListener implements View.OnClickListener {
        private int position;

        public PlayListOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (position == list.size()) {
                dialog.dismiss();
                FragmentActivity activity = dialog.getActivity();
                CreateMenuDialog dialog = new CreateMenuDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.MusicOthers.NEWPLAYLISTDIALOG, bundleBean);
                dialog.setArguments(bundle);
                dialog.show(activity.getSupportFragmentManager(), CreateMenuDialog.class.getSimpleName());
                return;
            }
            MusicInfoBean bean = new MusicInfoBean();
            bean.title = bundleBean.title;
            bean.singer = bundleBean.singer;
            bean.title = bundleBean.title;
//            bean.menuName = list.get(position).menuName;
            bean.path = bundleBean.path;
            L.d(TAG, bean.menuName);
            bean.artwork_url=bundleBean.artwork_url;
            long id = MusicLocalDataSource.getInstance(MusicApp.context).insertPlaylistNameAndMusic(list.get(position), bean);
//            boolean flag = SongMenuHelper.getDefault(v.getContext()).insertSong(bean);
            String success;
            if (id <= 0) {
                success = MusicApp.context.getResources().getString(R.string.song_exist);
            } else {
                success = MusicApp.context.getResources().getString(R.string.song_create_success);
                EventBus.getDefault().post(Constants.EventBusConstants.EVENT_UPDATE_UI);
            }
            dialog.dismiss();
            TipToast.makeText(MusicApp.context, success, Toast.LENGTH_SHORT).show();
        }
    }


}
