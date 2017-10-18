package com.music.android.receiver;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import com.music.android.MusicApp;
import com.music.android.bean.MessageIntentBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.utils.VarUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2017/5/4.
 */

public class Test {
    private String TAG = "Test";
    private final AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() { //监听,转发给mPlayerHandler处理

        @Override
        public void onAudioFocusChange(final int focusChange) {
            Log.d("Test", "focusChange=" + focusChange);
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:// 获得音频
                    Log.i(TAG, "AUDIOFOCUS_GAIN");

                    Toast.makeText(MusicApp.context, "222222222", Toast.LENGTH_LONG).show();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS://失去音频
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    Toast.makeText(MusicApp.context, "111111111", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    private AudioManager mAudioManager;
    private static MediaSessionCompat mSession;

    public void register() {
        mAudioManager = (AudioManager) MusicApp.context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(mAudioFocusListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

//       mAudioManager.abandonAudioFocus(mAudioFocusListener);//暂停第三方

        ComponentName mbCN = new ComponentName(MusicApp.context.getPackageName(), MediaButtonIntentReceiver.class.getName());
        mAudioManager.registerMediaButtonEventReceiver(mbCN);


        MediaSessionCompat   mSession = new MediaSessionCompat(MusicApp.context, "test", mbCN, null);



        mSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPause() {

            }

            @Override
            public void onPlay() {

            }

            @Override
            public void onSeekTo(long pos) {

            }

            @Override
            public void onSkipToNext() {

            }

            @Override
            public void onSkipToPrevious() {

            }

            @Override
            public void onStop() {

            }
        });
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
    }


    public static void registerMediaButtons(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.registerMediaButtonEventReceiver(new ComponentName(context, MediaButtonIntentReceiver.class));
    }

    public static void unregisterMediaButtons(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.unregisterMediaButtonEventReceiver(new ComponentName(context, MediaButtonIntentReceiver.class));
    }

//    <receiver android:name=".receiver.MediaButtonIntentReceiver">
//            <intent-filter>
//                <action android:name="android.intent.action.MEDIA_BUTTON"/>
//                <action android:name="android.media.AUDIO_BECOMING_NOISY"/>
//            </intent-filter>
//        </receiver>




    public static void setUpMediaSession() {
        mSession = new MediaSessionCompat(MusicApp.context, "Listener");
        mSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPause() {

            }

            @Override
            public void onPlay() {

            }

            @Override
            public void onSeekTo(long pos) {

            }

            @Override
            public void onSkipToNext() {

            }

            @Override
            public void onSkipToPrevious() {

            }

            @Override
            public void onStop() {

            }
        });
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);



    }

    public static  void onDestory() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mSession.release();
    }

    public static  void updateMediaState() {

        int playState = VarUtils.onPlaying
                ? PlaybackStateCompat.STATE_PLAYING
                : PlaybackStateCompat.STATE_PAUSED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSession.setActive(true);
            mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(playState, VarUtils.currentPosition, 1.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                    .build());
        }
    }


    public static  void updateMediaDetail(MessageIntentBean bean) {

        int playState = VarUtils.onPlaying
                ? PlaybackStateCompat.STATE_PLAYING
                : PlaybackStateCompat.STATE_PAUSED;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSession.setActive(true);
            mSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, VarUtils.currentPosition)
                    .build());

            mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(playState, VarUtils.currentPosition, 1.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE |
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                    .build());
        }
    }


}
