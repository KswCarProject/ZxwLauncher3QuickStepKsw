package com.android.launcher3.model;

import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.model.BgDataModel;

public class ReloadStringCacheTask extends BaseModelUpdateTask {
    private ModelDelegate mModelDelegate;

    public ReloadStringCacheTask(ModelDelegate modelDelegate) {
        this.mModelDelegate = modelDelegate;
    }

    public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
        synchronized (bgDataModel) {
            this.mModelDelegate.loadStringCache(bgDataModel.stringCache);
            scheduleCallbackTask(new LauncherModel.CallbackTask() {
                public final void execute(BgDataModel.Callbacks callbacks) {
                    callbacks.bindStringCache(StringCache.this);
                }
            });
        }
    }
}
