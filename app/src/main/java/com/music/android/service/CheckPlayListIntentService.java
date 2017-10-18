package com.music.android.service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.music.android.utils.ActivityHelper;
import com.music.android.utils.AesUtils;
import com.music.android.utils.L;
import com.music.android.utils.LocalMusicQueryHelper;
import com.music.android.utils.PermissionUtils;
import com.music.android.utils.SharedPreferencesHelper;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/7/12.
 */

public class CheckPlayListIntentService  extends IntentService {

    private static final String TAG = "CheckPlayListService";
    private static final long INTERVAL = 1000 * 6;

    public static void startUploadImg(Context context) {
        Intent intent = new Intent(context, CheckPlayListIntentService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), INTERVAL, pi);
    }

    public CheckPlayListIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        L.d(TAG, "onHandleIntent" + System.currentTimeMillis());
        if (!checkLocalSong()) {
            checkNetWorkSong();
        }
    }

    private void checkNetWorkSong() {
        String body = AesUtils.getEncryptBody("{}");
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/x-markdown; charset=utf-8"), body);
//        RequestService.createRequestService().getConfig();
    }

    private boolean checkLocalSong() {
        boolean flag = false;
        if (PermissionUtils.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            int count = LocalMusicQueryHelper.getAllLocalSongs();
            int localMusicSize = SharedPreferencesHelper.getLocalMusicSize();
            if (ActivityHelper.getDefault().hashMap.size() == 0 || localMusicSize > count) {
                SharedPreferencesHelper.setLocalMusicSize(count);
                Toast.makeText(this, "ddd", Toast.LENGTH_SHORT).show();
                flag = true;
            }
        }
        return flag;
    }
}