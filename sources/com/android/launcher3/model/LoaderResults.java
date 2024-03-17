package com.android.launcher3.model;

import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.util.Executors;
import java.util.HashMap;
import java.util.List;

public class LoaderResults extends BaseLoaderResults {
    public LoaderResults(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList, BgDataModel.Callbacks[] callbacksArr) {
        super(launcherAppState, bgDataModel, allAppsList, callbacksArr, Executors.MAIN_EXECUTOR);
    }

    public void bindDeepShortcuts() {
        HashMap hashMap;
        synchronized (this.mBgDataModel) {
            hashMap = new HashMap(this.mBgDataModel.deepShortcutMap);
        }
        executeCallbacksTask(new LauncherModel.CallbackTask(hashMap) {
            public final /* synthetic */ HashMap f$0;

            {
                this.f$0 = r1;
            }

            public final void execute(BgDataModel.Callbacks callbacks) {
                callbacks.bindDeepShortcutMap(this.f$0);
            }
        }, this.mUiExecutor);
    }

    public void bindWidgets() {
        executeCallbacksTask(new LauncherModel.CallbackTask(this.mBgDataModel.widgetsModel.getWidgetsListForPicker(this.mApp.getContext())) {
            public final /* synthetic */ List f$0;

            {
                this.f$0 = r1;
            }

            public final void execute(BgDataModel.Callbacks callbacks) {
                callbacks.bindAllWidgets(this.f$0);
            }
        }, this.mUiExecutor);
    }
}
