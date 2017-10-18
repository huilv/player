package com.music.android.ui.mvp.main.mysong;

import android.Manifest;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.music.android.R;
import com.music.android.base.BaseFragment;
import com.music.android.bean.CommonBean;
import com.music.android.listener.OnItemClickListener;
import com.music.android.managers.LocalMusicImpl;
import com.music.android.ui.adapter.FordAdapter;
import com.music.android.ui.mvp.main.MainActivity;
import com.music.android.utils.L;
import com.music.android.utils.LocalMusicUtils;
import com.music.android.utils.PermissionUtils;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

/**
 * Created by liuyun on 17/3/15.
 */

public class FordFragment extends BaseFragment implements OnItemClickListener {

    private static final String TAG = "FordFragment";
    private RecyclerView mRecyclerView;

    private FordAdapter mAdapter;

    public static FordFragment getInstants() {
        return new FordFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new FordAdapter(getContext());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (PermissionUtils.checkSelfPermission((MainActivity) getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            mLocalMusicImpl.getLocalArtistsOrFord();
        }
    }

    LocalMusicImpl<CommonBean> mLocalMusicImpl = new LocalMusicImpl<CommonBean>() {
        @Override
        protected void buildMapData(Cursor cursor, Map<String, CommonBean> commonBeanMap) {
            L.d(TAG,"buildMapData");
            LocalMusicUtils.getFord(cursor, commonBeanMap);
        }

        @Override
        protected CommonBean buildData(Cursor cursor) {
            L.d(TAG,"CommonBean");
            return null;
        }

        @Override
        public void getResult(List<CommonBean> list) {
            L.d(TAG,"getResult");
            mAdapter.setData(list);
        }
    };

    @Override
    public void onItemClick(View view) {
        CommonBean commonBean = (CommonBean) view.getTag();
        mOnAddFragmentListener.onAddFragment("mySongsListFragment", commonBean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Integer value) {
        if (value == 1 && mLocalMusicImpl != null) {
            mLocalMusicImpl.getLocalArtistsOrFord();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.d(TAG,"onDestroy");
    }
}
