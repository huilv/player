package com.music.android.ui.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.music.android.R;
import com.music.android.bean.HotListBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.managers.ImageLoaderManager;
import com.music.android.network.RequestService;
import com.music.android.ui.mvp.main.OnAddFragmentListener;
import com.music.android.utils.L;
import com.music.android.utils.PlayUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by liuyun on 17/4/24.
 */

public class IndexItemLayout extends LinearLayout implements View.OnClickListener {


    private SquareImageView logo1, logo2, logo3;

    private TextView title1, title2, title3;

    private TextView singer1, singer2, singer3;

    private View view1, view2, view3;

    private int position = -1;

    private List<MusicInfoBean> list;

    public OnAddFragmentListener mOnAddFragmentListener;

    public IndexItemLayout(Context context) {
        super(context);
        initView();
    }

    public IndexItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public IndexItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setOnAddFragmentListener(OnAddFragmentListener mOnAddFragmentListener) {
        this.mOnAddFragmentListener = mOnAddFragmentListener;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setData(@NonNull List<MusicInfoBean> list) {
        this.list = list;
        if (list.size() > 2) {
            if (list.get(0).artwork_url != null) {
                setLogo(logo1, list.get(0).artwork_url);
            } else {
                logo1.setImageResource(R.mipmap.icon_loading_default);
            }
            if (list.get(1).artwork_url != null) {
                setLogo(logo2, list.get(1).artwork_url);
            } else {
                logo2.setImageResource(R.mipmap.icon_loading_default);
            }
            if (list.get(2).artwork_url != null) {
                setLogo(logo3, list.get(2).artwork_url);
            } else {
                logo3.setImageResource(R.mipmap.icon_loading_default);
            }

            title1.setText(list.get(0).title);
            title2.setText(list.get(1).title);
            title3.setText(list.get(2).title);

            singer1.setText(list.get(0).singer);
            singer2.setText(list.get(1).singer);
            singer3.setText(list.get(2).singer);

        }
    }

    private void setLogo(SquareImageView logo, String url) {
        ImageLoaderManager.imageLoader(logo, R.mipmap.icon_loading_default, url);
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_index_item, this, true);

        view1 = view.findViewById(R.id.view1);
        view2 = view.findViewById(R.id.view2);
        view3 = view.findViewById(R.id.view3);

        logo1 = (SquareImageView) view1.findViewById(R.id.logo);
        logo2 = (SquareImageView) view2.findViewById(R.id.logo);
        logo3 = (SquareImageView) view3.findViewById(R.id.logo);

        title1 = (TextView) view1.findViewById(R.id.title);
        title2 = (TextView) view2.findViewById(R.id.title);
        title3 = (TextView) view3.findViewById(R.id.title);

        singer1 = (TextView) view1.findViewById(R.id.singer);
        singer2 = (TextView) view2.findViewById(R.id.singer);
        singer3 = (TextView) view3.findViewById(R.id.singer);

        view1.setOnClickListener(this);
        view2.setOnClickListener(this);
        view3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view1:
                click(0);
                break;
            case R.id.view2:
                click(1);
                break;
            case R.id.view3:
                click(2);
                break;

        }
    }

    private void click(int item) {
        switch (position) {
            case 0:
                getHotList(item);
                break;
            case 1:
                mOnAddFragmentListener.onAddFragment("rankFragment", list.get(item));
                break;
            case 2:
                mOnAddFragmentListener.onAddFragment("styleFragment", list.get(item));
                break;
            case 3:
                mOnAddFragmentListener.onAddFragment("audioFragment", list.get(item));
                break;
        }
    }


    private void getHotList(final int position) {
        Flowable<HotListBean> mFlowable = RequestService.createRequestService().getHotList();
        mFlowable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Subscriber<HotListBean>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(HotListBean hotListBean) {
                if (hotListBean.data != null && hotListBean.data.list != null) {
                    for (int i = 0; i < hotListBean.data.list.size(); i++) {
                        hotListBean.data.list.get(i).singer = hotListBean.data.list.get(i).user.username;
                    }
                    PlayUtils.play(hotListBean.data.list, position);
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
