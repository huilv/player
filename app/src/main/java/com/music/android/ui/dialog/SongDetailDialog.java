package com.music.android.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.base.BaseDialogFragment;
import com.music.android.bean.MusicInfoBean;
import com.music.android.listener.OnSongUpdateListener;
import com.music.android.ui.widgets.TipToast;
import com.music.android.utils.Constants;
import com.music.android.utils.L;
import com.music.android.utils.LocalMusicQueryHelper;
import com.music.android.utils.SizeUtils;
import com.music.android.utils.TimeHelper;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hui.lv on 2017/3/31.
 */

public class SongDetailDialog extends BaseDialogFragment implements View.OnClickListener, TextWatcher {
    private final String TAG = "SongDetailDialog";
    private RelativeLayout nameShow;
    private LinearLayout nameEdit;
    private EditText edtName;
    private TextView tvLength;
    private TextView tvDuration;
    private TextView tvName;
    private TextView tvArtist;
    private LinearLayout llBottom;
    private TextView tvCancel;
    private MusicInfoBean bean;
    private TextView confirm;
    private OnSongUpdateListener listener;
    private InputMethodManager imm;
    private RelativeLayout artistShow;
    private LinearLayout artistEdit;
    private ImageView deleteArtist;
    private EditText edtArtist;
    private RelativeLayout cancelBottom;
    private TextView tvConfirm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = View.inflate(getActivity(), R.layout.dialog_song_detail, null);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        tvCancel = (TextView) view.findViewById(R.id.tvCancel);
        tvConfirm = (TextView) view.findViewById(R.id.tvConfirm);
        confirm = (TextView) view.findViewById(R.id.confirm);
        llBottom = (LinearLayout) view.findViewById(R.id.llBottom);
        tvLength = (TextView) view.findViewById(R.id.tvLength);
        tvDuration = (TextView) view.findViewById(R.id.tvDuration);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvArtist = (TextView) view.findViewById(R.id.tvArtist);
        deleteArtist = (ImageView) view.findViewById(R.id.delete_artist);
        edtName = (EditText) view.findViewById(R.id.edt);
        edtArtist = (EditText) view.findViewById(R.id.edtArtist);
        edtName.addTextChangedListener(this);
        ImageView delete = (ImageView) view.findViewById(R.id.delete);
        nameShow = (RelativeLayout) view.findViewById(R.id.rllEditor);
        nameEdit = (LinearLayout) view.findViewById(R.id.llEditor);
        artistEdit = (LinearLayout) view.findViewById(R.id.artist_edit);
        artistShow = (RelativeLayout) view.findViewById(R.id.artist_show);
        ImageView imgEditorName = (ImageView) view.findViewById(R.id.imgEditorName);
        ImageView imgEditorArtist = (ImageView) view.findViewById(R.id.imgEditorArtist);
        cancelBottom = (RelativeLayout) view.findViewById(R.id.cancelBottom);
        tvCancel.setOnClickListener(this);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        imgEditorName.setOnClickListener(this);
        imgEditorArtist.setOnClickListener(this);
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        nameShow.setOnClickListener(this);
        nameEdit.setOnClickListener(this);
        delete.setOnClickListener(this);
        deleteArtist.setOnClickListener(this);
        setWatcher();
        initData();
        return view;
    }


    private void initData() {
        Bundle bundle = getArguments();
        bean = (MusicInfoBean) bundle.getSerializable(Constants.MusicOthers.SONGDETAILDIALOG);
        tvName.setText(bean.title);
        tvArtist.setText(bean.singer);
        tvDuration.setText(TimeHelper.formatTime(bean.duration));
        tvLength.setText(SizeUtils.formatFileSize(bean.size));
        edtName.setHint(bean.title);
        edtArtist.setHint(bean.singer);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                startConfirm();
                break;
            case R.id.tvCancel:
                dismiss();
                break;
            case R.id.delete:
                edtName.setText("");
                break;
            case R.id.cancel:
                close();
                nameShow.setVisibility(View.GONE);
                nameEdit.setVisibility(View.VISIBLE);
                artistShow.setVisibility(View.GONE);
                artistEdit.setVisibility(View.VISIBLE);
                cancelBottom.setVisibility(View.VISIBLE);
                llBottom.setVisibility(View.GONE);
                break;
            case R.id.imgEditorName:
                editName();
                break;
            case R.id.imgEditorArtist:
                editArtist();
                break;
            case R.id.delete_artist:
                edtArtist.setText("");
                break;

        }
    }

    private void editArtist() {
        edtArtist.requestFocus();
        imm.showSoftInput(edtArtist, InputMethodManager.SHOW_FORCED);
        artistEdit.setVisibility(View.GONE);
        artistShow.setVisibility(View.VISIBLE);
        nameShow.setVisibility(View.GONE);//EditText
        nameEdit.setVisibility(View.VISIBLE);//edtName
        cancelBottom.setVisibility(View.GONE);
        llBottom.setVisibility(View.VISIBLE);
    }

    private void editName() {
        edtName.requestFocus();
        imm.showSoftInput(edtName, InputMethodManager.SHOW_FORCED);
        nameShow.setVisibility(View.VISIBLE);//EditText
        nameEdit.setVisibility(View.GONE);//edtName
        artistEdit.setVisibility(View.VISIBLE);
        artistShow.setVisibility(View.GONE);
        cancelBottom.setVisibility(View.GONE);
        llBottom.setVisibility(View.VISIBLE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post("onDestroy");
    }

    private void startConfirm() {
        if (nameShow.getVisibility() == View.VISIBLE) {
            updateName();
            return;
        }
        updateArtist();
    }

    private void updateArtist() {
        String name = edtArtist.getText().toString().trim();
        if (TextUtils.isEmpty(name) || name.equalsIgnoreCase(bean.singer)) {
            return;
        }
        LocalMusicQueryHelper.updateSinger(getContext(), name, bean.path);
        bean.newSinger = name;
        if (listener != null) {
            listener.onSongArtistUpdateSuccess(bean);
        }
        sendBroadcast();
        String success = MusicApp.context.getResources().getString(R.string.artist_success);
        TipToast.makeText(MusicApp.context, success, Toast.LENGTH_SHORT).show();
        dismiss();
    }

    private void updateName() {
        String name = edtName.getText().toString().trim();
        if (TextUtils.isEmpty(name) || name.equalsIgnoreCase(bean.title)) {
            return;
        }
        LocalMusicQueryHelper.updateName(getContext(), name, bean.path);
        bean.newTitle = name;
        if (listener != null) {
            listener.onSongNameUpdateSuccess(bean);
        }
        sendBroadcast();
        String success = MusicApp.context.getResources().getString(R.string.name_success);
        TipToast.makeText(MusicApp.context, success, Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s) || s.toString().equalsIgnoreCase(bean.title)) {
            tvConfirm.setTextColor(Color.parseColor("#787878"));
        } else {
            tvConfirm.setTextColor(Color.parseColor("#dadada"));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void setOnSongUpdateListener(OnSongUpdateListener listener) {
        this.listener = listener;
    }


    private void close() {
        if (edtName.hasFocus()) {
            imm.hideSoftInputFromWindow(edtName.getWindowToken(), 0);
            return;
        }
        imm.hideSoftInputFromWindow(edtArtist.getWindowToken(), 0);
    }


    private void setWatcher() {
        edtArtist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s) || s.toString().equalsIgnoreCase(bean.singer)) {
                    tvConfirm.setTextColor(Color.parseColor("#787878"));
                } else {
                    tvConfirm.setTextColor(Color.parseColor("#dadada"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void sendBroadcast() {
        L.d(TAG, bean.title + "sendBroadcast" + bean.newTitle);
        L.d(TAG, bean.singer + "sendBroadcast" + bean.newSinger);
        Intent i1 = new Intent(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_FLAG);
        i1.putExtra(Constants.BroadcastConstants.NOTIFICATION_BROADCAST_VALUE, Constants.BroadcastConstants.SONG_UPDATE_FLAG);
        i1.putExtra(Constants.BroadcastConstants.SONGDETAILDIALOG, bean);
        getActivity().sendBroadcast(i1);
    }
}
