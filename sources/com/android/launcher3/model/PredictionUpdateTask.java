package com.android.launcher3.model;

import android.app.prediction.AppTarget;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.Utilities;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.QuickstepModelDelegate;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.quickstep.InstantAppResolverImpl;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PredictionUpdateTask extends BaseModelUpdateTask {
    private final QuickstepModelDelegate.PredictorState mPredictorState;
    private final List<AppTarget> mTargets;

    PredictionUpdateTask(QuickstepModelDelegate.PredictorState predictorState, List<AppTarget> list) {
        this.mPredictorState = predictorState;
        this.mTargets = list;
    }

    public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
        WorkspaceItemInfo workspaceItemInfo;
        Context context = launcherAppState.getContext();
        Utilities.getDevicePrefs(context).edit().putBoolean(QuickstepModelDelegate.LAST_PREDICTION_ENABLED_STATE, !this.mTargets.isEmpty()).apply();
        Set set = (Set) ((BgDataModel.FixedContainerItems) bgDataModel.extraItems.get(this.mPredictorState.containerId)).items.stream().filter($$Lambda$PredictionUpdateTask$9aZGrbuK68lx7T9qOwAmLGSentM.INSTANCE).map($$Lambda$PredictionUpdateTask$OOR8gXjJ128zOJlqPmz_csiOv4.INSTANCE).collect(Collectors.toSet());
        BgDataModel.FixedContainerItems fixedContainerItems = new BgDataModel.FixedContainerItems(this.mPredictorState.containerId);
        for (AppTarget next : this.mTargets) {
            ShortcutInfo shortcutInfo = next.getShortcutInfo();
            if (shortcutInfo != null) {
                set.add(shortcutInfo.getUserHandle());
                workspaceItemInfo = new WorkspaceItemInfo(shortcutInfo, context);
                launcherAppState.getIconCache().getShortcutIcon(workspaceItemInfo, shortcutInfo);
            } else {
                String className = next.getClassName();
                if (!InstantAppResolverImpl.COMPONENT_CLASS_MARKER.equals(className)) {
                    ComponentName componentName = new ComponentName(next.getPackageName(), className);
                    UserHandle user = next.getUser();
                    workspaceItemInfo = (WorkspaceItemInfo) allAppsList.data.stream().filter(new Predicate(user, componentName) {
                        public final /* synthetic */ UserHandle f$0;
                        public final /* synthetic */ ComponentName f$1;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                        }

                        public final boolean test(Object obj) {
                            return PredictionUpdateTask.lambda$execute$2(this.f$0, this.f$1, (AppInfo) obj);
                        }
                    }).map(new Function(context) {
                        public final /* synthetic */ Context f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final Object apply(Object obj) {
                            return LauncherAppState.this.getIconCache().getTitleAndIcon((AppInfo) obj, false);
                        }
                    }).findAny().orElseGet(new Supplier(context, componentName, user, launcherAppState) {
                        public final /* synthetic */ Context f$0;
                        public final /* synthetic */ ComponentName f$1;
                        public final /* synthetic */ UserHandle f$2;
                        public final /* synthetic */ LauncherAppState f$3;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                        }

                        public final Object get() {
                            return PredictionUpdateTask.lambda$execute$4(this.f$0, this.f$1, this.f$2, this.f$3);
                        }
                    });
                    if (workspaceItemInfo == null) {
                    }
                }
            }
            workspaceItemInfo.container = fixedContainerItems.containerId;
            fixedContainerItems.items.add(workspaceItemInfo);
        }
        bgDataModel.extraItems.put(fixedContainerItems.containerId, fixedContainerItems);
        bindExtraContainerItems(fixedContainerItems);
        set.forEach(new Consumer(launcherAppState) {
            public final /* synthetic */ LauncherAppState f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                BgDataModel.this.lambda$removeItem$0$BgDataModel(this.f$1.getContext(), (UserHandle) obj);
            }
        });
        this.mPredictorState.storage.write(context, fixedContainerItems.items);
    }

    static /* synthetic */ boolean lambda$execute$0(ItemInfo itemInfo) {
        return itemInfo.itemType == 6;
    }

    static /* synthetic */ boolean lambda$execute$2(UserHandle userHandle, ComponentName componentName, AppInfo appInfo) {
        return userHandle.equals(appInfo.user) && componentName.equals(appInfo.componentName);
    }

    static /* synthetic */ WorkspaceItemInfo lambda$execute$4(Context context, ComponentName componentName, UserHandle userHandle, LauncherAppState launcherAppState) {
        LauncherActivityInfo resolveActivity = ((LauncherApps) context.getSystemService(LauncherApps.class)).resolveActivity(AppInfo.makeLaunchIntent(componentName), userHandle);
        if (resolveActivity == null) {
            return null;
        }
        AppInfo appInfo = new AppInfo(context, resolveActivity, userHandle);
        launcherAppState.getIconCache().getTitleAndIcon(appInfo, resolveActivity, false);
        return appInfo.makeWorkspaceItem(context);
    }
}
