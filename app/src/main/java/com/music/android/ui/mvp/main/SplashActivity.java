package com.music.android.ui.mvp.main;

import android.content.Intent;
import android.os.Bundle;

import com.music.android.base.BaseActivity;

/**
 * Created by Administrator on 2017/4/21.
 */

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
