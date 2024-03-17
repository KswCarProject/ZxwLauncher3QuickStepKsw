package com.android.launcher3.recyclerview;

import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public interface ViewHolderBinder<T, V extends RecyclerView.ViewHolder> {
    public static final int POSITION_DEFAULT = 0;
    public static final int POSITION_FIRST = 1;
    public static final int POSITION_LAST = 2;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ListPosition {
    }

    void bindViewHolder(V v, T t, int i, List<Object> list);

    V newViewHolder(ViewGroup viewGroup);

    void unbindViewHolder(V v) {
    }
}
