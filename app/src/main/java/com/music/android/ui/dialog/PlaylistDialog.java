package com.music.android.ui.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.base.BaseDialogFragment;
import com.music.android.bean.DialogBundleBean;
import com.music.android.bean.PlaylistNameBean;
import com.music.android.bean.SongMenuBean;
import com.music.android.data.local.MusicLocalDataSource;
import com.music.android.data.local.SongMenuHelper;
import com.music.android.ui.adapter.PlayListAdapter;
import com.music.android.ui.widgets.PlayListItemDecoration;
import com.music.android.utils.Constants;
import com.music.android.utils.L;
import com.music.android.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hui.lv on 2017/3/17.
 */

public class PlaylistDialog extends BaseDialogFragment implements View.OnClickListener {
    private final String TAG = "PlaylistDialog";

    private PlayListAdapter adapter;
    private RecyclerView recyclerView;
    private DialogBundleBean bundleBean;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        bundleBean = (DialogBundleBean) getArguments().getSerializable(Constants.MusicOthers.NEWPLAYLISTDIALOG);
        View view = View.inflate(getActivity(), R.layout.dialog_playlist, null);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new PlayListItemDecoration(getContext(), SizeUtils.dp2Px(getContext(), 72)));
        adapter = new PlayListAdapter(this, bundleBean);
        recyclerView.setAdapter(adapter);
        cancel.setOnClickListener(this);
        initData();
        return view;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    private void initData() {

        MusicLocalDataSource.getInstance(MusicApp.context).getPlaylistNames()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<PlaylistNameBean>>() {
                    @Override
                    public void accept(List<PlaylistNameBean> playlistNameBeen) throws Exception {
                        adapter.setData(playlistNameBeen);
                    }
                });
    }

    private void setRecyclerViewHeight() {
        int itemCount = adapter.getItemCount();
        if (itemCount == 1) {
            recyclerView.getLayoutParams().height = SizeUtils.dp2Px(getContext(), 60);
        } else if (itemCount == 2) {
            recyclerView.getLayoutParams().height = SizeUtils.dp2Px(getContext(), 115);
        } else if (itemCount == 3) {
            recyclerView.getLayoutParams().height = SizeUtils.dp2Px(getContext(), 171);
        } else {
            recyclerView.getLayoutParams().height = SizeUtils.dp2Px(getContext(), 232);
        }
    }

}
