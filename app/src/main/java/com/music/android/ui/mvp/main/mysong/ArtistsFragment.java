package com.music.android.ui.mvp.main.mysong;

import android.Manifest;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
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
import com.music.android.ui.adapter.ArtistsAdapter;
import com.music.android.ui.mvp.main.MainActivity;
import com.music.android.utils.LocalMusicUtils;
import com.music.android.utils.PermissionUtils;


import java.util.List;
import java.util.Map;

/**
 * Created by liuyun on 17/3/15.
 */

public class ArtistsFragment extends BaseFragment implements OnItemClickListener {

    private RecyclerView recyclerView;
    private ArtistsAdapter mAdapter;

    public static ArtistsFragment getInstants() {
        return new ArtistsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ArtistsAdapter(getContext());
        mAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
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
            LocalMusicUtils.getArtists(cursor, commonBeanMap);
        }

        @Override
        protected CommonBean buildData(Cursor cursor) {
            return null;
        }

        @Override
        public void getResult(List<CommonBean> list) {
            mAdapter.setData(list);
        }
    };

    @Override
    public void onItemClick(View view) {
        CommonBean commonBean = (CommonBean) view.getTag();
        mOnAddFragmentListener.onAddFragment("mySongsListFragment", commonBean);
    }
}
