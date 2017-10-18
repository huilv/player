package com.music.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.data.local.MusicLocalDataSource;


/**
 * Created by liuyun on 17/4/27.
 */

public class HistoryAdapter extends BaseAdapter<HistoryAdapter.HistoryViewHolder, String> {

    public HistoryAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final String keyWord = data.get(position);
        holder.setPosition(position);
        holder.keyWord_TextView.setText(keyWord);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(keyWord);
                mOnItemClickListener.onItemClick(v);
            }
        });
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryViewHolder(mLayoutInflater.inflate(R.layout.item_hisory, parent, false));
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView keyWord_TextView;

        ImageView delete_ImageView;

        int position;

        public void setPosition(int position) {
            this.position = position;
        }

        HistoryViewHolder(View itemView) {
            super(itemView);
            keyWord_TextView = (TextView) itemView.findViewById(R.id.keyWord_TextView);
            delete_ImageView = (ImageView) itemView.findViewById(R.id.delete_ImageView);
            delete_ImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String keyWord = data.get(position);
                        MusicLocalDataSource.getInstance(MusicApp.context).deleteHistoryKeyWord(keyWord);
                        data.remove(position);
                        HistoryAdapter.this.notifyDataSetChanged();
                    }catch (Exception e){

                    }

                }
            });
        }
    }

}
