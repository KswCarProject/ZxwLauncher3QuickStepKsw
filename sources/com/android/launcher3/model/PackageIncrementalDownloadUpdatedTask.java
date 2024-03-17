package com.android.launcher3.model;

import android.os.UserHandle;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.pm.PackageInstallInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PackageIncrementalDownloadUpdatedTask extends BaseModelUpdateTask {
    private final String mPackageName;
    private final int mProgress;
    private final UserHandle mUser;

    public PackageIncrementalDownloadUpdatedTask(String str, UserHandle userHandle, float f) {
        this.mUser = userHandle;
        this.mProgress = ((double) (1.0f - f)) > 0.001d ? (int) (f * 100.0f) : 100;
        this.mPackageName = str;
    }

    public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
        PackageInstallInfo packageInstallInfo = new PackageInstallInfo(this.mPackageName, 2, this.mProgress, this.mUser);
        synchronized (allAppsList) {
            List<AppInfo> updatePromiseInstallInfo = allAppsList.updatePromiseInstallInfo(packageInstallInfo);
            if (!updatePromiseInstallInfo.isEmpty()) {
                for (AppInfo next : updatePromiseInstallInfo) {
                    next.runtimeStatusFlags &= -1025;
                    scheduleCallbackTask(new LauncherModel.CallbackTask() {
                        public final void execute(BgDataModel.Callbacks callbacks) {
                            callbacks.bindIncrementalDownloadProgressUpdated(AppInfo.this);
                        }
                    });
                }
            }
            bindApplicationsIfNeeded();
        }
        ArrayList arrayList = new ArrayList();
        synchronized (bgDataModel) {
            bgDataModel.forAllWorkspaceItemInfos(this.mUser, new Consumer(packageInstallInfo, arrayList) {
                public final /* synthetic */ PackageInstallInfo f$1;
                public final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void accept(Object obj) {
                    PackageIncrementalDownloadUpdatedTask.this.lambda$execute$1$PackageIncrementalDownloadUpdatedTask(this.f$1, this.f$2, (WorkspaceItemInfo) obj);
                }
            });
        }
        bindUpdatedWorkspaceItems(arrayList);
    }

    public /* synthetic */ void lambda$execute$1$PackageIncrementalDownloadUpdatedTask(PackageInstallInfo packageInstallInfo, ArrayList arrayList, WorkspaceItemInfo workspaceItemInfo) {
        if (this.mPackageName.equals(workspaceItemInfo.getTargetPackage())) {
            workspaceItemInfo.runtimeStatusFlags &= -1025;
            workspaceItemInfo.setProgressLevel(packageInstallInfo);
            arrayList.add(workspaceItemInfo);
        }
    }
}
