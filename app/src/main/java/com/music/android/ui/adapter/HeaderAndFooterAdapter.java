package com.music.android.ui.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liuyun on 17/6/1.
 */

public abstract class HeaderAndFooterAdapter<D> extends BaseAdapter<RecyclerView.ViewHolder, D> {

    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;
    private static final int BASE_ITEM_TYPE_NORMAL = 300000;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mAdViews = new SparseArrayCompat<>();

    private int normalViewId;

    public HeaderAndFooterAdapter(@NonNull Context context, @LayoutRes int normalViewId) {
        super(context);
        this.normalViewId = normalViewId;
    }

    public void addFooterView(@NonNull View view) {
        mFooterViews.put(BASE_ITEM_TYPE_FOOTER + mFooterViews.size(), view);
    }

    public void addHeaderView(@NonNull View view) {
        mHeaderViews.put(BASE_ITEM_TYPE_HEADER + mHeaderViews.size(), view);
    }

    public void addAdView(@NonNull int position, @NonNull View view) {
        mAdViews.put(position + getHeaderViewsCount() + getAdCurrentItemPosition(position) - 1, view);
    }

    public void addAdViewAndNotify(@NonNull int position, @NonNull View view) {
        addAdView(position, view);
        notifyDataSetChanged();
    }

    public int getFooterViewsCount() {
        return mFooterViews.size();
    }

    public int getHeaderViewsCount() {
        return mHeaderViews.size();
    }

    public int getRealItemCount() {
        return data.size();
    }

    public int getAdItemCount() {
        return mAdViews.size();
    }

    public boolean isHeaderViewPos(int position) {
        return position < getHeaderViewsCount();
    }

    public boolean isFooterViewPos(int position) {
        return position >= getHeaderViewsCount() + getRealItemCount() + getAdItemCount();
    }

    public boolean isAdViewPos(int position) {
        for (int i = 0; i < mAdViews.size(); i++) {
            if (mAdViews.keyAt(i) == position) {
                return true;
            }
        }
        return false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            return new HeaderAndFooterViewHolder(mHeaderViews.get(viewType));
        } else if (mFooterViews.get(viewType) != null) {
            return new HeaderAndFooterViewHolder(mFooterViews.get(viewType));
        } else if (mAdViews.get(viewType) != null) {
            return new HeaderAndFooterViewHolder(mAdViews.get(viewType));
        } else {
            return onNormalViewHolder(mLayoutInflater.inflate(normalViewId, parent, false), viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderViewPos(position)) {
            return;
        } else if (isFooterViewPos(position)) {
            return;
        } else if (isAdViewPos(position)) {
            return;
        } else {
            onNormalBindViewHolder(holder, position - getHeaderViewsCount() - getAdCurrentItemPosition(position));
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return mFooterViews.keyAt(position - getHeaderViewsCount() - getRealItemCount() - getAdItemCount());
        } else if (isAdViewPos(position)) {
            return position;
        } else {
            return BASE_ITEM_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderViewsCount() + getFooterViewsCount() + getAdItemCount() + getRealItemCount();
    }

    private int getAdCurrentItemPosition(int position) {
        int index = 0;
        for (int i = 0; i < mAdViews.size(); i++) {
            if (position >= mAdViews.keyAt(i)) {
                index++;
            }
        }
        return index;
    }

    public abstract void onNormalBindViewHolder(RecyclerView.ViewHolder viewHolder, int position);

    public abstract RecyclerView.ViewHolder onNormalViewHolder(View view, int viewType);

    private final class HeaderAndFooterViewHolder extends RecyclerView.ViewHolder {

        public HeaderAndFooterViewHolder(View itemView) {
            super(itemView);
        }
    }

}
