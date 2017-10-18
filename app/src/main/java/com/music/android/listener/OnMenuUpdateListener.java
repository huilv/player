package com.music.android.listener;
/**
 * Created by hui.lv on 2017/3/22.
 */

public interface OnMenuUpdateListener {
    /**
     * 歌单名字修改成功
     * @param oldName 要修改的歌单名字  newName 修改之后的名字
     */
    void onMenuNameUpdateSuccess(String oldName,String newName);

    /**
     * 歌单删除成功
     * @param name
     */
    void onMenuDeleteSuccess(String name);

    /**
     * 失败
     */
    void onFailed();
}
