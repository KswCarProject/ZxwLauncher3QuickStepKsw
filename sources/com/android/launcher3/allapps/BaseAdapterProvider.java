package com.android.launcher3.allapps;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.android.launcher3.allapps.BaseAllAppsAdapter;

public abstract class BaseAdapterProvider {
    public int getItemsPerRow(int i, int i2) {
        return i2;
    }

    public int[] getSupportedItemsPerRowArray() {
        return new int[0];
    }

    public abstract boolean isViewSupported(int i);

    public abstract void onBindView(BaseAllAppsAdapter.ViewHolder viewHolder, int i);

    public abstract BaseAllAppsAdapter.ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup, int i);
}
