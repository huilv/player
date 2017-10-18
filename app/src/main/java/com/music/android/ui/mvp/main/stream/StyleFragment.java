package com.music.android.ui.mvp.main.stream;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.base.BaseFragment;
import com.music.android.bean.StyleInfoBean;
import com.music.android.listener.OnItemClickListener;
import com.music.android.listener.OnRefreshListener;
import com.music.android.managers.AbsADManager;
import com.music.android.managers.Constant;
import com.music.android.network.RequestService;
import com.music.android.ui.adapter.StyleAdapter;
import com.music.android.ui.widgets.NoNetWorkLayout;
import com.music.android.utils.L;
import com.music.android.utils.NetWorkUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import mobi.android.adlibrary.AdAgent;
import mobi.android.adlibrary.internal.ad.Ad;
import mobi.android.adlibrary.internal.ad.AdError;
import mobi.android.adlibrary.internal.ad.IAd;
import mobi.android.adlibrary.internal.ad.OnAdLoadListener;
import mobi.android.adlibrary.internal.ad.WrapInterstitialAd;

/**
 * Created by liuyun on 17/3/21.
 */

public class StyleFragment extends BaseFragment implements OnItemClickListener, View.OnClickListener {

    private RecyclerView mRecyclerView;

    private StyleAdapter mStyleAdapter;

    private TextView title_TextView;

    private ImageView back_ImageView;

    private View loadingView;

    private NoNetWorkLayout no_network_layout;

    private int tag = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_style, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        tag = getArguments().getInt(Constant.PropertyConstant.BUNDLE_STYLE);

        back_ImageView = (ImageView) view.findViewById(R.id.back_ImageView);
        back_ImageView.setOnClickListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mStyleAdapter = new StyleAdapter(getContext());
        mStyleAdapter.setTag(tag);
        mStyleAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mStyleAdapter);
        loadingView = view.findViewById(R.id.loading_view);
        title_TextView = (TextView) view.findViewById(R.id.title_TextView);

        no_network_layout = (NoNetWorkLayout) view.findViewById(R.id.no_network_layout);
        no_network_layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        loadData();
    }

    private void loadAd() {

        new AbsADManager(Constant.SlotIdConstant.GENRES_AD, false) {
            @Override
            public void onViewLoaded(View view) {
                if (mStyleAdapter != null && view != null) {
                    mStyleAdapter.setAdView(view);
                }
            }
        };
    }

    private void loadData() {
        if (NetWorkUtils.isNetworkConnected()) {
            loadingView.setVisibility(View.VISIBLE);
            no_network_layout.setVisibility(View.GONE);

            switch (tag) {
                case Constant.PropertyConstant.TAG_STYLE:
                    getStyle();
                    title_TextView.setText(getContext().getString(R.string.genres));
                    break;
                case Constant.PropertyConstant.TAG_AUDIO:
                    getAudio();
                    title_TextView.setText(getContext().getString(R.string.audio));
                    break;
            }
        } else {
            loadingView.setVisibility(View.GONE);
            no_network_layout.setVisibility(View.VISIBLE);
        }
    }

    private void getStyle() {
        Flowable<StyleInfoBean> mFlowable = RequestService.createRequestService().getStyleIndex();
        mFlowable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(getSubscriber());
    }

    private void getAudio() {
        Flowable<StyleInfoBean> mFlowable = RequestService.createRequestService().getAudioList();
        mFlowable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(getSubscriber());
    }

    private Subscriber<StyleInfoBean> getSubscriber() {
        return new Subscriber<StyleInfoBean>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(StyleInfoBean styleInfoBean) {
                if (styleInfoBean != null && styleInfoBean.data != null) {
                    mStyleAdapter.setData(styleInfoBean.data);
                }
                loadingView.setVisibility(View.GONE);
                loadAd();
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
                no_network_layout.setVisibility(View.GONE);
            }
        };
    }

    @Override
    public void onItemClick(View view) {
        switch (tag) {
            case Constant.PropertyConstant.TAG_STYLE:
                mOnAddFragmentListener.onAddFragment("styleFragment", view.getTag());
                break;
            case Constant.PropertyConstant.TAG_AUDIO:
                mOnAddFragmentListener.onAddFragment("audioFragment", view.getTag());
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_ImageView:
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                break;
        }
    }
}
