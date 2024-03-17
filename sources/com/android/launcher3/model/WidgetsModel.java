package com.android.launcher3.model;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.util.Pair;
import androidx.collection.ArrayMap;
import com.android.launcher3.AppFilter;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.AlphabeticIndexCompat;
import com.android.launcher3.icons.ComponentWithLabelAndIcon;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.WidgetsModel;
import com.android.launcher3.model.data.PackageItemInfo;
import com.android.launcher3.pm.ShortcutConfigActivityInfo;
import com.android.launcher3.util.IntSet;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.widget.LauncherAppWidgetProviderInfo;
import com.android.launcher3.widget.WidgetManagerHelper;
import com.android.launcher3.widget.WidgetSections;
import com.android.launcher3.widget.model.WidgetsListBaseEntry;
import com.android.launcher3.widget.model.WidgetsListContentEntry;
import com.android.launcher3.widget.model.WidgetsListHeaderEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WidgetsModel {
    private static final boolean DEBUG = false;
    public static final boolean GO_DISABLE_NOTIFICATION_DOTS = false;
    public static final boolean GO_DISABLE_WIDGETS = false;
    private static final String TAG = "WidgetsModel";
    private final Map<PackageItemInfo, List<WidgetItem>> mWidgetsList = new HashMap();

    public synchronized ArrayList<WidgetsListBaseEntry> getWidgetsListForPicker(Context context) {
        ArrayList<WidgetsListBaseEntry> arrayList;
        String str;
        arrayList = new ArrayList<>();
        AlphabeticIndexCompat alphabeticIndexCompat = new AlphabeticIndexCompat(context);
        for (Map.Entry next : this.mWidgetsList.entrySet()) {
            PackageItemInfo packageItemInfo = (PackageItemInfo) next.getKey();
            List list = (List) next.getValue();
            if (packageItemInfo.title == null) {
                str = "";
            } else {
                str = alphabeticIndexCompat.computeSectionName(packageItemInfo.title);
            }
            arrayList.add(new WidgetsListHeaderEntry(packageItemInfo, str, list));
            arrayList.add(new WidgetsListContentEntry(packageItemInfo, str, list));
        }
        return arrayList;
    }

    public synchronized Map<PackageUserKey, List<WidgetItem>> getAllWidgetsWithoutShortcuts() {
        HashMap hashMap;
        hashMap = new HashMap();
        this.mWidgetsList.forEach(new BiConsumer(hashMap) {
            public final /* synthetic */ Map f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj, Object obj2) {
                WidgetsModel.lambda$getAllWidgetsWithoutShortcuts$1(this.f$0, (PackageItemInfo) obj, (List) obj2);
            }
        });
        return hashMap;
    }

    static /* synthetic */ void lambda$getAllWidgetsWithoutShortcuts$1(Map map, PackageItemInfo packageItemInfo, List list) {
        List list2 = (List) list.stream().filter($$Lambda$WidgetsModel$A7300p_ujxxHCQ4ebP1cXfHopk.INSTANCE).collect(Collectors.toList());
        if (list2.size() > 0) {
            map.put(new PackageUserKey(packageItemInfo.packageName, packageItemInfo.user), list2);
        }
    }

    static /* synthetic */ boolean lambda$getAllWidgetsWithoutShortcuts$0(WidgetItem widgetItem) {
        return widgetItem.widgetInfo != null;
    }

    public List<ComponentWithLabelAndIcon> update(LauncherAppState launcherAppState, PackageUserKey packageUserKey) {
        Preconditions.assertWorkerThread();
        Context context = launcherAppState.getContext();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        try {
            InvariantDeviceProfile invariantDeviceProfile = launcherAppState.getInvariantDeviceProfile();
            PackageManager packageManager = launcherAppState.getContext().getPackageManager();
            for (AppWidgetProviderInfo fromProviderInfo : new WidgetManagerHelper(context).getAllProviders(packageUserKey)) {
                LauncherAppWidgetProviderInfo fromProviderInfo2 = LauncherAppWidgetProviderInfo.fromProviderInfo(context, fromProviderInfo);
                arrayList.add(new WidgetItem(fromProviderInfo2, invariantDeviceProfile, launcherAppState.getIconCache()));
                arrayList2.add(fromProviderInfo2);
            }
            for (ShortcutConfigActivityInfo next : ShortcutConfigActivityInfo.queryList(context, packageUserKey)) {
                arrayList.add(new WidgetItem(next, launcherAppState.getIconCache(), packageManager));
                arrayList2.add(next);
            }
            setWidgetsAndShortcuts(arrayList, launcherAppState, packageUserKey);
        } catch (Exception e) {
            if (!Utilities.isBinderSizeError(e)) {
                throw e;
            }
        }
        return arrayList2;
    }

    private synchronized void setWidgetsAndShortcuts(ArrayList<WidgetItem> arrayList, LauncherAppState launcherAppState, PackageUserKey packageUserKey) {
        PackageItemInfoCache packageItemInfoCache = new PackageItemInfoCache();
        if (packageUserKey == null) {
            this.mWidgetsList.clear();
        } else {
            this.mWidgetsList.remove(packageItemInfoCache.getOrCreate(packageUserKey));
        }
        this.mWidgetsList.putAll((Map) arrayList.stream().filter(new WidgetValidityCheck(launcherAppState)).flatMap(new Function(launcherAppState, packageItemInfoCache) {
            public final /* synthetic */ LauncherAppState f$1;
            public final /* synthetic */ WidgetsModel.PackageItemInfoCache f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final Object apply(Object obj) {
                return WidgetsModel.this.lambda$setWidgetsAndShortcuts$3$WidgetsModel(this.f$1, this.f$2, (WidgetItem) obj);
            }
        }).collect(Collectors.groupingBy($$Lambda$WidgetsModel$rgLuQwejahrVxXth_3EySwWOxkQ.INSTANCE, Collectors.mapping($$Lambda$WidgetsModel$qD7kAcM7TxLFPNGOq70uV97yA0.INSTANCE, Collectors.toList()))));
        IconCache iconCache = launcherAppState.getIconCache();
        for (PackageItemInfo titleAndIconForApp : packageItemInfoCache.values()) {
            iconCache.getTitleAndIconForApp(titleAndIconForApp, true);
        }
    }

    public /* synthetic */ Stream lambda$setWidgetsAndShortcuts$3$WidgetsModel(LauncherAppState launcherAppState, PackageItemInfoCache packageItemInfoCache, WidgetItem widgetItem) {
        return getPackageUserKeys(launcherAppState.getContext(), widgetItem).stream().map(new Function(widgetItem) {
            public final /* synthetic */ WidgetItem f$1;

            {
                this.f$1 = r2;
            }

            public final Object apply(Object obj) {
                return WidgetsModel.lambda$setWidgetsAndShortcuts$2(WidgetsModel.PackageItemInfoCache.this, this.f$1, (PackageUserKey) obj);
            }
        });
    }

    static /* synthetic */ Pair lambda$setWidgetsAndShortcuts$2(PackageItemInfoCache packageItemInfoCache, WidgetItem widgetItem, PackageUserKey packageUserKey) {
        return new Pair(packageItemInfoCache.getOrCreate(packageUserKey), widgetItem);
    }

    static /* synthetic */ PackageItemInfo lambda$setWidgetsAndShortcuts$4(Pair pair) {
        return (PackageItemInfo) pair.first;
    }

    static /* synthetic */ WidgetItem lambda$setWidgetsAndShortcuts$5(Pair pair) {
        return (WidgetItem) pair.second;
    }

    public void onPackageIconsUpdated(Set<String> set, UserHandle userHandle, LauncherAppState launcherAppState) {
        for (Map.Entry next : this.mWidgetsList.entrySet()) {
            if (set.contains(((PackageItemInfo) next.getKey()).packageName)) {
                List list = (List) next.getValue();
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    WidgetItem widgetItem = (WidgetItem) list.get(i);
                    if (widgetItem.user.equals(userHandle)) {
                        if (widgetItem.activityInfo != null) {
                            list.set(i, new WidgetItem(widgetItem.activityInfo, launcherAppState.getIconCache(), launcherAppState.getContext().getPackageManager()));
                        } else {
                            list.set(i, new WidgetItem(widgetItem.widgetInfo, launcherAppState.getInvariantDeviceProfile(), launcherAppState.getIconCache()));
                        }
                    }
                }
            }
        }
    }

    public WidgetItem getWidgetProviderInfoByProviderName(ComponentName componentName, UserHandle userHandle) {
        List<WidgetItem> list = this.mWidgetsList.get(new PackageItemInfo(componentName.getPackageName(), userHandle));
        if (list == null) {
            return null;
        }
        for (WidgetItem widgetItem : list) {
            if (widgetItem.componentName.equals(componentName)) {
                return widgetItem;
            }
        }
        return null;
    }

    public static PackageItemInfo newPendingItemInfo(Context context, ComponentName componentName, UserHandle userHandle) {
        Map<ComponentName, IntSet> widgetsToCategory = WidgetSections.getWidgetsToCategory(context);
        if (!widgetsToCategory.containsKey(componentName)) {
            return new PackageItemInfo(componentName.getPackageName(), userHandle);
        }
        Iterator<Integer> it = widgetsToCategory.get(componentName).iterator();
        int i = -1;
        while (it.hasNext() && i == -1) {
            i = it.next().intValue();
        }
        return new PackageItemInfo(componentName.getPackageName(), i, userHandle);
    }

    private List<PackageUserKey> getPackageUserKeys(Context context, WidgetItem widgetItem) {
        IntSet intSet = WidgetSections.getWidgetsToCategory(context).get(widgetItem.componentName);
        if (intSet == null || intSet.isEmpty()) {
            return Arrays.asList(new PackageUserKey[]{new PackageUserKey(widgetItem.componentName.getPackageName(), widgetItem.user)});
        }
        ArrayList arrayList = new ArrayList();
        intSet.forEach(new Consumer(arrayList, widgetItem) {
            public final /* synthetic */ List f$0;
            public final /* synthetic */ WidgetItem f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                WidgetsModel.lambda$getPackageUserKeys$6(this.f$0, this.f$1, (Integer) obj);
            }
        });
        return arrayList;
    }

    static /* synthetic */ void lambda$getPackageUserKeys$6(List list, WidgetItem widgetItem, Integer num) {
        if (num.intValue() == -1) {
            list.add(new PackageUserKey(widgetItem.componentName.getPackageName(), widgetItem.user));
        } else {
            list.add(new PackageUserKey(num.intValue(), widgetItem.user));
        }
    }

    private static class WidgetValidityCheck implements Predicate<WidgetItem> {
        private final AppFilter mAppFilter;
        private final InvariantDeviceProfile mIdp;

        WidgetValidityCheck(LauncherAppState launcherAppState) {
            this.mIdp = launcherAppState.getInvariantDeviceProfile();
            this.mAppFilter = new AppFilter(launcherAppState.getContext());
        }

        public boolean test(WidgetItem widgetItem) {
            if ((widgetItem.widgetInfo == null || ((widgetItem.widgetInfo.getWidgetFeatures() & 2) == 0 && widgetItem.widgetInfo.isMinSizeFulfilled())) && this.mAppFilter.shouldShowApp(widgetItem.componentName)) {
                return true;
            }
            return false;
        }
    }

    private static final class PackageItemInfoCache {
        private final Map<PackageUserKey, PackageItemInfo> mMap;

        private PackageItemInfoCache() {
            this.mMap = new ArrayMap();
        }

        /* access modifiers changed from: package-private */
        public PackageItemInfo getOrCreate(PackageUserKey packageUserKey) {
            PackageItemInfo packageItemInfo = this.mMap.get(packageUserKey);
            if (packageItemInfo != null) {
                return packageItemInfo;
            }
            PackageItemInfo packageItemInfo2 = new PackageItemInfo(packageUserKey.mPackageName, packageUserKey.mWidgetCategory, packageUserKey.mUser);
            packageItemInfo2.user = packageUserKey.mUser;
            this.mMap.put(packageUserKey, packageItemInfo2);
            return packageItemInfo2;
        }

        /* access modifiers changed from: package-private */
        public Collection<PackageItemInfo> values() {
            return this.mMap.values();
        }
    }
}
