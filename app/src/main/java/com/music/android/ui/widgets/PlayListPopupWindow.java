package com.music.android.ui.widgets;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.bean.MusicInfoBean;
import com.music.android.listener.OnPlayModeChangeListener;
import com.music.android.service.MusicPlayService;
import com.music.android.ui.adapter.MusicsAdapter;
import com.music.android.utils.AnalyticsConstants;
import com.music.android.utils.AnalyticsUtils;
import com.music.android.utils.L;
import com.music.android.utils.PlayHelper;
import com.music.android.utils.ScreenUtils;
import com.music.android.utils.SharedPreferencesHelper;
import com.music.android.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hui.lv on 2017/3/22.
 */

public class PlayListPopupWindow extends PopupWindow implements View.OnClickListener ,Animator.AnimatorListener{

    private final String TAG = "PlayListPopupWindow";
    private MusicsAdapter adapter;
    private RecyclerView recyclerView;
    private TextView count;
    private ImageView imgMode;
    private OnPlayModeChangeListener listener;
    private View view;
    private String songs;
    private ArrayList<MusicInfoBean> musics = new ArrayList<>();
    private int currentPosition;
    private LinearLayout llAnimation;
    private ObjectAnimator objectAnimator;

    public PlayListPopupWindow(Context context, List<MusicInfoBean> list, int currentPosition) {
        super(context);
        musics.clear();
        musics.addAll(list);
        this.currentPosition = currentPosition;
        init(context);
    }

    public PlayListPopupWindow(Context context) {
        super(context);
        musics.clear();
        musics.addAll(MusicPlayService.getMusics());
        this.currentPosition = MusicPlayService.getCurrentPosition();
        init(context);
    }

    private void init(Context context) {
        initPopupWindow(context);
        initView(context);
    }

    private void initPopupWindow(Context context) {
        int screenWidth = ScreenUtils.getScreenWidth(context);
        int screenHeight = ScreenUtils.getScreenHeight(context);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        setClippingEnabled(false);
        setFocusable(true);
        setWidth(screenWidth);
        setHeight(screenHeight );
    }

    private void initView(Context context) {
        L.d(TAG, "initView");
        view = LayoutInflater.from(context).inflate(R.layout.dialog_musics, null);
        setContentView(view);
        llAnimation = (LinearLayout) view.findViewById(R.id.llAnimation);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        count = (TextView) view.findViewById(R.id.count);
        imgMode = (ImageView) view.findViewById(R.id.imgMode);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new PlayListItemDecoration(context, SizeUtils.dp2Px(context, 42)));
        adapter = new MusicsAdapter();
        recyclerView.setAdapter(adapter);
        cancel.setOnClickListener(this);
        imgMode.setOnClickListener(this);
        view.findViewById(R.id.outside).setOnClickListener(this);
        songs = context.getString(R.string.songs);
        setMode();
        initData();
        setAnimation();
//        setAnimationStyle(R.style.pop_animation);

    }


    private void setAnimation() {
        int px = SizeUtils.dp2Px(MusicApp.context, (55 + 50 + 51 * 5));
        L.d(TAG, "height=" + px);
        ObjectAnimator oat = ObjectAnimator.ofFloat(llAnimation, "translationY", px,0);
        oat.setDuration(250);
        oat.setInterpolator(new AccelerateDecelerateInterpolator());
        oat.start();

    }
    private void stopAnimation() {
        if(objectAnimator!=null&&objectAnimator.isRunning()){
            return;
        }
        int px = SizeUtils.dp2Px(MusicApp.context, (55 + 50 + 51 * 5));
        L.d(TAG, "height=" + px);
        objectAnimator = ObjectAnimator.ofFloat(llAnimation, "translationY", 0,px);
        objectAnimator.setDuration(250);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.addListener(this);
        objectAnimator.start();

    }
    @Override
    public void dismiss() {
        stopAnimation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.imgMode:
                changeMode();
                AnalyticsUtils.vMusicClick(AnalyticsConstants.SONGLIST, AnalyticsConstants.Action.CLICK_PLAYMODE,"");
                PlayHelper.getInstance().notifyModeChanged();
                break;
            case R.id.outside:
                dismiss();
                L.d(TAG,"dismiss");
                break;
        }

    }

    private void setRecyclerViewHeight() {
        recyclerView.getLayoutParams().height = SizeUtils.dp2Px(MusicApp.context, 51 * 5);
//        int itemCount = adapter.getItemCount();
//        if (itemCount == 1) {
//            recyclerView.getLayoutParams().height = SizeUtils.dp2Px(MusicApp.context, 51);
//        } else if (itemCount == 2) {
//            recyclerView.getLayoutParams().height = SizeUtils.dp2Px(MusicApp.context, 51*2);
//        } else if (itemCount == 3) {
//            recyclerView.getLayoutParams().height = SizeUtils.dp2Px(MusicApp.context, 51*3);
//        } else if(itemCount==4){
//            recyclerView.getLayoutParams().height = SizeUtils.dp2Px(MusicApp.context, 51*4);
//        }else{
//            recyclerView.getLayoutParams().height = SizeUtils.dp2Px(MusicApp.context, 51*5);
//        }
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
        if (listener != null) {
            listener.onChange();
        }
        setMode();
    }

    private void initData() {
        adapter.setData(musics, currentPosition);
        setRecyclerViewHeight();
        count.setText(musics.size() + " " + songs);
        recyclerView.scrollToPosition(currentPosition);
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
    public void showAtLocation(View parent, int gravity, int x, int y) {
        y=y+ ScreenUtils.getVirtualBarHeigh(parent.getContext());
        Log.d(TAG,"barHeight="+y);
        super.showAtLocation(parent, gravity, x, y);
    }

    public void setOnPlayModeChangeListener(OnPlayModeChangeListener listener) {
        this.listener = listener;
    }

    public void changeMusic() {
        adapter.changeMusic();
        currentPosition = MusicPlayService.getCurrentPosition();
        recyclerView.scrollToPosition(currentPosition);
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        super.dismiss();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
