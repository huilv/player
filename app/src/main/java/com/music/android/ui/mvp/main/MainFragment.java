package com.music.android.ui.mvp.main;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.music.android.R;
import com.music.android.base.BaseFragment;
import com.music.android.managers.AnalyticsManager;
import com.music.android.ui.adapter.MyFragmentPagerAdapter;
import com.music.android.ui.mvp.main.mylibrary.MyLibraryFragment;
import com.music.android.ui.mvp.main.stream.StreamFragment;
import com.music.android.ui.widgets.TabIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyun on 17/3/10.
 */

public class MainFragment extends BaseFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private ViewPager mViewPager;

    private MyLibraryFragment myLibraryFragment;

    private StreamFragment streamFragment;

    private List<Fragment> fragmentList;

    private RelativeLayout search_relativeLayout;

    private RadioGroup tab_radioGroup;

    private TabIndicatorView tab_indicator;

    private RadioButton stream_radioButton, my_library_radioButton;

    private ImageView sidebar_ImageView;

    private View rootView;

    private int position = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rootView = getView();
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        search_relativeLayout = (RelativeLayout) rootView.findViewById(R.id.search_relativeLayout);
        search_relativeLayout.setOnClickListener(this);
        tab_radioGroup = (RadioGroup) rootView.findViewById(R.id.tab_radioGroup);
        tab_radioGroup.setOnCheckedChangeListener(this);
        tab_indicator = (TabIndicatorView) rootView.findViewById(R.id.tab_indicator);
        tab_indicator.setPage(2);
        stream_radioButton = (RadioButton) rootView.findViewById(R.id.stream_radioGroup);
        my_library_radioButton = (RadioButton) rootView.findViewById(R.id.my_library_radioGroup);
        sidebar_ImageView = (ImageView) rootView.findViewById(R.id.sidebar_ImageView);
        sidebar_ImageView.setOnClickListener(this);
        initFragment();
    }

    private void initFragment() {
        fragmentList = new ArrayList<>();
        myLibraryFragment = new MyLibraryFragment();
        myLibraryFragment.setOnAddFragmentListener(mOnAddFragmentListener);
        streamFragment = new StreamFragment();
        streamFragment.setOnAddFragmentListener(mOnAddFragmentListener);
        fragmentList.add(streamFragment);
        fragmentList.add(myLibraryFragment);
        mViewPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList));
//        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tab_indicator.setOffset(positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                MainFragment.this.position = position;
                switch (position) {
                    case 0:
                        stream_radioButton.setChecked(true);
                        my_library_radioButton.setChecked(false);

                        break;
                    case 1:
                        stream_radioButton.setChecked(false);
                        my_library_radioButton.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getChildFragmentManager().getBackStackEntryCount() == 1) {
            ((MainActivity) getActivity()).setDrawerSate(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_relativeLayout:
                AnalyticsManager.getInstance().searchEnter(position);
                mOnAddFragmentListener.onAddFragment("searchFragment", null);
                break;
            case R.id.sidebar_ImageView:
                ((MainActivity) getActivity()).openDrawer();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        int nextPosition = 0;
        switch (checkedId) {
            case R.id.stream_radioGroup:
                nextPosition = 0;
//                stream_radioButton.setTextSize(17.5f);
//                my_library_radioButton.setTextSize(16.5f);
                break;
            case R.id.my_library_radioGroup:
                nextPosition = 1;
//                stream_radioButton.setTextSize(16.5f);
//                my_library_radioButton.setTextSize(17.5f);
                break;
        }
        int currentItem = mViewPager.getCurrentItem();
        if (Math.abs(currentItem - nextPosition) == 1) {
            mViewPager.setCurrentItem(nextPosition, true);
        } else {
            mViewPager.setCurrentItem(nextPosition, false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
