package com.android.launcher3.popup;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.os.Handler;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.notification.NotificationInfo;
import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.android.launcher3.shortcuts.ShortcutRequest;
import com.android.launcher3.views.ActivityContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PopupPopulator {
    public static final int MAX_SHORTCUTS = 4;
    public static final int MAX_SHORTCUTS_IF_NOTIFICATIONS = 2;
    static final int NUM_DYNAMIC = 2;
    private static final Comparator<ShortcutInfo> SHORTCUT_RANK_COMPARATOR = new Comparator<ShortcutInfo>() {
        public int compare(ShortcutInfo shortcutInfo, ShortcutInfo shortcutInfo2) {
            if (shortcutInfo.isDeclaredInManifest() && !shortcutInfo2.isDeclaredInManifest()) {
                return -1;
            }
            if (shortcutInfo.isDeclaredInManifest() || !shortcutInfo2.isDeclaredInManifest()) {
                return Integer.compare(shortcutInfo.getRank(), shortcutInfo2.getRank());
            }
            return 1;
        }
    };

    public static List<ShortcutInfo> sortAndFilterShortcuts(List<ShortcutInfo> list, String str) {
        if (str != null) {
            Iterator<ShortcutInfo> it = list.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next().getId().equals(str)) {
                        it.remove();
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        Collections.sort(list, SHORTCUT_RANK_COMPARATOR);
        if (list.size() <= 4) {
            return list;
        }
        ArrayList arrayList = new ArrayList(4);
        int size = list.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            ShortcutInfo shortcutInfo = list.get(i2);
            int size2 = arrayList.size();
            if (size2 < 4) {
                arrayList.add(shortcutInfo);
                if (shortcutInfo.isDynamic()) {
                    i++;
                }
            } else if (shortcutInfo.isDynamic() && i < 2) {
                i++;
                arrayList.remove(size2 - i);
                arrayList.add(shortcutInfo);
            }
        }
        return arrayList;
    }

    public static <T extends Context & ActivityContext> Runnable createUpdateRunnable(T t, ItemInfo itemInfo, Handler handler, PopupContainerWithArrow popupContainerWithArrow, List<DeepShortcutView> list, List<NotificationKeyData> list2) {
        return new Runnable(list2, t, itemInfo, handler, popupContainerWithArrow, itemInfo.user, itemInfo.getTargetComponent(), list) {
            public final /* synthetic */ List f$0;
            public final /* synthetic */ Context f$1;
            public final /* synthetic */ ItemInfo f$2;
            public final /* synthetic */ Handler f$3;
            public final /* synthetic */ PopupContainerWithArrow f$4;
            public final /* synthetic */ UserHandle f$5;
            public final /* synthetic */ ComponentName f$6;
            public final /* synthetic */ List f$7;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                PopupPopulator.lambda$createUpdateRunnable$3(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        };
    }

    static /* synthetic */ void lambda$createUpdateRunnable$3(List list, Context context, ItemInfo itemInfo, Handler handler, PopupContainerWithArrow popupContainerWithArrow, UserHandle userHandle, ComponentName componentName, List list2) {
        String str;
        List list3;
        if (!list.isEmpty()) {
            NotificationListener instanceIfConnected = NotificationListener.getInstanceIfConnected();
            if (instanceIfConnected == null) {
                list3 = Collections.emptyList();
            } else {
                list3 = (List) instanceIfConnected.getNotificationsForKeys(list).stream().map(new Function(context, itemInfo) {
                    public final /* synthetic */ Context f$0;
                    public final /* synthetic */ ItemInfo f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final Object apply(Object obj) {
                        return PopupPopulator.lambda$createUpdateRunnable$0(this.f$0, this.f$1, (StatusBarNotification) obj);
                    }
                }).collect(Collectors.toList());
            }
            handler.post(new Runnable(list3) {
                public final /* synthetic */ List f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PopupContainerWithArrow.this.applyNotificationInfos(this.f$1);
                }
            });
        }
        ShortcutRequest.QueryResult query = new ShortcutRequest(context, userHandle).withContainer(componentName).query(9);
        int i = 0;
        if (list.isEmpty()) {
            str = null;
        } else {
            str = ((NotificationKeyData) list.get(0)).shortcutId;
        }
        List<ShortcutInfo> sortAndFilterShortcuts = sortAndFilterShortcuts(query, str);
        IconCache iconCache = LauncherAppState.getInstance(context).getIconCache();
        while (i < sortAndFilterShortcuts.size() && i < list2.size()) {
            ShortcutInfo shortcutInfo = sortAndFilterShortcuts.get(i);
            WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo(shortcutInfo, context);
            iconCache.getShortcutIcon(workspaceItemInfo, shortcutInfo);
            workspaceItemInfo.rank = i;
            workspaceItemInfo.container = LauncherSettings.Favorites.CONTAINER_SHORTCUTS;
            handler.post(new Runnable(workspaceItemInfo, shortcutInfo, popupContainerWithArrow) {
                public final /* synthetic */ WorkspaceItemInfo f$1;
                public final /* synthetic */ ShortcutInfo f$2;
                public final /* synthetic */ PopupContainerWithArrow f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    DeepShortcutView.this.applyShortcutInfo(this.f$1, this.f$2, this.f$3);
                }
            });
            i++;
        }
    }

    static /* synthetic */ NotificationInfo lambda$createUpdateRunnable$0(Context context, ItemInfo itemInfo, StatusBarNotification statusBarNotification) {
        return new NotificationInfo(context, statusBarNotification, itemInfo);
    }
}
