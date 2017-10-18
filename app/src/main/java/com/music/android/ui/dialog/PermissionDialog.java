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
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.cover.CoverSdk;
import com.music.android.R;
import com.music.android.managers.ImageLoaderManager;

/**
 * Created by liuyun on 17/6/2.
 */

public class PermissionDialog extends DialogFragment implements View.OnClickListener{

    private ImageView gif_ImageView;

    private Button got_it_btn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(false);
        return inflater.inflate(R.layout.layout_usage, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        gif_ImageView = (ImageView) view.findViewById(R.id.gif_ImageView);
        got_it_btn = (Button) view.findViewById(R.id.got_it_btn);
        ImageLoaderManager.imageLoaderAssents(gif_ImageView, "music.gif");
        got_it_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        CoverSdk.goToUsageStatsSettings(getContext());
        dismiss();
    }

}
