package com.music.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.music.android.R;
import com.music.android.bean.CommonBean;
import com.music.android.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyun on 17/3/15.
 */

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.CommonViewHolder> {

    private LayoutInflater mLayoutInflater;

    private List<CommonBean> singerList = new ArrayList<>();

    public ArtistsAdapter(Context mContext) {
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public OnItemClickListener mOnItemClickListener;

    public void setData(List<CommonBean> singerList) {
        this.singerList.clear();
        this.singerList.addAll(singerList);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommonViewHolder(mLayoutInflater.inflate(R.layout.layout_artists_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        final CommonBean commonBean = singerList.get(position);
        holder.singer_textView.setText(commonBean.singer);
        final String songCount;
        if (commonBean.count == 1) {
            songCount = commonBean.count + " song";
        } else {
            songCount = commonBean.count + " songs";
        }
        holder.song_count_textView.setText(songCount);
        int index = position + 1;
        holder.index_textView.setText(String.valueOf(index));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(commonBean);
                mOnItemClickListener.onItemClick(v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return singerList.size();
    }

    public static class CommonViewHolder extends RecyclerView.ViewHolder {
        TextView index_textView;
        TextView song_count_textView;
        TextView singer_textView;

        public CommonViewHolder(View itemView) {
            super(itemView);
            index_textView = (TextView) itemView.findViewById(R.id.index_textView);
            song_count_textView = (TextView) itemView.findViewById(R.id.song_count_textView);
            singer_textView = (TextView) itemView.findViewById(R.id.singer_textView);
        }
    }
}
