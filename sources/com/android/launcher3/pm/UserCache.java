package com.android.launcher3.pm;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import android.util.LongSparseArray;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.SafeCloseable;
import com.android.launcher3.util.SimpleBroadcastReceiver;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UserCache {
    public static final MainThreadInitializedObject<UserCache> INSTANCE = new MainThreadInitializedObject<>($$Lambda$UserCache$UIrX6UKbSZYvgA7wUshqWkIKZkY.INSTANCE);
    private final Context mContext;
    private final ArrayList<Runnable> mUserChangeListeners = new ArrayList<>();
    private final SimpleBroadcastReceiver mUserChangeReceiver = new SimpleBroadcastReceiver(new Consumer() {
        public final void accept(Object obj) {
            UserCache.this.onUsersChanged((Intent) obj);
        }
    });
    private final UserManager mUserManager;
    private ArrayMap<UserHandle, Long> mUserToSerialMap;
    private LongSparseArray<UserHandle> mUsers;

    public static /* synthetic */ UserCache lambda$UIrX6UKbSZYvgA7wUshqWkIKZkY(Context context) {
        return new UserCache(context);
    }

    private UserCache(Context context) {
        this.mContext = context;
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
    }

    /* access modifiers changed from: private */
    public void onUsersChanged(Intent intent) {
        enableAndResetCache();
        this.mUserChangeListeners.forEach($$Lambda$YNFGg4v_quJTFq0zrWSJoDe4_Zo.INSTANCE);
    }

    public SafeCloseable addUserChangeListener(Runnable runnable) {
        $$Lambda$UserCache$iSMcL44WidpfGGQAGAxvOpZ7IY r0;
        synchronized (this) {
            if (this.mUserChangeListeners.isEmpty()) {
                this.mUserChangeReceiver.register(this.mContext, "android.intent.action.MANAGED_PROFILE_ADDED", "android.intent.action.MANAGED_PROFILE_REMOVED");
                enableAndResetCache();
            }
            this.mUserChangeListeners.add(runnable);
            r0 = new SafeCloseable(runnable) {
                public final /* synthetic */ Runnable f$1;

                {
                    this.f$1 = r2;
                }

                public final void close() {
                    UserCache.this.lambda$addUserChangeListener$0$UserCache(this.f$1);
                }
            };
        }
        return r0;
    }

    private void enableAndResetCache() {
        synchronized (this) {
            this.mUsers = new LongSparseArray<>();
            this.mUserToSerialMap = new ArrayMap<>();
            List<UserHandle> userProfiles = this.mUserManager.getUserProfiles();
            if (userProfiles != null) {
                for (UserHandle next : userProfiles) {
                    long serialNumberForUser = this.mUserManager.getSerialNumberForUser(next);
                    this.mUsers.put(serialNumberForUser, next);
                    this.mUserToSerialMap.put(next, Long.valueOf(serialNumberForUser));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: removeUserChangeListener */
    public void lambda$addUserChangeListener$0$UserCache(Runnable runnable) {
        synchronized (this) {
            this.mUserChangeListeners.remove(runnable);
            if (this.mUserChangeListeners.isEmpty()) {
                this.mContext.unregisterReceiver(this.mUserChangeReceiver);
                this.mUsers = null;
                this.mUserToSerialMap = null;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0015, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long getSerialNumberForUser(android.os.UserHandle r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            android.util.ArrayMap<android.os.UserHandle, java.lang.Long> r0 = r2.mUserToSerialMap     // Catch:{ all -> 0x001e }
            if (r0 == 0) goto L_0x0016
            java.lang.Object r3 = r0.get(r3)     // Catch:{ all -> 0x001e }
            java.lang.Long r3 = (java.lang.Long) r3     // Catch:{ all -> 0x001e }
            if (r3 != 0) goto L_0x0010
            r0 = 0
            goto L_0x0014
        L_0x0010:
            long r0 = r3.longValue()     // Catch:{ all -> 0x001e }
        L_0x0014:
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            return r0
        L_0x0016:
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            android.os.UserManager r0 = r2.mUserManager
            long r0 = r0.getSerialNumberForUser(r3)
            return r0
        L_0x001e:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x001e }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.pm.UserCache.getSerialNumberForUser(android.os.UserHandle):long");
    }

    public UserHandle getUserForSerialNumber(long j) {
        synchronized (this) {
            LongSparseArray<UserHandle> longSparseArray = this.mUsers;
            if (longSparseArray == null) {
                return this.mUserManager.getUserForSerialNumber(j);
            }
            UserHandle userHandle = longSparseArray.get(j);
            return userHandle;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        return java.util.Collections.emptyList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0013, code lost:
        r0 = r2.mUserManager.getUserProfiles();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0019, code lost:
        if (r0 != null) goto L_?;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<android.os.UserHandle> getUserProfiles() {
        /*
            r2 = this;
            monitor-enter(r2)
            android.util.LongSparseArray<android.os.UserHandle> r0 = r2.mUsers     // Catch:{ all -> 0x0020 }
            if (r0 == 0) goto L_0x0012
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x0020 }
            android.util.ArrayMap<android.os.UserHandle, java.lang.Long> r1 = r2.mUserToSerialMap     // Catch:{ all -> 0x0020 }
            java.util.Set r1 = r1.keySet()     // Catch:{ all -> 0x0020 }
            r0.<init>(r1)     // Catch:{ all -> 0x0020 }
            monitor-exit(r2)     // Catch:{ all -> 0x0020 }
            return r0
        L_0x0012:
            monitor-exit(r2)     // Catch:{ all -> 0x0020 }
            android.os.UserManager r0 = r2.mUserManager
            java.util.List r0 = r0.getUserProfiles()
            if (r0 != 0) goto L_0x001f
            java.util.List r0 = java.util.Collections.emptyList()
        L_0x001f:
            return r0
        L_0x0020:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0020 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.pm.UserCache.getUserProfiles():java.util.List");
    }
}
