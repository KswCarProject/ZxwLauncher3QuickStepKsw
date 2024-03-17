package com.android.launcher3.appprediction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.quickstep.InstantAppResolverImpl;

public class InstantAppItemInfo extends AppInfo {
    public InstantAppItemInfo(Intent intent, String str) {
        this.intent = intent;
        this.componentName = new ComponentName(str, InstantAppResolverImpl.COMPONENT_CLASS_MARKER);
    }

    public ComponentName getTargetComponent() {
        return this.componentName;
    }

    public WorkspaceItemInfo makeWorkspaceItem(Context context) {
        WorkspaceItemInfo makeWorkspaceItem = super.makeWorkspaceItem(context);
        makeWorkspaceItem.itemType = 0;
        makeWorkspaceItem.status = 14;
        makeWorkspaceItem.getIntent().setPackage(this.componentName.getPackageName());
        return makeWorkspaceItem;
    }
}
