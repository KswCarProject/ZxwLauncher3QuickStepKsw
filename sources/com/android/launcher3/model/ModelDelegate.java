package com.android.launcher3.model;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.ResourceBasedOverride;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Map;

public class ModelDelegate implements ResourceBasedOverride {
    protected LauncherAppState mApp;
    protected AllAppsList mAppsList;
    protected Context mContext;
    protected BgDataModel mDataModel;
    protected boolean mIsPrimaryInstance;

    public void destroy() {
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    public void loadItems(UserManagerState userManagerState, Map<ShortcutKey, ShortcutInfo> map) {
    }

    public void modelLoadComplete() {
    }

    public void workspaceLoadComplete() {
    }

    public static ModelDelegate newInstance(Context context, LauncherAppState launcherAppState, AllAppsList allAppsList, BgDataModel bgDataModel, boolean z) {
        ModelDelegate modelDelegate = (ModelDelegate) ResourceBasedOverride.Overrides.getObject(ModelDelegate.class, context, R.string.model_delegate_class);
        modelDelegate.init(context, launcherAppState, allAppsList, bgDataModel, z);
        return modelDelegate;
    }

    private void init(Context context, LauncherAppState launcherAppState, AllAppsList allAppsList, BgDataModel bgDataModel, boolean z) {
        this.mApp = launcherAppState;
        this.mAppsList = allAppsList;
        this.mDataModel = bgDataModel;
        this.mIsPrimaryInstance = z;
        this.mContext = context;
    }

    public void validateData() {
        if (PackageManagerHelper.hasShortcutsPermission(this.mApp.getContext()) != this.mAppsList.hasShortcutHostPermission()) {
            this.mApp.getModel().forceReload();
        }
    }

    public void loadStringCache(StringCache stringCache) {
        stringCache.loadStrings(this.mContext);
    }
}
