package com.music.android.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.music.android.R;
import com.music.android.base.BaseDialogFragment;
import com.music.android.ui.widgets.DrawerLinearLayout;
import com.music.android.utils.AnalyticsConstants;
import com.music.android.utils.AnalyticsUtils;
import com.music.android.utils.L;
import com.music.android.utils.SharedPreferencesHelper;
import com.music.android.utils.SizeUtils;

/**
 * Created by hui.lv on 2017/4/7.
 */

public class SleepTimeDialog extends BaseDialogFragment implements View.OnClickListener ,TextWatcher{
    private final String TAG = "SleepTimeDialog";
    private View view;
    private TextView itemOne;
    private TextView itemTwo;
    private TextView itemThree;
    private TextView itemFive;
    private TextView cancel;
    private RelativeLayout editParent;
    private EditText edit;
    private ImageView delete;

    private TextView itemFour;
    private Drawable circleOne;
    private Drawable circleTwo;
    private InputMethodManager imm;

    private int time;
    private TextView confirm;
    private DrawerLinearLayout.UpdateSuccessListener listener;
    private String customize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView();
        return view;
    }

    private void initView() {
        circleOne = getResources().getDrawable(R.drawable.circle_one);
        circleOne.setBounds(0, 0, SizeUtils.dp2Px(getContext(),16), SizeUtils.dp2Px(getContext(),16));
        circleTwo = getResources().getDrawable(R.drawable.circle_two);
        circleTwo.setBounds(0, 0, SizeUtils.dp2Px(getContext(),16), SizeUtils.dp2Px(getContext(),16));
        view = View.inflate(getActivity(), R.layout.dialog_sleep_time, null);
        itemOne = (TextView) view.findViewById(R.id.itemOne);
        itemTwo = (TextView) view.findViewById(R.id.itemTwo);
        itemThree = (TextView) view.findViewById(R.id.itemThree);
        itemFive = (TextView) view.findViewById(R.id.itemFive);
        itemFour = (TextView) view.findViewById(R.id.itemFour);
        cancel = (TextView) view.findViewById(R.id.cancel);
        confirm = (TextView) view.findViewById(R.id.confirm);
        editParent = (RelativeLayout) view.findViewById(R.id.editParent);
        edit = (EditText) view.findViewById(R.id.edt);
        delete = (ImageView) view.findViewById(R.id.delete);
        cancel.setOnClickListener(this);
        delete.setOnClickListener(this);
        itemOne.setOnClickListener(this);
        itemTwo.setOnClickListener(this);
        itemThree.setOnClickListener(this);
        itemFive.setOnClickListener(this);
        itemFour.setOnClickListener(this);
        edit.addTextChangedListener(this);
        confirm.setOnClickListener(this);
        initAllState();
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.confirm:
                dismiss();
                Log.i(TAG,"----TIME-----"+time);
                long sleepTime=(time==0?0:time*60*1000+System.currentTimeMillis());
                SharedPreferencesHelper.setSleepTime(sleepTime);
                AnalyticsUtils.vMusicClick(AnalyticsConstants.MENU, AnalyticsConstants.Action.CLICK_SLEEPTIME,""+sleepTime);
                if(listener!=null){
                    listener.onSuccess();
                }
                break;
            case R.id.itemOne:
                time=20;
                closeKeyboard();
                changeColorToWhite();
                itemOne.setTextColor(Color.parseColor("#d5ff44"));
                itemOne.setCompoundDrawables(circleTwo,null,null,null);
                break;
            case R.id.itemTwo:
                time=30;
                closeKeyboard();
                changeColorToWhite();
                itemTwo.setTextColor(Color.parseColor("#d5ff44"));
                itemTwo.setCompoundDrawables(circleTwo,null,null,null);
                break;
            case R.id.itemThree:
                time=60;
                closeKeyboard();
                changeColorToWhite();
                itemThree.setTextColor(Color.parseColor("#d5ff44"));
                itemThree.setCompoundDrawables(circleTwo,null,null,null);
                break;
            case R.id.itemFour:
                edit.requestFocus();
                changeColorToWhite();
                imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edit, InputMethodManager.SHOW_FORCED);
                editParent.setVisibility(View.VISIBLE);
                itemFour.setText("");
                itemFour.setCompoundDrawables(circleTwo,null,null,null);
                break;
            case R.id.itemFive:
                  time=0;
                 closeKeyboard();
                 changeColorToWhite();
                 initAllState();
                break;
            case R.id.delete:
                edit.setText("");
                break;
            default:
                break;

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        circleOne=null;
        circleTwo=null;

    }

    private void changeColorToWhite() {
        editParent.setVisibility(View.GONE);
        itemOne.setTextColor(Color.WHITE);
        itemTwo.setTextColor(Color.WHITE);
        itemThree.setTextColor(Color.WHITE);
        itemFive.setTextColor(Color.WHITE);
        itemFour.setTextColor(Color.WHITE);
        customize = getResources().getString(R.string.sleep_four);
        itemFour.setText(customize);
        itemOne.setCompoundDrawables(circleOne,null,null,null);
        itemTwo.setCompoundDrawables(circleOne,null,null,null);
        itemThree.setCompoundDrawables(circleOne,null,null,null);
        itemFive.setCompoundDrawables(circleOne,null,null,null);
        itemFour.setCompoundDrawables(circleOne,null,null,null);
    }

    private void initAllState() {
        changeColorToWhite();
        itemFive.setTextColor(Color.parseColor("#d5ff44"));
        itemFive.setCompoundDrawables(circleTwo,null,null,null);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(TextUtils.isEmpty(s)){
            return;
        }
        L.d(TAG,"onTextChanged---"+time);
        time = Integer.parseInt(s.toString());
        if(time<=0){
            time=1;
            edit.setText("1");
            edit.setSelection(("1").length());
            return;
        }else if(time>720){
            time=720;
            edit.setText("720");
            edit.setSelection(("720").length());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    public void setUpdateSuccessListener(DrawerLinearLayout.UpdateSuccessListener listener) {
        this.listener = listener;
    }
}
