package com.music.android.ui.mvp.main.search;

import android.Manifest;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.base.BaseFragment;
import com.music.android.bean.ClientIdBean;
import com.music.android.bean.MessageEventBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.listener.OnRefreshListener;
import com.music.android.managers.AbsADManager;
import com.music.android.managers.Constant;
import com.music.android.managers.LocalMusicImpl;
import com.music.android.network.RequestService;
import com.music.android.network.SoundCloudRequestService;
import com.music.android.network.UrlConst;
import com.music.android.service.MusicPlayService;
import com.music.android.ui.adapter.MusicAdapter;
import com.music.android.ui.mvp.EventBusFragment;
import com.music.android.ui.mvp.main.MainActivity;
import com.music.android.ui.widgets.LoadingLayout;
import com.music.android.ui.widgets.NoNetWorkLayout;
import com.music.android.ui.widgets.RecyclerViewEmptySupport;
import com.music.android.ui.widgets.loadmore.LoadMoreAdapter;
import com.music.android.ui.widgets.loadmore.LoadMoreWrapper;
import com.music.android.utils.L;
import com.music.android.utils.LocalMusicUtils;
import com.music.android.utils.NetWorkUtils;
import com.music.android.utils.PermissionUtils;
import com.music.android.utils.SharedPreferencesHelper;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import mobi.android.adlibrary.AdAgent;
import mobi.android.adlibrary.internal.ad.Ad;
import mobi.android.adlibrary.internal.ad.AdError;
import mobi.android.adlibrary.internal.ad.IAd;
import mobi.android.adlibrary.internal.ad.OnAdLoadListener;
import mobi.android.adlibrary.internal.ad.WrapInterstitialAd;

/**
 * Created by liuyun on 17/3/30.
 */

public class SearchResultFragment extends EventBusFragment implements SearchResultContract.View {

    private RecyclerViewEmptySupport mRecyclerView;

    private TextView empty_TextView;

    private NoNetWorkLayout no_network_layout;

    private MusicAdapter mAdapter;

    private int page = 0;

    private String keyWord = "keyWord";

    private String oldKeyWord = "oldKeyWord";


    public void searchRemoteData(String keyWord) {
        this.keyWord = keyWord;
        if (!keyWord.equals(oldKeyWord)&&mAdapter!=null) {
            mAdapter.clear();
            page = 0;
        }
        loadMore();
        oldKeyWord = this.keyWord;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void searchLocalData(String keyWord) {
        this.keyWord = keyWord;
        getMusicByLocal();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        mRecyclerView = (RecyclerViewEmptySupport) view.findViewById(R.id.recyclerView);
        mRecyclerView.setEmptyView(view.findViewById(R.id.empty_TextView));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        empty_TextView = (TextView) view.findViewById(R.id.empty_TextView);
        mAdapter = new MusicAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        no_network_layout = (NoNetWorkLayout) view.findViewById(R.id.no_network_layout);
        no_network_layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                empty_TextView.setVisibility(View.GONE);
                no_network_layout.setVisibility(View.GONE);
                loadMore();
            }
        });

    }

    private void loadAd() {

        new AbsADManager(Constant.SlotIdConstant.RESULT_AD, false) {
            @Override
            public void onViewLoaded(View view) {
                if (mAdapter != null && view != null) {
                    mAdapter.setAdView(view);
                }
            }
        };

    }

    private void getClientId() {
        if (NetWorkUtils.isNetworkConnected()) {

            if (SharedPreferencesHelper.getClientId() == null) {
                Flowable<ClientIdBean> mFlowable = RequestService.createRequestService().getClientId("");
                mFlowable.delay(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Subscriber<ClientIdBean>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(1);
                    }

                    @Override
                    public void onNext(ClientIdBean clientIdBean) {
                        SharedPreferencesHelper.setClientId(clientIdBean.data.client_id);
                        getMusicByNetWork();
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            } else {
                getMusicByNetWork();
            }
        }
    }


    private void getMusicByNetWork() {
        String clientId = SharedPreferencesHelper.getClientId();
        Flowable<List<MusicInfoBean>> mFlowable = SoundCloudRequestService.createRequestService().getMusicInfoByKeyWord(clientId == null ? UrlConst.CLIENT_ID : clientId, keyWord, String.valueOf(page));
        mFlowable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Subscriber<List<MusicInfoBean>>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(List<MusicInfoBean> musicInfoBeanList) {
                for (int i = 0; i < musicInfoBeanList.size(); i++) {
                    MusicInfoBean musicInfoBean = musicInfoBeanList.get(i);
                    musicInfoBean.singer = musicInfoBean.user.username;
                    musicInfoBean.path = musicInfoBean.stream_url + "?client_id=" + SharedPreferencesHelper.getClientId();
                }
                mAdapter.isShowEntry = true;
                if (keyWord.equals(oldKeyWord)) {
                    mAdapter.addItems(musicInfoBeanList);
                } else {
                    mAdapter.setData(musicInfoBeanList);
                }
                if (musicInfoBeanList.size() > 0) {
                    empty_TextView.setVisibility(View.GONE);
                } else {
                    loadMoreAdapter.setShowNoMoreEnabled(true);
                    loadMoreAdapter.setLoadMoreEnabled(false);
                    if (page == 0) {
                        empty_TextView.setVisibility(View.VISIBLE);
                        no_network_layout.setVisibility(View.GONE);
                    }
                }
                loadAd();
            }

            @Override
            public void onError(Throwable t) {
                empty_TextView.setVisibility(View.VISIBLE);
                no_network_layout.setVisibility(View.GONE);
            }

            @Override
            public void onComplete() {
                page++;
            }
        });
    }

    LoadMoreAdapter loadMoreAdapter;

    private void loadMore() {
        loadMoreAdapter = LoadMoreWrapper.with(mAdapter)
                .setFooterView(-1)
                .setShowNoMoreEnabled(false)
                .setListener(new LoadMoreAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(LoadMoreAdapter.Enabled enabled) {
                        if (NetWorkUtils.isNetworkConnected()) {
                            getClientId();
                        } else {
                            empty_TextView.setVisibility(View.GONE);
                            no_network_layout.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .into(mRecyclerView);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void getMusicByLocal() {
        if (keyWord.length() > 0) {
            if (PermissionUtils.checkSelfPermission((MainActivity) getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                localMusic.getMusicByArtistsName(keyWord);
            }
        }
    }

    LocalMusicImpl<MusicInfoBean> localMusic = new LocalMusicImpl<MusicInfoBean>() {
        @Override
        protected void buildMapData(Cursor cursor, Map<String, MusicInfoBean> commonBeanMap) {

        }

        @Override
        protected MusicInfoBean buildData(Cursor cursor) {
            return LocalMusicUtils.getAllSongByName(cursor);
        }

        @Override
        protected void getResult(List<MusicInfoBean> list) {
            mAdapter.isShowEntry = false;
            mAdapter.setData(list);
        }
    };

    @Override
    public void setPresenter(SearchResultContract.Presenter presenter) {

    }

    @Override
    public void onMusicChanged(MessageEventBean eventBean) {
        updateData(mAdapter.getData(), eventBean.path);
        mAdapter.notifyDataSetChanged();
    }

    private void updateData(List<MusicInfoBean> list, String url) {
        int size = MusicPlayService.getMusics().size();
        if (size == 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            MusicInfoBean musicInfoBean = list.get(i);
            if (musicInfoBean.path != null && url != null) {
                if (url.equals(musicInfoBean.path)) {
                    musicInfoBean.isPlaying = true;
                } else {
                    musicInfoBean.isPlaying = false;
                }
            }
        }
    }

}
