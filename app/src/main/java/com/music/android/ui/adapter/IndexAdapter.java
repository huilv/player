package com.music.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.android.R;
import com.music.android.bean.IndexInfoBean;
import com.music.android.bean.IndexInfoBean.DataBean;
import com.music.android.ui.mvp.main.SpacesItemDecoration;
import com.music.android.ui.widgets.IndexItemLayout;
import com.music.android.utils.L;
import com.music.android.utils.SizeUtils;


/**
 * Created by liuyun on 17/3/20.
 */

public class IndexAdapter extends BaseAdapter<IndexAdapter.IndexViewHolder, DataBean> {

    public IndexAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public IndexViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new IndexViewHolder(mLayoutInflater.inflate(R.layout.layout_types_item, parent, false));
    }

    @Override
    public void onBindViewHolder(IndexViewHolder holder, int position) {
        DataBean dataBean = data.get(position);
        holder.setPosition(position);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.item_layout.setPosition(position);
        holder.item_layout.setOnAddFragmentListener(mOnAddFragmentListener);
        holder.title_TextView.setText(dataBean.module_name);

        switch (position) {
            case 0:
                holder.logo_ImageView.setImageResource(R.mipmap.icon_new_hot);
                break;
            case 1:
                holder.logo_ImageView.setImageResource(R.mipmap.icon_rank);
                break;
            case 2:
                holder.logo_ImageView.setImageResource(R.mipmap.icon_genres);
                break;
            case 3:
                holder.logo_ImageView.setImageResource(R.mipmap.icon_audio);
                break;
        }
        holder.item_layout.setData(dataBean.list);
        if (position == 2 || position == 3) {
            holder.item_layout2.setPosition(position);
            holder.item_layout2.setOnAddFragmentListener(mOnAddFragmentListener);
            holder.item_layout2.setData(dataBean.list.subList(3, 6));
            holder.item_layout2.setVisibility(View.VISIBLE);

        } else {
            holder.item_layout2.setVisibility(View.GONE);
        }
    }

    class IndexViewHolder extends RecyclerView.ViewHolder {


        TextView title_TextView;

        TextView more_TextView;

        ImageView logo_ImageView;

        IndexItemLayout item_layout;

        IndexItemLayout item_layout2;

        int position;

        void setPosition(int position) {
            this.position = position;
        }

        IndexViewHolder(View itemView) {
            super(itemView);
            more_TextView = (TextView) itemView.findViewById(R.id.more_TextView);
            title_TextView = (TextView) itemView.findViewById(R.id.title_TextView);
            logo_ImageView = (ImageView) itemView.findViewById(R.id.logo_ImageView);
            item_layout = (IndexItemLayout) itemView.findViewById(R.id.item_layout);
            item_layout2 = (IndexItemLayout) itemView.findViewById(R.id.item_layout2);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setTag(position);
                    mOnItemClickListener.onItemClick(v);
                }
            });
        }
    }

}
