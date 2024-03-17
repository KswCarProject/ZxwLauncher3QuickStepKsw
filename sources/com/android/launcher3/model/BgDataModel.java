package com.android.launcher3.model;

import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.shortcuts.ShortcutRequest;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.IntSet;
import com.android.launcher3.util.IntSparseArrayMap;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.widget.model.WidgetsListBaseEntry;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BgDataModel {
    private static final String TAG = "BgDataModel";
    public final ArrayList<LauncherAppWidgetInfo> appWidgets = new ArrayList<>();
    public final HashMap<ComponentKey, Integer> deepShortcutMap = new HashMap<>();
    public final IntSparseArrayMap<FixedContainerItems> extraItems = new IntSparseArrayMap<>();
    public final IntSparseArrayMap<FolderInfo> folders = new IntSparseArrayMap<>();
    public final IntSparseArrayMap<ItemInfo> itemsIdMap = new IntSparseArrayMap<>();
    public int lastBindId = 0;
    public final StringCache stringCache = new StringCache();
    public final WidgetsModel widgetsModel = new WidgetsModel();
    public final ArrayList<ItemInfo> workspaceItems = new ArrayList<>();

    public synchronized void clear() {
        this.workspaceItems.clear();
        this.appWidgets.clear();
        this.folders.clear();
        this.itemsIdMap.clear();
        this.deepShortcutMap.clear();
        this.extraItems.clear();
    }

    public synchronized IntArray collectWorkspaceScreens() {
        IntSet intSet;
        intSet = new IntSet();
        Iterator<ItemInfo> it = this.itemsIdMap.iterator();
        while (it.hasNext()) {
            ItemInfo next = it.next();
            if (next.container == -100) {
                intSet.add(next.screenId);
            }
        }
        if (intSet.isEmpty()) {
            intSet.add(0);
        }
        return intSet.getArray();
    }

    public synchronized void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println(str + "Data Model:");
        printWriter.println(str + " ---- workspace items ");
        for (int i = 0; i < this.workspaceItems.size(); i++) {
            printWriter.println(str + 9 + this.workspaceItems.get(i).toString());
        }
        printWriter.println(str + " ---- appwidget items ");
        for (int i2 = 0; i2 < this.appWidgets.size(); i2++) {
            printWriter.println(str + 9 + this.appWidgets.get(i2).toString());
        }
        printWriter.println(str + " ---- folder items ");
        for (int i3 = 0; i3 < this.folders.size(); i3++) {
            printWriter.println(str + 9 + ((FolderInfo) this.folders.valueAt(i3)).toString());
        }
        printWriter.println(str + " ---- items id map ");
        for (int i4 = 0; i4 < this.itemsIdMap.size(); i4++) {
            printWriter.println(str + 9 + ((ItemInfo) this.itemsIdMap.valueAt(i4)).toString());
        }
        if (strArr.length > 0 && TextUtils.equals(strArr[0], "--all")) {
            printWriter.println(str + "shortcut counts ");
            for (Integer num : this.deepShortcutMap.values()) {
                printWriter.print(num + ", ");
            }
            printWriter.println();
        }
    }

    public synchronized void removeItem(Context context, ItemInfo... itemInfoArr) {
        removeItem(context, (Iterable<? extends ItemInfo>) Arrays.asList(itemInfoArr));
    }

    public synchronized void removeItem(Context context, Iterable<? extends ItemInfo> iterable) {
        ArraySet arraySet = new ArraySet();
        for (ItemInfo itemInfo : iterable) {
            int i = itemInfo.itemType;
            if (!(i == 0 || i == 1)) {
                if (i == 2) {
                    this.folders.remove(itemInfo.id);
                    this.workspaceItems.remove(itemInfo);
                } else if (i == 4 || i == 5) {
                    this.appWidgets.remove(itemInfo);
                } else if (i == 6) {
                    arraySet.add(itemInfo.user);
                }
                this.itemsIdMap.remove(itemInfo.id);
            }
            this.workspaceItems.remove(itemInfo);
            this.itemsIdMap.remove(itemInfo.id);
        }
        arraySet.forEach(new Consumer(context) {
            public final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                BgDataModel.this.lambda$removeItem$0$BgDataModel(this.f$1, (UserHandle) obj);
            }
        });
    }

    public synchronized void addItem(Context context, ItemInfo itemInfo, boolean z) {
        addItem(context, itemInfo, z, (LoaderMemoryLogger) null);
    }

    public synchronized void addItem(Context context, ItemInfo itemInfo, boolean z, LoaderMemoryLogger loaderMemoryLogger) {
        if (loaderMemoryLogger != null) {
            loaderMemoryLogger.addLog(3, TAG, String.format("Adding item to ID map: %s", new Object[]{itemInfo.toString()}), (Exception) null);
        }
        this.itemsIdMap.put(itemInfo.id, itemInfo);
        int i = itemInfo.itemType;
        if (!(i == 0 || i == 1)) {
            if (i == 2) {
                this.folders.put(itemInfo.id, (FolderInfo) itemInfo);
                this.workspaceItems.add(itemInfo);
            } else if (i == 4 || i == 5) {
                this.appWidgets.add((LauncherAppWidgetInfo) itemInfo);
            } else if (i != 6) {
            }
            if (z && itemInfo.itemType == 6) {
                lambda$removeItem$0$BgDataModel(context, itemInfo.user);
            }
        }
        if (itemInfo.container != -100) {
            if (itemInfo.container != -101) {
                if (!z) {
                    findOrMakeFolder(itemInfo.container).add((WorkspaceItemInfo) itemInfo, false);
                } else if (!this.folders.containsKey(itemInfo.container)) {
                    Log.e(TAG, "adding item: " + itemInfo + " to a folder that  doesn't exist");
                }
                lambda$removeItem$0$BgDataModel(context, itemInfo.user);
            }
        }
        this.workspaceItems.add(itemInfo);
        lambda$removeItem$0$BgDataModel(context, itemInfo.user);
    }

    public void updateShortcutPinnedState(Context context) {
        for (UserHandle updateShortcutPinnedState : UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getUserProfiles()) {
            lambda$removeItem$0$BgDataModel(context, updateShortcutPinnedState);
        }
    }

    /* renamed from: updateShortcutPinnedState */
    public synchronized void lambda$removeItem$0$BgDataModel(Context context, UserHandle userHandle) {
        ShortcutRequest.QueryResult query = new ShortcutRequest(context, userHandle).query(6);
        if (query.wasSuccess()) {
            Map map = (Map) query.stream().collect(Collectors.groupingBy($$Lambda$BgDataModel$Y4THUAe4ix7pnFdyaTy8qhRMy0.INSTANCE, Collectors.mapping($$Lambda$BgDataModel$hrGTsq__Mf0H7jjxY1_HswpRmuI.INSTANCE, Collectors.toSet())));
            Stream.Builder builder = Stream.builder();
            Objects.requireNonNull(builder);
            forAllWorkspaceItemInfos(userHandle, new Consumer(builder) {
                public final /* synthetic */ Stream.Builder f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    this.f$0.accept((WorkspaceItemInfo) obj);
                }
            });
            for (Map.Entry entry : ((Map) Stream.concat(builder.build().filter($$Lambda$BgDataModel$raSiFtWzWR6FJ2Zh8rl8buXmyWw.INSTANCE).map($$Lambda$i70ORUCJAhAjKmv_z_W8wAV12vw.INSTANCE), ItemInstallQueue.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getPendingShortcuts(userHandle)).collect(Collectors.groupingBy($$Lambda$OKxjMGSQFawXzQrLBck3JD1Jv8.INSTANCE, Collectors.mapping($$Lambda$SFupvY33ZvflkwSVgwu62eWgdY.INSTANCE, Collectors.toSet())))).entrySet()) {
                Set set = (Set) entry.getValue();
                Set set2 = (Set) map.remove(entry.getKey());
                if (set2 == null) {
                    set2 = Collections.emptySet();
                }
                if (set2.size() != set.size() || !set2.containsAll(set)) {
                    try {
                        ((LauncherApps) context.getSystemService(LauncherApps.class)).pinShortcuts((String) entry.getKey(), new ArrayList(set), userHandle);
                    } catch (IllegalStateException | SecurityException e) {
                        Log.w(TAG, "Failed to pin shortcut", e);
                    }
                }
            }
            map.keySet().forEach(new Consumer(context, userHandle) {
                public final /* synthetic */ Context f$0;
                public final /* synthetic */ UserHandle f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    BgDataModel.lambda$updateShortcutPinnedState$2(this.f$0, this.f$1, (String) obj);
                }
            });
        }
    }

    static /* synthetic */ boolean lambda$updateShortcutPinnedState$1(WorkspaceItemInfo workspaceItemInfo) {
        return workspaceItemInfo.itemType == 6;
    }

    static /* synthetic */ void lambda$updateShortcutPinnedState$2(Context context, UserHandle userHandle, String str) {
        try {
            ((LauncherApps) context.getSystemService(LauncherApps.class)).pinShortcuts(str, Collections.emptyList(), userHandle);
        } catch (IllegalStateException | SecurityException e) {
            Log.w(TAG, "Failed to unpin shortcut", e);
        }
    }

    public synchronized FolderInfo findOrMakeFolder(int i) {
        FolderInfo folderInfo;
        folderInfo = (FolderInfo) this.folders.get(i);
        if (folderInfo == null) {
            folderInfo = new FolderInfo();
            this.folders.put(i, folderInfo);
        }
        return folderInfo;
    }

    public synchronized void updateDeepShortcutCounts(String str, UserHandle userHandle, List<ShortcutInfo> list) {
        if (str != null) {
            Iterator<ComponentKey> it = this.deepShortcutMap.keySet().iterator();
            while (it.hasNext()) {
                ComponentKey next = it.next();
                if (next.componentName.getPackageName().equals(str) && next.user.equals(userHandle)) {
                    it.remove();
                }
            }
        }
        for (ShortcutInfo next2 : list) {
            int i = 1;
            if (next2.isEnabled() && (next2.isDeclaredInManifest() || next2.isDynamic()) && next2.getActivity() != null) {
                ComponentKey componentKey = new ComponentKey(next2.getActivity(), next2.getUserHandle());
                Integer num = this.deepShortcutMap.get(componentKey);
                HashMap<ComponentKey, Integer> hashMap = this.deepShortcutMap;
                if (num != null) {
                    i = 1 + num.intValue();
                }
                hashMap.put(componentKey, Integer.valueOf(i));
            }
        }
    }

    public synchronized ArrayList<ItemInfo> getAllWorkspaceItems() {
        ArrayList<ItemInfo> arrayList;
        arrayList = new ArrayList<>(this.workspaceItems.size() + this.appWidgets.size());
        arrayList.addAll(this.workspaceItems);
        arrayList.addAll(this.appWidgets);
        return arrayList;
    }

    public void forAllWorkspaceItemInfos(UserHandle userHandle, Consumer<WorkspaceItemInfo> consumer) {
        Iterator<ItemInfo> it = this.itemsIdMap.iterator();
        while (it.hasNext()) {
            ItemInfo next = it.next();
            if ((next instanceof WorkspaceItemInfo) && userHandle.equals(next.user)) {
                consumer.accept((WorkspaceItemInfo) next);
            }
        }
        for (int size = this.extraItems.size() - 1; size >= 0; size--) {
            for (ItemInfo next2 : ((FixedContainerItems) this.extraItems.valueAt(size)).items) {
                if ((next2 instanceof WorkspaceItemInfo) && userHandle.equals(next2.user)) {
                    consumer.accept((WorkspaceItemInfo) next2);
                }
            }
        }
    }

    public static class FixedContainerItems {
        public final int containerId;
        public final List<ItemInfo> items;

        public FixedContainerItems(int i) {
            this(i, new ArrayList());
        }

        public FixedContainerItems(int i, List<ItemInfo> list) {
            this.containerId = i;
            this.items = list;
        }

        public FixedContainerItems clone() {
            return new FixedContainerItems(this.containerId, new ArrayList(this.items));
        }

        public void setItems(List<ItemInfo> list) {
            this.items.clear();
            list.forEach(new Consumer() {
                public final void accept(Object obj) {
                    BgDataModel.FixedContainerItems.this.lambda$setItems$0$BgDataModel$FixedContainerItems((ItemInfo) obj);
                }
            });
        }

        public /* synthetic */ void lambda$setItems$0$BgDataModel$FixedContainerItems(ItemInfo itemInfo) {
            itemInfo.container = this.containerId;
            this.items.add(itemInfo);
        }
    }

    public interface Callbacks {
        public static final int FLAG_HAS_SHORTCUT_PERMISSION = 1;
        public static final int FLAG_QUIET_MODE_CHANGE_PERMISSION = 4;
        public static final int FLAG_QUIET_MODE_ENABLED = 2;

        void bindAllApplications(AppInfo[] appInfoArr, int i) {
        }

        void bindAllWidgets(List<WidgetsListBaseEntry> list) {
        }

        void bindAppsAdded(IntArray intArray, ArrayList<ItemInfo> arrayList, ArrayList<ItemInfo> arrayList2) {
        }

        void bindDeepShortcutMap(HashMap<ComponentKey, Integer> hashMap) {
        }

        void bindExtraContainerItems(FixedContainerItems fixedContainerItems) {
        }

        void bindIncrementalDownloadProgressUpdated(AppInfo appInfo) {
        }

        void bindItems(List<ItemInfo> list, boolean z) {
        }

        void bindItemsModified(List<ItemInfo> list) {
        }

        void bindRestoreItemsChange(HashSet<ItemInfo> hashSet) {
        }

        void bindScreens(IntArray intArray) {
        }

        void bindStringCache(StringCache stringCache) {
        }

        void bindWidgetsRestored(ArrayList<LauncherAppWidgetInfo> arrayList) {
        }

        void bindWorkspaceComponentsRemoved(Predicate<ItemInfo> predicate) {
        }

        void bindWorkspaceItemsChanged(List<WorkspaceItemInfo> list) {
        }

        void clearPendingBinds() {
        }

        void finishBindingItems(IntSet intSet) {
        }

        void preAddApps() {
        }

        void startBinding() {
        }

        IntSet getPagesToBindSynchronously(IntArray intArray) {
            return new IntSet();
        }

        void onInitialBindComplete(IntSet intSet, RunnableList runnableList) {
            runnableList.executeAllAndDestroy();
        }
    }
}
