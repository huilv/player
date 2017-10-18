package com.music.android.base;

import android.support.v4.app.Fragment;

import com.music.android.ui.mvp.main.OnAddFragmentListener;

/**
 * Created by liuyun on 17/3/3.
 */

public class BaseFragment extends Fragment {
    public OnAddFragmentListener mOnAddFragmentListener;

    public void setOnAddFragmentListener(OnAddFragmentListener mOnAddFragmentListener) {
        this.mOnAddFragmentListener = mOnAddFragmentListener;
    }
}
