package com.music.android.ui.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.music.android.base.BaseFragment;
import com.music.android.bean.MessageEventBean;
import com.music.android.utils.Constants;
import com.music.android.utils.L;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by liuyun on 17/4/6.
 */

public abstract class EventBusFragment extends BaseFragment {

    private static final String TAG = "EventBusFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerEventBus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterEventBus();
    }

    private void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    private void unRegisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEventBean eventBean) {
        if (eventBean.isSongChange) {
            onMusicChanged(eventBean);
        }
    }

    public abstract void onMusicChanged(MessageEventBean eventBean);

}
