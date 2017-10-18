package com.music.android.ui.mvp.main.mysong;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.bean.MessageEventBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.bean.PlaylistNameBean;
import com.music.android.data.local.FavoriteSongHelper;
import com.music.android.data.local.MusicLocalDataSource;
import com.music.android.managers.AbsADManager;
import com.music.android.managers.AnalyticsManager;
import com.music.android.managers.Constant;
import com.music.android.managers.LocalMusicImpl;
import com.music.android.service.MusicPlayService;
import com.music.android.ui.adapter.LocalMusicAdapter;
import com.music.android.ui.mvp.EventBusFragment;
import com.music.android.ui.mvp.main.MainActivity;
import com.music.android.utils.Constants;
import com.music.android.utils.LocalMusicUtils;
import com.music.android.utils.PermissionUtils;
import com.music.android.utils.PlayHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;


/**
 * Created by liuyun on 17/3/15.
 */

public class MySongsListFragment extends EventBusFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "MySongsListFragment";

    private RecyclerView mRecyclerView;

    private LocalMusicAdapter mAdapter;

    private String mFlag;

    private String mKeyWord;

    private RelativeLayout header_view;

    private LinearLayout check_LinearLayout;

    private LinearLayout delete_LinearLayout;

    private RelativeLayout delete_RelativeLayout;

    private CheckBox mCheckbox;

    private List<MusicInfoBean> data = new ArrayList<>();

    private PlaylistNameBean playlistNameBean;

    public void setValue(String flag, String keyWord) {
        mFlag = flag;
        mKeyWord = keyWord;
    }

    public void setData(ArrayList<MusicInfoBean> data) {
        this.data.clear();
        if (data.size() == 1 && data.get(0).path == null) {
            this.data.addAll(data.subList(1, data.size()));
        } else {
            this.data.addAll(data);
        }
        if (PlayHelper.getInstance().isServiceExist() && MusicPlayService.getCurrentMusic() != null) {
            MusicInfoBean musicInfoBean = MusicPlayService.getCurrentMusic();
            updateData(data, musicInfoBean.path);
        }

        this.data.add(0, new MusicInfoBean());
        AnalyticsManager.getInstance().myLibrary(2);
    }

    public void setPlaylistNameBean(PlaylistNameBean playlistNameBean) {
        this.playlistNameBean = playlistNameBean;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_common, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        ImageView back_ImageView = (ImageView) view.findViewById(R.id.back_ImageView);
        back_ImageView.setOnClickListener(this);
        delete_RelativeLayout = (RelativeLayout) view.findViewById(R.id.delete_RelativeLayout);
        check_LinearLayout = (LinearLayout) view.findViewById(R.id.check_LinearLayout);
        mCheckbox = (CheckBox) view.findViewById(R.id.checkbox);
        mCheckbox.setOnCheckedChangeListener(this);
        delete_LinearLayout = (LinearLayout) view.findViewById(R.id.delete_LinearLayout);
        header_view = (RelativeLayout) view.findViewById(R.id.title_bar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new LocalMusicAdapter(getContext(),mFlag);
        mRecyclerView.setAdapter(mAdapter);
        showHeaderView(view);
        initData();
        initListener();

    }

    private void showHeaderView(View view) {
        if (mFlag == null && mKeyWord == null) {
            header_view.setVisibility(View.GONE);
        } else {
            header_view.setVisibility(View.VISIBLE);
            TextView title_TextView = (TextView) view.findViewById(R.id.title_TextView);
            title_TextView.setText(mKeyWord);
        }
    }

    private void initListener() {
        mAdapter.setOnDeleteViewShowListener(new LocalMusicAdapter.OnDeleteViewShowListener() {
            @Override
            public void onDeleteViewShow(boolean isShown) {
                if (isShown) {
                    delete_RelativeLayout.setVisibility(View.VISIBLE);
                } else {
                    delete_RelativeLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void smoothScrollToPosition(int position, int maxPosition) {
                if (position > 4 && maxPosition > position + 4) {
                    mRecyclerView.scrollToPosition(position + 4);
                } else if (maxPosition <= position + 4) {
                    mRecyclerView.scrollToPosition(maxPosition - 1);
                } else {
                    mRecyclerView.scrollToPosition(0);
                }
            }
        });

        delete_RelativeLayout.setOnClickListener(this);
        check_LinearLayout.setOnClickListener(this);
        delete_LinearLayout.setOnClickListener(this);
    }

    private void loadAd() {

        new AbsADManager(Constant.SlotIdConstant.MY_SONGS_AD, false) {
            @Override
            public void onViewLoaded(View view) {
                if (mAdapter != null && view != null) {
                    mAdapter.setAdView(view);
                }
            }
        };

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initData() {
        if (mFlag != null && mFlag.equals("Favorites")) {
            mAdapter.setFlag(mFlag);
            getFavoritesLocalMusic();
        } else if (mFlag != null && mFlag.equals("RecentlyPlay")) {
            mAdapter.setFlag(mFlag);
            getRecentlyPlayData();
        } else if (mFlag != null && mFlag.equals("playList")) {  //某个歌单里面的歌曲
            getPlaylistMusics();
        } else {//My Songs 里面里面的 null singer folder
            mAdapter.setFlag("all");
            if (getActivity()!=null&&PermissionUtils.checkSelfPermission((MainActivity) getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                mLocalMusicImpl.getLocalSongs();
            }
        }
    }


    LocalMusicImpl<MusicInfoBean> mLocalMusicImpl = new LocalMusicImpl<MusicInfoBean>() {

        @Override
        protected void buildMapData(Cursor cursor, Map<String, MusicInfoBean> commonBeanMap) {

        }

        @Override
        protected MusicInfoBean buildData(Cursor cursor) {
            if (mFlag == null || mKeyWord == null) {
                return LocalMusicUtils.getAllSong(cursor);
            } else {
                if (mFlag.equals("singer")) {
                    return LocalMusicUtils.getAllSong(cursor, mKeyWord);
                } else {
                    return LocalMusicUtils.getAllSongByFord(cursor, mKeyWord);
                }
            }
        }

        @Override
        public void getResult(List<MusicInfoBean> list) {
            if (MusicPlayService.getMusics().size() > 0) {
                MusicInfoBean musicInfoBean = MusicPlayService.getCurrentMusic();
                updateData(list, musicInfoBean.path);
            }
            list.add(0, new MusicInfoBean());
            mAdapter.setData(list);
            loadAd();
        }
    };


    private void getRecentlyPlayData() {
        AnalyticsManager.getInstance().myLibrary(1);
        List<MusicInfoBean> musics = MusicLocalDataSource.getInstance(getContext()).getRecentlyMusics();
        if (MusicPlayService.getMusics().size() > 0 && MusicPlayService.getMusics().size() > 0) {
            MusicInfoBean musicInfoBean = MusicPlayService.getCurrentMusic();
            updateData(musics, musicInfoBean.path);
        }
        Collections.reverse(musics);
        musics.add(0, new MusicInfoBean());
        mAdapter.setData(musics);
        loadAd();
    }

    private void getFavoritesLocalMusic() {
        AnalyticsManager.getInstance().myLibrary(0);
        List<MusicInfoBean> musics = FavoriteSongHelper.getDefault(getContext()).query();
        if (PlayHelper.getInstance().isServiceExist()) {
            updateData(musics, MusicPlayService.getCurrentMusic().path);
        }
        musics.add(0, new MusicInfoBean());
        mAdapter.setData(musics);
        loadAd();
    }


    private void getPlaylistMusics() {
        if (playlistNameBean != null) {
            mAdapter.setFlag("playList");
            mAdapter.setMenuName(mKeyWord);
            MusicLocalDataSource.getInstance(MusicApp.context).getPlaylistMusics(String.valueOf(playlistNameBean.nameId)).subscribe(new Consumer<List<MusicInfoBean>>() {
                @Override
                public void accept(List<MusicInfoBean> musics) throws Exception {
                    if (PlayHelper.getInstance().isServiceExist()) {
                        updateData(musics, MusicPlayService.getCurrentMusic().path);
                    }
                    musics.add(0, new MusicInfoBean());
                    mAdapter.setMenuName(String.valueOf(playlistNameBean.nameId));
                    mAdapter.setData(musics);
                    loadAd();

                }
            });
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onMusicChanged(MessageEventBean eventBean) {
        updateData(mAdapter.getData(), eventBean.path);
        mAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUIChange(Integer integer) {
        if (integer == Constants.EventBusConstants.EVENT_INFO_UPDATE) {
            initData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_LinearLayout:
                selectAll();
                break;
            case R.id.delete_LinearLayout:
                mAdapter.removeCheckedItem();
                break;
            case R.id.back_ImageView:
                getActivity().onBackPressed();
                break;
            case R.id.delete_RelativeLayout:
                break;
        }
    }


    private void selectAll() {
        boolean isChecked = mCheckbox.isChecked();
        checkAll(isChecked);
        mCheckbox.setChecked(!isChecked);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checkAll(isChecked);
    }

    private void checkAll(boolean isChecked) {
        for (int i = 0; i < mAdapter.getData().size(); i++) {
            MusicInfoBean musicInfoBean = mAdapter.getData().get(i);
            musicInfoBean.isChecked = isChecked;
        }
        mAdapter.notifyDataSetChanged();
    }
}
