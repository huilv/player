package com.music.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;

import com.music.android.utils.Constants;

/**
 * Created by hui.lv on 2017/3/27.
 */

public class PhoneStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Intent itt = new Intent(Constants.MusicOthers.CALL_PHONE);
        int value = 0;
        switch (manager.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING://响铃
            case TelephonyManager.CALL_STATE_OFFHOOK: //通话中
                value = Constants.MusicOthers.CALL_PHONE_GOING;
                break;
            case TelephonyManager.CALL_STATE_IDLE: //电话挂断
                value = Constants.MusicOthers.CALL_PHONE_IDLE;
                break;
        }
        itt.putExtra(Constants.MusicOthers.CALL_PHONE_INTENT, value);
        context.sendBroadcast(itt);
    }
}
