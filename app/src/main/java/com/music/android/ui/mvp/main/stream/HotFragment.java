package com.music.android.ui.mvp.main.stream;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.bean.HotListBean;
import com.music.android.bean.MessageEventBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.bean.RankInfoBean;
import com.music.android.listener.OnRefreshListener;
import com.music.android.managers.AnalyticsManager;
import com.music.android.managers.ImageLoaderManager;
import com.music.android.network.RequestService;
import com.music.android.service.MusicPlayService;
import com.music.android.ui.adapter.HotAdapter;
import com.music.android.ui.mvp.EventBusFragment;
import com.music.android.ui.widgets.NoNetWorkLayout;
import com.music.android.utils.AesUtils;
import com.music.android.utils.L;
import com.music.android.utils.NetWorkUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static android.R.attr.id;

/**
 * Created by liuyun on 17/3/20.
 */

public class HotFragment extends EventBusFragment implements View.OnClickListener {

    private HotAdapter mHotAdapter;

    private View loadingView;

    private ImageView logo_ImageView;

    private TextView title_TextView;

    private NoNetWorkLayout no_network_layout;

    private TextView tips_TextView;

    private static int position = 0;

    private static String title = null;

    private static String id = null;

    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");

    public static HotFragment getInstants(int position, String title, String id) {
        HotFragment.position = position;
        HotFragment.title = title;
        HotFragment.id = id;
        return new HotFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hot, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        logo_ImageView = (ImageView) view.findViewById(R.id.logo_ImageView);
        loadingView = view.findViewById(R.id.loading_view);
        ImageView back_ImageView = (ImageView) view.findViewById(R.id.back_ImageView);
        back_ImageView.setOnClickListener(this);

        tips_TextView = (TextView) view.findViewById(R.id.tips_TextView);

        title_TextView = (TextView) view.findViewById(R.id.title_TextView);
        no_network_layout = (NoNetWorkLayout) view.findViewById(R.id.no_network_layout);
        no_network_layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mHotAdapter = new HotAdapter(getContext());
        mRecyclerView.setAdapter(mHotAdapter);

        loadData();

    }

    private void loadData() {
        if (NetWorkUtils.isNetworkConnected()) {
            loadingView.setVisibility(View.VISIBLE);
            no_network_layout.setVisibility(View.GONE);
            getDataById();
        } else {
            loadingView.setVisibility(View.GONE);
            no_network_layout.setVisibility(View.VISIBLE);
        }
    }

    private void getDataById() {
        switch (position) {
            case 0:
                getHotList();
                title_TextView.setText(getContext().getResources().getString(R.string.new_and_hot));
                break;
            case 1:
                title_TextView.setText(title);
                getRankList(id);
                break;
            case 2:
                title_TextView.setText(title);
                getStyleList(String.valueOf(id));
                break;
            case 3:
                title_TextView.setText(title);
                tips_TextView.setText(MusicApp.context.getResources().getString(R.string.audio_content));
                getAudioList(String.valueOf(id));
                break;
        }
    }

    @Override
    public void onMusicChanged(MessageEventBean eventBean) {
        updateData(mHotAdapter.getData(), eventBean.path);
        mHotAdapter.notifyDataSetChanged();
    }

    private void updateData(List<MusicInfoBean> list, String url) {
        int size = MusicPlayService.getMusics().size();
        if (size == 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            MusicInfoBean musicInfoBean = list.get(i);
            if (url.equals(musicInfoBean.path)) {
                musicInfoBean.isPlaying = true;
            } else {
                musicInfoBean.isPlaying = false;
            }
        }
    }

    private void getHotList() {
        AnalyticsManager.getInstance().stream(0, "");
        Flowable<HotListBean> mFlowable = RequestService.createRequestService().getHotList();
        mFlowable.delay(200, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(getSubscriber());
    }

    private void getRankList(String id) {
        String body = AesUtils.getEncryptBody("{\"rank_id\":\"" + id + "\"}");
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, body);
        Flowable<HotListBean> mFlowable = RequestService.createRequestService().getRankList(requestBody);
        mFlowable.delay(200, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(getSubscriber());
    }

    private void getStyleList(String id) {
        String body = AesUtils.getEncryptBody("{\"category_id\":\"" + id + "\"}");
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, body);
        Flowable<HotListBean> mFlowable = RequestService.createRequestService().getStyleList(requestBody);
        mFlowable.delay(200, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(getSubscriber());
    }

    private void getAudioList(String id) {
        String body = AesUtils.getEncryptBody("{\"audio_id\":\"" + id + "\"}");
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/x-markdown; charset=utf-8"), body);
        Flowable<HotListBean> mFlowable = RequestService.createRequestService().getAudioList(requestBody);
        mFlowable.delay(200, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(getSubscriber());
    }

    private Subscriber<HotListBean> getSubscriber() {
        return new Subscriber<HotListBean>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(HotListBean hotListBean) {
                if (hotListBean.data != null && hotListBean.data.list != null) {
                    hotListBean.data.list.add(0, new MusicInfoBean());
                    hotListBean.data.list.add(0, new MusicInfoBean());
                    for (int i = 2; i < hotListBean.data.list.size(); i++) {
                        if (hotListBean.data.list.get(i).user != null) {
                            hotListBean.data.list.get(i).singer = hotListBean.data.list.get(i).user.username;
                        } else {
                            hotListBean.data.list.get(i).singer = hotListBean.data.module_name;
                        }

                    }
                    if (hotListBean.data.module_logo != null) {
                        ImageLoaderManager.imageLoader(logo_ImageView, R.mipmap.icon_loading_default, hotListBean.data.module_logo);
                    }
                    mHotAdapter.setData(hotListBean.data.list);
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                loadingView.setVisibility(View.GONE);
            }
        };
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
