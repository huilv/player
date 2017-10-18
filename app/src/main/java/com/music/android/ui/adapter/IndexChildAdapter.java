package com.music.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.android.R;
import com.music.android.bean.HotListBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.data.local.MusicLocalDataSource;
import com.music.android.data.local.MusicPersistenceContract;
import com.music.android.managers.ImageLoaderManager;
import com.music.android.network.RequestService;
import com.music.android.network.SoundCloudRequestService;
import com.music.android.utils.L;
import com.music.android.utils.PlayUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuyun on 17/3/20.
 */

public class IndexChildAdapter extends BaseAdapter<IndexChildAdapter.IndexChildViewHolder, MusicInfoBean> {

    private boolean isShow = true;

    private int flag = -1;

    private List<MusicInfoBean> list = null;

    public IndexChildAdapter(Context mContext) {
        super(mContext);
    }

    public void isShowName(boolean isShow) {
        this.isShow = isShow;
    }

    public void setPosition(int position) {
        this.flag = position;
    }

    @Override
    public IndexChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new IndexChildViewHolder(mLayoutInflater.inflate(R.layout.layout_type_horizontal_item, parent, false));
    }

    @Override
    public void onBindViewHolder(IndexChildViewHolder holder, final int position) {
        MusicInfoBean dataBean = data.get(position);
        holder.setPosition(position);
        holder.setData(dataBean);
        ImageLoaderManager.imageLoaderDefault(holder.logo_ImageView, R.mipmap.icon_loading_default, dataBean.artwork_url);
        holder.song_name_TextView.setText(dataBean.title);
        if (isShow) {
            holder.singer_TextView.setText(dataBean.singer);
            holder.singer_TextView.setVisibility(View.VISIBLE);
        } else {
            holder.singer_TextView.setVisibility(View.INVISIBLE);
        }

    }

    class IndexChildViewHolder extends RecyclerView.ViewHolder {

        ImageView logo_ImageView;

        TextView song_name_TextView;

        TextView singer_TextView;

        private int position = 0;

        private MusicInfoBean dataBean;

        public void setData(MusicInfoBean dataBean) {
            this.dataBean = dataBean;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        IndexChildViewHolder(View itemView) {
            super(itemView);
            logo_ImageView = (ImageView) itemView.findViewById(R.id.logo_ImageView);
            song_name_TextView = (TextView) itemView.findViewById(R.id.song_name_TextView);
            singer_TextView = (TextView) itemView.findViewById(R.id.singer_TextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (flag) {
                        case 0:
                            if (list == null) {
                                getHotList(position);
                            } else {
                                PlayUtils.play(list, position);
                            }
                            break;
                        case 1:
                            mOnAddFragmentListener.onAddFragment("rankFragment", dataBean);
                            break;
                        case 2:
                            mOnAddFragmentListener.onAddFragment("styleFragment", dataBean);
                            break;
                    }
                }
            });
        }
    }

    private void getHotList(final int position) {
        Flowable<HotListBean> mFlowable = RequestService.createRequestService().getHotList();
        mFlowable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<HotListBean>() {
            @Override
            public void accept(HotListBean hotListBean) throws Exception {
                if (hotListBean.data != null && hotListBean.data.list != null) {
                    for (int i = 0; i < hotListBean.data.list.size(); i++) {
                        hotListBean.data.list.get(i).singer = hotListBean.data.list.get(i).user.username;
                    }
                    list = hotListBean.data.list;
                    PlayUtils.play(hotListBean.data.list, position);
                }
            }
        });
    }

}
