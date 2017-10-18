package com.music.android.listener;

import com.music.android.ui.widgets.SeekBarView;
import com.music.android.ui.widgets.SeekView;

/**
 * Created by Administrator on 2017/3/14.
 */

public interface OnProgressChangeListener {
    /**
     * 手势抬起
     * @param seekView 当前滑动对象
     * @param progress 当前进度 用百分数表示
     */
    void onUp(SeekBarView seekView, float progress);

    /**
     *  滑动
     * @param seekView 当前滑动对象
     * @param progress 当前进度 用百分数表示
     */
    void onMove(SeekBarView seekView, float progress);
}
