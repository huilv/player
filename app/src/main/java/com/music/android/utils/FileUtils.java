package com.music.android.utils;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import com.music.android.MusicApp;
import com.music.android.ui.widgets.ControlPopupWindow;

import java.io.File;

/**
 * Created by liuyun on 17/3/31.
 */

public class FileUtils {

    private static void deleteFile(String path, ControlPopupWindow.OnDeleteFileListener mOnDeleteFileListener) {
        File file = new File(path);
        if (file.delete()) {
            mOnDeleteFileListener.onDelete(true, path);
        } else {
            mOnDeleteFileListener.onDelete(false, path);
        }

    }

    public static void scanFileAsync(String path, final ControlPopupWindow.OnDeleteFileListener mOnDeleteFileListener) {
        MediaScannerConnection.scanFile(MusicApp.context,
                new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        if (MusicApp.context.getContentResolver().delete(uri, null, null) > 0) {
                            deleteFile(path, mOnDeleteFileListener);
                        }

                    }
                });

    }

}
