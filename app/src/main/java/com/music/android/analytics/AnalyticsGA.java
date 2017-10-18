package com.music.android.analytics;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.music.android.R;

import java.util.Map;

/**
 * Created by CTer on 17/4/26.
 */

public class AnalyticsGA implements IAnalytics {

    private Tracker mTracker;

    public AnalyticsGA(Context context) {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
    }

    @Override
    public void sendEvent(String category, String action, String label, String value, String extra) {
        if (mTracker != null) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .setValue(Long.valueOf(value))
                    .build());
        }
    }

    @Override
    public void sendEvent(String category, String action, String label, String value) {
        if (mTracker != null) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .setValue(Long.valueOf(value))
                    .build());
        }
    }

    @Override
    public void sendEvent(String category, String action, String label) {
        if (mTracker != null) {
            sendEvent(category, action, label, "0");
        }
    }

    @Override
    public void onActivityStart(Context activity, String name) {
        if (mTracker != null) {
            mTracker.setScreenName(name);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public void onActivityStop(Context activity, String name) {
        if (mTracker != null) {

        }
    }

    @Override
    public void onFragmentStart(String name) {
        if (mTracker != null) {
            mTracker.setScreenName(name);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public void onFragmentStop(String name) {
        if (mTracker != null) {

        }
    }

    @Override
    public void setProperty(String name, String value) {
        if (mTracker != null) {

        }
    }

    @Override
    public void setProperty(Map<String, String> properties) {
        if (mTracker != null) {

        }
    }
}
