package com.music.android.ui.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.bean.DialogBundleBean;
import com.music.android.bean.MessageEventBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.data.local.FavoriteSongHelper;
import com.music.android.data.local.MusicLocalDataSource;
import com.music.android.data.local.SongMenuHelper;
import com.music.android.listener.OnSongUpdateListener;
import com.music.android.managers.AnalyticsManager;
import com.music.android.service.MusicPlayService;
import com.music.android.ui.dialog.CreateMenuDialog;
import com.music.android.ui.dialog.DeleteMusicDialog;
import com.music.android.ui.dialog.PlaylistDialog;
import com.music.android.ui.dialog.SongDetailDialog;
import com.music.android.ui.mvp.main.MainActivity;
import com.music.android.utils.Constants;
import com.music.android.utils.FileUtils;
import com.music.android.utils.L;
import com.music.android.utils.PlayHelper;
import com.music.android.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.music.android.utils.Constants.FlagConstants.FLAG_HOT;

/**
 * Created by liuyun on 17/3/23.
 */

public class ControlPopupWindow extends PopupWindow implements View.OnClickListener {

    private LinearLayout add_next_song_LinearLayout;

    private LinearLayout song_info_LinearLayout;

    private LinearLayout delete_LinearLayout;

    private LinearLayout add_to_playlist_LinearLayout;

    private MusicInfoBean musicInfoBean;

    private String playlistNameId = null;

    private String flag;

    private int type = -1;

    private Context context;

    private int position = 0;

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setMenuName(@NonNull String playlistNameId) {
        this.playlistNameId = playlistNameId;
    }

    public ControlPopupWindow(Context context, int type, List<MusicInfoBean> musics, int position) {
        this.context = context;
        this.type = type;
        musicInfoBean = musics.get(position);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_control, null);
        add_next_song_LinearLayout = (LinearLayout) view.findViewById(R.id.add_next_song_LinearLayout);
        add_next_song_LinearLayout.setOnClickListener(this);
        song_info_LinearLayout = (LinearLayout) view.findViewById(R.id.song_info_LinearLayout);
        song_info_LinearLayout.setOnClickListener(this);
        delete_LinearLayout = (LinearLayout) view.findViewById(R.id.delete_LinearLayout);
        delete_LinearLayout.setOnClickListener(this);
        add_to_playlist_LinearLayout = (LinearLayout) view.findViewById(R.id.add_to_playlist_LinearLayout);
        view.findViewById(R.id.cancel_TextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        add_to_playlist_LinearLayout.setOnClickListener(this);

        switch (type) {
            case 0:
                song_info_LinearLayout.setVisibility(View.GONE);
                delete_LinearLayout.setVisibility(View.GONE);
                break;
            case 1:
                song_info_LinearLayout.setVisibility(View.GONE);
                break;
            case 2:
                song_info_LinearLayout.setVisibility(View.GONE);
                add_to_playlist_LinearLayout.setVisibility(View.GONE);
                break;
        }

        setContentView(view);
//        showBg();
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setFocusable(true);
        setAnimationStyle(R.style.control_anim_style);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
    }

    private void showBg() {
        Activity activity = (Activity) context;
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = 0.4f;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        activity.getWindow().setAttributes(lp);
    }

    private void hideBg() {
        Activity activity = (Activity) context;
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = 1f;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        activity.getWindow().setAttributes(lp);
    }

    @Override
    public void dismiss() {
        super.dismiss();
//        hideBg();
    }

    private void showDeleteDialog(@NonNull String content) {

        Bundle bundle = new Bundle();
        bundle.putString("content", content);

        MainActivity activity = (MainActivity) context;
        DeleteMusicDialog deleteMusicDialog = new DeleteMusicDialog();
        deleteMusicDialog.setArguments(bundle);
        deleteMusicDialog.setOnConfirmListener(new DeleteMusicDialog.OnConfirmListener() {
            @Override
            public void onConfirm(DialogFragment dialogFragment) {
                if (flag.equals(Constants.FlagConstants.FLAG_ALL)) {
                    FileUtils.scanFileAsync(musicInfoBean.path, mOnDeleteFileListener);
                } else if (flag.equals(Constants.FlagConstants.FLAG_FAVORITES)) {
                    FavoriteSongHelper.getDefault(MusicApp.context).delete(musicInfoBean.path);
                    EventBus.getDefault().post(Constants.EventBusConstants.EVENT_INFO_UPDATE);
                } else if (flag.equals(Constants.FlagConstants.FLAG_RECENTLY_PLAY)) {
                    MusicLocalDataSource.getInstance(MusicApp.context).deleteRecentlyMusic(musicInfoBean);
                    if (mOnDeleteFileListener != null) {
                        mOnDeleteFileListener.onDelete(true, musicInfoBean.path);
                    }
                } else if (flag.equals(Constants.FlagConstants.FLAG_PLAY_LIST)) {
                    int id = MusicLocalDataSource.getInstance(MusicApp.context).deletePlayListJunction(playlistNameId,String.valueOf(musicInfoBean.id));
                    if (id > 0) {
                        EventBus.getDefault().post(Constants.EventBusConstants.EVENT_INFO_UPDATE);
                    }
                }
                EventBus.getDefault().post(Constants.EventBusConstants.EVENT_UPDATE_UI);
                dialogFragment.dismiss();
            }
        });
        deleteMusicDialog.show(activity.getSupportFragmentManager(), "deleteMusicDialog");
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.add_next_song_LinearLayout:
                if (PlayHelper.getInstance().isServiceExist()) {
                    MusicPlayService.insertNextMusic(musicInfoBean);
                } else {
                    PlayHelper.getInstance().startPlayList(MusicApp.context, musicInfoBean);
                }
                emenu(0);
                this.dismiss();
                break;
            case R.id.song_info_LinearLayout:
                emenu(1);
                SongDetailDialog dialog = new SongDetailDialog();
                bundle = new Bundle();
                bundle.putSerializable(Constants.MusicOthers.SONGDETAILDIALOG, musicInfoBean);
                dialog.setArguments(bundle);
                dialog.setOnSongUpdateListener(new OnSongUpdateListener() {
                    @Override
                    public void onSongNameUpdateSuccess(MusicInfoBean bean) {
                        EventBus.getDefault().post(Constants.EventBusConstants.EVENT_INFO_UPDATE);
                    }

                    @Override
                    public void onSongArtistUpdateSuccess(MusicInfoBean bean) {
                        EventBus.getDefault().post(Constants.EventBusConstants.EVENT_INFO_UPDATE);
                    }
                });
                this.dismiss();
                dialog.show(((MainActivity) context).getSupportFragmentManager(), SongDetailDialog.class.getSimpleName());
                break;
            case R.id.delete_LinearLayout:
                emenu(2);
                String content;
                if (flag.equals(Constants.FlagConstants.FLAG_ALL)) {
                    content = MusicApp.context.getResources().getString(R.string.delete_song);
                } else {
                    content = MusicApp.context.getResources().getString(R.string.remove_song);
                }
                showDeleteDialog(content);
                this.dismiss();
                break;
            case R.id.add_to_playlist_LinearLayout:
                emenu(3);
                if (flag.equals(Constants.FlagConstants.FLAG_ALL)) {
                    position = 2;
                } else if (flag.equals(Constants.FlagConstants.FLAG_FAVORITES)) {
                    position = 3;
                } else if (flag.equals(Constants.FlagConstants.FLAG_RECENTLY_PLAY)) {
                    position = 4;
                } else if (flag.equals(Constants.FlagConstants.FLAG_HOT)) {
                    position = 5;
                } else if (flag.equals(Constants.FlagConstants.FLAG_PLAY_LIST)) {
                    position = 6;
                }
                showPlayListDialog(musicInfoBean);
                this.dismiss();
                break;
        }

    }

    private void emenu(int position) {
        if (flag.equals(Constants.FlagConstants.FLAG_ALL)) {
            AnalyticsManager.getInstance().emenuMySongs(position);
        } else if (flag.equals(Constants.FlagConstants.FLAG_FAVORITES)) {
            AnalyticsManager.getInstance().emenuFavorite(position);
        } else if (flag.equals(Constants.FlagConstants.FLAG_RECENTLY_PLAY)) {
            AnalyticsManager.getInstance().emenuRecent(position);
        } else if (flag.equals(Constants.FlagConstants.FLAG_HOT)) {
            AnalyticsManager.getInstance().emenuStream(position);
        } else if (flag.equals(Constants.FlagConstants.FLAG_PLAY_LIST)) {
            AnalyticsManager.getInstance().emenuPlaylist(position);
        }
    }

    private void showPlayListDialog(MusicInfoBean musicInfoBean) {
        PlaylistDialog dialog = new PlaylistDialog();
        Bundle bundle = new Bundle();
        DialogBundleBean bundleBean = new DialogBundleBean();
        bundleBean.comeFrom = position;
        MusicInfoBean infoBean = musicInfoBean;
        bundleBean.path = infoBean.path;
        bundleBean.title = infoBean.title;
        bundleBean.singer = infoBean.singer;
        bundleBean.artwork_url = infoBean.artwork_url;
        bundle.putSerializable(Constants.MusicOthers.NEWPLAYLISTDIALOG, bundleBean);
        dialog.setArguments(bundle);
        dialog.show(((MainActivity) context).getSupportFragmentManager(), CreateMenuDialog.class.getSimpleName());
        EventBus.getDefault().post(Constants.EventBusConstants.EVENT_UPDATE_UI);
    }

    public OnDeleteFileListener mOnDeleteFileListener;

    public void setOnDeleteFileListener(OnDeleteFileListener mOnDeleteFileListener) {
        this.mOnDeleteFileListener = mOnDeleteFileListener;
    }


    public interface OnDeleteFileListener {
        void onDelete(boolean isSuccess, String path);
    }

}
