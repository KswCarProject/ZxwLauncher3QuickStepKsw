package com.android.launcher3.model;

import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.util.IntSparseArrayMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class BaseModelUpdateTask implements LauncherModel.ModelUpdateTask {
    private static final boolean DEBUG_TASKS = false;
    private static final String TAG = "BaseModelUpdateTask";
    private AllAppsList mAllAppsList;
    private LauncherAppState mApp;
    private BgDataModel mDataModel;
    private LauncherModel mModel;
    private Executor mUiExecutor;

    public abstract void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList);

    public void init(LauncherAppState launcherAppState, LauncherModel launcherModel, BgDataModel bgDataModel, AllAppsList allAppsList, Executor executor) {
        this.mApp = launcherAppState;
        this.mModel = launcherModel;
        this.mDataModel = bgDataModel;
        this.mAllAppsList = allAppsList;
        this.mUiExecutor = executor;
    }

    public final void run() {
        if (this.mModel.isModelLoaded()) {
            execute(this.mApp, this.mDataModel, this.mAllAppsList);
        }
    }

    public final void scheduleCallbackTask(LauncherModel.CallbackTask callbackTask) {
        for (BgDataModel.Callbacks r3 : this.mModel.getCallbacks()) {
            this.mUiExecutor.execute(new Runnable(r3) {
                public final /* synthetic */ BgDataModel.Callbacks f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    LauncherModel.CallbackTask.this.execute(this.f$1);
                }
            });
        }
    }

    public ModelWriter getModelWriter() {
        return this.mModel.getWriter(false, false, (BgDataModel.Callbacks) null);
    }

    public void bindUpdatedWorkspaceItems(List<WorkspaceItemInfo> list) {
        List list2 = (List) list.stream().filter($$Lambda$BaseModelUpdateTask$gIjQEYqsg2Jp7xFCuFXcq6Mdxo0.INSTANCE).collect(Collectors.toList());
        if (!list2.isEmpty()) {
            scheduleCallbackTask(new LauncherModel.CallbackTask(list2) {
                public final /* synthetic */ List f$0;

                {
                    this.f$0 = r1;
                }

                public final void execute(BgDataModel.Callbacks callbacks) {
                    callbacks.bindWorkspaceItemsChanged(this.f$0);
                }
            });
        }
        IntStream distinct = list.stream().mapToInt($$Lambda$BaseModelUpdateTask$GB7zwpISi4g_qiNG0fwyJAG6XSg.INSTANCE).distinct();
        IntSparseArrayMap<BgDataModel.FixedContainerItems> intSparseArrayMap = this.mDataModel.extraItems;
        Objects.requireNonNull(intSparseArrayMap);
        distinct.mapToObj(new IntFunction() {
            public final Object apply(int i) {
                return (BgDataModel.FixedContainerItems) IntSparseArrayMap.this.get(i);
            }
        }).filter($$Lambda$BaseModelUpdateTask$4ZefZrXQ24hmdx8EYqnlKuLDM8.INSTANCE).forEach(new Consumer() {
            public final void accept(Object obj) {
                BaseModelUpdateTask.this.bindExtraContainerItems((BgDataModel.FixedContainerItems) obj);
            }
        });
    }

    static /* synthetic */ boolean lambda$bindUpdatedWorkspaceItems$1(WorkspaceItemInfo workspaceItemInfo) {
        return workspaceItemInfo.id != -1;
    }

    public void bindExtraContainerItems(BgDataModel.FixedContainerItems fixedContainerItems) {
        scheduleCallbackTask(new LauncherModel.CallbackTask() {
            public final void execute(BgDataModel.Callbacks callbacks) {
                callbacks.bindExtraContainerItems(BgDataModel.FixedContainerItems.this);
            }
        });
    }

    public void bindDeepShortcuts(BgDataModel bgDataModel) {
        scheduleCallbackTask(new LauncherModel.CallbackTask(new HashMap(bgDataModel.deepShortcutMap)) {
            public final /* synthetic */ HashMap f$0;

            {
                this.f$0 = r1;
            }

            public final void execute(BgDataModel.Callbacks callbacks) {
                callbacks.bindDeepShortcutMap(this.f$0);
            }
        });
    }

    public void bindUpdatedWidgets(BgDataModel bgDataModel) {
        scheduleCallbackTask(new LauncherModel.CallbackTask(bgDataModel.widgetsModel.getWidgetsListForPicker(this.mApp.getContext())) {
            public final /* synthetic */ ArrayList f$0;

            {
                this.f$0 = r1;
            }

            public final void execute(BgDataModel.Callbacks callbacks) {
                callbacks.bindAllWidgets(this.f$0);
            }
        });
    }

    public void deleteAndBindComponentsRemoved(Predicate<ItemInfo> predicate, String str) {
        getModelWriter().deleteItemsFromDatabase(predicate, str);
        scheduleCallbackTask(new LauncherModel.CallbackTask(predicate) {
            public final /* synthetic */ Predicate f$0;

            {
                this.f$0 = r1;
            }

            public final void execute(BgDataModel.Callbacks callbacks) {
                callbacks.bindWorkspaceComponentsRemoved(this.f$0);
            }
        });
    }

    public void bindApplicationsIfNeeded() {
        if (this.mAllAppsList.getAndResetChangeFlag()) {
            scheduleCallbackTask(new LauncherModel.CallbackTask(this.mAllAppsList.copyData(), this.mAllAppsList.getFlags()) {
                public final /* synthetic */ AppInfo[] f$0;
                public final /* synthetic */ int f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void execute(BgDataModel.Callbacks callbacks) {
                    callbacks.bindAllApplications(this.f$0, this.f$1);
                }
            });
        }
    }
}
