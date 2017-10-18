package com.music.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.view.KeyEvent;


public class MediaButtonIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if (Intent.ACTION_MEDIA_BUTTON.equals(action)) {
            int keycode = event.getKeyCode();
            switch (keycode) {
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    Log.d("MediaButton","11111111111");
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    Log.d("MediaButton","2222222222222");
                    break;
                case KeyEvent.KEYCODE_HEADSETHOOK:
                    Log.d("MediaButton","33333333333");
                    break;
                default:
                    break;
            }
        }
    }
}
