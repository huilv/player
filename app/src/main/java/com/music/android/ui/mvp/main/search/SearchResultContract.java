package com.music.android.ui.mvp.main.search;

import com.music.android.base.BasePresenter;
import com.music.android.base.BaseView;

/**
 * Created by liuyun on 17/4/17.
 */

public interface SearchResultContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {

        void setKeyWord(String keyWord);

        void loadLocalData();

        void loadRemoteData();
    }
}
