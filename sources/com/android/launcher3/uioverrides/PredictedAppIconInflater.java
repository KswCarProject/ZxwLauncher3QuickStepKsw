package com.android.launcher3.uioverrides;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.R;
import com.android.launcher3.model.data.WorkspaceItemInfo;

public class PredictedAppIconInflater {
    public static View inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, WorkspaceItemInfo workspaceItemInfo) {
        PredictedAppIcon predictedAppIcon = (PredictedAppIcon) layoutInflater.inflate(R.layout.predicted_app_icon, viewGroup, false);
        predictedAppIcon.applyFromWorkspaceItem(workspaceItemInfo);
        return predictedAppIcon;
    }
}
