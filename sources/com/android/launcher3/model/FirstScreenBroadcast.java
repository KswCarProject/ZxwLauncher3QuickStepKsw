package com.android.launcher3.model;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.pm.InstallSessionHelper;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FirstScreenBroadcast {
    private static final String ACTION_FIRST_SCREEN_ACTIVE_INSTALLS = "com.android.launcher3.action.FIRST_SCREEN_ACTIVE_INSTALLS";
    private static final boolean DEBUG = false;
    private static final String FOLDER_ITEM_EXTRA = "folderItem";
    private static final String HOTSEAT_ITEM_EXTRA = "hotseatItem";
    private static final String TAG = "FirstScreenBroadcast";
    private static final String VERIFICATION_TOKEN_EXTRA = "verificationToken";
    private static final String WIDGET_ITEM_EXTRA = "widgetItem";
    private static final String WORKSPACE_ITEM_EXTRA = "workspaceItem";
    private final HashMap<PackageUserKey, PackageInstaller.SessionInfo> mSessionInfoForPackage;

    public FirstScreenBroadcast(HashMap<PackageUserKey, PackageInstaller.SessionInfo> hashMap) {
        this.mSessionInfoForPackage = hashMap;
    }

    public void sendBroadcasts(Context context, List<ItemInfo> list) {
        ((Map) this.mSessionInfoForPackage.values().stream().filter(new Predicate(Process.myUserHandle()) {
            public final /* synthetic */ UserHandle f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return this.f$0.equals(InstallSessionHelper.getUserHandle((PackageInstaller.SessionInfo) obj));
            }
        }).collect(Collectors.groupingBy($$Lambda$FirstScreenBroadcast$yRJMSjoVbjNnjnD_BzGj4xecE.INSTANCE, Collectors.mapping($$Lambda$FirstScreenBroadcast$nuXLkPMK3siDYMvSdUn362tWk4.INSTANCE, Collectors.toSet())))).forEach(new BiConsumer(context, list) {
            public final /* synthetic */ Context f$1;
            public final /* synthetic */ List f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void accept(Object obj, Object obj2) {
                FirstScreenBroadcast.this.lambda$sendBroadcasts$1$FirstScreenBroadcast(this.f$1, this.f$2, (String) obj, (Set) obj2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: sendBroadcastToInstaller */
    public void lambda$sendBroadcasts$1$FirstScreenBroadcast(Context context, String str, Set<String> set, List<ItemInfo> list) {
        HashSet hashSet = new HashSet();
        HashSet hashSet2 = new HashSet();
        HashSet hashSet3 = new HashSet();
        HashSet hashSet4 = new HashSet();
        for (ItemInfo next : list) {
            if (next instanceof FolderInfo) {
                for (WorkspaceItemInfo packageName : cloneOnMainThread(((FolderInfo) next).contents)) {
                    String packageName2 = getPackageName(packageName);
                    if (packageName2 != null && set.contains(packageName2)) {
                        hashSet.add(packageName2);
                    }
                }
            }
            String packageName3 = getPackageName(next);
            if (packageName3 != null && set.contains(packageName3)) {
                if (next instanceof LauncherAppWidgetInfo) {
                    hashSet4.add(packageName3);
                } else if (next.container == -101) {
                    hashSet3.add(packageName3);
                } else if (next.container == -100) {
                    hashSet2.add(packageName3);
                }
            }
        }
        if (!hashSet.isEmpty() || !hashSet2.isEmpty() || !hashSet3.isEmpty() || !hashSet4.isEmpty()) {
            context.sendBroadcast(new Intent(ACTION_FIRST_SCREEN_ACTIVE_INSTALLS).setPackage(str).putStringArrayListExtra(FOLDER_ITEM_EXTRA, new ArrayList(hashSet)).putStringArrayListExtra(WORKSPACE_ITEM_EXTRA, new ArrayList(hashSet2)).putStringArrayListExtra(HOTSEAT_ITEM_EXTRA, new ArrayList(hashSet3)).putStringArrayListExtra(WIDGET_ITEM_EXTRA, new ArrayList(hashSet4)).putExtra(VERIFICATION_TOKEN_EXTRA, PendingIntent.getActivity(context, 0, new Intent(), 1140850688)));
        }
    }

    private static String getPackageName(ItemInfo itemInfo) {
        if (itemInfo instanceof LauncherAppWidgetInfo) {
            LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) itemInfo;
            if (launcherAppWidgetInfo.providerName != null) {
                return launcherAppWidgetInfo.providerName.getPackageName();
            }
            return null;
        } else if (itemInfo.getTargetComponent() != null) {
            return itemInfo.getTargetComponent().getPackageName();
        } else {
            return null;
        }
    }

    private static void printList(String str, String str2, Set<String> set) {
        for (String str3 : set) {
            Log.d(TAG, str + ":" + str2 + ":" + str3);
        }
    }

    private static List<WorkspaceItemInfo> cloneOnMainThread(ArrayList<WorkspaceItemInfo> arrayList) {
        try {
            return (List) Executors.MAIN_EXECUTOR.submit(new Callable(arrayList) {
                public final /* synthetic */ ArrayList f$0;

                {
                    this.f$0 = r1;
                }

                public final Object call() {
                    return FirstScreenBroadcast.lambda$cloneOnMainThread$2(this.f$0);
                }
            }).get();
        } catch (Exception unused) {
            return Collections.emptyList();
        }
    }

    static /* synthetic */ ArrayList lambda$cloneOnMainThread$2(ArrayList arrayList) throws Exception {
        return new ArrayList(arrayList);
    }
}
