package com.android.launcher3.model;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.os.UserHandle;
import android.util.Log;
import android.util.Pair;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.model.ItemInstallQueue;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.shortcuts.ShortcutRequest;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.PersistedItemArray;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.widget.LauncherAppWidgetProviderInfo;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemInstallQueue {
    private static final String APPS_PENDING_INSTALL = "apps_to_install";
    public static final int FLAG_ACTIVITY_PAUSED = 1;
    public static final int FLAG_DRAG_AND_DROP = 4;
    public static final int FLAG_LOADER_RUNNING = 2;
    public static MainThreadInitializedObject<ItemInstallQueue> INSTANCE = new MainThreadInitializedObject<>($$Lambda$ItemInstallQueue$WTKciH_7lXJjNEZTk9sAnzzlO4.INSTANCE);
    private static final String LOG = "ItemInstallQueue";
    public static final int NEW_SHORTCUT_BOUNCE_DURATION = 450;
    public static final int NEW_SHORTCUT_STAGGER_DELAY = 85;
    private static final String TAG = "InstallShortcutReceiver";
    private final Context mContext;
    private int mInstallQueueDisabledFlags = 0;
    private List<PendingInstallShortcutInfo> mItems;
    private final PersistedItemArray<PendingInstallShortcutInfo> mStorage = new PersistedItemArray<>(APPS_PENDING_INSTALL);

    /* renamed from: lambda$WTKciH_7lXJjNEZ-Tk9sAnzzlO4  reason: not valid java name */
    public static /* synthetic */ ItemInstallQueue m40lambda$WTKciH_7lXJjNEZTk9sAnzzlO4(Context context) {
        return new ItemInstallQueue(context);
    }

    private ItemInstallQueue(Context context) {
        this.mContext = context;
    }

    private void ensureQueueLoaded() {
        Preconditions.assertWorkerThread();
        if (this.mItems == null) {
            this.mItems = this.mStorage.read(this.mContext, new PersistedItemArray.ItemFactory() {
                public final ItemInfo createInfo(int i, UserHandle userHandle, Intent intent) {
                    return ItemInstallQueue.this.decode(i, userHandle, intent);
                }
            });
        }
    }

    private void addToQueue(PendingInstallShortcutInfo pendingInstallShortcutInfo) {
        ensureQueueLoaded();
        if (!this.mItems.contains(pendingInstallShortcutInfo)) {
            this.mItems.add(pendingInstallShortcutInfo);
            this.mStorage.write(this.mContext, this.mItems);
        }
    }

    /* access modifiers changed from: private */
    public void flushQueueInBackground() {
        Launcher launcher = (Launcher) Launcher.ACTIVITY_TRACKER.getCreatedActivity();
        if (launcher != null) {
            ensureQueueLoaded();
            if (!this.mItems.isEmpty()) {
                List list = (List) this.mItems.stream().map(new Function() {
                    public final Object apply(Object obj) {
                        return ItemInstallQueue.this.lambda$flushQueueInBackground$0$ItemInstallQueue((ItemInstallQueue.PendingInstallShortcutInfo) obj);
                    }
                }).collect(Collectors.toList());
                if (!list.isEmpty()) {
                    if (TestProtocol.sDebugTracing) {
                        Log.d(TestProtocol.MISSING_PROMISE_ICON, "ItemInstallQueue flushQueueInBackground launcher addAndBindAddedWorkspaceItems");
                    }
                    launcher.getModel().addAndBindAddedWorkspaceItems(list);
                }
                this.mItems.clear();
                this.mStorage.getFile(this.mContext).delete();
            } else if (TestProtocol.sDebugTracing) {
                Log.d(TestProtocol.MISSING_PROMISE_ICON, "ItemInstallQueue flushQueueInBackground no items to load");
            }
        } else if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.MISSING_PROMISE_ICON, "ItemInstallQueue flushQueueInBackground launcher not loaded");
        }
    }

    public /* synthetic */ Pair lambda$flushQueueInBackground$0$ItemInstallQueue(PendingInstallShortcutInfo pendingInstallShortcutInfo) {
        return pendingInstallShortcutInfo.getItemInfo(this.mContext);
    }

    public void removeFromInstallQueue(HashSet<String> hashSet, UserHandle userHandle) {
        if (!hashSet.isEmpty()) {
            ensureQueueLoaded();
            if (this.mItems.removeIf(new Predicate(userHandle, hashSet) {
                public final /* synthetic */ UserHandle f$0;
                public final /* synthetic */ HashSet f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final boolean test(Object obj) {
                    return ItemInstallQueue.lambda$removeFromInstallQueue$1(this.f$0, this.f$1, (ItemInstallQueue.PendingInstallShortcutInfo) obj);
                }
            })) {
                this.mStorage.write(this.mContext, this.mItems);
            }
        }
    }

    static /* synthetic */ boolean lambda$removeFromInstallQueue$1(UserHandle userHandle, HashSet hashSet, PendingInstallShortcutInfo pendingInstallShortcutInfo) {
        return pendingInstallShortcutInfo.user.equals(userHandle) && hashSet.contains(getIntentPackage(pendingInstallShortcutInfo.intent));
    }

    public void queueItem(ShortcutInfo shortcutInfo) {
        queuePendingShortcutInfo(new PendingInstallShortcutInfo(shortcutInfo));
    }

    public void queueItem(AppWidgetProviderInfo appWidgetProviderInfo, int i) {
        queuePendingShortcutInfo(new PendingInstallShortcutInfo(appWidgetProviderInfo, i));
    }

    public void queueItem(String str, UserHandle userHandle) {
        queuePendingShortcutInfo(new PendingInstallShortcutInfo(str, userHandle));
    }

    public Stream<ShortcutKey> getPendingShortcuts(UserHandle userHandle) {
        ensureQueueLoaded();
        return this.mItems.stream().filter(new Predicate(userHandle) {
            public final /* synthetic */ UserHandle f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return ItemInstallQueue.lambda$getPendingShortcuts$2(this.f$0, (ItemInstallQueue.PendingInstallShortcutInfo) obj);
            }
        }).map(new Function(userHandle) {
            public final /* synthetic */ UserHandle f$0;

            {
                this.f$0 = r1;
            }

            public final Object apply(Object obj) {
                return ShortcutKey.fromIntent(((ItemInstallQueue.PendingInstallShortcutInfo) obj).intent, this.f$0);
            }
        });
    }

    static /* synthetic */ boolean lambda$getPendingShortcuts$2(UserHandle userHandle, PendingInstallShortcutInfo pendingInstallShortcutInfo) {
        return pendingInstallShortcutInfo.itemType == 6 && userHandle.equals(pendingInstallShortcutInfo.user);
    }

    private void queuePendingShortcutInfo(PendingInstallShortcutInfo pendingInstallShortcutInfo) {
        Executors.MODEL_EXECUTOR.post(new Runnable(pendingInstallShortcutInfo, new Exception()) {
            public final /* synthetic */ ItemInstallQueue.PendingInstallShortcutInfo f$1;
            public final /* synthetic */ Exception f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ItemInstallQueue.this.lambda$queuePendingShortcutInfo$4$ItemInstallQueue(this.f$1, this.f$2);
            }
        });
        flushInstallQueue();
    }

    public /* synthetic */ void lambda$queuePendingShortcutInfo$4$ItemInstallQueue(PendingInstallShortcutInfo pendingInstallShortcutInfo, Exception exc) {
        Pair<ItemInfo, Object> itemInfo = pendingInstallShortcutInfo.getItemInfo(this.mContext);
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.MISSING_PROMISE_ICON, "ItemInstallQueue queuePendingShortcutInfo, itemInfo=" + itemInfo);
        }
        if (itemInfo == null) {
            FileLog.d(LOG, "Adding PendingInstallShortcutInfo with no attached info to queue.", exc);
        } else {
            FileLog.d(LOG, "Adding PendingInstallShortcutInfo to queue. Attached info: " + itemInfo.first, exc);
        }
        addToQueue(pendingInstallShortcutInfo);
    }

    public void pauseModelPush(int i) {
        this.mInstallQueueDisabledFlags = i | this.mInstallQueueDisabledFlags;
    }

    public void resumeModelPush(int i) {
        this.mInstallQueueDisabledFlags = (~i) & this.mInstallQueueDisabledFlags;
        flushInstallQueue();
    }

    private void flushInstallQueue() {
        if (this.mInstallQueueDisabledFlags == 0) {
            Executors.MODEL_EXECUTOR.post(new Runnable() {
                public final void run() {
                    ItemInstallQueue.this.flushQueueInBackground();
                }
            });
        }
    }

    private static class PendingInstallShortcutInfo extends ItemInfo {
        final Intent intent;
        AppWidgetProviderInfo providerInfo;
        ShortcutInfo shortcutInfo;

        static /* synthetic */ LauncherActivityInfo lambda$getItemInfo$0(LauncherActivityInfo launcherActivityInfo) {
            return launcherActivityInfo;
        }

        public PendingInstallShortcutInfo(String str, UserHandle userHandle) {
            this.itemType = 0;
            this.intent = new Intent().setPackage(str);
            this.user = userHandle;
        }

        public PendingInstallShortcutInfo(ShortcutInfo shortcutInfo2) {
            this.itemType = 6;
            this.intent = ShortcutKey.makeIntent(shortcutInfo2);
            this.user = shortcutInfo2.getUserHandle();
            this.shortcutInfo = shortcutInfo2;
        }

        public PendingInstallShortcutInfo(AppWidgetProviderInfo appWidgetProviderInfo, int i) {
            this.itemType = 4;
            this.intent = new Intent().setComponent(appWidgetProviderInfo.provider).putExtra(LauncherSettings.Favorites.APPWIDGET_ID, i);
            this.user = appWidgetProviderInfo.getProfile();
            this.providerInfo = appWidgetProviderInfo;
        }

        public Intent getIntent() {
            return this.intent;
        }

        public Pair<ItemInfo, Object> getItemInfo(Context context) {
            LauncherActivityInfo launcherActivityInfo;
            int i = this.itemType;
            if (i == 0) {
                String str = this.intent.getPackage();
                List<LauncherActivityInfo> activityList = ((LauncherApps) context.getSystemService(LauncherApps.class)).getActivityList(str, this.user);
                WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo();
                workspaceItemInfo.user = this.user;
                workspaceItemInfo.itemType = 0;
                boolean isEmpty = activityList.isEmpty();
                if (isEmpty) {
                    workspaceItemInfo.intent = AppInfo.makeLaunchIntent(new ComponentName(str, "")).setPackage(str);
                    workspaceItemInfo.status |= 2;
                    launcherActivityInfo = null;
                } else {
                    launcherActivityInfo = activityList.get(0);
                    workspaceItemInfo.intent = AppInfo.makeLaunchIntent(launcherActivityInfo);
                }
                LauncherAppState.getInstance(context).getIconCache().getTitleAndIcon(workspaceItemInfo, new Supplier(launcherActivityInfo) {
                    public final /* synthetic */ LauncherActivityInfo f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final Object get() {
                        return ItemInstallQueue.PendingInstallShortcutInfo.lambda$getItemInfo$0(this.f$0);
                    }
                }, isEmpty, false);
                return Pair.create(workspaceItemInfo, (Object) null);
            } else if (i == 4) {
                LauncherAppWidgetProviderInfo fromProviderInfo = LauncherAppWidgetProviderInfo.fromProviderInfo(context, this.providerInfo);
                LauncherAppWidgetInfo launcherAppWidgetInfo = new LauncherAppWidgetInfo(this.intent.getIntExtra(LauncherSettings.Favorites.APPWIDGET_ID, 0), fromProviderInfo.provider);
                InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
                launcherAppWidgetInfo.minSpanX = fromProviderInfo.minSpanX;
                launcherAppWidgetInfo.minSpanY = fromProviderInfo.minSpanY;
                launcherAppWidgetInfo.spanX = Math.min(fromProviderInfo.spanX, idp.numColumns);
                launcherAppWidgetInfo.spanY = Math.min(fromProviderInfo.spanY, idp.numRows);
                launcherAppWidgetInfo.user = this.user;
                return Pair.create(launcherAppWidgetInfo, this.providerInfo);
            } else if (i != 6) {
                return null;
            } else {
                WorkspaceItemInfo workspaceItemInfo2 = new WorkspaceItemInfo(this.shortcutInfo, context);
                LauncherAppState.getInstance(context).getIconCache().getShortcutIcon(workspaceItemInfo2, this.shortcutInfo);
                return Pair.create(workspaceItemInfo2, this.shortcutInfo);
            }
        }

        public boolean equals(Object obj) {
            boolean z;
            boolean z2;
            if (!(obj instanceof PendingInstallShortcutInfo)) {
                return false;
            }
            PendingInstallShortcutInfo pendingInstallShortcutInfo = (PendingInstallShortcutInfo) obj;
            boolean equals = this.user.equals(pendingInstallShortcutInfo.user);
            boolean z3 = this.itemType == pendingInstallShortcutInfo.itemType;
            boolean equals2 = this.intent.toUri(0).equals(pendingInstallShortcutInfo.intent.toUri(0));
            ShortcutInfo shortcutInfo2 = this.shortcutInfo;
            if (shortcutInfo2 != null ? pendingInstallShortcutInfo.shortcutInfo == null || !shortcutInfo2.getId().equals(pendingInstallShortcutInfo.shortcutInfo.getId()) || !this.shortcutInfo.getPackage().equals(pendingInstallShortcutInfo.shortcutInfo.getPackage()) : pendingInstallShortcutInfo.shortcutInfo != null) {
                z = false;
            } else {
                z = true;
            }
            AppWidgetProviderInfo appWidgetProviderInfo = this.providerInfo;
            if (appWidgetProviderInfo != null ? pendingInstallShortcutInfo.providerInfo == null || !appWidgetProviderInfo.provider.equals(pendingInstallShortcutInfo.providerInfo.provider) : pendingInstallShortcutInfo.providerInfo != null) {
                z2 = false;
            } else {
                z2 = true;
            }
            if (!equals || !z3 || !equals2 || !z || !z2) {
                return false;
            }
            return true;
        }
    }

    private static String getIntentPackage(Intent intent) {
        return intent.getComponent() == null ? intent.getPackage() : intent.getComponent().getPackageName();
    }

    /* access modifiers changed from: private */
    public PendingInstallShortcutInfo decode(int i, UserHandle userHandle, Intent intent) {
        if (i == 0) {
            return new PendingInstallShortcutInfo(intent.getPackage(), userHandle);
        }
        if (i == 4) {
            int intExtra = intent.getIntExtra(LauncherSettings.Favorites.APPWIDGET_ID, 0);
            AppWidgetProviderInfo appWidgetInfo = AppWidgetManager.getInstance(this.mContext).getAppWidgetInfo(intExtra);
            if (appWidgetInfo == null || !appWidgetInfo.provider.equals(intent.getComponent()) || !appWidgetInfo.getProfile().equals(userHandle)) {
                return null;
            }
            return new PendingInstallShortcutInfo(appWidgetInfo, intExtra);
        } else if (i != 6) {
            Log.e(TAG, "Unknown item type");
            return null;
        } else {
            ShortcutRequest.QueryResult query = ShortcutKey.fromIntent(intent, userHandle).buildRequest(this.mContext).query(11);
            if (query.isEmpty()) {
                return null;
            }
            return new PendingInstallShortcutInfo((ShortcutInfo) query.get(0));
        }
    }
}
