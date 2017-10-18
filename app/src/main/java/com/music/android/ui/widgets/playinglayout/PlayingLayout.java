package com.music.android.ui.widgets.playinglayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.music.android.MusicApp;
import com.music.android.R;
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
import com.music.android.ui.widgets.BlurView;
import com.music.android.ui.widgets.PlayListPopupWindow;
import com.music.android.ui.widgets.SeekBarView;
import com.music.android.utils.AnalyticsConstants;
import com.music.android.utils.AnalyticsUtils;
import com.music.android.utils.Constants;
import com.music.android.utils.L;
import com.music.android.utils.PlayHelper;
import com.music.android.utils.ScreenUtils;
import com.music.android.utils.SharedPreferencesHelper;
import com.music.android.utils.SizeUtils;
import com.music.android.utils.TimeHelper;
import com.music.android.utils.VarUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hui.lv on 2017/3/8.
 */
public class PlayingLayout extends RelativeLayout implements View.OnClickListener, OnPlayModeChangeListener, PopupWindow.OnDismissListener, OnProgressChangeListener {

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

    private ViewPager mViewPager;
    private MusicImageAdapter mMusicImageAdapter;

    public PlayingLayout(Context context) {
        this(context, null);
    }

    public PlayingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {

        LayoutInflater.from(getContext()).inflate(R.layout.activity_playing, this, true);

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

//        imgMusic = (ImageView) findViewById(R.id.imgMusic);
        mViewPager = (ViewPager) findViewById(R.id.music_view_pager);
        mMusicImageAdapter = new MusicImageAdapter(getContext());
        mViewPager.setAdapter(mMusicImageAdapter);
        mViewPager.setCurrentItem(MusicImageAdapter.INIT_INDEX);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int currentItem = MusicImageAdapter.INIT_INDEX;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position > currentItem) {
                    next();
                } else if (position < currentItem) {
                    previous();
                }
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        seekBarView = (SeekBarView) findViewById(R.id.seekBarView);
        llBlur = (BlurView) findViewById(R.id.blur);

        seekBarView.setOnProgressChangeListener(this);
        imgMode.setOnClickListener(this);
        imgPre.setOnClickListener(this);
        imgPlay.setOnClickListener(this);
        imgNext.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
//        imgBack.setOnClickListener(this);
        imgList.setOnClickListener(this);
        tvLike.setOnClickListener(this);
        EventBus.getDefault().register(this);
        icFavorite = getResources().getDrawable(R.drawable.ic_favorite);
        icFavorite.setBounds(0, 0, SizeUtils.dp2Px(MusicApp.context, 17), SizeUtils.dp2Px(MusicApp.context, 15));
        redFavorite = getResources().getDrawable(R.drawable.red_favorite);
        redFavorite.setBounds(0, 0, SizeUtils.dp2Px(MusicApp.context, 17), SizeUtils.dp2Px(MusicApp.context, 15));
        setHeadPadding();
        initData();
        setMode();

    }

    private void setLikeCount(String path, String count) {
        if (path.startsWith("http")) {
            String text = SizeUtils.formText(count);
            tvLike.setText("" + text);
            return;
        }
        tvLike.setText("");
    }

    private void initData() {
        List<MusicInfoBean> beans = MusicPlayService.getMusics();
        if (beans.size() <= 0) {
            return;
        }
        musics.clear();
        musics.addAll(beans);
        if (MusicPlayService.getCurrentState()) {
            imgPlay.setImageResource(R.drawable.selector_pause);
        } else {
            imgPlay.setImageResource(R.drawable.selector_play);
        }
        duration = MusicPlayService.getDuration();
        currentProgress = MusicPlayService.getCurrentProgress();
        currentPosition = MusicPlayService.getCurrentPosition();

        setHeadBitmap(beans.size() > 0 ? beans.get(currentPosition).artwork_url : null);

        tvAuthor.setText(musics.get(currentPosition).singer);
        tvTitle.setText(musics.get(currentPosition).title);
        setLikeCount(musics.get(currentPosition).path, "" + musics.get(currentPosition).likes_count);
        tvMount.setText((currentPosition + 1) + " of " + musics.size());
        tvTimeNow.setText(TimeHelper.formatTime(currentProgress));
        tvTimeTotal.setText(TimeHelper.formatTime(duration));
        if (duration > 0) {
            seekBarView.setCurrentProgress(SizeUtils.div(currentProgress, duration));
        }
        songExist = FavoriteSongHelper.getDefault(MusicApp.context).isSongExist(MusicPlayService.getCurrentMusic().path);
        setFavoriteState();
    }

    private void setFavoriteState() {
        if (songExist) {
            tvLike.setCompoundDrawables(redFavorite, null, null, null);
            return;
        }
        tvLike.setCompoundDrawables(icFavorite, null, null, null);
    }

    private void setHeadPadding() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            head.setPadding(0, ScreenUtils.getStatusBarHeight(MusicApp.context), 0, 0);
        }
    }

//    @Override
//    public void onBackPressed() {
//        L.d(TAG, "onBackPressed");
//        if (!ActivityHelper.getDefault().isActivityExist(MainActivity.class)) {
//            L.d(TAG, "ActivityHelper");
//            startActivity(new Intent(this, MainActivity.class));
//        }
//        super.onBackPressed();
//    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        if (icFavorite != null) {
            icFavorite = null;
        }
        if (redFavorite != null) {
            redFavorite = null;
        }
        EventBus.getDefault().unregister(this);
    }

//    @Override
//    protected void on() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:

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
                int prevItem = mViewPager.getCurrentItem() - 1;
                if (prevItem >= 0) {
                    mViewPager.setCurrentItem(prevItem);
                }
                break;
            case R.id.imgPlay:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYPAGE, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.PLAY);
                play();
                break;
            case R.id.imgNext:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYPAGE, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.NEXT);
                int nextItem = mViewPager.getCurrentItem() + 1;
                if (nextItem < mMusicImageAdapter.getCount()) {
                    mViewPager.setCurrentItem(nextItem);
                }
                break;
            case R.id.imgAdd:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYPAGE, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.ADDPLAYLIST);
                showPlayListDialog();
                break;
            case R.id.tvLike:
                songExist = !songExist;
                if(VarUtils.currentPosition>=VarUtils.list.size()){
                    return;
                }
                if (songExist) {
                    AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYPAGE, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.FAVORITES);
                    FavoriteSongHelper.getDefault(getContext()).insert(MusicPlayService.getCurrentMusic());
                } else {
                    AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYPAGE, AnalyticsConstants.Action.CLICK, AnalyticsConstants.Value.CANCEL_FAVORITES);
                    FavoriteSongHelper.getDefault(getContext()).delete(MusicPlayService.getCurrentMusic().path);
                }
                setFavoriteState();
                EventBus.getDefault().post(Constants.EventBusConstants.EVENT_UPDATE_UI);
                break;
            default:
                break;
        }
    }

    public void setCurrentMusicList(List<MusicInfoBean> musicList) {
        setMode();

        if (musicList == null) {
            return;
        }
        if (musicList.size() > 0) {
            musics.clear();
            musics.addAll(musicList);
        }
    }

    private void showPopupWindow() {

        popupWindow = new PlayListPopupWindow(getContext(), musics, currentPosition);
        popupWindow.setOnDismissListener(this);
        popupWindow.setOnPlayModeChangeListener(this);
        popupWindow.setOnPlayModeChangeListener(this);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    private WeakReference<FragmentActivity> activityWeakReference;

    public void setContextActivity(FragmentActivity contextActivity) {
        activityWeakReference = new WeakReference<>(contextActivity);
    }

    private void showPlayListDialog() {

        if (activityWeakReference == null) {
            return;
        }

        FragmentActivity contextActivity = activityWeakReference.get();
        if (contextActivity == null|| VarUtils.currentPosition>=VarUtils.list.size()) {
            return;
        }

        PlaylistDialog dialog = new PlaylistDialog();
        Bundle bundle = new Bundle();
        DialogBundleBean bundleBean = new DialogBundleBean();
        MusicInfoBean infoBean = MusicPlayService.getMusics().get(MusicPlayService.getCurrentPosition());
        bundleBean.path = infoBean.path;
        bundleBean.title = infoBean.title;
        bundleBean.singer = infoBean.singer;
        bundleBean.comeFrom = 1;
        bundleBean.artwork_url = infoBean.artwork_url;
        bundle.putSerializable(Constants.MusicOthers.NEWPLAYLISTDIALOG, bundleBean);
        dialog.setArguments(bundle);
        dialog.show(contextActivity.getSupportFragmentManager(), CreateMenuDialog.class.getSimpleName());
    }

    private void next() {
        if (!isServiceExist()) {
            initPlayer();
            return;
        }
        Intent i3 = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        i3.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.NEXT_FLAG);
        getContext().sendBroadcast(i3);
    }

    private void play() {
        if (!isServiceExist()) {
            initPlayer();
            return;
        }
        Intent i2 = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        i2.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.PAUSE_OPEN_FLAG);
        getContext().sendBroadcast(i2);
    }

    private void previous() {
        if (!isServiceExist()) {
            initPlayer();
            return;
        }

        Intent i1 = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        i1.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.PREVIOUS_FLAG);
        getContext().sendBroadcast(i1);
    }

    private void initPlayer() {
        MusicPlayService.setMusics(musics);
        Intent intent = new Intent(getContext(), MusicPlayService.class);
        MessageIntentBean intentBean = new MessageIntentBean();
        intentBean.onPlaying = true;
        intentBean.currentPosition = currentPosition;
        intentBean.currentProgress = currentProgress;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.IntentType.BUNDLE_KEY_FLAG, intentBean);
        intent.putExtras(bundle);
        getContext().startService(intent);
    }

    public boolean isServiceExist() {
        int size = MusicPlayService.getMusics().size();
        return (size == 0 ? false : true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEventBean eventBean) {
        seekBarView.setCanDrag(eventBean.isPrepared);
        isStartAnimation(eventBean.isPrepared);
        if (eventBean.onPlaying) {//&& eventBean.isPrepared
            imgPlay.setImageResource(R.drawable.selector_pause);
        } else {
            imgPlay.setImageResource(R.drawable.selector_play);
        }
        duration = eventBean.duration;
        currentProgress = eventBean.currentProgress;
        currentPosition = eventBean.currentPosition;
        if (currentPosition == -1 || eventBean.isSongChange) {//上一首下一首切换时
            setHeadBitmap(eventBean.imgUrl);
            changeMusic(eventBean.currentPosition);
            songExist = FavoriteSongHelper.getDefault(MusicApp.context).isSongExist(MusicPlayService.getCurrentMusic().path);
            setFavoriteState();
            tvTitle.setText(eventBean.musicName);

//            mMusicImageAdapter.setMusicImage(eventBean.currentPosition, eventBean.imgUrl);
        }
        if (!eventBean.isServiceExist) {
            musics.clear();
            musics.addAll(eventBean.list);
        }

        tvAuthor.setText(eventBean.authorName);

        setLikeCount(eventBean.path, eventBean.likeCount);
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
        PlayHelper.getInstance().notifyModeChanged();
        setMode();
    }

    public void setMode() {
        switch (SharedPreferencesHelper.getPlayingType()) {
            case 0://默认的顺序
                imgMode.setImageResource(R.drawable.selector_mode_list);
                break;
            case 1://单曲
                imgMode.setImageResource(R.drawable.selector_mode_oneloop);
                break;
            case 2://随机
                imgMode.setImageResource(R.drawable.selector_mode_random);
                break;
            default:
                break;
        }
    }

    @Override
    public void onUp(SeekBarView seekView, float progress) {
        Intent intent = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        intent.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.SEEK_FLAG);
        intent.putExtra(Constants.BroadcastConstants.SEEK_OR_OPEN, progress);
        getContext().sendBroadcast(intent);
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
        if (TextUtils.isEmpty(avatar)) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_loading_default);
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
        L.d("loadUrlBitmap", "avatar---" + avatar);
        ImageLoaderManager.imageLoaderBitMap(avatar, new OnBitmapLoadListener() {
            @Override
            public void onLoadFailed() {
                if (MusicPlayService.getCurrentMusic()!=null&&!avatar.equalsIgnoreCase(MusicPlayService.getCurrentMusic().artwork_url)) {
                    L.d("onLoadFailed", "----onLoadFailed");
                    return;
                }
                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_loading_default);
                changeBitmap();
            }

            @Override
            public void onResourceReady(GlideDrawable resource) {
                if (MusicPlayService.getCurrentMusic()!=null&&MusicPlayService.getCurrentMusic().artwork_url != null && !avatar.equalsIgnoreCase(MusicPlayService.getCurrentMusic().artwork_url)) {
                    L.d("onLoadFailed", "----onResourceReady");
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
                if (MusicPlayService.getCurrentMusic()==null||!string.equalsIgnoreCase(MusicPlayService.getCurrentMusic().artwork_url)) {
                    L.d("onLoadFailed", "====onLoadFailed");
                    return;
                }
                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_loading_default);
                changeBitmap();
            }

            @Override
            public void onResourceReady(GlideDrawable resource) {
                if (MusicPlayService.getCurrentMusic()==null||!string.equalsIgnoreCase(MusicPlayService.getCurrentMusic().artwork_url)) {
                    L.d("onLoadFailed", "====onResourceReady");
                    return;
                }
                drawableToBitmap(resource);
                changeBitmap();
            }
        });
    }

    public void drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
    }

    private void changeBitmap() {
        mMusicImageAdapter.setCurrentBitmap(bitmap);
//        imgMusic.setImageBitmap(bitmap);
        llBlur.setBlurBitmap(bitmap);
    }


}
