package com.music.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.music.android.R;
import com.music.android.bean.RankInfoBean;
import com.music.android.managers.ImageLoaderManager;
import com.music.android.ui.mvp.main.SpacesLinearItemDecoration;
import com.music.android.ui.viewholder.CommonViewHolder;
import com.music.android.utils.L;
import com.music.android.utils.SizeUtils;

/**
 * Created by liuyun on 17/3/21.
 */

public class RankAdapter extends BaseAdapter<RecyclerView.ViewHolder, RankInfoBean.DataBean> {

    private static final int TIPS_TYPE = 0;

    private static final int COMMON_TYPE = 1;

    private static final int AD_TYPE = 2;

    private View adView = null;

    public RankAdapter(Context context) {
        super(context);
    }

    public void setAdView(View adView) {
        this.adView = adView;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        L.d("onBindViewHolder","position="+position);
        switch (getItemViewType(position)) {
            case TIPS_TYPE:

                break;
            case COMMON_TYPE:
                RankViewHolder rankViewHolder = (RankViewHolder) holder;
                int index = getPosition(position);
                RankInfoBean.DataBean dataBean = data.get(index);
                rankViewHolder.setData(dataBean);
                LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext);
                mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                RankChildAdapter mRankChildAdapter = new RankChildAdapter(mContext);
                mRankChildAdapter.setOnClickListener(new MyClickListener(dataBean));
                rankViewHolder.mRecyclerView.setLayoutManager(mLinearLayoutManager);
                rankViewHolder.mRecyclerView.setAdapter(mRankChildAdapter);
                rankViewHolder.mRecyclerView.addItemDecoration(new SpacesLinearItemDecoration(SizeUtils.dp2Px(mContext, 4)));
                mRankChildAdapter.setData(dataBean.list);

                rankViewHolder.title_TextView.setText(dataBean.title);

                if (dataBean.artwork_url != null) {
                    ImageLoaderManager.imageLoader(rankViewHolder.logo_ImageView, R.mipmap.icon_loading_default, dataBean.artwork_url);
                } else {
                    rankViewHolder.logo_ImageView.setImageResource(R.mipmap.icon_loading_default);
                }
                break;
            case AD_TYPE:
                CommonViewHolder commonViewHolder = (CommonViewHolder) holder;
                if (adView != null && commonViewHolder.ad_container.getChildCount() == 0) {
                    commonViewHolder.ad_container.addView(adView);
                }
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return TIPS_TYPE;
        } else if (position == 3) {
            return AD_TYPE;
        } else {
            return COMMON_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TIPS_TYPE) {
            return new RankTipsViewHolder(mLayoutInflater.inflate(R.layout.layout_rank_tips, parent, false));
        } else if (viewType == AD_TYPE) {
            return new CommonViewHolder(mLayoutInflater.inflate(R.layout.item_native_ad, parent, false));
        } else {
            return new RankViewHolder(mLayoutInflater.inflate(R.layout.layout_rank_item, parent, false));
        }
    }

    private class RankViewHolder extends RecyclerView.ViewHolder {
        RecyclerView mRecyclerView;
        ImageView logo_ImageView;
        TextView title_TextView;
        RankInfoBean.DataBean dataBean;

        void setData(RankInfoBean.DataBean dataBean) {
            this.dataBean = dataBean;
        }

        RankViewHolder(View itemView) {
            super(itemView);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
            logo_ImageView = (ImageView) itemView.findViewById(R.id.logo_ImageView);
            title_TextView = (TextView) itemView.findViewById(R.id.title_TextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setTag(dataBean);
                    mOnItemClickListener.onItemClick(v);
                }
            });
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

    private class MyClickListener implements View.OnClickListener {
        RankInfoBean.DataBean dataBean;

        MyClickListener(RankInfoBean.DataBean dataBean) {
            this.dataBean = dataBean;
        }

        @Override
        public void onClick(View v) {
            v.setTag(dataBean);
            mOnItemClickListener.onItemClick(v);
        }
    }

    private class RankTipsViewHolder extends RecyclerView.ViewHolder {

        RankTipsViewHolder(View itemView) {
            super(itemView);
        }
    }

}
