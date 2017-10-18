package com.music.android.ui.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.music.android.R;

/**
 * Created by liuyun on 17/4/17.
 */

public class DeleteMusicDialog extends DialogFragment implements View.OnClickListener {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_delete_comfirm, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        Bundle bundle = getArguments();
        String content = bundle.getString("content");
        TextView cancel_TextView = (TextView) view.findViewById(R.id.cancel_TextView);
        cancel_TextView.setOnClickListener(this);
        TextView confirm_TextView = (TextView) view.findViewById(R.id.confirm_TextView);
        confirm_TextView.setOnClickListener(this);
        TextView content_TextView = (TextView) view.findViewById(R.id.content_TextView);
        content_TextView.setText(content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_TextView:
                getDialog().dismiss();
                break;
            case R.id.confirm_TextView:
                mOnConfirmListener.onConfirm(this);
                break;
        }
    }

    private OnConfirmListener mOnConfirmListener;

    public void setOnConfirmListener(OnConfirmListener mOnConfirmListener) {
        this.mOnConfirmListener = mOnConfirmListener;
    }

    public interface OnConfirmListener {
        void onConfirm(DialogFragment dialogFragment);
    }

}
