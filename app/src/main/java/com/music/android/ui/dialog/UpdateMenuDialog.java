package com.music.android.ui.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.music.android.R;
import com.music.android.base.BaseDialogFragment;
import com.music.android.listener.OnMenuUpdateListener;
import com.music.android.utils.AnalyticsConstants;
import com.music.android.utils.AnalyticsUtils;
import com.music.android.utils.Constants;

import java.util.ArrayList;

/**
 * Created by hui.lv on 2017/3/22.
 */

public class UpdateMenuDialog extends BaseDialogFragment implements View.OnClickListener {

    private final String TAG = "UpdateMenuDialog";
    private String oldName;
    private String nameId;
    private OnMenuUpdateListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = View.inflate(getActivity(), R.layout.dialog_menu_update, null);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView tvRename = (TextView) view.findViewById(R.id.tvRename);
        TextView tvDelete = (TextView) view.findViewById(R.id.tvDelete);
        cancel.setOnClickListener(this);
        tvRename.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        getData();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.tvDelete:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYLIST, AnalyticsConstants.Action.CLICK_EMENU, AnalyticsConstants.Value.DELETE);
                dismiss();
                DeleteMenuDialog menuDialog = new DeleteMenuDialog();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.MusicOthers.MENU_NAME, oldName);
                bundle.putString(Constants.MusicOthers.PLAY_LIST_NAME_ID, nameId);
                menuDialog.setArguments(bundle);
                menuDialog.setOnMenuUpdateListener(listener);
                menuDialog.show(getActivity().getSupportFragmentManager(), DeleteMenuDialog.class.getSimpleName());
                break;
            case R.id.tvRename:
                AnalyticsUtils.vMusicClick(AnalyticsConstants.PLAYLIST, AnalyticsConstants.Action.CLICK_EMENU, AnalyticsConstants.Value.RENAME);
                dismiss();
                RenameMenuDialog dialog = new RenameMenuDialog();
                Bundle bd = new Bundle();
                bd.putString(Constants.MusicOthers.MENU_NAME, oldName);
                bd.putString(Constants.MusicOthers.PLAY_LIST_NAME_ID, nameId);
                dialog.setArguments(bd);
                dialog.setOnMenuUpdateListener(listener);
                dialog.show(getActivity().getSupportFragmentManager(), RenameMenuDialog.class.getSimpleName());
                break;
        }

    }

    public void getData() {
        Bundle arguments = getArguments();
        oldName = arguments.getString(Constants.MusicOthers.MENU_NAME);
        nameId = arguments.getString(Constants.MusicOthers.PLAY_LIST_NAME_ID);
        if (oldName == null || nameId == null) {
            throw new RuntimeException("Can not be null !!");
        }
    }

    public void setOnMenuUpdateListener(OnMenuUpdateListener listener) {
        this.listener = listener;
    }

//    private void updateMenu() {
//        UpdateMenuDialog dialog=new UpdateMenuDialog();
//        Bundle bundle=new Bundle();
//        bundle.putString(Constants.MusicOthers.MENU_NAME,);
//        ArrayList<String> list=new ArrayList<>();
//        list.add("aa");list.add("bb");list.add("cc");list.add("dd");
//        bundle.putStringArrayList(Constants.MusicOthers.ALL_MENU_NAMES,);
//        dialog.setArguments(bundle);
//        dialog.setOnMenuUpdateListener(new OnMenuUpdateListener() {
//            @Override
//            public void onMenuNameUpdateSuccess(String oldName,String newName) {
//                L.d(TAG,oldName+"----onMenuNameUpdateSuccess----"+newName);
//            }
//
//            @Override
//            public void onMenuDeleteSuccess(String name) {
//                L.d(TAG,"onMenuDeleteSuccess----"+name);
//            }
//
//            @Override
//            public void onFailed() {
//
//            }
//        });
//        dialog.show(getSupportFragmentManager(),UpdateMenuDialog.class.getSimpleName());
//    }
}
