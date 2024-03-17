package com.android.launcher3.model;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.pm.PackageInstallInfo;
import com.android.launcher3.util.InstantAppResolver;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class PackageInstallStateChangedTask extends BaseModelUpdateTask {
    private final PackageInstallInfo mInstallInfo;

    public PackageInstallStateChangedTask(PackageInstallInfo packageInstallInfo) {
        this.mInstallInfo = packageInstallInfo;
    }

    public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
        if (this.mInstallInfo.state == 0) {
            try {
                ApplicationInfo applicationInfo = launcherAppState.getContext().getPackageManager().getApplicationInfo(this.mInstallInfo.packageName, 0);
                if (InstantAppResolver.newInstance(launcherAppState.getContext()).isInstantApp(applicationInfo)) {
                    launcherAppState.getModel().onPackageAdded(applicationInfo.packageName, this.mInstallInfo.user);
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
        } else {
            synchronized (allAppsList) {
                List<AppInfo> updatePromiseInstallInfo = allAppsList.updatePromiseInstallInfo(this.mInstallInfo);
                if (!updatePromiseInstallInfo.isEmpty()) {
                    for (AppInfo r0 : updatePromiseInstallInfo) {
                        scheduleCallbackTask(new LauncherModel.CallbackTask() {
                            public final void execute(BgDataModel.Callbacks callbacks) {
                                callbacks.bindIncrementalDownloadProgressUpdated(AppInfo.this);
                            }
                        });
                    }
                }
                bindApplicationsIfNeeded();
            }
            synchronized (bgDataModel) {
                HashSet hashSet = new HashSet();
                bgDataModel.forAllWorkspaceItemInfos(this.mInstallInfo.user, new Consumer(hashSet) {
                    public final /* synthetic */ HashSet f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void accept(Object obj) {
                        PackageInstallStateChangedTask.this.lambda$execute$1$PackageInstallStateChangedTask(this.f$1, (WorkspaceItemInfo) obj);
                    }
                });
                Iterator<LauncherAppWidgetInfo> it = bgDataModel.appWidgets.iterator();
                while (it.hasNext()) {
                    LauncherAppWidgetInfo next = it.next();
                    if (next.providerName.getPackageName().equals(this.mInstallInfo.packageName)) {
                        next.installProgress = this.mInstallInfo.progress;
                        hashSet.add(next);
                    }
                }
                if (!hashSet.isEmpty()) {
                    scheduleCallbackTask(new LauncherModel.CallbackTask(hashSet) {
                        public final /* synthetic */ HashSet f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void execute(BgDataModel.Callbacks callbacks) {
                            callbacks.bindRestoreItemsChange(this.f$0);
                        }
                    });
                }
            }
        }
    }

    public /* synthetic */ void lambda$execute$1$PackageInstallStateChangedTask(HashSet hashSet, WorkspaceItemInfo workspaceItemInfo) {
        if (workspaceItemInfo.hasPromiseIconUi() && this.mInstallInfo.packageName.equals(workspaceItemInfo.getTargetPackage())) {
            workspaceItemInfo.setProgressLevel(this.mInstallInfo);
            hashSet.add(workspaceItemInfo);
        }
    }
}
