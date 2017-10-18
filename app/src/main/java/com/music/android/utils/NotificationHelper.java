package com.music.android.utils;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.bean.MusicInfoBean;
import com.music.android.service.MusicPlayService;
import com.music.android.ui.mvp.main.MainActivity;

/**
 * Created by hui.lv on 2017/3/8.
 */

public class NotificationHelper {

    private static NotificationHelper helper;


    private NotificationHelper() {
    }

    public static NotificationHelper getInstance() {
        if (helper == null) {
            synchronized (NotificationHelper.class) {
                helper = new NotificationHelper();
            }
        }
        return helper;
    }

    /**
     * 显示文字
     *
     * @param bean
     * @return
     */
    public Notification getNotification(MusicInfoBean bean) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MusicApp.context);
        MusicInfoBean currentMusic = MusicPlayService.getCurrentMusic();

        RemoteViews bigRemoteViews = getBigRemoteViews(currentMusic, null);
        RemoteViews smallRemoteViews = getSmallRemoteViews(currentMusic, null);

        builder.setCustomBigContentView(bigRemoteViews)
                .setContent(smallRemoteViews)
                .setTicker(bean.title)
                .setSmallIcon(R.drawable.black_logo);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT | notification.flags;

        return notification;
    }

    /**
     * 显示文字
     *
     * @param bitmap
     * @param bean
     * @return
     */
    public Notification getNotification(Bitmap bitmap, MusicInfoBean bean) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MusicApp.context);
        MusicInfoBean currentMusic = MusicPlayService.getCurrentMusic();

        RemoteViews bigRemoteViews = getBigRemoteViews(currentMusic, bitmap);
        RemoteViews smallRemoteViews = getSmallRemoteViews(currentMusic, bitmap);
        builder.setContent(smallRemoteViews)
                .setCustomBigContentView(bigRemoteViews)
                .setTicker(bean.title)
                .setSmallIcon(R.drawable.black_logo);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT | notification.flags;
        return notification;
    }

    /**
     * 不显示文字
     *
     * @param bitmap
     * @param bean
     * @return
     */
    public Notification changeNotification(Bitmap bitmap, MusicInfoBean bean) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MusicApp.context);
        MusicInfoBean currentMusic = MusicPlayService.getCurrentMusic();

        RemoteViews bigRemoteViews = getBigRemoteViews(currentMusic, bitmap);
        RemoteViews smallRemoteViews = getSmallRemoteViews(currentMusic, bitmap);
        builder.setContent(smallRemoteViews)
                .setCustomBigContentView(bigRemoteViews)
                .setSmallIcon(R.drawable.black_logo);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT | notification.flags;
        return notification;
    }

    public PendingIntent getPendingIntent(int flag) {
        PendingIntent pendingIntent = null;
        switch (flag) {
            case 0://页面跳转
                Intent itt = new Intent(MusicApp.context, MainActivity.class);
                itt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                itt.setAction(Intent.ACTION_MAIN);
//                itt.addCategory(Intent.CATEGORY_LAUNCHER);
                itt.putExtra(Constants.BroadcastConstants.NOTIFICATION_COME_KEY, Constants.BroadcastConstants.NOTIFICATION_COME_VALUE);
                pendingIntent = PendingIntent.getActivity(MusicApp.context, 1, itt, PendingIntent.FLAG_UPDATE_CURRENT);
                break;
            case 1://上一首
                Intent preIntent = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
                preIntent.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.PREVIOUS_FLAG);
                preIntent.putExtra(Constants.BroadcastConstants.NOTIFICATION_COME_KEY, Constants.BroadcastConstants.NOTIFICATION_COME_VALUE);
                pendingIntent = PendingIntent.getBroadcast(MusicApp.context, 2,
                        preIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;
            case 2://下一首
                Intent nextIntent = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
                nextIntent.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.NEXT_FLAG);
                nextIntent.putExtra(Constants.BroadcastConstants.NOTIFICATION_COME_KEY, Constants.BroadcastConstants.NOTIFICATION_COME_VALUE);
                pendingIntent = PendingIntent.getBroadcast(MusicApp.context, 3,
                        nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;
            case 3://消失
                Intent dismissIntent = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
                dismissIntent.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.DISMISS_FLAG);
                pendingIntent = PendingIntent.getBroadcast(MusicApp.context, 4,
                        dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;
            case 4://暂停或者播放
                Intent intent = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
                intent.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.PAUSE_OPEN_FLAG);
                intent.putExtra(Constants.BroadcastConstants.NOTIFICATION_COME_KEY, Constants.BroadcastConstants.NOTIFICATION_COME_VALUE);
                pendingIntent = PendingIntent.getBroadcast(MusicApp.context, 5,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;

            default:
                break;
        }
        return pendingIntent;
    }

    public RemoteViews getBigRemoteViews(MusicInfoBean currentMusic, Bitmap bitmap) {
        RemoteViews remoteViews = new RemoteViews(MusicApp.context.getPackageName(), R.layout.notification_remote_view);
        //设置歌曲
        remoteViews.setTextViewText(R.id.ntf_title, currentMusic.title);
        //设置作者
        remoteViews.setTextViewText(R.id.ntf_author, currentMusic.singer);
        //设置头像
        remoteViews.setImageViewBitmap(R.id.portrait, bitmap);
        //打开界面
        PendingIntent activityOpenIntent = getPendingIntent(Constants.BroadcastConstants.ACTIVITY_OPEN_FLAG);
        remoteViews.setOnClickPendingIntent(R.id.portrait, activityOpenIntent);
        //上一首
        PendingIntent preIntent = getPendingIntent(Constants.BroadcastConstants.PREVIOUS_FLAG);
        remoteViews.setOnClickPendingIntent(R.id.ntf_img_pre, preIntent);
        // 下一首
        PendingIntent nextIntent = getPendingIntent(Constants.BroadcastConstants.NEXT_FLAG);
        remoteViews.setOnClickPendingIntent(R.id.ntf_img_next, nextIntent);
        //消失
        PendingIntent dismissIntent = getPendingIntent(Constants.BroadcastConstants.DISMISS_FLAG);
        remoteViews.setOnClickPendingIntent(R.id.delete, dismissIntent);
        //暂停或者播放
        if (MusicPlayService.getCurrentState() && !MusicPlayService.getRingState()) {//&& MusicPlayService.isPrepared()
            remoteViews.setImageViewResource(R.id.ntf_img_play, R.drawable.ntf_play);
        } else {
            remoteViews.setImageViewResource(R.id.ntf_img_play, R.drawable.ntf_pause);
        }
        PendingIntent intent = getPendingIntent(Constants.BroadcastConstants.PAUSE_OPEN_FLAG);
        remoteViews.setOnClickPendingIntent(R.id.ntf_img_play, intent);

        return remoteViews;
    }


    public RemoteViews getSmallRemoteViews(MusicInfoBean currentMusic, Bitmap bitmap) {

        RemoteViews smallViews = new RemoteViews(MusicApp.context.getPackageName(), R.layout.notification_small_view);
        //设置歌曲
        smallViews.setTextViewText(R.id.tv_title, currentMusic.title);
        //设置作者
        smallViews.setTextViewText(R.id.tv_author, currentMusic.singer);
        //设置头像
        smallViews.setImageViewBitmap(R.id.small_music, bitmap);
        //打开界面
        PendingIntent openIntent = getPendingIntent(Constants.BroadcastConstants.ACTIVITY_OPEN_FLAG);
        smallViews.setOnClickPendingIntent(R.id.rlStart, openIntent);
        smallViews.setOnClickPendingIntent(R.id.llContent, openIntent);
        //上一首
        smallViews.setImageViewResource(R.id.img_pre, R.drawable.nft_pre_selector);

        PendingIntent pre = getPendingIntent(Constants.BroadcastConstants.PREVIOUS_FLAG);
        smallViews.setOnClickPendingIntent(R.id.img_pre, pre);
        // 下一首
        smallViews.setImageViewResource(R.id.img_next, R.drawable.nft_next_selector);
        PendingIntent next = getPendingIntent(Constants.BroadcastConstants.NEXT_FLAG);
        smallViews.setOnClickPendingIntent(R.id.img_next, next);
        //消失
        PendingIntent dismissIntent = getPendingIntent(Constants.BroadcastConstants.DISMISS_FLAG);
        smallViews.setOnClickPendingIntent(R.id.delete, dismissIntent);

        //暂停或者播放
        if (MusicPlayService.getCurrentState() && !MusicPlayService.getRingState()) {//&& MusicPlayService.isPrepared()
            smallViews.setImageViewResource(R.id.img_play, R.drawable.ntf_play);
        } else {
            smallViews.setImageViewResource(R.id.img_play, R.drawable.ntf_pause);
        }
        PendingIntent intent = getPendingIntent(Constants.BroadcastConstants.PAUSE_OPEN_FLAG);
        smallViews.setOnClickPendingIntent(R.id.img_play, intent);
        return smallViews;
    }


}
