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

import com.music.android.R;
import com.music.android.base.BaseFragment;
import com.music.android.bean.RankInfoBean;
import com.music.android.listener.OnItemClickListener;
import com.music.android.listener.OnRefreshListener;
import com.music.android.managers.AbsADManager;
import com.music.android.managers.Constant;
import com.music.android.network.RequestService;
import com.music.android.ui.adapter.RankAdapter;
import com.music.android.ui.widgets.NoNetWorkLayout;
import com.music.android.utils.L;
import com.music.android.utils.NetWorkUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuyun on 17/3/21.
 */

public class RankFragment extends BaseFragment implements OnItemClickListener, View.OnClickListener {

    private RecyclerView mRecyclerView;

    private TextView title_TextView;

    private RankAdapter mRankAdapter;

    private ImageView back_ImageView;

    private View loadingView;

    private NoNetWorkLayout no_network_layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rank, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final View view = getView();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRankAdapter = new RankAdapter(getContext());
        mRankAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mRankAdapter);

        loadingView = view.findViewById(R.id.loading_view);
        no_network_layout = (NoNetWorkLayout) view.findViewById(R.id.no_network_layout);
        no_network_layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        title_TextView = (TextView) view.findViewById(R.id.title_TextView);
        title_TextView.setText(getContext().getResources().getString(R.string.rank));
        back_ImageView = (ImageView) view.findViewById(R.id.back_ImageView);
        back_ImageView.setOnClickListener(this);

        loadData();

        loadAd();

    }

    private void loadData() {
        if (NetWorkUtils.isNetworkConnected()) {
            loadingView.setVisibility(View.VISIBLE);
            no_network_layout.setVisibility(View.GONE);
            getRankIndex();
        } else {
            loadingView.setVisibility(View.GONE);
            no_network_layout.setVisibility(View.VISIBLE);
        }
    }

    private void getRankIndex() {
        Flowable<RankInfoBean> mFlowable = RequestService.createRequestService().getRankIndex();
        mFlowable.delay(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Subscriber<RankInfoBean>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(RankInfoBean rankInfoBean) {
                if (rankInfoBean != null && mRankAdapter.data != null) {
                    L.d("onNext","size="+rankInfoBean.data.size());
                    rankInfoBean.data.add(0, new RankInfoBean.DataBean());
                    mRankAdapter.setData(rankInfoBean.data);
                }
            }

            @Override
            public void onError(Throwable t) {
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onComplete() {
                loadingView.setVisibility(View.GONE);
            }
        });
    }

    private void loadAd() {

        new AbsADManager(Constant.SlotIdConstant.RANK_AD, false) {
            @Override
            public void onViewLoaded(View view) {
                if (mRankAdapter != null && view != null) {
                    mRankAdapter.setAdView(view);
                }
            }
        };

    }

    @Override
    public void onItemClick(View view) {
        mOnAddFragmentListener.onAddFragment("rankFragment", view.getTag());
    }

    @Override
    public void onClick(View v) {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }
}
