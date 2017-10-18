package com.music.android.ui.mvp.main;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.music.android.base.BaseFragment;
import com.music.android.bean.MessageEventBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.ui.widgets.playinglayout.QuickControllerLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by liuyun on 17/4/25.
 */

public class ControlsFragment extends BaseFragment implements View.OnClickListener, SlidingUpPanelLayout.PanelSlideListener{

    private QuickControllerLayout mControllerView;

    private boolean isExpanded = false;

    private WeakReference<SlidingUpPanelLayout> slidingUpPanelLayoutWeakReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new QuickControllerLayout(getContext());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mControllerView = (QuickControllerLayout) getView();
        mControllerView.setContextActivity(getActivity());
        mControllerView.setExpanded(isExpanded);
        EventBus.getDefault().register(this);
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
        if (mControllerView != null) {
            mControllerView.setExpanded(isExpanded);
        }
    }

    public void setSlidingUpPanelLayoutWeakReference(SlidingUpPanelLayout slidingUpPanelLayoutWeakReference) {
        this.slidingUpPanelLayoutWeakReference = new WeakReference<>(slidingUpPanelLayoutWeakReference);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private int position = -1;

    private boolean isPlaying = false;

    private ArrayList<MusicInfoBean> musics = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEventBean eventBean) {
        if ((position == -1 || eventBean.isSongChange) && mControllerView != null) {
            mControllerView.setProgress(0, 100);
            mControllerView.setData(eventBean);
            mControllerView.showUI();
            position = eventBean.currentPosition;
            if (slidingUpPanelLayoutWeakReference != null) {
                SlidingUpPanelLayout layout = slidingUpPanelLayoutWeakReference.get();
                if (layout != null) {
                    layout.setTouchEnabled(true);
                }
            }
        }

        if (isPlaying != eventBean.onPlaying && mControllerView != null) {
            mControllerView.isPlaying(eventBean.onPlaying);
            isPlaying = eventBean.onPlaying;
        }

        if (!eventBean.isServiceExist && mControllerView != null) {
            musics.clear();
            musics.addAll(eventBean.list);
            mControllerView.savePosition(eventBean.currentPosition, eventBean.currentProgress);
            mControllerView.setMusic(musics);
        }

        if (mControllerView != null) {
            mControllerView.setProgress(eventBean.currentProgress, eventBean.duration);
        }

        saveMusicInfoBean(eventBean);

    }

    MusicInfoBean musicInfoBean = new MusicInfoBean();

    private void saveMusicInfoBean(MessageEventBean eventBean) {
        musicInfoBean.title = eventBean.musicName;
        musicInfoBean.duration = (int) eventBean.duration;
        musicInfoBean.singer = eventBean.authorName;
        musicInfoBean.collection = -1;
        musicInfoBean.path = eventBean.path;
        musicInfoBean.artwork_url = eventBean.imgUrl;
        musicInfoBean.currentProgress = eventBean.currentProgress;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        if (mControllerView != null) {
            mControllerView.onPanelSlide(panel, slideOffset);
        }
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        if (mControllerView != null) {
            mControllerView.onPanelStateChanged(panel, previousState, newState);
        }
    }
}
