package com.music.android.listener;

import android.graphics.Bitmap;

/**
 * Created by liuyun on 17/6/2.
 */

public interface OnBitmapListener {

    void onLoadFailed();

    void onResourceReady(Bitmap resource);

}
