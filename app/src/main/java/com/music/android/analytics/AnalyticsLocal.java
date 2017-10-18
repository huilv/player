package com.music.android.analytics;

import android.content.Context;

import com.music.android.BuildConfig;
import com.music.android.network.UrlConst;
import com.music.android.utils.L;
import com.stat.analytics.AnalyticsSdk;

import java.util.Map;

/**
 * Created by CTer on 16/6/28.
 */
public class AnalyticsLocal implements IAnalytics {

    private AnalyticsSdk analyticsSdk;


    public static final String ANALYTICS_URL = BuildConfig.DEBUG ? "http://192.168.5.222:11011" : UrlConst.STT_URL;

    public AnalyticsLocal(Context context) {

        String url = ANALYTICS_URL;
        String trafficId = UrlConst.TID;
        String channel = "";
        String installChannel = "PUSH";
        String source = "0";
        AnalyticsSdk.Configuration.Builder builder = new AnalyticsSdk.Configuration.Builder();
        builder.setAnalyticsUrl(url);
        builder.setTrafficId(trafficId);
        builder.setChannel(channel);
        builder.setInstallChannel(installChannel);
        builder.setSource(source);
        builder.setMtaEventTranslator(AnalyticsSdk.MTA_EVENT_TRANSLATOR_SIMPLE);
        builder.setCategoryCanBeEmpty(true);
        builder.setBuglyAppId(UrlConst.BUGLY_ID)
                .setBuglyDebugMode(false)
                .setAppsFlyerKey(UrlConst.APPS_FLYER_KEY)
                .setReferrer("nature");
        analyticsSdk = AnalyticsSdk.getInstance(context);
        analyticsSdk.setDebugMode(L.DEBUG);
        analyticsSdk.init(builder.build());

    }

    @Override
    public void sendEvent(String category, String action, String label, String value, String extra) {
        analyticsSdk.sendEvent(category, action, label, value, extra);
    }

    @Override
    public void sendEvent(String category, String action, String label, String value) {
        analyticsSdk.sendEvent(category, action, label, value);
    }

    @Override
    public void sendEvent(String category, String action, String label) {
        analyticsSdk.sendEvent(category, action, label, "0");
    }

    @Override
    public void onActivityStart(Context activity, String name) {
        analyticsSdk.onPageBegin(name);
        analyticsSdk.sendRealActive();
    }

    @Override
    public void onActivityStop(Context activity, String name) {
        analyticsSdk.onPageEnd(name);
    }

    @Override
    public void onFragmentStart(String name) {
        analyticsSdk.onPageBegin(name);
    }

    @Override
    public void onFragmentStop(String name) {
        analyticsSdk.onPageEnd(name);
    }

    public void setProperty(String name, String value) {
        analyticsSdk.setProperty(name, value);
    }

    public void setProperty(Map<String, String> properties) {
        analyticsSdk.setProperties(properties);
    }
}
