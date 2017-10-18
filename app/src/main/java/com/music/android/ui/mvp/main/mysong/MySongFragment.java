package com.music.android.ui.mvp.main.mysong;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.music.android.R;
import com.music.android.base.BaseFragment;
import com.music.android.managers.AnalyticsManager;
import com.music.android.ui.adapter.MyFragmentPagerAdapter;
import com.music.android.ui.widgets.TabIndicatorView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liuyun on 17/3/13.
 */

public class MySongFragment extends BaseFragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager mViewPager;

    private List<Fragment> fragmentList = new ArrayList<>();

    private TextView tab_songs_textView, tab_artists_textView, tab_ford_textView;

    private RelativeLayout search_relativeLayout;

    private ImageView back_imageView;

    private TextView editText;

    private TabIndicatorView mTabIndicatorView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_song, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AnalyticsManager.getInstance().myLibraryMySongs(1);
        View view = getView();
        initFragment();
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList));
        mViewPager.setCurrentItem(0);
        editText = (TextView) view.findViewById(R.id.editText);
        editText.setOnClickListener(this);
        tab_songs_textView = (TextView) view.findViewById(R.id.tab_songs_textView);
        tab_artists_textView = (TextView) view.findViewById(R.id.tab_artists_textView);
        tab_ford_textView = (TextView) view.findViewById(R.id.tab_ford_textView);
        tab_songs_textView.getPaint().setFakeBoldText(true);
        search_relativeLayout = (RelativeLayout) view.findViewById(R.id.search_relativeLayout);
        back_imageView = (ImageView) view.findViewById(R.id.back_imageView);
        mTabIndicatorView = (TabIndicatorView) view.findViewById(R.id.tab_indicator);
        mTabIndicatorView.setPage(3);
        mViewPager.addOnPageChangeListener(this);
        tab_songs_textView.setOnClickListener(this);
        tab_artists_textView.setOnClickListener(this);
        tab_ford_textView.setOnClickListener(this);
        search_relativeLayout.setOnClickListener(this);
        back_imageView.setOnClickListener(this);
    }

    private void initFragment() {
        if (fragmentList.size() == 0) {
            MySongsListFragment mySongsListFragment = new MySongsListFragment();
            mySongsListFragment.setValue(null, null);
            mySongsListFragment.setOnAddFragmentListener(mOnAddFragmentListener);
            ArtistsFragment mArtistsFragment = ArtistsFragment.getInstants();
            mArtistsFragment.setOnAddFragmentListener(mOnAddFragmentListener);
            FordFragment mFordFragment = FordFragment.getInstants();
            mFordFragment.setOnAddFragmentListener(mOnAddFragmentListener);

            fragmentList.add(new MySongsListFragment());
            fragmentList.add(mArtistsFragment);
            fragmentList.add(mFordFragment);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mTabIndicatorView.setOffset(position + positionOffset);
    }

    @Override
    public void onPageSelected(int position) {
        selectTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void selectTab(int position) {
        AnalyticsManager.getInstance().myLibraryMySongs(position);

        tab_songs_textView.setTextSize(14);
        tab_songs_textView.setTextColor(getContext().getResources().getColor(R.color.radio_up));
        tab_songs_textView.getPaint().setFakeBoldText(false);
        tab_artists_textView.setTextSize(14);
        tab_artists_textView.setTextColor(getContext().getResources().getColor(R.color.radio_up));
        tab_artists_textView.getPaint().setFakeBoldText(false);
        tab_ford_textView.setTextSize(14);
        tab_ford_textView.setTextColor(getContext().getResources().getColor(R.color.radio_up));
        tab_ford_textView.getPaint().setFakeBoldText(false);

        switch (position) {
            case 0:
                tab_songs_textView.setTextSize(15);
                tab_songs_textView.setTextColor(getContext().getResources().getColor(R.color.white));
                tab_songs_textView.getPaint().setFakeBoldText(true);
                break;
            case 1:
                tab_artists_textView.setTextSize(15);
                tab_artists_textView.setTextColor(getContext().getResources().getColor(R.color.white));
                tab_artists_textView.getPaint().setFakeBoldText(true);
                break;
            case 2:
                tab_ford_textView.setTextSize(15);
                tab_ford_textView.setTextColor(getContext().getResources().getColor(R.color.white));
                tab_ford_textView.getPaint().setFakeBoldText(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editText:
                AnalyticsManager.getInstance().searchEnter(2);
                mOnAddFragmentListener.onAddFragment("searchFragment", null);
                break;
            case R.id.back_imageView:
                getActivity().onBackPressed();
                break;
            case R.id.tab_songs_textView:
                selectTab(0);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tab_artists_textView:
                selectTab(1);
                mViewPager.setCurrentItem(1);
                break;
            case R.id.tab_ford_textView:
                selectTab(2);
                mViewPager.setCurrentItem(2);
                break;
        }
    }

}
