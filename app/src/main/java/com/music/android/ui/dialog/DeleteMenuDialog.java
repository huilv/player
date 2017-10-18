package com.music.android.ui.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.base.BaseDialogFragment;
import com.music.android.bean.MusicInfoBean;
import com.music.android.data.local.MusicLocalDataSource;
import com.music.android.data.local.SongMenuHelper;
import com.music.android.listener.OnMenuDeleteListener;
import com.music.android.listener.OnMenuSuccessListener;
import com.music.android.listener.OnMenuUpdateListener;
import com.music.android.ui.widgets.TipToast;
import com.music.android.utils.Constants;
import com.music.android.utils.L;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hui.lv on 2017/3/17.
 */

public class DeleteMenuDialog extends BaseDialogFragment implements View.OnClickListener {

    private final String TAG = "DeleteMenuDialog";
    private OnMenuUpdateListener listener;
    private String name;
    private String nameId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = View.inflate(getActivity(), R.layout.dialog_delete_menu, null);
        getDialog().setCanceledOnTouchOutside(false);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        getData();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.confirm:
                delete();
                break;

            default:
                break;
        }
    }


    private void delete() {
        String success;
        int id1 = MusicLocalDataSource.getInstance(MusicApp.context).deletePlayListName(nameId);
        int id2 = MusicLocalDataSource.getInstance(MusicApp.context).deletePlayListJunction(nameId);
        L.d("DeleteMenu", " id1 : " + id1);
        L.d("DeleteMenu", " id2 : " + id2);
        if (id1 > 0) {
            success = MusicApp.context.getResources().getString(R.string.delete_success);
            if (listener != null) {
                listener.onMenuDeleteSuccess(name);
            }
        } else {
            success = MusicApp.context.getResources().getString(R.string.delete_failed);
            if (listener != null) {
                listener.onFailed();
            }
        }
        TipToast.makeText(MusicApp.context, success, Toast.LENGTH_SHORT).show();
        dismiss();
    }


    public void setOnMenuUpdateListener(OnMenuUpdateListener listener) {
        this.listener = listener;
    }

    public void getData() {
        Bundle arguments = getArguments();
        name = arguments.getString(Constants.MusicOthers.MENU_NAME);
        nameId = arguments.getString(Constants.MusicOthers.PLAY_LIST_NAME_ID);
    }
}
