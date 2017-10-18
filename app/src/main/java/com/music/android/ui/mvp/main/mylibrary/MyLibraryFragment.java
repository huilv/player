package com.music.android.ui.mvp.main.mylibrary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.base.BaseFragment;
import com.music.android.bean.PlaylistNameBean;
import com.music.android.bean.SongMenuBean;
import com.music.android.data.local.MusicLocalDataSource;
import com.music.android.data.local.SongMenuHelper;
import com.music.android.ui.adapter.PlayList2Adapter;
import com.music.android.ui.mvp.main.OnAddFragmentListener;
import com.music.android.utils.Constants;
import com.music.android.utils.L;
import com.music.android.utils.SharedPreferencesHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * Created by liuyun on 17/3/10.
 */

public class MyLibraryFragment extends BaseFragment {

    private RecyclerView mRecyclerView;

    private PlayList2Adapter mPlayList2Adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_library, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        View view = getView();
        if (SharedPreferencesHelper.isFirstUpdate()) {
            updatePlaylistData();
            SharedPreferencesHelper.setFirstUpdate();
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPlayList2Adapter = new PlayList2Adapter(getContext(), getChildFragmentManager());
        mRecyclerView.setAdapter(mPlayList2Adapter);
        mPlayList2Adapter.setOnAddFragmentListener(mOnAddFragmentListener);
    }

    private void updatePlaylistData() {
        SongMenuHelper.getDefault(MusicApp.context).queryAllSortSongs().subscribe(new Consumer<ArrayList<SongMenuBean>>() {
            @Override
            public void accept(ArrayList<SongMenuBean> list) throws Exception {
                String menuName = "";
                for (int i = 0; i < list.size(); i++) {
                    SongMenuBean songMenuBean = list.get(i);
                    if (songMenuBean.menuName.equals(menuName)) {
                        long nameId = MusicLocalDataSource.getInstance(MusicApp.context).getPlaylistNameId(songMenuBean.menuName);
                        MusicLocalDataSource.getInstance(MusicApp.context).insertPlaylistJunction(String.valueOf(nameId), String.valueOf(songMenuBean.id));
                    } else {
                        if (songMenuBean.path == null || songMenuBean.path.equals("")) {
                            MusicLocalDataSource.getInstance(MusicApp.context).insertPlaylistName(songMenuBean.menuName);
                        } else {
                            MusicLocalDataSource.getInstance(MusicApp.context).insertPlaylistNameAndMusic(songMenuBean);
                        }
                    }
                    menuName = songMenuBean.menuName;
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlayList2Adapter.updateUI();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Integer value) {
        if (value == Constants.EventBusConstants.EVENT_UPDATE_UI && mPlayList2Adapter != null) {
            mPlayList2Adapter.updateUI();
        }
    }
}
