package com.music.android.managers;

import android.util.Log;
import android.view.View;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.utils.L;
import com.music.android.utils.ScreenUtils;
import com.music.android.utils.SharedPreferencesHelper;
import com.music.android.utils.SizeUtils;

import mobi.android.adlibrary.AdAgent;
import mobi.android.adlibrary.internal.ad.Ad;
import mobi.android.adlibrary.internal.ad.AdError;
import mobi.android.adlibrary.internal.ad.IAd;
import mobi.android.adlibrary.internal.ad.OnAdLoadListener;
import mobi.android.adlibrary.internal.ad.WrapInterstitialAd;

/**
 * Created by liuyun on 17/6/1.
 */

public abstract class AbsADManager {

    private static long HOUR = 60 * 60 * 1000;

    public AbsADManager(String slotId, boolean isFullAD) {
        if (isFullAD) {
            long currentTime = System.currentTimeMillis();
            if (SharedPreferencesHelper.getPShowAdCurrentCount() <= 8 &&
                    currentTime - SharedPreferencesHelper.getADShowCurrentTime() > HOUR) {
                initAd(slotId);
            }
        } else {
            initAd(slotId);
        }

    }

    public AbsADManager(String slotId, final int layoutId, String value) {
        initAd(slotId, layoutId, value);
    }

    private void initAd(final String slotId) {
        int screenWidth = ScreenUtils.getScreenWidth(MusicApp.context);
        int dp = SizeUtils.px2Dp(MusicApp.context, screenWidth);
        L.d("initAd","dp+"+dp);
        Ad ad = (new Ad.Builder(MusicApp.context, slotId))
                .setWidth(dp)
                .setHight(80)
                .setParentViewGroup(null)
                .isPreLoad(false)
                .setTransparent(true)
                .setTitleColor(R.color.white)
                .setCtaBackground(R.color.ad_btn_bg)
                .setCtaTextColor(R.color.white)
                .build();
        loadAd(ad);
    }

    private void initAd(final String slotId, final int layoutId, String value) {
        int screenWidth = ScreenUtils.getScreenWidth(MusicApp.context);
        int with = SizeUtils.px2Dp(MusicApp.context, screenWidth);
        int w=with-24;
        Ad ad = (new Ad.Builder(MusicApp.context, slotId))
                .setWidth(w)
                .setHight(w*10/16)
                .setParentViewGroup(null)
                .setAppSelfLayout(layoutId)
                .isPreLoad(false)
                .setTransparent(true)
                .setTitleColor(R.color.white)
                .setCtaBackground(R.drawable.shape_download)
                .setCtaTextColor(R.color.white)
                .build();
        loadAd(ad);
    }


    private void loadAd(Ad ad) {
        AdAgent.getInstance().loadAd(MusicApp.context, ad, new OnAdLoadListener() {
            @Override
            public void onLoad(IAd iAd) {
                if (iAd != null && iAd.getAdView() != null) {
                    onViewLoaded(iAd.getAdView());
                }
            }

            @Override
            public void onLoadFailed(AdError adError) {
            }

            @Override
            public void onLoadInterstitialAd(WrapInterstitialAd wrapInterstitialAd) {
                if (wrapInterstitialAd != null) {
                    wrapInterstitialAd.show();

                    SharedPreferencesHelper.setAdShowCurrentTime(System.currentTimeMillis());
                    int count = SharedPreferencesHelper.getPShowAdCurrentCount() + 1;
                    SharedPreferencesHelper.setShowAdCurrentCount(count);
                }
            }
        });
    }

    public abstract void onViewLoaded(View view);

}
