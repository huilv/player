package com.music.android.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.music.android.R;
import com.music.android.listener.OnTimeUpdateSuccessListener;
import com.music.android.ui.dialog.EvaluateDialog;
import com.music.android.ui.dialog.SleepTimeDialog;
import com.music.android.ui.mvp.main.SettingsActivity;
import com.music.android.utils.AnalyticsConstants;
import com.music.android.utils.AnalyticsUtils;
import com.music.android.utils.L;
import com.music.android.utils.SharedPreferencesHelper;
import com.music.android.utils.TimeHelper;

/**
 * Created by hui.lv on 2017/3/23.
 */

public class DrawerLinearLayout extends RelativeLayout implements View.OnClickListener {

    private LinearLayout sleepTime;
    private View llSleep;
    private TextView tvTime;
    private final String TAG="DrawerLinearLayout";
    public DrawerLinearLayout(Context context) {
        super(context);
    }
    public DrawerLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public DrawerLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LinearLayout view = (LinearLayout) getChildAt(0);
        initView(view);
    }

    private void initView(LinearLayout view) {
        llSleep = view.findViewById(R.id.llSleep);
        View llLike =  view.findViewById(R.id.llLike);
        View llInvite = view.findViewById(R.id.llInvite);
        View llSettings =  view.findViewById(R.id.llSettings);
        sleepTime = (LinearLayout) view.findViewById(R.id.sleepTime);

        TextView title= (TextView) view.findViewById(R.id.title);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        View bottom=  view.findViewById(R.id.bottom);
        View mView=  view.findViewById(R.id.view);
        View child = getChildAt(1);
        child.setOnClickListener(this);
        llSleep.setOnClickListener(this);
        mView.setOnClickListener(this);
        llLike.setOnClickListener(this);
        sleepTime.setOnClickListener(this);
        llInvite.setOnClickListener(this);
        llSettings.setOnClickListener(this);
        title.setOnClickListener(this);
        bottom.setOnClickListener(this);
        initState();
    }

    private void initState() {
        long time = SharedPreferencesHelper.getSleepTime();
        if(time<=System.currentTimeMillis()){
            cancel();
             llSleep.setVisibility(VISIBLE);
             sleepTime.setVisibility(GONE);
        }else{
            llSleep.setVisibility(GONE);
            sleepTime.setVisibility(VISIBLE);
           changeState(time);
        }
    }

    private void changeState(long time) {
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
        timer=new MyCountDownTimer(time-System.currentTimeMillis(),1000);
        timer.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llSleep:
                SleepTimeDialog dialog=new SleepTimeDialog();
                dialog.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), SleepTimeDialog.class.getSimpleName());
                dialog.setUpdateSuccessListener(new UpdateSuccessListener());
                break;
            case R.id.llLike:
                EvaluateDialog evaluateDialog = new EvaluateDialog();
                evaluateDialog.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), EvaluateDialog.class.getSimpleName());
                break;
            case R.id.llInvite:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.MENU, AnalyticsConstants.Action.CLICK_SHARE,"");
                shareText();
                break;
            case R.id.llSettings:
                getContext().startActivity(new Intent(getContext(), SettingsActivity.class));
                break;
            case R.id.sleepTime:
                SleepTimeDialog sleepTimeDialog=new SleepTimeDialog();
                sleepTimeDialog.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), SleepTimeDialog.class.getSimpleName());
                sleepTimeDialog.setUpdateSuccessListener(new UpdateSuccessListener());
                break;
            case R.id.title: //屏蔽点击事件，防止穿透
                L.d("DrawerLinearLayout","title");
                break;
            case R.id.bottom:
                L.d("DrawerLinearLayout","bottom");
                break;
            case R.id.mar:
                L.d("DrawerLinearLayout","mar");
                break;
            case R.id.view:
                L.d("DrawerLinearLayout","view");
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancel();

    }

    private void cancel() {
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }

    public void shareText() {
        String shareTitle = getResources().getString(R.string.share_title);
        String shareContent = getResources().getString(R.string.share_content);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        shareIntent.setType("text/plain");
        try {
            getContext().startActivity(Intent.createChooser(shareIntent, shareTitle));
        }catch (Exception e){}

    }
    private MyCountDownTimer timer;
    private  class MyCountDownTimer extends CountDownTimer {



        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);

        }

        @Override
        public void onTick(long millisUntilFinished) {
           onTicked( millisUntilFinished);

        }

        @Override
        public void onFinish() {
            onFinished( );
        }
    }

    private void onFinished() {
        L.d(TAG,"onFinish");
        llSleep.setVisibility(VISIBLE);
        sleepTime.setVisibility(GONE);
//        SharedPreferencesHelper.setSleepTime(0);
    }

    private void onTicked(long millisUntilFinished) {
        String formatTime = TimeHelper.formatTime(millisUntilFinished);
        tvTime.setText(formatTime);
    }

    public class UpdateSuccessListener implements OnTimeUpdateSuccessListener{
        @Override
        public void onSuccess() {
             initState();
        }
    }
}
