package com.android.launcher3.model;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.shortcuts.ShortcutRequest;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.ItemInfoMatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Consumer;

public class UserLockStateChangedTask extends BaseModelUpdateTask {
    private boolean mIsUserUnlocked;
    private final UserHandle mUser;

    public UserLockStateChangedTask(UserHandle userHandle, boolean z) {
        this.mUser = userHandle;
        this.mIsUserUnlocked = z;
    }

    public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
        Context context = launcherAppState.getContext();
        HashMap hashMap = new HashMap();
        if (this.mIsUserUnlocked) {
            ShortcutRequest.QueryResult query = new ShortcutRequest(context, this.mUser).query(2);
            if (query.wasSuccess()) {
                Iterator it = query.iterator();
                while (it.hasNext()) {
                    ShortcutInfo shortcutInfo = (ShortcutInfo) it.next();
                    hashMap.put(ShortcutKey.fromInfo(shortcutInfo), shortcutInfo);
                }
            } else {
                this.mIsUserUnlocked = false;
            }
        }
        ArrayList arrayList = new ArrayList();
        HashSet hashSet = new HashSet();
        synchronized (bgDataModel) {
            bgDataModel.forAllWorkspaceItemInfos(this.mUser, new Consumer(hashMap, hashSet, context, launcherAppState, arrayList) {
                public final /* synthetic */ HashMap f$1;
                public final /* synthetic */ HashSet f$2;
                public final /* synthetic */ Context f$3;
                public final /* synthetic */ LauncherAppState f$4;
                public final /* synthetic */ ArrayList f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void accept(Object obj) {
                    UserLockStateChangedTask.this.lambda$execute$0$UserLockStateChangedTask(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, (WorkspaceItemInfo) obj);
                }
            });
        }
        bindUpdatedWorkspaceItems(arrayList);
        if (!hashSet.isEmpty()) {
            deleteAndBindComponentsRemoved(ItemInfoMatcher.ofShortcutKeys(hashSet), "removed during unlock because it's no longer available (possibly due to clear data)");
        }
        Iterator<ComponentKey> it2 = bgDataModel.deepShortcutMap.keySet().iterator();
        while (it2.hasNext()) {
            if (it2.next().user.equals(this.mUser)) {
                it2.remove();
            }
        }
        if (this.mIsUserUnlocked) {
            bgDataModel.updateDeepShortcutCounts((String) null, this.mUser, new ShortcutRequest(context, this.mUser).query(11));
        }
        bindDeepShortcuts(bgDataModel);
    }

    public /* synthetic */ void lambda$execute$0$UserLockStateChangedTask(HashMap hashMap, HashSet hashSet, Context context, LauncherAppState launcherAppState, ArrayList arrayList, WorkspaceItemInfo workspaceItemInfo) {
        if (workspaceItemInfo.itemType == 6) {
            if (this.mIsUserUnlocked) {
                ShortcutKey fromItemInfo = ShortcutKey.fromItemInfo(workspaceItemInfo);
                ShortcutInfo shortcutInfo = (ShortcutInfo) hashMap.get(fromItemInfo);
                if (shortcutInfo == null) {
                    hashSet.add(fromItemInfo);
                    return;
                }
                workspaceItemInfo.runtimeStatusFlags &= -33;
                workspaceItemInfo.updateFromDeepShortcutInfo(shortcutInfo, context);
                launcherAppState.getIconCache().getShortcutIcon(workspaceItemInfo, shortcutInfo);
            } else {
                workspaceItemInfo.runtimeStatusFlags |= 32;
            }
            arrayList.add(workspaceItemInfo);
        }
    }
}
