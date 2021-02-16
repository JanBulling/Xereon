package com.xereon.xereon.util.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Custom override of {@link ListUpdateCallback}
 * Similar to
 * @see androidx.recyclerview.widget.AdapterListUpdateCallback
 *
 * Idea from:
 * https://proandroiddev.com/android-paging-library-with-multiple-view-type-68f85fe1222d
 */
public final class CustomAdapterListUpdateCallback implements ListUpdateCallback {

    @NonNull
    private final RecyclerView.Adapter<? extends RecyclerView.ViewHolder> mAdapter;

    private final int mOffset;

    public CustomAdapterListUpdateCallback(@NonNull RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter, int offset) {
        mAdapter = adapter;
        mOffset = offset;
    }

    @Override
    public void onInserted(int position, int count) {
        mAdapter.notifyItemRangeInserted(position + mOffset, count);
    }

    @Override
    public void onRemoved(int position, int count) {
        mAdapter.notifyItemRangeRemoved(position + mOffset, count);
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        mAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onChanged(int position, int count, Object payload) {
        mAdapter.notifyItemRangeChanged(position, count, payload);
    }
}
