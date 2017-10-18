package com.music.android.listener;

import com.music.android.bean.LoginResultBean;

/**
 * Created by hui.lv on 2017/5/15.
 */

public interface OnLoginCallBackListener {
    void onSuccess(LoginResultBean bean);
    void onCancel();
    void onError();
}
