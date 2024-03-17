package com.android.launcher3.model;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.shortcuts.ShortcutRequest;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.PackageManagerHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ShortcutsChangedTask extends BaseModelUpdateTask {
    private final String mPackageName;
    private final List<ShortcutInfo> mShortcuts;
    private final boolean mUpdateIdMap;
    private final UserHandle mUser;

    public ShortcutsChangedTask(String str, List<ShortcutInfo> list, UserHandle userHandle, boolean z) {
        this.mPackageName = str;
        this.mShortcuts = list;
        this.mUser = userHandle;
        this.mUpdateIdMap = z;
    }

    public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
        Context context = launcherAppState.getContext();
        ArrayList arrayList = new ArrayList();
        synchronized (bgDataModel) {
            bgDataModel.forAllWorkspaceItemInfos(this.mUser, new Consumer(arrayList) {
                public final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    ShortcutsChangedTask.this.lambda$execute$0$ShortcutsChangedTask(this.f$1, (WorkspaceItemInfo) obj);
                }
            });
        }
        if (!arrayList.isEmpty()) {
            if (!this.mShortcuts.isEmpty() || new PackageManagerHelper(launcherAppState.getContext()).isAppInstalled(this.mPackageName, this.mUser)) {
                List list = (List) arrayList.stream().map($$Lambda$JXT5ULOAYVPTR_TBJhDw_Ir0.INSTANCE).distinct().collect(Collectors.toList());
                ShortcutRequest.QueryResult<ShortcutInfo> query = new ShortcutRequest(context, this.mUser).forPackage(this.mPackageName, (List<String>) list).query(11);
                HashSet hashSet = new HashSet(list);
                ArrayList arrayList2 = new ArrayList();
                for (ShortcutInfo shortcutInfo : query) {
                    if (shortcutInfo.isPinned()) {
                        String id = shortcutInfo.getId();
                        hashSet.remove(id);
                        arrayList.stream().filter(new Predicate(id) {
                            public final /* synthetic */ String f$0;

                            {
                                this.f$0 = r1;
                            }

                            public final boolean test(Object obj) {
                                return this.f$0.equals(((WorkspaceItemInfo) obj).getDeepShortcutId());
                            }
                        }).forEach(new Consumer(shortcutInfo, context, launcherAppState, arrayList2) {
                            public final /* synthetic */ ShortcutInfo f$0;
                            public final /* synthetic */ Context f$1;
                            public final /* synthetic */ LauncherAppState f$2;
                            public final /* synthetic */ ArrayList f$3;

                            {
                                this.f$0 = r1;
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                            }

                            public final void accept(Object obj) {
                                ShortcutsChangedTask.lambda$execute$2(this.f$0, this.f$1, this.f$2, this.f$3, (WorkspaceItemInfo) obj);
                            }
                        });
                    }
                }
                bindUpdatedWorkspaceItems(arrayList2);
                if (!hashSet.isEmpty()) {
                    deleteAndBindComponentsRemoved(ItemInfoMatcher.ofShortcutKeys((Set) hashSet.stream().map(new Function() {
                        public final Object apply(Object obj) {
                            return ShortcutsChangedTask.this.lambda$execute$3$ShortcutsChangedTask((String) obj);
                        }
                    }).collect(Collectors.toSet())), "removed because the shortcut is no longer available in shortcut service");
                }
            } else {
                return;
            }
        }
        if (this.mUpdateIdMap) {
            bgDataModel.updateDeepShortcutCounts(this.mPackageName, this.mUser, this.mShortcuts);
            bindDeepShortcuts(bgDataModel);
        }
    }

    public /* synthetic */ void lambda$execute$0$ShortcutsChangedTask(ArrayList arrayList, WorkspaceItemInfo workspaceItemInfo) {
        if (workspaceItemInfo.itemType == 6 && this.mPackageName.equals(workspaceItemInfo.getIntent().getPackage())) {
            arrayList.add(workspaceItemInfo);
        }
    }

    static /* synthetic */ void lambda$execute$2(ShortcutInfo shortcutInfo, Context context, LauncherAppState launcherAppState, ArrayList arrayList, WorkspaceItemInfo workspaceItemInfo) {
        workspaceItemInfo.updateFromDeepShortcutInfo(shortcutInfo, context);
        launcherAppState.getIconCache().getShortcutIcon(workspaceItemInfo, shortcutInfo);
        arrayList.add(workspaceItemInfo);
    }

    public /* synthetic */ ShortcutKey lambda$execute$3$ShortcutsChangedTask(String str) {
        return new ShortcutKey(this.mPackageName, this.mUser, str);
    }
}
