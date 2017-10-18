package com.music.android.ui.mvp.main.stream;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.music.android.R;
import com.music.android.base.BaseFragment;
import com.music.android.bean.IndexInfoBean;
import com.music.android.listener.OnItemClickListener;
import com.music.android.listener.OnRefreshListener;
import com.music.android.network.RequestService;
import com.music.android.network.api.MyServerApi;
import com.music.android.ui.adapter.IndexAdapter;
import com.music.android.ui.widgets.NoNetWorkLayout;
import com.music.android.ui.widgets.RecyclerViewEmptySupport;
import com.music.android.utils.L;
import com.music.android.utils.NetWorkUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuyun on 17/3/10.
 */

public class StreamFragment extends BaseFragment implements OnItemClickListener, OnRefreshListener {

    private RecyclerViewEmptySupport mRecyclerView;

    private IndexAdapter mIndexAdapter;

    private NoNetWorkLayout mNoNetWorkLayout;

    private View loading_view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stream, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        mNoNetWorkLayout = (NoNetWorkLayout) view.findViewById(R.id.no_network_layout);
        mNoNetWorkLayout.setOnRefreshListener(this);
        loading_view = view.findViewById(R.id.loading_view);
        mRecyclerView = (RecyclerViewEmptySupport) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mIndexAdapter = new IndexAdapter(getContext());
        mIndexAdapter.setOnItemClickListener(this);
        mIndexAdapter.setOnAddFragmentListener(mOnAddFragmentListener);
        mRecyclerView.setAdapter(mIndexAdapter);

        loadData();
    }

    private void loadData() {
        if (NetWorkUtils.isNetworkConnected()) {
            loading_view.setVisibility(View.VISIBLE);
            mNoNetWorkLayout.setVisibility(View.GONE);
            getIndex();
        } else {
            loading_view.setVisibility(View.GONE);
            mNoNetWorkLayout.setVisibility(View.VISIBLE);
        }
    }

    private void getIndex() {
        Flowable<IndexInfoBean> mFlowable = RequestService.createRequestService().getIndex();
        mFlowable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Subscriber<IndexInfoBean>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(IndexInfoBean indexInfoBean) {
                Log.d("onError","IndexInfoBean=");
                if (indexInfoBean != null && indexInfoBean.code == 0 && indexInfoBean.data != null && indexInfoBean.data.size() > 0 && indexInfoBean.data.get(0).list != null) {
                    for (int i = 0; i < indexInfoBean.data.get(0).list.size(); i++) {
                        indexInfoBean.data.get(0).list.get(i).singer = indexInfoBean.data.get(0).list.get(i).user.username;
                    }
                    mIndexAdapter.setData(indexInfoBean.data);
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.d("onError","Throwable="+t.toString());
                loading_view.setVisibility(View.GONE);
                mNoNetWorkLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onComplete() {
                Log.d("onError","onComplete=");
                loading_view.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemClick(View view) {
        int position = (int) view.getTag();
        mOnAddFragmentListener.onAddFragment("hotFragment", position);
    }

    @Override
    public void onRefresh() {
        loadData();
    }
}
