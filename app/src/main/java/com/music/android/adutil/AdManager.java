package com.music.android.adutil;

import android.content.Context;

import com.boot.sdk.allinone.AllInOneSdk;
import com.google.gson.Gson;
import com.music.android.network.UrlConst;
import com.music.android.utils.L;
import com.stat.analytics.AnalyticsSdk;

import java.util.Map;

import mobi.android.adlibrary.AdAgent;
import mobi.android.adlibrary.internal.dot.DotAdEventsListener;

/**
 * Created by CTer on 17/4/20.
 */

public class AdManager {

    public static boolean isInit = false;


    // http://169.55.74.167:15580  // For test
    public static final String AIO_CONFIG_BASE_URL = L.DEBUG ? "http://169.55.74.167:15580/" : UrlConst.CF_URL;
    public static final String AD_URL = L.DEBUG ? "http://169.55.74.167:12201" : UrlConst.AD_URL;
    public static void initAd(final Context context) {

        if (isInit) {
            return;
        }

        AllInOneSdk.Configuration inOneCfg = new AllInOneSdk.Configuration.Builder()
                .setDomainUrl(AIO_CONFIG_BASE_URL)
                .setPubId(UrlConst.PUB_ID)
                .setPubKey(UrlConst.PUB_KEY)
                .setAppWallSourceId(UrlConst.APP_WALL_SOURCE_ID)
                .build();
        AllInOneSdk.init(context, inOneCfg, new AllInOneSdk.AnalyticsProvider() {

            @Override
            public void sendEvent(String s, String s1, String s2, String s3, String s4, String s5) {
                AnalyticsSdk.getInstance(context).sendEvent(s, s1, s2, s3, s4, s5);
            }

            @Override
            public void sendCountableEvent(String s, String s1, String s2, int i) {
                AnalyticsSdk.getInstance(context).sendCountableEvent(s, s1, s2, i);
            }

            @Override
            public void onPageBegin(Object o, String s) {
                AnalyticsSdk.getInstance(context).onPageBegin(o, s);
            }

            @Override
            public void onPageEnd(Object o) {
                AnalyticsSdk.getInstance(context).onPageEnd(o);
            }
        });

        AdAgent.getInstance().init(context, AD_URL,  UrlConst.PUB_ID ,
                 "gp", "gp", new DotAdEventsListener() {

                    final Gson g = new Gson();

                    @Override
                    public void sendUAEvent(String s, String s1, Long aLong, Map<String, String> map, String s2) {

                    }
                });

        isInit = true;
    }



}
