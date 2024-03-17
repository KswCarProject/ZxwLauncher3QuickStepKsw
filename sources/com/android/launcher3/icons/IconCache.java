package com.android.launcher3.icons;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Trace;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import androidx.core.util.Pair;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherFiles;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.icons.BaseIconFactory;
import com.android.launcher3.icons.ComponentWithLabel;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.icons.cache.BaseIconCache;
import com.android.launcher3.icons.cache.CachingLogic;
import com.android.launcher3.icons.cache.HandlerRunnable;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.IconRequestInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.model.data.PackageItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.InstantAppResolver;
import com.android.launcher3.util.LooperExecutor;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.widget.WidgetSections;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IconCache extends BaseIconCache {
    private static final String TAG = "Launcher.IconCache";
    private final CachingLogic<ComponentWithLabel> mComponentWithLabelCachingLogic;
    private final IconProvider mIconProvider;
    private final InstantAppResolver mInstantAppResolver;
    private final Predicate<ItemInfoWithIcon> mIsUsingFallbackOrNonDefaultIconCheck;
    private final CachingLogic<LauncherActivityInfo> mLauncherActivityInfoCachingLogic;
    private final LauncherApps mLauncherApps;
    private int mPendingIconRequestCount;
    private final CachingLogic<ShortcutInfo> mShortcutCachingLogic;
    private final UserCache mUserManager;
    private final SparseArray<BitmapInfo> mWidgetCategoryBitmapInfos;

    public interface ItemInfoUpdateReceiver {
        void reapplyItemInfo(ItemInfoWithIcon itemInfoWithIcon);
    }

    static /* synthetic */ ShortcutInfo lambda$getShortcutIcon$4(ShortcutInfo shortcutInfo) {
        return shortcutInfo;
    }

    static /* synthetic */ LauncherActivityInfo lambda$getTitleAndIcon$3(LauncherActivityInfo launcherActivityInfo) {
        return launcherActivityInfo;
    }

    static /* synthetic */ ComponentWithLabel lambda$getTitleNoCache$6(ComponentWithLabel componentWithLabel) {
        return componentWithLabel;
    }

    static /* synthetic */ LauncherActivityInfo lambda$updateTitleAndIcon$2() {
        return null;
    }

    public /* synthetic */ boolean lambda$new$0$IconCache(ItemInfoWithIcon itemInfoWithIcon) {
        return itemInfoWithIcon.bitmap != null && (itemInfoWithIcon.bitmap.isNullOrLowRes() || !isDefaultIcon(itemInfoWithIcon.bitmap, itemInfoWithIcon.user));
    }

    public IconCache(Context context, InvariantDeviceProfile invariantDeviceProfile) {
        this(context, invariantDeviceProfile, LauncherFiles.APP_ICONS_DB, new IconProvider(context));
    }

    public IconCache(Context context, InvariantDeviceProfile invariantDeviceProfile, String str, IconProvider iconProvider) {
        super(context, str, Executors.MODEL_EXECUTOR.getLooper(), invariantDeviceProfile.fillResIconDpi, invariantDeviceProfile.iconBitmapSize, true);
        this.mIsUsingFallbackOrNonDefaultIconCheck = new Predicate() {
            public final boolean test(Object obj) {
                return IconCache.this.lambda$new$0$IconCache((ItemInfoWithIcon) obj);
            }
        };
        this.mPendingIconRequestCount = 0;
        this.mComponentWithLabelCachingLogic = new ComponentWithLabel.ComponentCachingLogic(context, false);
        this.mLauncherActivityInfoCachingLogic = LauncherActivityCachingLogic.newInstance(context);
        this.mShortcutCachingLogic = new ShortcutCachingLogic();
        this.mLauncherApps = (LauncherApps) this.mContext.getSystemService(LauncherApps.class);
        this.mUserManager = UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext);
        this.mInstantAppResolver = InstantAppResolver.newInstance(this.mContext);
        this.mIconProvider = iconProvider;
        this.mWidgetCategoryBitmapInfos = new SparseArray<>();
    }

    /* access modifiers changed from: protected */
    public long getSerialNumberForUser(UserHandle userHandle) {
        return this.mUserManager.getSerialNumberForUser(userHandle);
    }

    /* access modifiers changed from: protected */
    public boolean isInstantApp(ApplicationInfo applicationInfo) {
        return this.mInstantAppResolver.isInstantApp(applicationInfo);
    }

    public BaseIconFactory getIconFactory() {
        return LauncherIcons.obtain(this.mContext);
    }

    public synchronized void updateIconsForPkg(String str, UserHandle userHandle) {
        removeIconsForPkg(str, userHandle);
        try {
            PackageInfo packageInfo = this.mPackageManager.getPackageInfo(str, 8192);
            long serialNumberForUser = this.mUserManager.getSerialNumberForUser(userHandle);
            for (LauncherActivityInfo addIconToDBAndMemCache : this.mLauncherApps.getActivityList(str, userHandle)) {
                addIconToDBAndMemCache(addIconToDBAndMemCache, this.mLauncherActivityInfoCachingLogic, packageInfo, serialNumberForUser, false);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Package not found", e);
        }
        return;
    }

    public void close() {
        getUpdateHandler();
        this.mIconDb.close();
    }

    public HandlerRunnable updateIconInBackground(ItemInfoUpdateReceiver itemInfoUpdateReceiver, ItemInfoWithIcon itemInfoWithIcon) {
        Preconditions.assertUIThread();
        if (this.mPendingIconRequestCount <= 0) {
            Executors.MODEL_EXECUTOR.setThreadPriority(-2);
        }
        this.mPendingIconRequestCount++;
        Handler handler = this.mWorkerHandler;
        $$Lambda$IconCache$bfpQm6NAOX06XlPRSNzv0RSZXF4 r3 = new Supplier(itemInfoWithIcon) {
            public final /* synthetic */ ItemInfoWithIcon f$1;

            {
                this.f$1 = r2;
            }

            public final Object get() {
                return IconCache.this.lambda$updateIconInBackground$1$IconCache(this.f$1);
            }
        };
        LooperExecutor looperExecutor = Executors.MAIN_EXECUTOR;
        Objects.requireNonNull(itemInfoUpdateReceiver);
        HandlerRunnable handlerRunnable = new HandlerRunnable(handler, r3, looperExecutor, new Consumer() {
            public final void accept(Object obj) {
                IconCache.ItemInfoUpdateReceiver.this.reapplyItemInfo((ItemInfoWithIcon) obj);
            }
        }, new Runnable() {
            public final void run() {
                IconCache.this.onIconRequestEnd();
            }
        });
        Utilities.postAsyncCallback(this.mWorkerHandler, handlerRunnable);
        return handlerRunnable;
    }

    public /* synthetic */ ItemInfoWithIcon lambda$updateIconInBackground$1$IconCache(ItemInfoWithIcon itemInfoWithIcon) {
        if ((itemInfoWithIcon instanceof AppInfo) || (itemInfoWithIcon instanceof WorkspaceItemInfo)) {
            getTitleAndIcon(itemInfoWithIcon, false);
        } else if (itemInfoWithIcon instanceof PackageItemInfo) {
            getTitleAndIconForApp((PackageItemInfo) itemInfoWithIcon, false);
        }
        return itemInfoWithIcon;
    }

    /* access modifiers changed from: private */
    public void onIconRequestEnd() {
        int i = this.mPendingIconRequestCount - 1;
        this.mPendingIconRequestCount = i;
        if (i <= 0) {
            Executors.MODEL_EXECUTOR.setThreadPriority(10);
        }
    }

    public synchronized void updateTitleAndIcon(AppInfo appInfo) {
        BaseIconCache.CacheEntry cacheLocked = cacheLocked(appInfo.componentName, appInfo.user, $$Lambda$IconCache$h00ccRreskQTtjklLbqhiHGTWvQ.INSTANCE, this.mLauncherActivityInfoCachingLogic, false, appInfo.usingLowResIcon());
        if (cacheLocked.bitmap != null && !isDefaultIcon(cacheLocked.bitmap, appInfo.user)) {
            applyCacheEntry(cacheLocked, appInfo);
        }
    }

    public synchronized void getTitleAndIcon(ItemInfoWithIcon itemInfoWithIcon, LauncherActivityInfo launcherActivityInfo, boolean z) {
        getTitleAndIcon(itemInfoWithIcon, new Supplier(launcherActivityInfo) {
            public final /* synthetic */ LauncherActivityInfo f$0;

            {
                this.f$0 = r1;
            }

            public final Object get() {
                return IconCache.lambda$getTitleAndIcon$3(this.f$0);
            }
        }, false, z);
    }

    public void getShortcutIcon(ItemInfoWithIcon itemInfoWithIcon, ShortcutInfo shortcutInfo) {
        getShortcutIcon(itemInfoWithIcon, shortcutInfo, this.mIsUsingFallbackOrNonDefaultIconCheck);
    }

    public <T extends ItemInfoWithIcon> void getShortcutIcon(T t, ShortcutInfo shortcutInfo, Predicate<T> predicate) {
        BitmapInfo bitmapInfo;
        if (FeatureFlags.ENABLE_DEEP_SHORTCUT_ICON_CACHE.get()) {
            bitmapInfo = cacheLocked(ShortcutKey.fromInfo(shortcutInfo).componentName, shortcutInfo.getUserHandle(), new Supplier(shortcutInfo) {
                public final /* synthetic */ ShortcutInfo f$0;

                {
                    this.f$0 = r1;
                }

                public final Object get() {
                    return IconCache.lambda$getShortcutIcon$4(this.f$0);
                }
            }, this.mShortcutCachingLogic, false, false).bitmap;
        } else {
            bitmapInfo = this.mShortcutCachingLogic.loadIcon(this.mContext, shortcutInfo);
        }
        if (bitmapInfo.isNullOrLowRes()) {
            bitmapInfo = getDefaultIcon(shortcutInfo.getUserHandle());
        }
        if (!isDefaultIcon(bitmapInfo, shortcutInfo.getUserHandle()) || !predicate.test(t)) {
            t.bitmap = bitmapInfo.withBadgeInfo(getShortcutInfoBadge(shortcutInfo));
        }
    }

    public BitmapInfo getShortcutInfoBadge(ShortcutInfo shortcutInfo) {
        ComponentName activity = shortcutInfo.getActivity();
        if (activity != null) {
            AppInfo appInfo = new AppInfo();
            appInfo.user = shortcutInfo.getUserHandle();
            appInfo.componentName = activity;
            appInfo.intent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER").setComponent(activity);
            getTitleAndIcon(appInfo, false);
            return appInfo.bitmap;
        }
        PackageItemInfo packageItemInfo = new PackageItemInfo(shortcutInfo.getPackage(), shortcutInfo.getUserHandle());
        getTitleAndIconForApp(packageItemInfo, false);
        return packageItemInfo.bitmap;
    }

    public synchronized void getTitleAndIcon(ItemInfoWithIcon itemInfoWithIcon, boolean z) {
        if (itemInfoWithIcon.getTargetComponent() == null) {
            itemInfoWithIcon.bitmap = getDefaultIcon(itemInfoWithIcon.user);
            itemInfoWithIcon.title = "";
            itemInfoWithIcon.contentDescription = "";
        } else {
            getTitleAndIcon(itemInfoWithIcon, new Supplier(itemInfoWithIcon.getIntent(), itemInfoWithIcon) {
                public final /* synthetic */ Intent f$1;
                public final /* synthetic */ ItemInfoWithIcon f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final Object get() {
                    return IconCache.this.lambda$getTitleAndIcon$5$IconCache(this.f$1, this.f$2);
                }
            }, true, z);
        }
    }

    public /* synthetic */ LauncherActivityInfo lambda$getTitleAndIcon$5$IconCache(Intent intent, ItemInfoWithIcon itemInfoWithIcon) {
        return this.mLauncherApps.resolveActivity(intent, itemInfoWithIcon.user);
    }

    public synchronized String getTitleNoCache(ComponentWithLabel componentWithLabel) {
        return Utilities.trim(cacheLocked(componentWithLabel.getComponent(), componentWithLabel.getUser(), new Supplier() {
            public final Object get() {
                return IconCache.lambda$getTitleNoCache$6(ComponentWithLabel.this);
            }
        }, this.mComponentWithLabelCachingLogic, false, true).title);
    }

    public synchronized void getTitleAndIcon(ItemInfoWithIcon itemInfoWithIcon, Supplier<LauncherActivityInfo> supplier, boolean z, boolean z2) {
        applyCacheEntry(cacheLocked(itemInfoWithIcon.getTargetComponent(), itemInfoWithIcon.user, supplier, this.mLauncherActivityInfoCachingLogic, z, z2), itemInfoWithIcon);
    }

    private <T extends ItemInfoWithIcon> Cursor createBulkQueryCursor(List<IconRequestInfo<T>> list, UserHandle userHandle, boolean z) throws SQLiteException {
        String[] strArr = (String[]) Stream.concat(list.stream().map($$Lambda$IconCache$NU8C6WuM7kSDL9RdDmAP_WJFCkM.INSTANCE).filter($$Lambda$IconCache$QykguMtrdAvt37qk5jJ34Yd4.INSTANCE).distinct().map($$Lambda$IconCache$ad7CABArn8pKj6kDCoIXdtDJAM.INSTANCE), Stream.of(Long.toString(getSerialNumberForUser(userHandle)))).toArray($$Lambda$IconCache$uSLrqB2uJPXpAgI0AajKGrxtZV0.INSTANCE);
        return this.mIconDb.query(z ? BaseIconCache.IconDB.COLUMNS_LOW_RES : BaseIconCache.IconDB.COLUMNS_HIGH_RES, "componentName IN ( " + TextUtils.join(",", Collections.nCopies(strArr.length - 1, "?")) + " ) AND " + "profileId" + " = ?", strArr);
    }

    static /* synthetic */ String[] lambda$createBulkQueryCursor$8(int i) {
        return new String[i];
    }

    public synchronized <T extends ItemInfoWithIcon> void getTitlesAndIconsInBulk(List<IconRequestInfo<T>> list) {
        Trace.beginSection("loadIconsInBulk");
        ((Map) list.stream().filter(new Predicate() {
            public final boolean test(Object obj) {
                return IconCache.this.lambda$getTitlesAndIconsInBulk$9$IconCache((IconRequestInfo) obj);
            }
        }).collect(Collectors.groupingBy($$Lambda$IconCache$_hKplvRrV6UZkyR11vRm4JaV2w.INSTANCE))).forEach(new BiConsumer() {
            public final void accept(Object obj, Object obj2) {
                IconCache.this.lambda$getTitlesAndIconsInBulk$13$IconCache((Pair) obj, (List) obj2);
            }
        });
        Trace.endSection();
    }

    public /* synthetic */ boolean lambda$getTitlesAndIconsInBulk$9$IconCache(IconRequestInfo iconRequestInfo) {
        if (iconRequestInfo.itemInfo.getTargetComponent() != null) {
            return true;
        }
        Log.i(TAG, "Skipping Item info with null component name: " + iconRequestInfo.itemInfo);
        iconRequestInfo.itemInfo.bitmap = getDefaultIcon(iconRequestInfo.itemInfo.user);
        return false;
    }

    public /* synthetic */ void lambda$getTitlesAndIconsInBulk$13$IconCache(Pair pair, List list) {
        Trace.beginSection("loadIconSubsectionInBulk");
        loadIconSubsection(pair, list, (Map) list.stream().filter($$Lambda$IconCache$7e8B4RNyj_Te1LieLf0Zi5YOIY.INSTANCE).collect(Collectors.groupingBy($$Lambda$IconCache$UWQOKuy8Ab5Rugfz6KVCtAz6k.INSTANCE)));
        Trace.endSection();
    }

    static /* synthetic */ boolean lambda$getTitlesAndIconsInBulk$11(IconRequestInfo iconRequestInfo) {
        if (iconRequestInfo.itemInfo.itemType != 6) {
            return true;
        }
        Log.e(TAG, "Skipping Item info for deep shortcut: " + iconRequestInfo.itemInfo, new IllegalStateException());
        return false;
    }

    private <T extends ItemInfoWithIcon> void loadIconSubsection(Pair<UserHandle, Boolean> pair, List<IconRequestInfo<T>> list, Map<ComponentName, List<IconRequestInfo<T>>> map) {
        Iterator<ComponentName> it;
        LauncherActivityInfo launcherActivityInfo;
        Cursor createBulkQueryCursor;
        Throwable th;
        Pair<UserHandle, Boolean> pair2 = pair;
        Map<ComponentName, List<IconRequestInfo<T>>> map2 = map;
        Trace.beginSection("loadIconSubsectionWithDatabase");
        try {
            createBulkQueryCursor = createBulkQueryCursor(list, (UserHandle) pair2.first, ((Boolean) pair2.second).booleanValue());
            int columnIndexOrThrow = createBulkQueryCursor.getColumnIndexOrThrow(BaseIconCache.IconDB.COLUMN_COMPONENT);
            while (createBulkQueryCursor.moveToNext()) {
                ComponentName unflattenFromString = ComponentName.unflattenFromString(createBulkQueryCursor.getString(columnIndexOrThrow));
                List<IconRequestInfo> list2 = map2.get(unflattenFromString);
                if (unflattenFromString != null) {
                    BaseIconCache.CacheEntry cacheLocked = cacheLocked(unflattenFromString, (UserHandle) pair2.first, new Supplier(list2) {
                        public final /* synthetic */ List f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final Object get() {
                            return ((IconRequestInfo) this.f$0.get(0)).launcherActivityInfo;
                        }
                    }, this.mLauncherActivityInfoCachingLogic, createBulkQueryCursor, false, ((Boolean) pair2.second).booleanValue());
                    for (IconRequestInfo iconRequestInfo : list2) {
                        applyCacheEntry(cacheLocked, iconRequestInfo.itemInfo);
                    }
                }
            }
            if (createBulkQueryCursor != null) {
                createBulkQueryCursor.close();
            }
        } catch (SQLiteException e) {
            try {
                Log.d(TAG, "Error reading icon cache", e);
            } catch (Throwable th2) {
                Trace.endSection();
                throw th2;
            }
        } catch (Throwable th3) {
            th.addSuppressed(th3);
        }
        Trace.endSection();
        Trace.beginSection("loadIconSubsectionWithFallback");
        Iterator<ComponentName> it2 = map.keySet().iterator();
        while (it2.hasNext()) {
            ComponentName next = it2.next();
            boolean z = false;
            IconRequestInfo iconRequestInfo2 = (IconRequestInfo) map2.get(next).get(0);
            T t = iconRequestInfo2.itemInfo;
            BitmapInfo bitmapInfo = t.bitmap;
            boolean isEmpty = TextUtils.isEmpty(t.title);
            if (bitmapInfo == null || isDefaultIcon(bitmapInfo, t.user) || bitmapInfo == BitmapInfo.LOW_RES_INFO) {
                z = true;
            }
            if (isEmpty || z) {
                Log.i(TAG, "Database bulk icon loading failed, using fallback bulk icon loading for: " + next);
                BaseIconCache.CacheEntry cacheEntry = new BaseIconCache.CacheEntry();
                LauncherActivityInfo launcherActivityInfo2 = iconRequestInfo2.launcherActivityInfo;
                cacheEntry.title = t.title;
                if (bitmapInfo != null) {
                    cacheEntry.bitmap = bitmapInfo;
                }
                cacheEntry.contentDescription = t.contentDescription;
                if (z) {
                    it = it2;
                    launcherActivityInfo = launcherActivityInfo2;
                    loadFallbackIcon(launcherActivityInfo2, cacheEntry, this.mLauncherActivityInfoCachingLogic, false, isEmpty, next, (UserHandle) pair2.first);
                } else {
                    it = it2;
                    launcherActivityInfo = launcherActivityInfo2;
                }
                if (isEmpty && TextUtils.isEmpty(cacheEntry.title) && launcherActivityInfo != null) {
                    loadFallbackTitle(launcherActivityInfo, cacheEntry, this.mLauncherActivityInfoCachingLogic, (UserHandle) pair2.first);
                }
                for (IconRequestInfo iconRequestInfo3 : map2.get(next)) {
                    applyCacheEntry(cacheEntry, iconRequestInfo3.itemInfo);
                }
            } else {
                it = it2;
            }
            it2 = it;
        }
        Trace.endSection();
        return;
        throw th;
    }

    public synchronized void getTitleAndIconForApp(PackageItemInfo packageItemInfo, boolean z) {
        LauncherIcons obtain;
        applyCacheEntry(getEntryForPackageLocked(packageItemInfo.packageName, packageItemInfo.user, z), packageItemInfo);
        if (packageItemInfo.widgetCategory != -1) {
            WidgetSections.WidgetSection widgetSection = WidgetSections.getWidgetSections(this.mContext).get(packageItemInfo.widgetCategory);
            packageItemInfo.title = this.mContext.getString(widgetSection.mSectionTitle);
            packageItemInfo.contentDescription = this.mPackageManager.getUserBadgedLabel(packageItemInfo.title, packageItemInfo.user);
            BitmapInfo bitmapInfo = this.mWidgetCategoryBitmapInfos.get(packageItemInfo.widgetCategory);
            if (bitmapInfo != null) {
                packageItemInfo.bitmap = getBadgedIcon(bitmapInfo, packageItemInfo.user);
                return;
            }
            try {
                obtain = LauncherIcons.obtain(this.mContext);
                BitmapInfo createBadgedIconBitmap = obtain.createBadgedIconBitmap(this.mContext.getDrawable(widgetSection.mSectionDrawable), new BaseIconFactory.IconOptions().setShrinkNonAdaptiveIcons(false));
                this.mWidgetCategoryBitmapInfos.put(packageItemInfo.widgetCategory, createBadgedIconBitmap);
                packageItemInfo.bitmap = getBadgedIcon(createBadgedIconBitmap, packageItemInfo.user);
                if (obtain != null) {
                    obtain.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error initializing bitmap for icons with widget category", e);
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
        } else {
            return;
        }
        return;
        throw th;
    }

    private synchronized BitmapInfo getBadgedIcon(BitmapInfo bitmapInfo, UserHandle userHandle) {
        if (bitmapInfo == null) {
            return getDefaultIcon(userHandle);
        }
        return bitmapInfo.withFlags(getUserFlagOpLocked(userHandle));
    }

    /* access modifiers changed from: protected */
    public void applyCacheEntry(BaseIconCache.CacheEntry cacheEntry, ItemInfoWithIcon itemInfoWithIcon) {
        itemInfoWithIcon.title = Utilities.trim(cacheEntry.title);
        itemInfoWithIcon.contentDescription = cacheEntry.contentDescription;
        itemInfoWithIcon.bitmap = cacheEntry.bitmap == null ? getDefaultIcon(itemInfoWithIcon.user) : cacheEntry.bitmap;
    }

    public Drawable getFullResIcon(LauncherActivityInfo launcherActivityInfo) {
        return this.mIconProvider.getIcon(launcherActivityInfo, this.mIconDpi);
    }

    public void updateSessionCache(PackageUserKey packageUserKey, PackageInstaller.SessionInfo sessionInfo) {
        cachePackageInstallInfo(packageUserKey.mPackageName, packageUserKey.mUser, sessionInfo.getAppIcon(), sessionInfo.getAppLabel());
    }

    /* access modifiers changed from: protected */
    public String getIconSystemState(String str) {
        return this.mIconProvider.getSystemStateForPackage(this.mSystemState, str);
    }
}
