package com.music.android.ui.mvp.audio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.music.android.R;
import com.music.android.base.BaseActivity;
import com.music.android.bean.DialogBundleBean;
import com.music.android.bean.MessageEventBean;
import com.music.android.bean.MessageIntentBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.data.local.FavoriteSongHelper;
import com.music.android.listener.OnBitmapLoadListener;
import com.music.android.listener.OnPlayModeChangeListener;
import com.music.android.listener.OnProgressChangeListener;
import com.music.android.managers.ImageLoaderManager;
import com.music.android.service.MusicPlayService;
import com.music.android.ui.dialog.CreateMenuDialog;
import com.music.android.ui.dialog.PlaylistDialog;
import com.music.android.ui.mvp.main.MainActivity;
import com.music.android.ui.widgets.BlurView;
import com.music.android.ui.widgets.PlayListPopupWindow;
import com.music.android.ui.widgets.SeekBarView;
import com.music.android.utils.ActivityHelper;
import com.music.android.utils.AnalyticsConstants;
import com.music.android.utils.AnalyticsUtils;
import com.music.android.utils.Constants;
import com.music.android.utils.L;
import com.music.android.utils.PlayHelper;
import com.music.android.utils.ScreenUtils;
import com.music.android.utils.SharedPreferencesHelper;
import com.music.android.utils.SizeUtils;
import com.music.android.utils.TimeHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hui.lv on 2017/3/8.
 */
public class PlayingActivity extends BaseActivity implements View.OnClickListener, OnPlayModeChangeListener, PopupWindow.OnDismissListener, OnProgressChangeListener {

    private final String TAG = "PlayingActivity";
    private TextView tvMount, tvLike, tvTimeNow, tvTimeTotal, tvTitle, tvAuthor;
    private ImageView imgMode, imgPre, imgPlay, imgNext, imgAdd, imgMusic;

    private long duration;
    private long currentProgress;
    private int currentPosition = -1;
    public ArrayList<MusicInfoBean> musics = new ArrayList<>();
    private BlurView llBlur;
    private View view;
    private PlayListPopupWindow popupWindow;
    private RelativeLayout head;
    private Bitmap bitmap;
    private SeekBarView seekBarView;
    private boolean songExist;
    private Drawable icFavorite;
    private Drawable redFavorite;
    private int height;
    private View bottom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_playing);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMode();
    }

    private void initView() {

        view = findViewById(R.id.parent);
        bottom = findViewById(R.id.bottom);
        head = (RelativeLayout) findViewById(R.id.head);
        View imgBack = findViewById(R.id.imgBack);
        View imgList = findViewById(R.id.imgList);
        tvMount = (TextView) findViewById(R.id.tvMount);
        tvLike = (TextView) findViewById(R.id.tvLike);
        tvTimeNow = (TextView) findViewById(R.id.tvTimeNow);
        tvTimeTotal = (TextView) findViewById(R.id.tvTimeTotal);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        imgMode = (ImageView) findViewById(R.id.imgMode);
        imgPre = (ImageView) findViewById(R.id.imgPre);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        imgAdd = (ImageView) findViewById(R.id.imgAdd);
        imgMusic = (ImageView) findViewById(R.id.imgMusic);
        seekBarView = (SeekBarView) findViewById(R.id.seekBarView);
        llBlur = (BlurView) findViewById(R.id.blur);

        seekBarView.setOnProgressChangeListener(this);
        imgMode.setOnClickListener(this);
        imgPre.setOnClickListener(this);
        imgPlay.setOnClickListener(this);
        imgNext.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgList.setOnClickListener(this);
        tvLike.setOnClickListener(this);
        EventBus.getDefault().register(this);
        icFavorite =   getResources().getDrawable(R.drawable.ic_favorite);
        icFavorite.setBounds(0, 0, SizeUtils.dp2Px(this,17),  SizeUtils.dp2Px(this,15));
        redFavorite = getResources().getDrawable(R.drawable.red_favorite);
        redFavorite.setBounds(0, 0, SizeUtils.dp2Px(this,17),  SizeUtils.dp2Px(this,15));
        setHeadPadding();
        initData();

    }

    private void setLikeCount(String path, String count) {
        L.d(TAG,"text="+count);
        if (!TextUtils.isEmpty(count)&&!count.equals("0")&&path.startsWith("http")) {
            String text = SizeUtils.formText(count);
            tvLike.setText("" + text);
            return;
        }
        tvLike.setText("");
    }



    private void initData() {
        List<MusicInfoBean> beans = MusicPlayService.getMusics();
        musics.clear();
        musics.addAll(beans);
        if (MusicPlayService.getCurrentState()) {
            imgPlay.setImageResource(R.drawable.palying_play);
        } else {
            imgPlay.setImageResource(R.drawable.playing_pause);
        }
        duration = MusicPlayService.getDuration();
        currentProgress = MusicPlayService.getCurrentProgress();
        currentPosition = MusicPlayService.getCurrentPosition();
        setHeadBitmap(beans.get(currentPosition).artwork_url);
        tvAuthor.setText(musics.get(currentPosition).singer);
        tvTitle.setText(musics.get(currentPosition).title);
        setLikeCount(musics.get(currentPosition).path,""+musics.get(currentPosition).likes_count);
        tvMount.setText((currentPosition + 1) + " of " + musics.size());
        tvTimeNow.setText(TimeHelper.formatTime(currentProgress));
        tvTimeTotal.setText(TimeHelper.formatTime(duration));
        if (duration > 0) {
            seekBarView.setCurrentProgress(SizeUtils.div(currentProgress, duration));
        }
        songExist = FavoriteSongHelper.getDefault(this).isSongExist(MusicPlayService.getCurrentMusic().path);
        setFavoriteState();
    }

    private void setFavoriteState() {
        if(songExist){
            tvLike.setCompoundDrawables(redFavorite,null,null,null);
            return;
        }
        tvLike.setCompoundDrawables(icFavorite,null,null,null);
    }

    private void setHeadPadding() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            head.setPadding(0,  ScreenUtils.getStatusBarHeight(this), 0, 0);
        }
    }

    @Override
    public void onBackPressed() {
        L.d(TAG, "onBackPressed");
        if (!ActivityHelper.getDefault().isActivityExist(MainActivity.class)) {
            L.d(TAG, "ActivityHelper");
            startActivity(new Intent(this, MainActivity.class));
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap=null;
        }
        if(icFavorite!=null){
            icFavorite=null;
        }
        if(redFavorite!=null){
            redFavorite=null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.imgList:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYPAGE, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.SONGLIST);
                showPopupWindow();
                break;
            case R.id.imgMode:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYPAGE, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.PLAYMODE);
                changeMode();
                break;
            case R.id.imgPre:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYPAGE, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.PREVIOUS);
                previous();
                break;
            case R.id.imgPlay:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYPAGE, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.PLAY);
                play();
                break;
            case R.id.imgNext:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYPAGE, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.NEXT);
                next();
                break;
            case R.id.imgAdd:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYPAGE, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.ADDPLAYLIST);
                showPlayListDialog();
                break;
            case R.id.tvLike:
                songExist=!songExist;
                if(songExist){
                    AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYPAGE, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.FAVORITES);
                    FavoriteSongHelper.getDefault(this).insert(MusicPlayService.getCurrentMusic());
                }else{
                    AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYPAGE, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.CANCEL_FAVORITES);
                    FavoriteSongHelper.getDefault(this).delete(MusicPlayService.getCurrentMusic().path);
                }
                setFavoriteState();
                break;
            default:
                break;
        }
    }

    private void showPopupWindow() {
        popupWindow = new PlayListPopupWindow(this,musics,currentPosition);
        popupWindow.setOnDismissListener(this);
        popupWindow.setOnPlayModeChangeListener(this);
        popupWindow.setOnPlayModeChangeListener(this);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);

    }

    private void showPlayListDialog() {
        PlaylistDialog dialog = new PlaylistDialog();
        Bundle bundle = new Bundle();
        DialogBundleBean bundleBean = new DialogBundleBean();
        MusicInfoBean infoBean = MusicPlayService.getMusics().get(MusicPlayService.getCurrentPosition());
        bundleBean.path = infoBean.path;
        bundleBean.title = infoBean.title;
        bundleBean.singer = infoBean.singer;
        bundleBean.artwork_url=infoBean.artwork_url;
        bundle.putSerializable(Constants.MusicOthers.NEWPLAYLISTDIALOG, bundleBean);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), CreateMenuDialog.class.getSimpleName());
    }


    private void next() {
        if (!isServiceExist()) {
            initPlayer(2);
            return;
        }
        Intent i3 = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        i3.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.NEXT_FLAG);
        sendBroadcast(i3);
    }

    private void play() {

        if (!isServiceExist()) {
            initPlayer(0);
            return;
        }
        Intent i2 = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        i2.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.PAUSE_OPEN_FLAG);
        sendBroadcast(i2);
    }

    private void previous() {
        if (!isServiceExist()) {
            initPlayer(1);
//            return;
        }
        Intent i1 = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        i1.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.PREVIOUS_FLAG);
        sendBroadcast(i1);
    }


    private void initPlayer(int onNext) {
        MusicPlayService.setMusics(musics);
        Intent intent = new Intent(this, MusicPlayService.class);
        MessageIntentBean intentBean = new MessageIntentBean();
        intentBean.onPlaying = true;
        intentBean.currentPosition = currentPosition;
        intentBean.currentProgress = currentProgress;
        intentBean.onNext=onNext;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.IntentType.BUNDLE_KEY_FLAG, intentBean);
        intent.putExtras(bundle);
        startService(intent);
    }

    public boolean isServiceExist() {
        int size = MusicPlayService.getMusics().size();
        return (size == 0 ? false : true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEventBean eventBean) {
        seekBarView.setCanDrag(eventBean.isPrepared);
        isStartAnimation(eventBean.isPrepared);
        if (eventBean.onPlaying ) {//&& eventBean.isPrepared
            imgPlay.setImageResource(R.drawable.palying_play);
        } else {
            imgPlay.setImageResource(R.drawable.playing_pause);
        }

        if (currentPosition==-1||eventBean.isSongChange) {//上一首下一首切换时
            setHeadBitmap(eventBean.imgUrl);
            changeMusic(eventBean.currentPosition);
            songExist = FavoriteSongHelper.getDefault(this).isSongExist(MusicPlayService.getCurrentMusic().path);
            setFavoriteState();
            tvTitle.setText(eventBean.musicName);
        }
        if (!eventBean.isServiceExist) {
            musics.clear();
            musics.addAll(eventBean.list);
        }
        duration = eventBean.duration;
        currentProgress = eventBean.currentProgress;
        currentPosition = eventBean.currentPosition;
        tvAuthor.setText(eventBean.authorName);

        setLikeCount(eventBean.path,eventBean.likeCount);
        String of = getResources().getString(R.string.of);
        tvMount.setText((eventBean.currentPosition + 1) + " " + of + " " + MusicPlayService.getMusics().size());
        tvTimeNow.setText(TimeHelper.formatTime(eventBean.currentProgress));
        tvTimeTotal.setText(TimeHelper.formatTime(eventBean.duration));
        float percent = SizeUtils.div(eventBean.currentProgress, eventBean.duration);
        seekBarView.setCurrentProgress(percent);
    }

    private void isStartAnimation(boolean isPrepared) {
        if (!isPrepared) {
            seekBarView.startAnimation();
            return;
        }
        seekBarView.stopAnimation();
    }

    private void changeMode() {
        switch (SharedPreferencesHelper.getPlayingType()) {
            case 0://默认的顺序
                SharedPreferencesHelper.setPlayingType(1);
                break;
            case 1://单曲
                SharedPreferencesHelper.setPlayingType(2);
                break;
            case 2://随机
                SharedPreferencesHelper.setPlayingType(0);
                break;
            default:
                break;
        }
        setMode();
        PlayHelper.getInstance().notifyModeChanged();
    }


    public void setMode() {
        switch (SharedPreferencesHelper.getPlayingType()) {
            case 0://默认的顺序
                imgMode.setImageResource(R.drawable.type_looper);
                break;
            case 1://单曲
                imgMode.setImageResource(R.drawable.type_repate);
                break;
            case 2://随机
                imgMode.setImageResource(R.drawable.type_randam);
                break;
            default:
                break;
        }
    }

    @Override
    public void onUp(SeekBarView seekView, float progress) {
        L.d("--seekTo--" + progress);
        Intent intent = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        intent.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.SEEK_FLAG);
        intent.putExtra(Constants.BroadcastConstants.SEEK_OR_OPEN, progress);
        sendBroadcast(intent);
    }

    @Override
    public void onMove(SeekBarView seekView, float progress) {
        tvTimeNow.setText(TimeHelper.formatTime((long) (duration * progress)));
    }


    @Override
    public void onChange() {
        setMode();
    }

    @Override
    public void onDismiss() {
        L.d(TAG, "onDismiss");
        popupWindow = null;
    }

    private void changeMusic(int position) {
        if (popupWindow != null) {
            popupWindow.changeMusic();
        }
    }

    private void setHeadBitmap(final String avatar) {
        if(TextUtils.isEmpty(avatar)){
            bitmap =  BitmapFactory.decodeResource(getResources(), R.mipmap.icon_loading_default);
            changeBitmap();
            return;
        }
        if (!avatar.startsWith("http")) {
            Uri uri = Uri.parse(avatar);
            loadUriBitmap(uri);
            return;
        }
        loadUrlBitmap(avatar);
    }

    private void loadUrlBitmap(final String avatar) {
        L.d("loadUrlBitmap","avatar---"+avatar);
        ImageLoaderManager.imageLoaderBitMap(avatar, new OnBitmapLoadListener() {
            @Override
            public void onLoadFailed() {
                if(PlayingActivity.this.isFinishing()||!avatar.equalsIgnoreCase(MusicPlayService.getCurrentMusic().artwork_url)){
                    L.d("onLoadFailed","----onLoadFailed");
                    return;
                }
                bitmap =  BitmapFactory.decodeResource(getResources(), R.mipmap.icon_loading_default);
                changeBitmap();
            }

            @Override
            public void onResourceReady(GlideDrawable resource) {
                if(PlayingActivity.this.isFinishing()|!avatar.equalsIgnoreCase(MusicPlayService.getCurrentMusic().artwork_url)){
                    L.d("onLoadFailed","----onResourceReady");
                    return;
                }
                drawableToBitmap(resource);
                changeBitmap();
            }
        });

    }

    private void loadUriBitmap(Uri uri) {
        final String string = uri.toString();
        ImageLoaderManager.imageLoaderBitMap(uri, new OnBitmapLoadListener() {
            @Override
            public void onLoadFailed() {
                if(PlayingActivity.this.isFinishing()||!string.equalsIgnoreCase(MusicPlayService.getCurrentMusic().artwork_url)){
                    L.d("onLoadFailed","====onLoadFailed");
                    return;
                }
                bitmap =  BitmapFactory.decodeResource(getResources(), R.mipmap.icon_loading_default);
                changeBitmap();
            }

            @Override
            public void onResourceReady(GlideDrawable resource) {
                if(PlayingActivity.this.isFinishing()||!string.equalsIgnoreCase(MusicPlayService.getCurrentMusic().artwork_url)){
                    L.d("onLoadFailed","====onResourceReady");
                    return;
                }
                drawableToBitmap(resource);
                changeBitmap();
            }
        });
    }

    public  void  drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
    }

    private void changeBitmap() {
        imgMusic.setImageBitmap(bitmap);
        llBlur.setBlurBitmap(bitmap);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }
}
