package com.music.android.analytics;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by CTer on 16/6/22.
 */
public class AnalyticsManager {

    private static AnalyticsManager mAnalyticsManager;
    private static Context mAppContext;
    private ArrayList<IAnalytics> analyticsArrayList = new ArrayList<>();


    private AnalyticsManager() {
    }

    public static void init(Context context) {
        mAppContext = context;
        if (mAnalyticsManager == null) {
            mAnalyticsManager = new AnalyticsManager();
        }
    }

    public void addAnalytics(IAnalytics analytics) {
        analyticsArrayList.add(analytics);
    }

    public void clearAnalytics() {
        analyticsArrayList.clear();
    }

    public static AnalyticsManager getInstance() {
        if (mAnalyticsManager == null) {
            throw new RuntimeException("Analytics need to be inited!");
        }
        return mAnalyticsManager;
    }

    public void sendEvent(String event_id ,String action, String label, String value, String extra) {
        for (IAnalytics oneAnalytics : analyticsArrayList) {
            if (oneAnalytics != null) {
                oneAnalytics.sendEvent(event_id ,action, label, value, extra);
            }
        }
    }

    public void sendEvent(String event_id ,String action, String label, String value) {
        for (IAnalytics oneAnalytics : analyticsArrayList) {
            if (oneAnalytics != null) {
                oneAnalytics.sendEvent(event_id ,action, label, value);
            }
        }
    }

    public void sendEvent(String event_id ,String action, String label) {
        for (IAnalytics oneAnalytics : analyticsArrayList) {
            if (oneAnalytics != null) {
                oneAnalytics.sendEvent(event_id, action, label);
            }
        }
    }


    public void onActivityStart(Context activity, String name) {
        for (IAnalytics oneAnalytics : analyticsArrayList) {
            if (oneAnalytics != null) {
                oneAnalytics.onActivityStart(activity, name);
            }
        }
    }

    public void onActivityStop(Context activity, String name) {
        for (IAnalytics oneAnalytics : analyticsArrayList) {
            if (oneAnalytics != null) {
                oneAnalytics.onActivityStop(activity, name);
            }
        }
    }

    public void onFragmentStart(String name) {
        for (IAnalytics oneAnalytics : analyticsArrayList) {
            if (oneAnalytics != null) {
                oneAnalytics.onFragmentStart(name);
            }
        }
    }

    public void onFragmentStop(String name) {
        for (IAnalytics oneAnalytics : analyticsArrayList) {
            if (oneAnalytics != null) {
                oneAnalytics.onFragmentStop(name);
            }
        }
    }

    public void setProperty(String name, String value) {
        for (IAnalytics oneAnalytics : analyticsArrayList) {
            if (oneAnalytics != null) {
                oneAnalytics.setProperty(name, value);
            }
        }
    }

    public void setProperty(Map<String, String> properties) {
        for (IAnalytics oneAnalytics : analyticsArrayList) {
            if (oneAnalytics != null) {
                oneAnalytics.setProperty(properties);
            }
        }
    }
}
