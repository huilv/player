package com.music.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.music.android.base.BaseBean;

/**
 * Created by hui.lv on 2017/4/19.
 */

public class PathBean extends BaseBean implements Parcelable {

    public String path;

    public PathBean(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
    }

    public static final Parcelable.Creator<PathBean> CREATOR = new Parcelable.Creator<PathBean>() {

        @Override
        public PathBean createFromParcel(Parcel source) {
            return new PathBean(source.readString());
        }

        @Override
        public PathBean[] newArray(int size) {
            return new PathBean[size];
        }
    };
}
