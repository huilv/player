package com.music.android.ui.mvp.main;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.base.BaseActivity;
import com.music.android.config.AppConfigManager;
import com.music.android.ui.widgets.TipToast;
import com.music.android.utils.AnalyticsConstants;
import com.music.android.utils.AnalyticsUtils;
import com.music.android.utils.ColorStatusUtils;
import com.music.android.utils.Constants;
import com.music.android.utils.ShareHelper;
import com.music.android.utils.SharedPreferencesHelper;
import com.music.android.utils.SizeUtils;
import com.music.android.utils.StatusUtils;

import org.greenrobot.eventbus.EventBus;


public class SettingsActivity extends BaseActivity implements View.OnClickListener{

    private final String TAG="SettingsActivity";
    private ImageView imgToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusUtils.init(this);
        setContentView(R.layout.activity_settings);
        ColorStatusUtils.buildPadding(this);
        ImageView imgBack= (ImageView) findViewById(R.id.imgBack);
        imgToggle = (ImageView) findViewById(R.id.toggle);
        TextView feedBack= (TextView) findViewById(R.id.feedBack);
        TextView aboutUs= (TextView) findViewById(R.id.aboutUs);
        imgBack.setOnClickListener(this);
        imgToggle.setOnClickListener(this);
        feedBack.setOnClickListener(this);
        aboutUs.setOnClickListener(this);
        toggle();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgBack :
                finish();
                break;
            case R.id.toggle :
                if(SharedPreferencesHelper.getMusicFilter()){
                    AnalyticsUtils.vMusicClick(AnalyticsConstants.MENU, AnalyticsConstants.Action.CLICK_SETTING, AnalyticsConstants.Value.CLICK_IST_OFF);
                }else{
                    AnalyticsUtils.vMusicClick(AnalyticsConstants.MENU, AnalyticsConstants.Action.CLICK_SETTING, AnalyticsConstants.Value.CLICK_IST_ON);
                }
                SharedPreferencesHelper.setMusicFilter(!SharedPreferencesHelper.getMusicFilter());
                EventBus.getDefault().post(Constants.EventBusConstants.EVENT_DURATION_CHANGE);
                toggle();
                break;
            case R.id.feedBack :
                AnalyticsUtils.vMusicClick(AnalyticsConstants.MENU, AnalyticsConstants.Action.CLICK_SETTING, AnalyticsConstants.Value.CLICK_FEEDBACK);
                ShareHelper.startShare(this);
                break;

            case R.id.aboutUs :
                AnalyticsUtils.vMusicClick(AnalyticsConstants.MENU, AnalyticsConstants.Action.CLICK_SETTING, AnalyticsConstants.Value.CLICK_PRIVACY);
               startActivity(new Intent(this,AboutUsActivity.class));
                break;
            default:
                break;
        }
    }

    private void toggle() {
        boolean filter = SharedPreferencesHelper.getMusicFilter();
        if(filter){
            imgToggle.setImageResource(R.drawable.toggle_open);
            return;
        }
        imgToggle.setImageResource(R.drawable.toggle_close);
    }
}
