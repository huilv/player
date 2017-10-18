package com.music.android.analytics;

import android.content.Context;

import java.util.Map;

/**
 * Created by CTer on 16/6/22.
 */
public interface IAnalytics {

    void sendEvent(final String category, final String action, final String label, final String value, final String extra);

    void sendEvent(final String category, final String action, final String label, final String value);

    void sendEvent(final String category, final String action, final String label);

    void onActivityStart(Context activity, String name);

    void onActivityStop(Context activity, String name);

    void onFragmentStart(String name);

    void onFragmentStop(String name);

    void setProperty(String name, String value);

    void setProperty(Map<String, String> properties);
}
