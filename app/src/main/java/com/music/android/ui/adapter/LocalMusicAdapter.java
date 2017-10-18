package com.music.android.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.bean.MusicInfoBean;
import com.music.android.data.local.FavoriteSongHelper;
import com.music.android.data.local.HistorySongHelper;
import com.music.android.data.local.MusicLocalDataSource;
import com.music.android.managers.AnalyticsManager;
import com.music.android.ui.viewholder.CommonViewHolder;
import com.music.android.ui.widgets.ControlPopupWindow;
import com.music.android.utils.AnalyticsConstants;
import com.music.android.utils.Constants;
import com.music.android.utils.FileUtils;
import com.music.android.utils.L;
import com.music.android.utils.PlayHelper;
import com.music.android.utils.PlayUtils;
import com.music.android.utils.comparator.AZComparator;
import com.music.android.utils.comparator.SongIdComparator;
import com.music.android.utils.comparator.TimeComparator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liuyun on 17/3/15.
 */

public class LocalMusicAdapter extends BaseAdapter<RecyclerView.ViewHolder, MusicInfoBean> {

    private final static int CONTROL_VIEW = 0;

    private final static int COMMON_VIEW = 1;

    private final static int FOOTER_VIEW = 2;

    private final static int AD_VIEW = 3;

    public boolean isShowCheckBox = false;

    public String flag;

    private String playlistNameId;

    private View adView = null;
    private String tag;
    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setMenuName(String playlistNameId) {
        this.playlistNameId = playlistNameId;
    }

    public void setAdView(View adView) {
        this.adView = adView;
        notifyDataSetChanged();
    }

    public LocalMusicAdapter(Context mContext,String tag) {
        super(mContext);
        this.tag=tag;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CONTROL_VIEW) {
            return new HeaderViewHolder(mLayoutInflater.inflate(R.layout.layout_common_header, parent, false));
        } else if (viewType == COMMON_VIEW) {
            return new CommonMusicViewHolder(mLayoutInflater.inflate(R.layout.layout_music_item, parent, false));
        }
//        else if (viewType == FOOTER_VIEW) {
//            return new FooterViewHolder(mLayoutInflater.inflate(R.layout.layout_mysong_footer, parent, false));
//        }
        else if (viewType == AD_VIEW) {
            return new CommonViewHolder(mLayoutInflater.inflate(R.layout.item_native_ad, parent, false));
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        switch (holder.getItemViewType()) {
            case CONTROL_VIEW:
                final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.list_change_imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnDeleteViewShowListener.onDeleteViewShow(true);
                        isShowCheckBox = !isShowCheckBox;
//                        data.add(data.size(), new MusicInfoBean());
                        LocalMusicAdapter.this.notifyDataSetChanged();
                        edit();
                    }
                });

                headerViewHolder.cancel_TextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnDeleteViewShowListener.onDeleteViewShow(false);
                        isShowCheckBox = !isShowCheckBox;
//                        data.remove(data.size() - 1);
                        for (int i = 0; i < data.size(); i++) {
                            data.get(i).isChecked = false;
                        }
                        LocalMusicAdapter.this.notifyDataSetChanged();
                    }
                });
                headerViewHolder.random_LinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (data.size() > 1) {
                            PlayUtils.randomPlay(LocalMusicAdapter.this, data);
                            mOnDeleteViewShowListener.smoothScrollToPosition(PlayUtils.count, data.size());
                        }
                        shufflePlay();
                    }
                });
                headerViewHolder.sort_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        final int position;
                        if (isChecked) {
                            position = 0;
                            azSort();
                        } else {
                            position = 1;
                            if (flag.equals("Favorites")) {
                                timeSort();
                            } else if (flag.equals("RecentlyPlay")) {
                                reverse();
                            } else {
                                songIdSort();
                            }

                        }
                        sequence(position);
                    }
                });

                int count;
                if (isShowCheckBox) {
                    count = data.size() - 1;
                    headerViewHolder.hide_RelativeLayout.setVisibility(View.GONE);
                    headerViewHolder.cancel_TextView.setVisibility(View.VISIBLE);
                } else {
                    count = data.size() - 1;
                    headerViewHolder.hide_RelativeLayout.setVisibility(View.VISIBLE);
                    headerViewHolder.cancel_TextView.setVisibility(View.GONE);
                }

                headerViewHolder.my_songs_count_TextView.setText(getSongsCount(count));

                if (data.size() <= 1) {
                    headerViewHolder.list_change_imageView.setVisibility(View.INVISIBLE);
                    headerViewHolder.sort_CheckBox.setVisibility(View.INVISIBLE);
                } else {
                    headerViewHolder.list_change_imageView.setVisibility(View.VISIBLE);
                    headerViewHolder.sort_CheckBox.setVisibility(View.VISIBLE);
                }
                break;
            case COMMON_VIEW:

                CommonMusicViewHolder localMusicViewHolder = (CommonMusicViewHolder) holder;

                int index = getPosition(position);

                MusicInfoBean infoBean = data.get(index);

                localMusicViewHolder.setData(infoBean, index);
                localMusicViewHolder.singer_textView.setText(infoBean.singer);
                localMusicViewHolder.song_name_textView.setText(infoBean.title);

                localMusicViewHolder.index_textView.setText(String.valueOf(index));

                if (infoBean.isPlaying) {
                    localMusicViewHolder.song_name_textView.setTextColor(mContext.getResources().getColor(R.color.playing_text_bg));
                } else {
                    localMusicViewHolder.song_name_textView.setTextColor(mContext.getResources().getColor(R.color.radio_up));
                }

                localMusicViewHolder.checkbox.setChecked(infoBean.isChecked);

                if (isShowCheckBox) {
                    localMusicViewHolder.checkbox.setVisibility(View.VISIBLE);
                    localMusicViewHolder.index_textView.setVisibility(View.GONE);
                    localMusicViewHolder.setting_LinearLayout.setVisibility(View.GONE);
                } else {
                    localMusicViewHolder.checkbox.setVisibility(View.INVISIBLE);
                    localMusicViewHolder.index_textView.setVisibility(View.VISIBLE);
                    localMusicViewHolder.setting_LinearLayout.setVisibility(View.VISIBLE);
                }

                break;
            case AD_VIEW:
                CommonViewHolder commonViewHolder = (CommonViewHolder) holder;
                if (adView != null && commonViewHolder.ad_container.getChildCount() == 0) {
                    commonViewHolder.ad_container.addView(adView);
                }
                break;
        }
    }

    private int getPosition(int position) {
        int index;
        if (data.size() > 3 && position > 2) {
            index = position - 1;
        } else {
            index = position;
        }
        return index;
    }

    @Override
    public int getItemCount() {
        return data.size() > 3 ? data.size() + 1 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return CONTROL_VIEW;
        }
//        else if (position == data.size() - 1 && isShowCheckBox) {
//            return FOOTER_VIEW;
//        }
        else if (position == 3) {
            return AD_VIEW;
        } else {
            return COMMON_VIEW;
        }
    }

    private void reverse() {
        List<MusicInfoBean> list = getData();
        list.remove(0);
        Collections.reverse(list);
        list.add(0, new MusicInfoBean());
        notifyDataSetChanged();
    }

    private void sequence(int position) {
        if (flag != null) {
            if (flag.equals("RecentlyPlay")) {
                AnalyticsManager.getInstance().sequence(AnalyticsConstants.Sequence.ACTION_IN_RECENT, position);
            } else if (flag.equals("Favorites")) {
                AnalyticsManager.getInstance().sequence(AnalyticsConstants.Sequence.ACTION_IN_FAVORITES, position);
            } else if (flag.equals("playList")) {
                AnalyticsManager.getInstance().sequence(AnalyticsConstants.Sequence.ACTION_IN_PLAYLIST, position);
            } else {
                AnalyticsManager.getInstance().sequence(AnalyticsConstants.Sequence.ACTION_IN_MYSONGS, position);
            }
        }
    }

    private void edit() {
        if (flag != null) {
            if (flag.equals("RecentlyPlay")) {
                AnalyticsManager.getInstance().edite(2);
            } else if (flag.equals("Favorites")) {
                AnalyticsManager.getInstance().edite(1);
            } else if (flag.equals("playList")) {
                AnalyticsManager.getInstance().edite(3);
            } else {
                AnalyticsManager.getInstance().edite(0);
            }
        }
    }

    private void shufflePlay() {
        if (flag != null) {
            if (flag.equals("RecentlyPlay")) {
                AnalyticsManager.getInstance().shuffleplay(2);
            } else if (flag.equals("Favorites")) {
                AnalyticsManager.getInstance().shuffleplay(1);
            } else if (flag.equals("playList")) {
                AnalyticsManager.getInstance().shuffleplay(3);
            } else {
                AnalyticsManager.getInstance().shuffleplay(0);
            }
        }
    }

    private void removeData(MusicInfoBean musicInfoBean) {
        Log.d("LocalMusicAdapter","tag="+tag);
        if("Favorites".equalsIgnoreCase(tag)){
            FavoriteSongHelper.getDefault(MusicApp.context).delete(musicInfoBean.path);
            EventBus.getDefault().post(Constants.EventBusConstants.EVENT_UPDATE_UI);
            return;
        }

        if("RecentlyPlay".equalsIgnoreCase(tag)){
            MusicLocalDataSource.getInstance(MusicApp.context).deleteRecentlyMusic(musicInfoBean);
            EventBus.getDefault().post(Constants.EventBusConstants.EVENT_UPDATE_UI);
            return;
        }

        FileUtils.scanFileAsync(musicInfoBean.path, new ControlPopupWindow.OnDeleteFileListener() {
            @Override
            public void onDelete(boolean isSuccess, String path) {
                EventBus.getDefault().post(Constants.EventBusConstants.EVENT_UPDATE_UI);
            }
        });
    }

    private List<MusicInfoBean> deleteMusics = new ArrayList<>();

    public void removeCheckedItem() {
        deleteMusics.clear();
        Iterator<MusicInfoBean> infoBeanIterator = data.subList(1, data.size() ).iterator();
        while (infoBeanIterator.hasNext()) {
            MusicInfoBean musicInfoBean = infoBeanIterator.next();
            if (musicInfoBean.isChecked) {
                Log.d("removeCheckedItem",""+musicInfoBean.singer);
                deleteMusics.add(musicInfoBean);
                removeData(musicInfoBean);
                infoBeanIterator.remove();
            }
        }
        PlayHelper.getInstance().deleteSongList(deleteMusics);
        this.notifyDataSetChanged();
    }

    private void azSort() {
        List<MusicInfoBean> list = data.subList(1, data.size());
        Collections.sort(list, new AZComparator());
        this.notifyDataSetChanged();
    }

    private void timeSort() {
        Collections.sort(data.subList(1, data.size()), new TimeComparator());
        this.notifyDataSetChanged();
    }

    private void songIdSort() {
        Collections.sort(data.subList(1, data.size()), new SongIdComparator());
        this.notifyDataSetChanged();
    }

    private class DeleteMusic implements ControlPopupWindow.OnDeleteFileListener {

        @Override
        public void onDelete(boolean isSuccess, String path) {
            if (isSuccess) {
                for (int i = 0; i < data.size(); i++) {
                    if (path.equals(data.get(i).path)) {
                        if (data.get(i).isPlaying) {
                            if (i < data.size() && i > 1&&i+1<data.size()) {
                                data.get(i + 1).isPlaying = true;
                            } else {
                                if (data.size() > 0) {
                                    data.get(0).isPlaying = true;
                                }
                            }
                        }
                        PlayHelper.getInstance().deleteSong(path);
                        data.remove(i);
                        break;
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            EventBus.getDefault().post(data.size());
            LocalMusicAdapter.this.notifyDataSetChanged();

        }
    };

    private String getSongsCount(int count) {
        String songs;
        if (count > 1) {
            songs = count + " " + MusicApp.context.getResources().getString(R.string.songs1);
        } else {
            songs = count + " " + MusicApp.context.getResources().getString(R.string.songs2);
        }
        return songs;
    }

    private class CommonMusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        MusicInfoBean musicInfoBean;

        int position;

        TextView song_name_textView;

        TextView singer_textView;

        LinearLayout setting_LinearLayout;

        TextView index_textView;

        CheckBox checkbox;

        RelativeLayout left_RelativeLayout;

        RelativeLayout root_LinearLayout;

        public void setData(MusicInfoBean musicInfoBean, int position) {
            this.musicInfoBean = musicInfoBean;
            this.position = position;
        }

        CommonMusicViewHolder(View itemView) {
            super(itemView);
            song_name_textView = (TextView) itemView.findViewById(R.id.song_name_textView);
            singer_textView = (TextView) itemView.findViewById(R.id.singer_textView);
            setting_LinearLayout = (LinearLayout) itemView.findViewById(R.id.setting_LinearLayout);
            index_textView = (TextView) itemView.findViewById(R.id.index_textView);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            left_RelativeLayout = (RelativeLayout) itemView.findViewById(R.id.left_RelativeLayout);
            root_LinearLayout = (RelativeLayout) itemView.findViewById(R.id.root_LinearLayout);
            setting_LinearLayout.setOnClickListener(this);
            left_RelativeLayout.setOnClickListener(this);
            root_LinearLayout.setOnClickListener(this);
            checkbox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.checkbox:
                    checkbox.setChecked(!checkbox.isChecked());
                    break;
                case R.id.setting_LinearLayout:
                    ControlPopupWindow controlPopupWindow;
                    if (!isShowCheckBox) {
                        if (flag.equals(Constants.FlagConstants.FLAG_ALL)) {
                            controlPopupWindow = new ControlPopupWindow(mContext, -1, data, position);
                        } else if (flag.equals(Constants.FlagConstants.FLAG_RECENTLY_PLAY) || flag.equals(Constants.FlagConstants.FLAG_FAVORITES)) {
                            controlPopupWindow = new ControlPopupWindow(mContext, 1, data, position);
                        } else if (flag.equals(Constants.FlagConstants.FLAG_PLAY_LIST)) {
                            controlPopupWindow = new ControlPopupWindow(mContext, 2, data, position);
                            controlPopupWindow.setMenuName(playlistNameId);
                        } else {
                            controlPopupWindow = new ControlPopupWindow(mContext, 0, data, position);
                        }
                        controlPopupWindow.setFlag(flag);
                        controlPopupWindow.setMenuName(playlistNameId);
                        controlPopupWindow.setOnDeleteFileListener(new DeleteMusic());
                        controlPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                    }
                    break;
                case R.id.root_LinearLayout:
                    if (!isShowCheckBox) {
                        PlayUtils.play(LocalMusicAdapter.this, data, position);
                    } else {
                        if (checkbox.isChecked()) {
                            checkbox.setChecked(false);
                        } else {
                            checkbox.setChecked(true);
                        }
                    }
                    break;
            }

        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                musicInfoBean.isChecked = true;
            } else {
                musicInfoBean.isChecked = false;
            }
        }


    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        ImageView list_change_imageView;

        TextView my_songs_count_TextView;

        LinearLayout random_LinearLayout;

        RelativeLayout hide_RelativeLayout;

        TextView cancel_TextView;

        CheckBox sort_CheckBox;

        HeaderViewHolder(View itemView) {
            super(itemView);
            my_songs_count_TextView = (TextView) itemView.findViewById(R.id.my_songs_count_TextView);
            random_LinearLayout = (LinearLayout) itemView.findViewById(R.id.random_LinearLayout);
            hide_RelativeLayout = (RelativeLayout) itemView.findViewById(R.id.hide_RelativeLayout);
            cancel_TextView = (TextView) itemView.findViewById(R.id.cancel_TextView);
            list_change_imageView = (ImageView) itemView.findViewById(R.id.list_change_imageView);
            sort_CheckBox = (CheckBox) itemView.findViewById(R.id.sort_CheckBox);
        }
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterViewHolder(View itemView) {
            super(itemView);
        }
    }


    OnDeleteViewShowListener mOnDeleteViewShowListener;

    public void setOnDeleteViewShowListener(OnDeleteViewShowListener mOnDeleteViewShowListener) {
        this.mOnDeleteViewShowListener = mOnDeleteViewShowListener;
    }

    public interface OnDeleteViewShowListener {
        void onDeleteViewShow(boolean isShown);

        void smoothScrollToPosition(int position, int maxPosition);
    }


}
