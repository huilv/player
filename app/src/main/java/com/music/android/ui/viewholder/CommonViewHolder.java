package com.music.android.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.music.android.R;

/**
 * Created by liuyun on 17/5/31.
 */

public class CommonViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout ad_container;

    public CommonViewHolder(View itemView) {
        super(itemView);
        ad_container = (RelativeLayout) itemView.findViewById(R.id.ad_container);
    }

}
