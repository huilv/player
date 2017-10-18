package com.music.android.ui.widgets.playinglayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.music.android.R;
import com.music.android.managers.ImageLoaderManager;
import com.music.android.utils.SizeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CTer on 17/5/2.
 */

public class MusicImageAdapter extends PagerAdapter {

    public static final int INIT_INDEX = Integer.MAX_VALUE / 2;

    private Context mContext;
    private Bitmap mCurrentBitmap;
    private int mChildCount = 0;

    public MusicImageAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        RelativeLayout disc = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_disc, null, false);

        ImageView imageMusic = (ImageView) disc.findViewById(R.id.imgMusic);

        float viewX = imageMusic.getX();

//        if (viewX > 0 && viewX < SizeUtils.getScreenWidth(mContext)) {

        if (mCurrentBitmap != null) {
            imageMusic.setImageBitmap(mCurrentBitmap);
//            }
        }
//        else {
//            imageMusic.setImageResource(R.mipmap.icon_loading_default);
//        }

        container.addView(disc);
        return disc;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    public void setCurrentBitmap(Bitmap bitmap) {
        mCurrentBitmap = bitmap;
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if (mChildCount > 0) {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

}
