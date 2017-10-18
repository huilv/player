package com.music.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.bean.HotListBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.managers.Constant;
import com.music.android.managers.ImageLoaderManager;
import com.music.android.network.RequestService;
import com.music.android.ui.viewholder.CommonViewHolder;
import com.music.android.utils.AesUtils;
import com.music.android.utils.PlayUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by liuyun on 17/3/21.
 */

public class StyleAdapter extends BaseAdapter<RecyclerView.ViewHolder, MusicInfoBean> {

    private int tag;

    public StyleAdapter(Context context) {
        super(context);
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    private View adView = null;

    private final static int COMMON_VIEW = 1;

    private final static int AD_VIEW = 2;

    public void setAdView(View adView) {
        this.adView = adView;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        switch (getItemViewType(position)) {
            case COMMON_VIEW:
                StyleViewHolder styleViewHolder = (StyleViewHolder) holder;

                int index = getPosition(position);

                if (index == 0) {
                    styleViewHolder.tips_TextView.setVisibility(View.VISIBLE);
                } else {
                    styleViewHolder.tips_TextView.setVisibility(View.GONE);
                }
                switch (tag) {
                    case Constant.PropertyConstant.TAG_STYLE:
                        styleViewHolder.tips_TextView.setText(MusicApp.context.getResources().getString(R.string.style_content));
                        break;
                    case Constant.PropertyConstant.TAG_AUDIO:
                        styleViewHolder.tips_TextView.setText(MusicApp.context.getResources().getString(R.string.audio_tips));
                        break;
                }
                MusicInfoBean musicInfoBean = data.get(index);
                styleViewHolder.setData(musicInfoBean);
                styleViewHolder.title_TextView.setText(musicInfoBean.title);
                if (musicInfoBean.artwork_url != null) {
                    ImageLoaderManager.imageLoader(styleViewHolder.logo_ImageView, R.mipmap.icon_loading_default, musicInfoBean.artwork_url);
                } else {
                    styleViewHolder.logo_ImageView.setImageResource(R.mipmap.icon_loading_default);
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == COMMON_VIEW) {
            return new StyleViewHolder(mLayoutInflater.inflate(R.layout.layout_style_item, parent, false));
        } else if (viewType == AD_VIEW) {
            return new CommonViewHolder(mLayoutInflater.inflate(R.layout.item_native_ad, parent, false));
        } else {
            return null;
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

    class StyleViewHolder extends RecyclerView.ViewHolder {
        TextView tips_TextView;
        ImageView logo_ImageView;
        ImageView play_ImageView;
        TextView title_TextView;
        MusicInfoBean musicInfoBean;

        public void setData(MusicInfoBean musicInfoBean) {
            this.musicInfoBean = musicInfoBean;
        }

        StyleViewHolder(View itemView) {
            super(itemView);
            tips_TextView = (TextView) itemView.findViewById(R.id.tips_TextView);
            logo_ImageView = (ImageView) itemView.findViewById(R.id.logo_ImageView);
            play_ImageView = (ImageView) itemView.findViewById(R.id.play_ImageView);
            title_TextView = (TextView) itemView.findViewById(R.id.title_TextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setTag(musicInfoBean);
                    mOnItemClickListener.onItemClick(v);
                }
            });
            play_ImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (tag) {
                        case Constant.PropertyConstant.TAG_STYLE:
                            getStyleList(String.valueOf(musicInfoBean.id));
                            break;
                        case Constant.PropertyConstant.TAG_AUDIO:
                            getAudioList(String.valueOf(musicInfoBean.id));
                            break;
                    }

                }
            });
        }
    }

    private void getStyleList(String id) {
        String body = AesUtils.getEncryptBody("{\"category_id\":\"" + id + "\"}");
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/x-markdown; charset=utf-8"), body);
        Flowable<HotListBean> mFlowable = RequestService.createRequestService().getStyleList(requestBody);
        mFlowable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(getSubscriber());
    }

    private void getAudioList(String id) {
        String body = AesUtils.getEncryptBody("{\"audio_id\":\"" + id + "\"}");
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/x-markdown; charset=utf-8"), body);
        Flowable<HotListBean> mFlowable = RequestService.createRequestService().getAudioList(requestBody);
        mFlowable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(getSubscriber());
    }

    private Subscriber<HotListBean> getSubscriber() {
        return new Subscriber<HotListBean>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(HotListBean hotListBean) {
                if (hotListBean.data != null && hotListBean.data.list != null && hotListBean.data.list.size() > 0) {
                    for (int i = 0; i < hotListBean.data.list.size(); i++) {
                        if (hotListBean.data.list.get(i).user != null && hotListBean.data.list.get(i).user.username != null) {
                            hotListBean.data.list.get(i).singer = hotListBean.data.list.get(i).user.username;
                        } else {
                            hotListBean.data.list.get(i).singer = hotListBean.data.module_name;
                        }
                    }
                    PlayUtils.play(hotListBean.data.list, 0);
                }

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

}
