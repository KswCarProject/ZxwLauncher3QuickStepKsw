package com.android.launcher3.popup;

import android.content.ComponentName;
import android.service.notification.StatusBarNotification;
import com.android.launcher3.dot.DotInfo;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.ShortcutUtil;
import com.android.launcher3.widget.model.WidgetsListBaseEntry;
import com.android.launcher3.widget.model.WidgetsListContentEntry;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PopupDataProvider implements NotificationListener.NotificationsChangedListener {
    private static final boolean LOGD = false;
    private static final String TAG = "PopupDataProvider";
    private List<WidgetsListBaseEntry> mAllWidgets = Collections.emptyList();
    private PopupDataChangeListener mChangeListener = PopupDataChangeListener.INSTANCE;
    private HashMap<ComponentKey, Integer> mDeepShortcutMap = new HashMap<>();
    private final Consumer<Predicate<PackageUserKey>> mNotificationDotsChangeListener;
    private Map<PackageUserKey, DotInfo> mPackageUserToDotInfos = new HashMap();
    private List<ItemInfo> mRecommendedWidgets = Collections.emptyList();

    public interface PopupDataChangeListener {
        public static final PopupDataChangeListener INSTANCE = new PopupDataChangeListener() {
        };

        void onNotificationDotsUpdated(Predicate<PackageUserKey> predicate) {
        }

        void onRecommendedWidgetsBound() {
        }

        void onSystemShortcutsUpdated() {
        }

        void onWidgetsBound() {
        }

        void trimNotifications(Map<PackageUserKey, DotInfo> map) {
        }
    }

    public PopupDataProvider(Consumer<Predicate<PackageUserKey>> consumer) {
        this.mNotificationDotsChangeListener = consumer;
    }

    private void updateNotificationDots(Predicate<PackageUserKey> predicate) {
        this.mNotificationDotsChangeListener.accept(predicate);
        this.mChangeListener.onNotificationDotsUpdated(predicate);
    }

    public void onNotificationPosted(PackageUserKey packageUserKey, NotificationKeyData notificationKeyData) {
        DotInfo dotInfo = this.mPackageUserToDotInfos.get(packageUserKey);
        if (dotInfo == null) {
            dotInfo = new DotInfo();
            this.mPackageUserToDotInfos.put(packageUserKey, dotInfo);
        }
        if (dotInfo.addOrUpdateNotificationKey(notificationKeyData)) {
            Objects.requireNonNull(packageUserKey);
            updateNotificationDots(new Predicate() {
                public final boolean test(Object obj) {
                    return PackageUserKey.this.equals((PackageUserKey) obj);
                }
            });
        }
    }

    public void onNotificationRemoved(PackageUserKey packageUserKey, NotificationKeyData notificationKeyData) {
        DotInfo dotInfo = this.mPackageUserToDotInfos.get(packageUserKey);
        if (dotInfo != null && dotInfo.removeNotificationKey(notificationKeyData)) {
            if (dotInfo.getNotificationKeys().size() == 0) {
                this.mPackageUserToDotInfos.remove(packageUserKey);
            }
            Objects.requireNonNull(packageUserKey);
            updateNotificationDots(new Predicate() {
                public final boolean test(Object obj) {
                    return PackageUserKey.this.equals((PackageUserKey) obj);
                }
            });
            trimNotifications(this.mPackageUserToDotInfos);
        }
    }

    public void onNotificationFullRefresh(List<StatusBarNotification> list) {
        if (list != null) {
            HashMap hashMap = new HashMap(this.mPackageUserToDotInfos);
            this.mPackageUserToDotInfos.clear();
            for (StatusBarNotification next : list) {
                PackageUserKey fromNotification = PackageUserKey.fromNotification(next);
                DotInfo dotInfo = this.mPackageUserToDotInfos.get(fromNotification);
                if (dotInfo == null) {
                    dotInfo = new DotInfo();
                    this.mPackageUserToDotInfos.put(fromNotification, dotInfo);
                }
                dotInfo.addOrUpdateNotificationKey(NotificationKeyData.fromNotification(next));
            }
            for (PackageUserKey next2 : this.mPackageUserToDotInfos.keySet()) {
                DotInfo dotInfo2 = (DotInfo) hashMap.get(next2);
                DotInfo dotInfo3 = this.mPackageUserToDotInfos.get(next2);
                if (dotInfo2 == null || dotInfo2.getNotificationCount() != dotInfo3.getNotificationCount()) {
                    hashMap.put(next2, dotInfo3);
                } else {
                    hashMap.remove(next2);
                }
            }
            if (!hashMap.isEmpty()) {
                Objects.requireNonNull(hashMap);
                updateNotificationDots(new Predicate(hashMap) {
                    public final /* synthetic */ HashMap f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final boolean test(Object obj) {
                        return this.f$0.containsKey((PackageUserKey) obj);
                    }
                });
            }
            trimNotifications(hashMap);
        }
    }

    private void trimNotifications(Map<PackageUserKey, DotInfo> map) {
        this.mChangeListener.trimNotifications(map);
    }

    public void setDeepShortcutMap(HashMap<ComponentKey, Integer> hashMap) {
        this.mDeepShortcutMap = hashMap;
    }

    public int getShortcutCountForItem(ItemInfo itemInfo) {
        ComponentName targetComponent;
        Integer num;
        if (!ShortcutUtil.supportsDeepShortcuts(itemInfo) || (targetComponent = itemInfo.getTargetComponent()) == null || (num = this.mDeepShortcutMap.get(new ComponentKey(targetComponent, itemInfo.user))) == null) {
            return 0;
        }
        return num.intValue();
    }

    public DotInfo getDotInfoForItem(ItemInfo itemInfo) {
        DotInfo dotInfo;
        if (ShortcutUtil.supportsShortcuts(itemInfo) && (dotInfo = this.mPackageUserToDotInfos.get(PackageUserKey.fromItemInfo(itemInfo))) != null && !getNotificationsForItem(itemInfo, dotInfo.getNotificationKeys()).isEmpty()) {
            return dotInfo;
        }
        return null;
    }

    public List<NotificationKeyData> getNotificationKeysForItem(ItemInfo itemInfo) {
        DotInfo dotInfoForItem = getDotInfoForItem(itemInfo);
        if (dotInfoForItem == null) {
            return Collections.EMPTY_LIST;
        }
        return getNotificationsForItem(itemInfo, dotInfoForItem.getNotificationKeys());
    }

    public void cancelNotification(String str) {
        NotificationListener instanceIfConnected = NotificationListener.getInstanceIfConnected();
        if (instanceIfConnected != null) {
            instanceIfConnected.cancelNotificationFromLauncher(str);
        }
    }

    public void setRecommendedWidgets(List<ItemInfo> list) {
        this.mRecommendedWidgets = list;
        this.mChangeListener.onRecommendedWidgetsBound();
    }

    public void setAllWidgets(List<WidgetsListBaseEntry> list) {
        this.mAllWidgets = list;
        this.mChangeListener.onWidgetsBound();
    }

    public void setChangeListener(PopupDataChangeListener popupDataChangeListener) {
        if (popupDataChangeListener == null) {
            popupDataChangeListener = PopupDataChangeListener.INSTANCE;
        }
        this.mChangeListener = popupDataChangeListener;
    }

    public List<WidgetsListBaseEntry> getAllWidgets() {
        return this.mAllWidgets;
    }

    public List<WidgetItem> getRecommendedWidgets() {
        HashMap hashMap = new HashMap();
        this.mAllWidgets.stream().filter($$Lambda$PopupDataProvider$BqL1t7I7mmexPhDRcT7qqZYfNbc.INSTANCE).forEach(new Consumer(hashMap) {
            public final /* synthetic */ HashMap f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                ((WidgetsListContentEntry) ((WidgetsListBaseEntry) obj)).mWidgets.forEach(new Consumer(this.f$0) {
                    public final /* synthetic */ HashMap f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void accept(Object obj) {
                        this.f$0.put(new ComponentKey(((WidgetItem) obj).componentName, ((WidgetItem) obj).user), (WidgetItem) obj);
                    }
                });
            }
        });
        return (List) this.mRecommendedWidgets.stream().map(new Function(hashMap) {
            public final /* synthetic */ HashMap f$0;

            {
                this.f$0 = r1;
            }

            public final Object apply(Object obj) {
                return PopupDataProvider.lambda$getRecommendedWidgets$3(this.f$0, (ItemInfo) obj);
            }
        }).filter($$Lambda$PopupDataProvider$PCBBgCzFPJejNVwCGTbivZdj96c.INSTANCE).collect(Collectors.toList());
    }

    static /* synthetic */ boolean lambda$getRecommendedWidgets$0(WidgetsListBaseEntry widgetsListBaseEntry) {
        return widgetsListBaseEntry instanceof WidgetsListContentEntry;
    }

    static /* synthetic */ WidgetItem lambda$getRecommendedWidgets$3(HashMap hashMap, ItemInfo itemInfo) {
        return (WidgetItem) hashMap.get(new ComponentKey(itemInfo.getTargetComponent(), itemInfo.user));
    }

    public List<WidgetItem> getWidgetsForPackageUser(PackageUserKey packageUserKey) {
        return (List) this.mAllWidgets.stream().filter(new Predicate() {
            public final boolean test(Object obj) {
                return PopupDataProvider.lambda$getWidgetsForPackageUser$4(PackageUserKey.this, (WidgetsListBaseEntry) obj);
            }
        }).flatMap($$Lambda$PopupDataProvider$KvGzoE0TUR5JsWIqkZFqkPGE3k.INSTANCE).filter(new Predicate() {
            public final boolean test(Object obj) {
                return PackageUserKey.this.mUser.equals(((WidgetItem) obj).user);
            }
        }).collect(Collectors.toList());
    }

    static /* synthetic */ boolean lambda$getWidgetsForPackageUser$4(PackageUserKey packageUserKey, WidgetsListBaseEntry widgetsListBaseEntry) {
        return (widgetsListBaseEntry instanceof WidgetsListContentEntry) && widgetsListBaseEntry.mPkgItem.packageName.equals(packageUserKey.mPackageName);
    }

    public static List<NotificationKeyData> getNotificationsForItem(ItemInfo itemInfo, List<NotificationKeyData> list) {
        String shortcutIdIfPinnedShortcut = ShortcutUtil.getShortcutIdIfPinnedShortcut(itemInfo);
        if (shortcutIdIfPinnedShortcut == null) {
            return list;
        }
        return (List) list.stream().filter(new Predicate(shortcutIdIfPinnedShortcut, ShortcutUtil.getPersonKeysIfPinnedShortcut(itemInfo)) {
            public final /* synthetic */ String f$0;
            public final /* synthetic */ String[] f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final boolean test(Object obj) {
                return PopupDataProvider.lambda$getNotificationsForItem$7(this.f$0, this.f$1, (NotificationKeyData) obj);
            }
        }).collect(Collectors.toList());
    }

    static /* synthetic */ boolean lambda$getNotificationsForItem$7(String str, String[] strArr, NotificationKeyData notificationKeyData) {
        if (notificationKeyData.shortcutId != null) {
            return notificationKeyData.shortcutId.equals(str);
        }
        if (notificationKeyData.personKeysFromNotification.length != 0) {
            return Arrays.equals(notificationKeyData.personKeysFromNotification, strArr);
        }
        return false;
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.println(str + "PopupDataProvider:");
        printWriter.println(str + "\tmPackageUserToDotInfos:" + this.mPackageUserToDotInfos);
    }

    public void redrawSystemShortcuts() {
        this.mChangeListener.onSystemShortcutsUpdated();
    }
}
