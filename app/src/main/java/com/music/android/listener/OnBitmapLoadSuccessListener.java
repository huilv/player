package com.music.android.listener;

import android.graphics.Bitmap;

/**
 * Created by hui.lv on 2017/3/24.
 */

public interface OnBitmapLoadSuccessListener {

    /**
     *  图片加载成功
     */
    void onSuccess(Bitmap bitmap,String path);
    /**
     * 加载失败
     */
    void onFailed();
}
