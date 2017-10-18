package com.music.android.utils;

import com.music.android.analytics.AnalyticsManager;

/**
 * Created by hui.lv on 17/4/26.
 */
public class AnalyticsUtils {

    public static void vMusicClick(String category, String action, String value) {
        AnalyticsManager.getInstance().sendEvent(category, action, value);
    }

}
