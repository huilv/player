package com.music.android.ui.mvp.main.search;

/**
 * Created by liuyun on 17/4/17.
 */

public class SearchResultPresenter implements SearchResultContract.Presenter {

    private final SearchResultContract.View mSearchResultView = null;

    public SearchResultPresenter(SearchResultContract.View searchResultView) {
        mSearchResultView.setPresenter(this);
    }

    @Override
    public void start() {

    }


    @Override
    public void setKeyWord(String keyWord) {

    }

    @Override
    public void loadLocalData() {

    }

    @Override
    public void loadRemoteData() {

    }
}
