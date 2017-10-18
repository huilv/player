package com.music.android.listener;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;

/**
 * Created by hui.lv on 2017/3/31.
 */

public interface OnBitmapLoadListener {

    void onLoadFailed();

    void onResourceReady(GlideDrawable resource);

}
