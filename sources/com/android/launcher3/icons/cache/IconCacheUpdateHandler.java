package com.android.launcher3.icons.cache;

import android.content.ComponentName;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseBooleanArray;
import com.android.launcher3.icons.cache.BaseIconCache;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class IconCacheUpdateHandler {
    /* access modifiers changed from: private */
    public static final Object ICON_UPDATE_TOKEN = new Object();
    private static final boolean MODE_CLEAR_VALID_ITEMS = false;
    private static final boolean MODE_SET_INVALID_ITEMS = true;
    private static final String TAG = "IconCacheUpdateHandler";
    private boolean mFilterMode = true;
    /* access modifiers changed from: private */
    public final BaseIconCache mIconCache;
    private final SparseBooleanArray mItemsToDelete = new SparseBooleanArray();
    private final ArrayMap<UserHandle, Set<String>> mPackagesToIgnore = new ArrayMap<>();
    /* access modifiers changed from: private */
    public final HashMap<String, PackageInfo> mPkgInfoMap;

    public interface OnUpdateCallback {
        void onPackageIconsUpdated(HashSet<String> hashSet, UserHandle userHandle);
    }

    IconCacheUpdateHandler(BaseIconCache baseIconCache) {
        this.mIconCache = baseIconCache;
        this.mPkgInfoMap = new HashMap<>();
        baseIconCache.mWorkerHandler.removeCallbacksAndMessages(ICON_UPDATE_TOKEN);
        createPackageInfoMap();
    }

    public void addPackagesToIgnore(UserHandle userHandle, String str) {
        Set set = this.mPackagesToIgnore.get(userHandle);
        if (set == null) {
            set = new HashSet();
            this.mPackagesToIgnore.put(userHandle, set);
        }
        set.add(str);
    }

    private void createPackageInfoMap() {
        for (PackageInfo next : this.mIconCache.mPackageManager.getInstalledPackages(8192)) {
            this.mPkgInfoMap.put(next.packageName, next);
        }
    }

    public <T> void updateIcons(List<T> list, CachingLogic<T> cachingLogic, OnUpdateCallback onUpdateCallback) {
        HashMap hashMap = new HashMap();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            T t = list.get(i);
            UserHandle user = cachingLogic.getUser(t);
            HashMap hashMap2 = (HashMap) hashMap.get(user);
            if (hashMap2 == null) {
                hashMap2 = new HashMap();
                hashMap.put(user, hashMap2);
            }
            hashMap2.put(cachingLogic.getComponent(t), t);
        }
        for (Map.Entry entry : hashMap.entrySet()) {
            updateIconsPerUser((UserHandle) entry.getKey(), (HashMap) entry.getValue(), cachingLogic, onUpdateCallback);
        }
        this.mFilterMode = false;
    }

    private <T> void updateIconsPerUser(UserHandle userHandle, HashMap<ComponentName, T> hashMap, CachingLogic<T> cachingLogic, OnUpdateCallback onUpdateCallback) {
        Cursor query;
        Throwable th;
        int i;
        boolean z;
        int i2;
        UserHandle userHandle2 = userHandle;
        Set set = this.mPackagesToIgnore.get(userHandle2);
        if (set == null) {
            set = Collections.emptySet();
        }
        long serialNumberForUser = this.mIconCache.getSerialNumberForUser(userHandle2);
        Stack stack = new Stack();
        try {
            query = this.mIconCache.mIconDb.query(new String[]{BaseIconCache.IconDB.COLUMN_ROWID, BaseIconCache.IconDB.COLUMN_COMPONENT, BaseIconCache.IconDB.COLUMN_LAST_UPDATED, BaseIconCache.IconDB.COLUMN_VERSION, BaseIconCache.IconDB.COLUMN_SYSTEM_STATE}, "profileId = ? ", new String[]{Long.toString(serialNumberForUser)});
            int columnIndex = query.getColumnIndex(BaseIconCache.IconDB.COLUMN_COMPONENT);
            int columnIndex2 = query.getColumnIndex(BaseIconCache.IconDB.COLUMN_LAST_UPDATED);
            int columnIndex3 = query.getColumnIndex(BaseIconCache.IconDB.COLUMN_VERSION);
            int columnIndex4 = query.getColumnIndex(BaseIconCache.IconDB.COLUMN_ROWID);
            int columnIndex5 = query.getColumnIndex(BaseIconCache.IconDB.COLUMN_SYSTEM_STATE);
            while (query.moveToNext()) {
                ComponentName unflattenFromString = ComponentName.unflattenFromString(query.getString(columnIndex));
                PackageInfo packageInfo = this.mPkgInfoMap.get(unflattenFromString.getPackageName());
                int i3 = query.getInt(columnIndex4);
                if (packageInfo == null) {
                    if (!set.contains(unflattenFromString.getPackageName())) {
                        if (this.mFilterMode) {
                            this.mIconCache.remove(unflattenFromString, userHandle2);
                            this.mItemsToDelete.put(i3, true);
                        }
                    }
                } else if ((packageInfo.applicationInfo.flags & 16777216) == 0) {
                    long j = query.getLong(columnIndex2);
                    int i4 = query.getInt(columnIndex3);
                    int i5 = columnIndex3;
                    int i6 = columnIndex2;
                    T remove = hashMap.remove(unflattenFromString);
                    if (i4 == packageInfo.versionCode) {
                        i2 = columnIndex;
                        i = columnIndex4;
                        if (j == packageInfo.lastUpdateTime && TextUtils.equals(query.getString(columnIndex5), this.mIconCache.getIconSystemState(packageInfo.packageName))) {
                            if (!this.mFilterMode) {
                                z = false;
                                this.mItemsToDelete.put(i3, false);
                                columnIndex4 = i;
                                columnIndex2 = i6;
                                boolean z2 = z;
                                columnIndex = i2;
                                columnIndex3 = i5;
                            } else {
                                columnIndex = i2;
                                columnIndex4 = i;
                                columnIndex3 = i5;
                                columnIndex2 = i6;
                            }
                        }
                    } else {
                        i2 = columnIndex;
                        i = columnIndex4;
                    }
                    z = false;
                    if (remove != null) {
                        stack.add(remove);
                    } else if (this.mFilterMode) {
                        this.mIconCache.remove(unflattenFromString, userHandle2);
                        this.mItemsToDelete.put(i3, true);
                    }
                    columnIndex4 = i;
                    columnIndex2 = i6;
                    boolean z22 = z;
                    columnIndex = i2;
                    columnIndex3 = i5;
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (SQLiteException e) {
            Log.d(TAG, "Error reading icon cache", e);
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
        if (!hashMap.isEmpty() || !stack.isEmpty()) {
            Stack stack2 = new Stack();
            stack2.addAll(hashMap.values());
            new SerializedIconUpdateTask(serialNumberForUser, userHandle, stack2, stack, cachingLogic, onUpdateCallback).scheduleNext();
            return;
        }
        return;
        throw th;
    }

    public void finish() {
        StringBuilder append = new StringBuilder().append(BaseIconCache.IconDB.COLUMN_ROWID).append(" IN (");
        int size = this.mItemsToDelete.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            if (this.mItemsToDelete.valueAt(i2)) {
                if (i > 0) {
                    append.append(", ");
                }
                append.append(this.mItemsToDelete.keyAt(i2));
                i++;
            }
        }
        append.append(')');
        if (i > 0) {
            this.mIconCache.mIconDb.delete(append.toString(), (String[]) null);
        }
    }

    private class SerializedIconUpdateTask<T> implements Runnable {
        private final Stack<T> mAppsToAdd;
        private final Stack<T> mAppsToUpdate;
        private final CachingLogic<T> mCachingLogic;
        private final OnUpdateCallback mOnUpdateCallback;
        private final HashSet<String> mUpdatedPackages = new HashSet<>();
        private final UserHandle mUserHandle;
        private final long mUserSerial;

        SerializedIconUpdateTask(long j, UserHandle userHandle, Stack<T> stack, Stack<T> stack2, CachingLogic<T> cachingLogic, OnUpdateCallback onUpdateCallback) {
            this.mUserHandle = userHandle;
            this.mUserSerial = j;
            this.mAppsToAdd = stack;
            this.mAppsToUpdate = stack2;
            this.mCachingLogic = cachingLogic;
            this.mOnUpdateCallback = onUpdateCallback;
        }

        public void run() {
            if (!this.mAppsToUpdate.isEmpty()) {
                T pop = this.mAppsToUpdate.pop();
                String packageName = this.mCachingLogic.getComponent(pop).getPackageName();
                IconCacheUpdateHandler.this.mIconCache.addIconToDBAndMemCache(pop, this.mCachingLogic, (PackageInfo) IconCacheUpdateHandler.this.mPkgInfoMap.get(packageName), this.mUserSerial, true);
                this.mUpdatedPackages.add(packageName);
                if (this.mAppsToUpdate.isEmpty() && !this.mUpdatedPackages.isEmpty()) {
                    this.mOnUpdateCallback.onPackageIconsUpdated(this.mUpdatedPackages, this.mUserHandle);
                }
                scheduleNext();
            } else if (!this.mAppsToAdd.isEmpty()) {
                T pop2 = this.mAppsToAdd.pop();
                PackageInfo packageInfo = (PackageInfo) IconCacheUpdateHandler.this.mPkgInfoMap.get(this.mCachingLogic.getComponent(pop2).getPackageName());
                if (packageInfo != null) {
                    IconCacheUpdateHandler.this.mIconCache.addIconToDBAndMemCache(pop2, this.mCachingLogic, packageInfo, this.mUserSerial, false);
                }
                if (!this.mAppsToAdd.isEmpty()) {
                    scheduleNext();
                }
            }
        }

        public void scheduleNext() {
            IconCacheUpdateHandler.this.mIconCache.mWorkerHandler.postAtTime(this, IconCacheUpdateHandler.ICON_UPDATE_TOKEN, SystemClock.uptimeMillis() + 1);
        }
    }
}
