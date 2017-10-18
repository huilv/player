package com.music.android.ui.dialog;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.music.android.R;
import com.music.android.base.BaseDialogFragment;
import com.music.android.ui.widgets.TipToast;
import com.music.android.utils.AnalyticsConstants;
import com.music.android.utils.AnalyticsUtils;

/**
 * Created by hui.lv on 2017/3/17.
 */

public class EvaluateDialog extends BaseDialogFragment implements View.OnClickListener {

    private int count = 0;
    private ImageView starOne;
    private ImageView starTwo;
    private ImageView starThree;
    private ImageView starFour;
    private ImageView starFive;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = View.inflate(getActivity(), R.layout.dialog_evaluate, null);
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        starOne = (ImageView) view.findViewById(R.id.starOne);
        starTwo = (ImageView) view.findViewById(R.id.starTwo);
        starThree = (ImageView) view.findViewById(R.id.starThree);
        starFour = (ImageView) view.findViewById(R.id.starFour);
        starFive = (ImageView) view.findViewById(R.id.starFive);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        starOne.setOnClickListener(this);
        starTwo.setOnClickListener(this);
        starThree.setOnClickListener(this);
        starFour.setOnClickListener(this);
        starFive.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.MENU, AnalyticsConstants.Action.CLICK_RATEUS, AnalyticsConstants.Value.CANCEL);
                dismiss();
                break;
            case R.id.confirm:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.MENU, AnalyticsConstants.Action.CLICK_RATEUS, AnalyticsConstants.Value.CONFIRM);
                jumpActivity();
                break;
            case R.id.starOne:
                count=1;
                setSolidStarCount();
                break;
            case R.id.starTwo:
                count=2;
                setSolidStarCount();
                break;
            case R.id.starThree:
                count=3;
                setSolidStarCount();
                break;
            case R.id.starFour:
                count=4;
                setSolidStarCount();
                break;
            case R.id.starFive:
                count=5;
                setSolidStarCount();
                break;
            default:
                break;
        }
    }

    private void jumpActivity() {
        if(count==5){
            goToGooglePlay();
        }else{
            String value = getResources().getString(R.string.evalution);
            TipToast.makeText(getContext(),value,Toast.LENGTH_LONG).show();
        }
       dismiss();
    }
    private void goToGooglePlay() {
        final String appPackageName = getActivity().getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
    public void setSolidStarCount() {
        if(count==1){
            starOne.setImageResource(R.drawable.star_solid);
            starTwo.setImageResource(R.drawable.star_modest);
            starThree.setImageResource(R.drawable.star_modest);
            starFour.setImageResource(R.drawable.star_modest);
            starFive.setImageResource(R.drawable.star_modest);
        }else if(count==2){
            starOne.setImageResource(R.drawable.star_solid);
            starTwo.setImageResource(R.drawable.star_solid);
            starThree.setImageResource(R.drawable.star_modest);
            starFour.setImageResource(R.drawable.star_modest);
            starFive.setImageResource(R.drawable.star_modest);
        }else if(count==3){
            starOne.setImageResource(R.drawable.star_solid);
            starTwo.setImageResource(R.drawable.star_solid);
            starThree.setImageResource(R.drawable.star_solid);
            starFour.setImageResource(R.drawable.star_modest);
            starFive.setImageResource(R.drawable.star_modest);
        }else if(count==4){
            starOne.setImageResource(R.drawable.star_solid);
            starTwo.setImageResource(R.drawable.star_solid);
            starThree.setImageResource(R.drawable.star_solid);
            starFour.setImageResource(R.drawable.star_solid);
            starFive.setImageResource(R.drawable.star_modest);
        }else if(count==5){
            starOne.setImageResource(R.drawable.star_solid);
            starTwo.setImageResource(R.drawable.star_solid);
            starThree.setImageResource(R.drawable.star_solid);
            starFour.setImageResource(R.drawable.star_solid);
            starFive.setImageResource(R.drawable.star_solid);
        }

    }
}
