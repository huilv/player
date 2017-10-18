package com.music.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.music.android.R;
import com.music.android.bean.MusicInfoBean;

/**
 * Created by liuyun on 17/3/21.
 */

public class RankChildAdapter extends BaseAdapter<RankChildAdapter.RankChildViewHolder, MusicInfoBean> {

    private View.OnClickListener mOnClickListener;

    public RankChildAdapter(Context context) {
        super(context);
    }

    public void setOnClickListener(View.OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public void onBindViewHolder(RankChildViewHolder holder, int position) {
        MusicInfoBean musicInfoBean = data.get(position);
        holder.index_TextView.setText(String.valueOf(position + 1));
        holder.singer_TextView.setText(musicInfoBean.user.username);
        holder.title_TextView.setText(musicInfoBean.title);
    }

    @Override
    public RankChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RankChildViewHolder(mLayoutInflater.inflate(R.layout.layout_rank_songs_item, parent, false));
    }

    class RankChildViewHolder extends RecyclerView.ViewHolder {

        TextView index_TextView, title_TextView, singer_TextView;

        RankChildViewHolder(View itemView) {
            super(itemView);
            index_TextView = (TextView) itemView.findViewById(R.id.index_TextView);
            title_TextView = (TextView) itemView.findViewById(R.id.title_TextView);
            singer_TextView = (TextView) itemView.findViewById(R.id.singer_TextView);
            itemView.setOnClickListener(mOnClickListener);
        }
    }
}
