package com.music.android.listener;
/**
 * Created by hui.lv on 2017/3/22.
 */

public interface OnMenuNameUpdateListener {
    /**
     *  修改成功
     * @param newName 修改之后的名字
     */
    void onSuccess(String newName);

    /**
     * 失败
     */
    void onFailed();

}
