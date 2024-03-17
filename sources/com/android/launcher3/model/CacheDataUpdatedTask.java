package com.android.launcher3.model;

import android.content.ComponentName;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;

public class CacheDataUpdatedTask extends BaseModelUpdateTask {
    public static final int OP_CACHE_UPDATE = 1;
    public static final int OP_SESSION_UPDATE = 2;
    private final int mOp;
    private final HashSet<String> mPackages;
    private final UserHandle mUser;

    public CacheDataUpdatedTask(int i, UserHandle userHandle, HashSet<String> hashSet) {
        this.mOp = i;
        this.mUser = userHandle;
        this.mPackages = hashSet;
    }

    public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
        IconCache iconCache = launcherAppState.getIconCache();
        ArrayList arrayList = new ArrayList();
        synchronized (bgDataModel) {
            bgDataModel.forAllWorkspaceItemInfos(this.mUser, new Consumer(iconCache, arrayList) {
                public final /* synthetic */ IconCache f$1;
                public final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void accept(Object obj) {
                    CacheDataUpdatedTask.this.lambda$execute$0$CacheDataUpdatedTask(this.f$1, this.f$2, (WorkspaceItemInfo) obj);
                }
            });
            allAppsList.updateIconsAndLabels(this.mPackages, this.mUser);
        }
        bindUpdatedWorkspaceItems(arrayList);
        bindApplicationsIfNeeded();
    }

    public /* synthetic */ void lambda$execute$0$CacheDataUpdatedTask(IconCache iconCache, ArrayList arrayList, WorkspaceItemInfo workspaceItemInfo) {
        ComponentName targetComponent = workspaceItemInfo.getTargetComponent();
        if (workspaceItemInfo.itemType == 0 && isValidShortcut(workspaceItemInfo) && targetComponent != null && this.mPackages.contains(targetComponent.getPackageName())) {
            iconCache.getTitleAndIcon(workspaceItemInfo, workspaceItemInfo.usingLowResIcon());
            arrayList.add(workspaceItemInfo);
        }
    }

    public boolean isValidShortcut(WorkspaceItemInfo workspaceItemInfo) {
        int i = this.mOp;
        if (i == 1) {
            return true;
        }
        if (i != 2) {
            return false;
        }
        return workspaceItemInfo.hasPromiseIconUi();
    }
}
