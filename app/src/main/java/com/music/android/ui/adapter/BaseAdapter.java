package com.music.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.music.android.bean.MusicInfoBean;
import com.music.android.listener.OnItemClickListener;
import com.music.android.ui.mvp.main.OnAddFragmentListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liuyun on 17/3/20.
 */

public class BaseAdapter<T extends ViewHolder, D> extends RecyclerView.Adapter<T> {

    public List<D> data = new ArrayList<>();

    public OnItemClickListener mOnItemClickListener;

    public LayoutInflater mLayoutInflater;

    public Context mContext;

    public OnAddFragmentListener mOnAddFragmentListener;

    public BaseAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setOnAddFragmentListener(OnAddFragmentListener mOnAddFragmentListener) {
        this.mOnAddFragmentListener = mOnAddFragmentListener;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setData(List<D> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void addItems(List<D> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void addItem(D data) {
        this.data.add(data);
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public List<D> getData() {
        return data;
    }

    public void setItem(D item) {
        this.data.clear();
        this.data.add(item);
        notifyDataSetChanged();
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(T holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
