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
import com.music.android.data.local.MusicLocalDataSource;
import com.music.android.data.local.SongMenuHelper;
import com.music.android.listener.OnMenuNameUpdateListener;
import com.music.android.listener.OnMenuUpdateListener;
import com.music.android.ui.widgets.TipToast;
import com.music.android.utils.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hui.lv on 2017/3/17.
 */

public class RenameMenuDialog extends BaseDialogFragment implements View.OnClickListener, TextWatcher, Runnable {

    private final String TAG = "CreateMenuDialog";
    private EditText editText;
    private TextView confirm;
    private InputMethodManager imm;
    private OnMenuUpdateListener listener;
    private String oldName;
    //    private ArrayList<String> list;
    private boolean clickabled = false;
    private TextView tvSelector;
    private String nameId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = View.inflate(getActivity(), R.layout.dialog_rename_playlist, null);
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
        String value = editText.getText().toString().trim();
        if (!clickabled) {
            return;
        }
        if (oldName.equalsIgnoreCase(value)) {
            String success = MusicApp.context.getResources().getString(R.string.menu_create_success);
            TipToast.makeText(MusicApp.context, success, Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }
        update(value);
    }

    private void update(final String value) {

        int id = MusicLocalDataSource.getInstance(MusicApp.context).updatePlaylistName(value, nameId);
        String success;
        if (id <= 0) {
            success = MusicApp.context.getResources().getString(R.string.rename_failed);
            if (listener != null) {
                listener.onFailed();
            }
        } else {
            success = MusicApp.context.getResources().getString(R.string.rename_success);
            if (listener != null) {
                listener.onMenuNameUpdateSuccess(oldName, value);
            }
        }
        TipToast.makeText(MusicApp.context, success, Toast.LENGTH_SHORT).show();
        dismiss();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s)
//                ||list.contains(s.toString().trim())
                ) {
            clickabled = false;
            confirm.setTextColor(Color.parseColor("#787878"));
            return;
        }
        clickabled = true;
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


    public void setOnMenuUpdateListener(OnMenuUpdateListener listener) {
        this.listener = listener;
    }

    public void getData() {
        Bundle arguments = getArguments();
        //要修改的歌单名字
        oldName = arguments.getString(Constants.MusicOthers.MENU_NAME);
        //所有的歌单名字
//        list = arguments.getStringArrayList(Constants.MusicOthers.ALL_MENU_NAMES);
        nameId = arguments.getString(Constants.MusicOthers.PLAY_LIST_NAME_ID);
        editText.setHint(oldName);
    }
}
