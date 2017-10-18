package com.music.android.ui.mvp.main.search;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.base.BaseFragment;
import com.music.android.data.local.MusicLocalDataSource;
import com.music.android.listener.OnItemClickListener;
import com.music.android.managers.AbsADManager;
import com.music.android.managers.AnalyticsManager;
import com.music.android.managers.Constant;
import com.music.android.ui.adapter.HistoryAdapter;
import com.music.android.ui.adapter.MyFragmentPagerAdapter;
import com.music.android.ui.widgets.RoundIndicatorView;

import java.util.ArrayList;
import java.util.List;

import static com.music.android.R.id.editText;

/**
 * Created by liuyun on 17/3/13.
 */

public class SearchFragment extends BaseFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener, TextView.OnEditorActionListener, OnItemClickListener {

    private ImageView search_ImageView;

    private ImageView delete_ImageView;

    private EditText mEditText;

    private ImageView back_ImageView;

    private RadioGroup mRadioGroup;

    private RadioButton stream_radioButton;

    private RadioButton my_library_radioButton;

    private ViewPager mViewPager;

    private List<Fragment> fragments = new ArrayList<>();

    private SearchResultFragment streamFragment;

    private SearchResultFragment localFragment;

    private RoundIndicatorView mRoundIndicatorView;

    private RecyclerView mRecyclerView;

    private TextView clear_all_TextView;

    private HistoryAdapter mHistoryAdapter;

    private RelativeLayout history_RelativeLayout;

    private int position = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        initFragment();

        back_ImageView = (ImageView) view.findViewById(R.id.back_imageView);
        delete_ImageView = (ImageView) view.findViewById(R.id.delete_ImageView);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.tab_radioGroup);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragments));
        mViewPager.setCurrentItem(0);
        mEditText = (EditText) view.findViewById(editText);
        search_ImageView = (ImageView) view.findViewById(R.id.search_ImageView);
        stream_radioButton = (RadioButton) view.findViewById(R.id.stream_radioButton);
        my_library_radioButton = (RadioButton) view.findViewById(R.id.my_library_radioButton);
        mRoundIndicatorView = (RoundIndicatorView) view.findViewById(R.id.round_indication);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mHistoryAdapter = new HistoryAdapter(getContext());
        mRecyclerView.setAdapter(mHistoryAdapter);
        mHistoryAdapter.setOnItemClickListener(this);
        history_RelativeLayout = (RelativeLayout) view.findViewById(R.id.history_RelativeLayout);
        clear_all_TextView = (TextView) view.findViewById(R.id.clear_all_TextView);

        search_ImageView.setOnClickListener(this);
        back_ImageView.setOnClickListener(this);
        mRadioGroup.setOnCheckedChangeListener(this);
        mViewPager.addOnPageChangeListener(this);
        mEditText.setOnEditorActionListener(this);
        delete_ImageView.setOnClickListener(this);
        clear_all_TextView.setOnClickListener(this);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    history_RelativeLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        showKeyBoard();
        initHistory();
        loadAd();
    }

    private void loadAd() {

        new AbsADManager(Constant.SlotIdConstant.FULL_SCREEN_SEARCH_AD, true) {

            @Override
            public void onViewLoaded(View view) {

            }
        };

    }

    private void initHistory() {
        mHistoryAdapter.setData(MusicLocalDataSource.getInstance(MusicApp.context).querySearchHistory());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void searchData(String keyWord) {
        if (!TextUtils.isEmpty(keyWord)) {

            AnalyticsManager.getInstance().searchMusic(position);
            MusicLocalDataSource.getInstance(MusicApp.context).saveSearchHistory(keyWord);

            streamFragment.searchRemoteData(keyWord);
            localFragment.searchLocalData(keyWord);
            closeKeyboard();
            history_RelativeLayout.setVisibility(View.GONE);
        }
    }

    private void initFragment() {
        streamFragment = new SearchResultFragment();
        localFragment = new SearchResultFragment();
        fragments.add(streamFragment);
        fragments.add(localFragment);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_imageView:

                String keyWord = mEditText.getText().toString().trim();
                searchData(keyWord);
                break;
            case R.id.back_imageView:
                closeKeyboard();
                getActivity().onBackPressed();
                break;
            case R.id.delete_ImageView:
                if (mEditText != null) {
                    mEditText.setText("");
                }
                break;
            case R.id.clear_all_TextView:
                MusicLocalDataSource.getInstance(MusicApp.context).removeAllKeyWord();
                mHistoryAdapter.clear();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.stream_radioButton:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.my_library_radioButton:
                mViewPager.setCurrentItem(1);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mRoundIndicatorView.setOffset(positionOffset);
    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
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

    private void showKeyBoard() {
        if (mEditText != null) {
            mEditText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mEditText.setFocusable(true);
                    mEditText.setFocusableInTouchMode(true);
                    mEditText.requestFocus();
                    InputMethodManager inputManager =
                            (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(mEditText, 0);
                }
            },400);
        }
    }

    public void closeKeyboard() {
        if (mEditText != null) {
            InputMethodManager imm = (InputMethodManager) MusicApp.context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (mEditText != null) {
                String keyWord = mEditText.getText().toString().trim();
                searchData(keyWord);
            }
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(View view) {
        String keyWord = (String) view.getTag();
        mEditText.setText(keyWord);
        if(!TextUtils.isEmpty(keyWord)){
            mEditText.setSelection(keyWord.length());
        }
        searchData(keyWord);
    }
}
