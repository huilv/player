package com.music.android.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.music.android.R;
import com.music.android.bean.MusicInfoBean;
import com.music.android.service.MusicPlayService;
import com.music.android.utils.AnalyticsConstants;
import com.music.android.utils.AnalyticsUtils;
import com.music.android.utils.L;
import com.music.android.utils.PlayHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hui.lv on 2017/3/20.
 */

public class MusicsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = "PlayListAdapter";
    private ArrayList<MusicInfoBean> list = new ArrayList();
    private int  currentPosition;

    public MusicsAdapter( ){}
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_musics, null, false);
        holder = new PlayListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PlayListHolder playListHolder = (PlayListHolder) holder;
        playListHolder.count.setText(""+(position+1));
        playListHolder.name.setText(list.get(position).title);
        playListHolder.singer.setText(list.get(position).singer);
        playListHolder.rll.setOnClickListener(new PlayListOnClickListener(position));
        L.d(TAG,"currentPosition--"+currentPosition+"---position="+position);
        if(position==currentPosition){
            playListHolder.name.setTextColor(Color.parseColor("#d5ff44"));
        }else{
            playListHolder.name.setTextColor(Color.parseColor("#ffffff"));
        }
    }


    public void setData(List<MusicInfoBean> values, int currentPosition) {
        Log.d(TAG,"values.size()=="+values.size());
        list.clear();
        list.addAll(values);
        this.currentPosition=currentPosition;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void changeMusic() {
        currentPosition=MusicPlayService.getCurrentPosition();
        notifyDataSetChanged();
    }

    private class PlayListHolder extends RecyclerView.ViewHolder {

        public TextView singer;
        public TextView name;
        public TextView count;
        public  View rll;

        public PlayListHolder(View itemView) {
            super(itemView);
            rll = itemView.findViewById(R.id.item);
            count = (TextView) itemView.findViewById(R.id.count);
            name = (TextView) itemView.findViewById(R.id.name);
            singer = (TextView) itemView.findViewById(R.id.singer);
        }
    }

    private class PlayListOnClickListener implements View.OnClickListener {
        private int position;
        private final String TAG = "PlayListOnClickListener";
        public PlayListOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            currentPosition=position;
            L.d(TAG,"currentPosition=="+position);
//            LocalMusicQueryHelper.startPlay(v.getContext(),position);
            AnalyticsUtils.vMusicClick(AnalyticsConstants.SONGLIST, AnalyticsConstants.Action.CLICK_SONGS,"");
            PlayHelper.getInstance().changePlayList(v.getContext(),list,position);
            notifyDataSetChanged();
        }
    }


}
