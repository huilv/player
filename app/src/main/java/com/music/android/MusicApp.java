package com.music.android;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.bumptech.glide.request.target.ViewTarget;
import com.google.android.gms.update.UpdateSdk;
import com.music.android.adutil.AdManager;
import com.music.android.analytics.AnalyticsGA;
import com.music.android.analytics.AnalyticsLocal;
import com.music.android.analytics.AnalyticsManager;
import com.music.android.managers.permission.PermissionManager;
import com.music.android.network.UrlConst;
import com.stat.analytics.AnalyticsSdk;

import java.util.Iterator;

import mobi.andrutil.autolog.AnalyticsProvider;
import mobi.andrutil.autolog.AutologManager;

/**
 * Created by liuyun on 17/3/3.
 */

public class MusicApp extends MultiDexApplication {

    public static Application context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        initAd();
        initAnalytics();
        initUpdate();
        PermissionManager.init(this);
        ViewTarget.setTagId(R.id.imageloader_uri);

    }

    private void initAd() {
        AdManager.initAd(this);
    }

    private void initAnalytics() {
        AnalyticsManager.init(getApplicationContext());
        AnalyticsManager.getInstance().addAnalytics(new AnalyticsLocal(getApplicationContext()));
        AnalyticsManager.getInstance().addAnalytics(new AnalyticsGA(getApplicationContext()));
    }

    @Override
    public void attachBaseContext(Context base) {
        ActivityManager.RunningAppProcessInfo rail = takeCurrentRAPI(base);
        if (rail.processName.equals(base.getPackageName())) {
            initAutoLog(base);
        }
        super.attachBaseContext(base);
    }

    private ActivityManager.RunningAppProcessInfo takeCurrentRAPI(Context context) {
        Iterator<ActivityManager.RunningAppProcessInfo> iterator = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses()
                .iterator();
        while (iterator.hasNext()) {
            ActivityManager.RunningAppProcessInfo rai = iterator.next();
            if (rai.pid == android.os.Process.myPid()) {
                return rai;
            }
        }
        return null;
    }

    private void initAutoLog(Context base) {
        int bucketId = AnalyticsSdk.getBucketId(base);   // Note: 这里一定要用base，而不能用getApplicationContext()
        AutologManager.Config config = new AutologManager.Config();
        config.setAnalyticsProvider(bucketId, new AnalyticsProvider() {
            @Override
            public void sendEvent(String eventId, String label, String value, String extra, String eid) {
//                Analytics.sendEventWithCatEid(null, eventId, label, value, extra, eid);
                // Note: 在输入法应用中，由于历史原因，可能调用方法会略有不同
            }
        });
        config.setRouserEnable(true);
        config.setUninstallFeedbackHost(UrlConst.FEEDBACK_URL);
//        config.setMainActivity(MainActivity.class);    // Note: 可选，如果需要隐藏应用图标，则必须设置wkfc
        AutologManager.init(base, config);
    }

    private void initUpdate() {
        UpdateSdk.shared().setDebugMode(true);
        UpdateSdk.shared().init(this, "http://import.ad.vmusicfree.info/v3/config", UrlConst.PUB_ID, new UpdateSdk.AnalyticsProvider() {
            @Override
            public void sendEvent(String category, String action, String label, String value, String extra, String eid) {
                // do
            }
        });
    }

}
