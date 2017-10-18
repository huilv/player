package com.music.android.ui.dialog;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.base.BaseDialogFragment;
import com.music.android.bean.DialogBundleBean;
import com.music.android.bean.MusicInfoBean;
import com.music.android.bean.PlaylistNameBean;
import com.music.android.data.local.MusicLocalDataSource;
import com.music.android.data.local.MusicPersistenceContract;
import com.music.android.data.local.SongMenuHelper;
import com.music.android.listener.OnMenuSuccessListener;
import com.music.android.managers.AnalyticsManager;
import com.music.android.ui.widgets.TipToast;
import com.music.android.utils.Constants;
import com.music.android.utils.L;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hui.lv on 2017/3/17.
 */

public class CreateMenuDialog extends BaseDialogFragment implements View.OnClickListener, TextWatcher, Runnable {
    private final String TAG = "CreateMenuDialog";
    private EditText editText;
    private TextView confirm;
    private InputMethodManager imm;
    private OnMenuSuccessListener listener;
    private DialogBundleBean bundleBean;
    private TextView tvSelector;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = View.inflate(getActivity(), R.layout.dialog_create_playlist, null);
        getDialog().setCanceledOnTouchOutside(false);
        editText = (EditText) view.findViewById(R.id.edt);
        ImageView delete = (ImageView) view.findViewById(R.id.delete);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        confirm = (TextView) view.findViewById(R.id.confirm);
        tvSelector = (TextView) view.findViewById(R.id.tvSelector);
        delete.setOnClickListener(this);
        cancel.setOnClickListener(this);
        tvSelector.setOnClickListener(this);
        editText.addTextChangedListener(this);
        editText.post(this);
        getData();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.tvSelector:
                startConfirm();
                break;
            case R.id.delete:
                editText.setText("");
                break;
            default:
                break;
        }
    }

    private void startConfirm() {

        MusicInfoBean bean = new MusicInfoBean();
        bean.title = bundleBean.title;
        bean.singer = bundleBean.singer;
        bean.title = bundleBean.title;
        bean.path = bundleBean.path;
        bean.artwork_url = bundleBean.artwork_url;

        String value = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(value)) {
            long id = insertPlaylistName(value);
            if (id > 0 && bundleBean.title != null) {
                PlaylistNameBean playlistNameBean = new PlaylistNameBean();
                playlistNameBean.name = "";
                playlistNameBean.nameId = (int) id;

                if (MusicLocalDataSource.getInstance(MusicApp.context).insertPlaylistNameAndMusic(playlistNameBean, bean) > 0) {
                    EventBus.getDefault().post(Constants.EventBusConstants.EVENT_UPDATE_UI);
                }
            }
        }

    }

    private long insertPlaylistName(String name) {
        String success;
        long id = -1;
        if (MusicLocalDataSource.getInstance(MusicApp.context).isPlaylistNameExist(name)) {
            success = MusicApp.context.getResources().getString(R.string.menu_exist);
        } else {
            id = MusicLocalDataSource.getInstance(MusicApp.context).insertPlaylistName(name);
            if (id > 0) {
                success = MusicApp.context.getResources().getString(R.string.menu_create_success);
                EventBus.getDefault().post(Constants.EventBusConstants.EVENT_UPDATE_UI);
                AnalyticsManager.getInstance().playlistCreation(bundleBean.comeFrom);
                if (listener != null) {
                    listener.onSuccess();
                }
            } else {
                success = MusicApp.context.getResources().getString(R.string.add_failed);
            }
        }
        TipToast.makeText(MusicApp.context, success, Toast.LENGTH_SHORT).show();
        dismiss();
        return id;
    }

    private void insert(final MusicInfoBean bean) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                e.onNext(SongMenuHelper.getDefault(getContext()).insert(bean));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean s) throws Exception {
                        String success;
                        if (s) {
                            success = MusicApp.context.getResources().getString(R.string.menu_exist);
                        } else {
                            success = MusicApp.context.getResources().getString(R.string.menu_create_success);
                            EventBus.getDefault().post(Constants.EventBusConstants.EVENT_UPDATE_UI);
                            AnalyticsManager.getInstance().playlistCreation(bundleBean.comeFrom);
                            if (listener != null) {
                                listener.onSuccess();
                            }
                        }
                        TipToast.makeText(MusicApp.context, success, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s)) {
            confirm.setTextColor(Color.parseColor("#787878"));
            return;
        }
        confirm.setTextColor(Color.parseColor("#dadada"));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void run() {
        editText.requestFocus();
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post("onDestroy");
    }


    public void setOnMenuCreateSuccessListener(OnMenuSuccessListener listener) {
        this.listener = listener;
    }

    public void getData() {
        Bundle arguments = getArguments();
        bundleBean = (DialogBundleBean) arguments.getSerializable(Constants.MusicOthers.NEWPLAYLISTDIALOG);
    }
}
