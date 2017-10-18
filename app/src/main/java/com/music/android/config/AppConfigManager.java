package com.music.android.config;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.music.android.MusicApp;
import com.music.android.bean.ConfigData;
import com.music.android.network.UrlConst;
import com.music.android.utils.PrefUtils;
import com.music.android.utils.SharedPreferencesHelper;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by CTer on 17/4/17.
 */

public class AppConfigManager {

    public static final String LAST_CONFIG_TIME = "last_config_time";
    public static final String PUB_ID = "10004";
    public static final String AIO_CONFIG_BASE_URL = UrlConst.CF_URL;

    public static final long HOUR = 60 * 60 * 1000;

    public static void getConfig() {
        long last_time = PrefUtils.getLong(MusicApp.context, LAST_CONFIG_TIME, 0);

        if (System.currentTimeMillis() - last_time < 24 * HOUR) {
            return;
        }
        checkUpdate(null);
    }

    public static void checkUpdate(final OnCheckVersionListener mOnCheckVersionListener) {
        createConfigApi().getAppConfig(PUB_ID, "01", MusicApp.context.getPackageName(),
                getVersion(), "1", "android", "app")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ConfigData>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(ConfigData configData) {
                        if (configData.getData().last_version != null) {
                            SharedPreferencesHelper.setVersionCode(configData.getData().last_version);
                            PrefUtils.putLong(MusicApp.context, LAST_CONFIG_TIME, System.currentTimeMillis());
                            if (mOnCheckVersionListener != null) {
                                mOnCheckVersionListener.onCheck();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (mOnCheckVersionListener != null) {
                            mOnCheckVersionListener.onError();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion() {
        try {
            PackageManager manager = MusicApp.context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(MusicApp.context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ConfigApi createConfigApi() {

        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(AIO_CONFIG_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();


        return retrofit.create(ConfigApi.class);
    }

    public interface OnCheckVersionListener {
        void onCheck();

        void onError();

    }

}
