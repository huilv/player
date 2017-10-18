package com.music.android.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.bean.MusicInfoBean;
import com.music.android.managers.ImageLoaderManager;
import com.music.android.ui.viewholder.CommonViewHolder;
import com.music.android.utils.L;
import com.music.android.utils.PlayHelper;

/**
 * Created by liuyun on 17/3/13.
 */

public class MusicAdapter extends BaseAdapter<RecyclerView.ViewHolder, MusicInfoBean> {

    public boolean isShowEntry = true;

    private final static int COMMON_VIEW = 1;

    private final static int AD_VIEW = 2;

    private View adView = null;

    private boolean isSHowAd = false;

    public void setAdView(View adView) {
        this.adView = adView;
        notifyDataSetChanged();
    }

    public MusicAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == COMMON_VIEW) {
            return new SearchMusicViewHolder(mLayoutInflater.inflate(R.layout.layout_search_item, parent, false));
        } else if (viewType == AD_VIEW) {
            return new CommonViewHolder(mLayoutInflater.inflate(R.layout.item_native_ad, parent, false));
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        switch (getItemViewType(position)) {
            case COMMON_VIEW:
                SearchMusicViewHolder searchMusicViewHolder = (SearchMusicViewHolder) holder;
                int index = getPosition(position);
                MusicInfoBean infoBean = data.get(index);
                searchMusicViewHolder.setData(infoBean, index);
                searchMusicViewHolder.singer_TextView.setText(infoBean.user == null ? infoBean.singer : infoBean.user.username);
                searchMusicViewHolder.title_TextView.setText(infoBean.title);
                searchMusicViewHolder.duration_TextView.setText(durationFormat(infoBean.duration));
                searchMusicViewHolder.play_count_TextView.setText(countFormat(infoBean.playback_count));
                searchMusicViewHolder.favorite_count_TextView.setText(countFormat(infoBean.likes_count));
                if (infoBean.artwork_url != null) {
                    if (infoBean.artwork_url.startsWith("http")) {
                        ImageLoaderManager.imageLoader(searchMusicViewHolder.logo_ImageView, R.mipmap.icon_loading_default, infoBean.artwork_url);
                    } else {
                        ImageLoaderManager.imageLoader(searchMusicViewHolder.logo_ImageView, R.mipmap.icon_loading_default, Uri.parse(infoBean.artwork_url));
                    }
                } else {
                    searchMusicViewHolder.logo_ImageView.setImageResource(R.mipmap.icon_loading_default);
                }

                if (infoBean.isPlaying) {
                    searchMusicViewHolder.title_TextView.setTextColor(mContext.getResources().getColor(R.color.radius_green_bg));
                } else {
                    searchMusicViewHolder.title_TextView.setTextColor(mContext.getResources().getColor(R.color.white));
                }

                if (isShowEntry) {
                    searchMusicViewHolder.entry_LinearLayout.setVisibility(View.VISIBLE);
                } else {
                    searchMusicViewHolder.entry_LinearLayout.setVisibility(View.INVISIBLE);
                }
                break;
            case AD_VIEW:
                CommonViewHolder commonViewHolder = (CommonViewHolder) holder;
                if (adView != null && !isSHowAd) {
                    commonViewHolder.ad_container.removeAllViews();
                    commonViewHolder.ad_container.addView(adView);
                    isSHowAd = true;
                }
                break;
        }

    }

    private int getPosition(int position) {
        int index;
        if (data.size() > 2 && position > 1) {
            index = position - 1;
        } else {
            index = position;
        }
        return index;
    }

    @Override
    public int getItemCount() {
        return data.size() > 2 ? data.size() + 1 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 2) {
            return AD_VIEW;
        } else {
            return COMMON_VIEW;
        }
    }

    private class SearchMusicViewHolder extends RecyclerView.ViewHolder {

        TextView title_TextView;
        TextView singer_TextView;
        TextView duration_TextView;
        TextView play_count_TextView;
        TextView favorite_count_TextView;
        ImageView logo_ImageView;
        LinearLayout entry_LinearLayout;
        MusicInfoBean infoBean;
        int position;

        public void setData(MusicInfoBean infoBean, int position) {
            this.infoBean = infoBean;
            this.position = position;
        }

        SearchMusicViewHolder(View itemView) {
            super(itemView);
            title_TextView = (TextView) itemView.findViewById(R.id.title_TextView);
            singer_TextView = (TextView) itemView.findViewById(R.id.singer_TextView);
            logo_ImageView = (ImageView) itemView.findViewById(R.id.logo_ImageView);
            duration_TextView = (TextView) itemView.findViewById(R.id.duration_TextView);
            play_count_TextView = (TextView) itemView.findViewById(R.id.play_count_TextView);
            favorite_count_TextView = (TextView) itemView.findViewById(R.id.favorite_count_TextView);
            entry_LinearLayout = (LinearLayout) itemView.findViewById(R.id.entry_LinearLayout);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayHelper.getInstance().changePlayList(MusicApp.context, data, position);
                }
            });
        }
    }

    private String durationFormat(long duration) {
        long second = (duration / 1000);
        long min = second / 60;
        long sec = second % 60;
        if (min < 10) {
            return "0" + min + ":" + sec;
        } else {
            return min + ":" + sec;
        }
    }

    private String countFormat(int count) {
        String result;
        if (count > 1000 && count < 1000000) {
            result = count / 1000 + "k";
        } else if (count > 1000000) {
            result = count / 1000000 + "m";
        } else {
            result = count + "";
        }
        return result;
    }

}
