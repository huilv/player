package com.music.android.ui.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.music.android.R;
import com.music.android.bean.MusicInfoBean;
import com.music.android.managers.AnalyticsManager;
import com.music.android.managers.ImageLoaderManager;
import com.music.android.ui.widgets.ControlPopupWindow;
import com.music.android.utils.L;
import com.music.android.utils.PlayHelper;

import java.util.List;
import java.util.Random;

import static com.music.android.utils.Constants.FlagConstants.FLAG_HOT;

/**
 * Created by liuyun on 17/3/20.
 */

public class HotAdapter extends BaseAdapter<RecyclerView.ViewHolder, MusicInfoBean> {

    private final static int TITLE_VIEW = -1;

    private final static int CONTROL_VIEW = 0;

    private final static int COMMON_VIEW = 1;

    public HotAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CONTROL_VIEW) {
            return new HeaderViewHolder(mLayoutInflater.inflate(R.layout.layout_common_header, parent, false));
        } else if (viewType == COMMON_VIEW) {
            return new CommonMusicViewHolder(mLayoutInflater.inflate(R.layout.layout_remote_music_item, parent, false));
        } else if (viewType == TITLE_VIEW) {
            return new TitleViewHolder(mLayoutInflater.inflate(R.layout.layout_transparent, parent, false));
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case CONTROL_VIEW:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.list_change_imageView.setVisibility(View.GONE);
                headerViewHolder.sort_CheckBox.setVisibility(View.GONE);
                headerViewHolder.my_songs_count_TextView.setText(getSongsCount(data.size() - 2));
                break;
            case COMMON_VIEW:
                CommonMusicViewHolder localMusicViewHolder = (CommonMusicViewHolder) holder;
                MusicInfoBean infoBean = data.get(position);
                localMusicViewHolder.setData(infoBean, position);
                localMusicViewHolder.singer_textView.setText(infoBean.singer);
                localMusicViewHolder.song_name_textView.setText(infoBean.title);
                if (infoBean.isPlaying) {
                    localMusicViewHolder.song_name_textView.setTextColor(mContext.getResources().getColor(R.color.playing_text_bg));
                } else {
                    localMusicViewHolder.song_name_textView.setTextColor(mContext.getResources().getColor(R.color.radio_up));
                }

                localMusicViewHolder.index_textView.setText(String.valueOf(position - 1));
                if (infoBean.artwork_url != null) {
                    ImageLoaderManager.imageLoader(localMusicViewHolder.logo_ImageView, R.mipmap.icon_loading_default, infoBean.artwork_url);
                } else {
                    localMusicViewHolder.logo_ImageView.setImageResource(R.mipmap.icon_loading_default);
                }
                break;
            case TITLE_VIEW:
                TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
                titleViewHolder.itemView.setAlpha(1);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TITLE_VIEW;
        } else if (position == 1) {
            return CONTROL_VIEW;
        } else {
            return COMMON_VIEW;
        }
    }


    private String getSongsCount(int count) {
        String songs;
        if (count > 1) {
            songs = count + " Songs";
        } else {
            songs = count + " Song";
        }
        return songs;
    }


    private class CommonMusicViewHolder extends RecyclerView.ViewHolder {
        TextView song_name_textView;
        TextView singer_textView;
        LinearLayout setting_LinearLayout;
        TextView index_textView;
        ImageView logo_ImageView;
        MusicInfoBean infoBean;
        int position;

        public void setData(MusicInfoBean infoBean, int position) {
            this.infoBean = infoBean;
            this.position = position;
        }


        CommonMusicViewHolder(View itemView) {
            super(itemView);
            song_name_textView = (TextView) itemView.findViewById(R.id.song_name_textView);
            singer_textView = (TextView) itemView.findViewById(R.id.singer_textView);
            setting_LinearLayout = (LinearLayout) itemView.findViewById(R.id.setting_LinearLayout);
            index_textView = (TextView) itemView.findViewById(R.id.index_textView);
            logo_ImageView = (ImageView) itemView.findViewById(R.id.logo_ImageView);
            setting_LinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ControlPopupWindow controlPopupWindow = new ControlPopupWindow(mContext, 0, data, position);
                    controlPopupWindow.setFlag(FLAG_HOT);
                    controlPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollToPlayItem(position);
                }
            });
        }
    }

    private class TitleViewHolder extends RecyclerView.ViewHolder {


        TitleViewHolder(View itemView) {
            super(itemView);

        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        ImageView list_change_imageView;

        LinearLayout random_LinearLayout;

        TextView my_songs_count_TextView;

        CheckBox sort_CheckBox;

        HeaderViewHolder(View itemView) {
            super(itemView);
            list_change_imageView = (ImageView) itemView.findViewById(R.id.list_change_imageView);
            random_LinearLayout = (LinearLayout) itemView.findViewById(R.id.random_LinearLayout);
            my_songs_count_TextView = (TextView) itemView.findViewById(R.id.my_songs_count_TextView);
            sort_CheckBox = (CheckBox) itemView.findViewById(R.id.sort_CheckBox);
            random_LinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.size() > 2) {
                        int count = getRandom(2, data.size() - 1);
                        scrollToPlayItem(count);
                    }
                    AnalyticsManager.getInstance().shuffleplay(4);

                }
            });
        }
    }

    private void scrollToPlayItem(int position) {
        for (int i = 0; i < data.size(); i++) {
            MusicInfoBean musicInfoBean = data.get(i);
            if (position == i) {
                musicInfoBean.isPlaying = true;
            } else {
                musicInfoBean.isPlaying = false;
            }
            HotAdapter.this.notifyDataSetChanged();
        }
        play(data.subList(2, data.size()), position - 2);
    }

    private void play(List<MusicInfoBean> list, int position) {
        PlayHelper.getInstance().changePlayList(mContext, list, position);
    }

    private int oldPosition;

    private int getRandom(int min, int max) {
        Random random = new Random();
        int position = random.nextInt(max) % (max - min + 1) + min;
        if (position == oldPosition) {
            if (position > 0) {
                position--;
            } else {
                position++;
            }
        }
        oldPosition = position;
        return position;
    }
}
