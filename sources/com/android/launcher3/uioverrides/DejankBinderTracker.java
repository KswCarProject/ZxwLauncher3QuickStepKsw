package com.android.launcher3.uioverrides;

import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import java.util.HashSet;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class DejankBinderTracker implements Binder.ProxyTransactListener {
    private static final String TAG = "DejankBinderTracker";
    private static final Object sLock = new Object();
    private static boolean sTemporarilyIgnoreTracking = false;
    private static boolean sTrackingAllowed = false;
    private static final HashSet<String> sWhitelistedFrameworkClasses;
    private boolean mIsTracking = false;
    private BiConsumer<String, Integer> mUnexpectedTransactionCallback;

    public void onTransactEnded(Object obj) {
    }

    public Object onTransactStarted(IBinder iBinder, int i) {
        return null;
    }

    static {
        HashSet<String> hashSet = new HashSet<>();
        sWhitelistedFrameworkClasses = hashSet;
        hashSet.add("android.view.IWindowSession");
        hashSet.add("android.os.IPowerManager");
    }

    public static void whitelistIpcs(Runnable runnable) {
        sTemporarilyIgnoreTracking = true;
        runnable.run();
        sTemporarilyIgnoreTracking = false;
    }

    public static <T> T whitelistIpcs(Supplier<T> supplier) {
        sTemporarilyIgnoreTracking = true;
        T t = supplier.get();
        sTemporarilyIgnoreTracking = false;
        return t;
    }

    public static void allowBinderTrackingInTests() {
        sTrackingAllowed = true;
    }

    public static void disallowBinderTrackingInTests() {
        sTrackingAllowed = false;
    }

    public DejankBinderTracker(BiConsumer<String, Integer> biConsumer) {
        this.mUnexpectedTransactionCallback = biConsumer;
    }

    public void startTracking() {
        if (!Build.TYPE.toLowerCase(Locale.ROOT).contains("debug") && !Build.TYPE.toLowerCase(Locale.ROOT).equals("eng")) {
            Log.wtf(TAG, "Unexpected use of binder tracker in non-debug build", new Exception());
        } else if (!this.mIsTracking) {
            this.mIsTracking = true;
            Binder.setProxyTransactListener(this);
        }
    }

    public void stopTracking() {
        if (this.mIsTracking) {
            this.mIsTracking = false;
            Binder.setProxyTransactListener((Binder.ProxyTransactListener) null);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x003f, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.lang.Object onTransactStarted(android.os.IBinder r3, int r4, int r5) {
        /*
            r2 = this;
            monitor-enter(r2)
            boolean r0 = r2.mIsTracking     // Catch:{ all -> 0x0040 }
            r1 = 0
            if (r0 == 0) goto L_0x003e
            boolean r0 = sTrackingAllowed     // Catch:{ all -> 0x0040 }
            if (r0 == 0) goto L_0x003e
            boolean r0 = sTemporarilyIgnoreTracking     // Catch:{ all -> 0x0040 }
            if (r0 != 0) goto L_0x003e
            r0 = 1
            r5 = r5 & r0
            if (r5 == r0) goto L_0x003e
            boolean r5 = isMainThread()     // Catch:{ all -> 0x0040 }
            if (r5 != 0) goto L_0x0019
            goto L_0x003e
        L_0x0019:
            java.lang.String r5 = r3.getInterfaceDescriptor()     // Catch:{ RemoteException -> 0x0027 }
            java.util.HashSet<java.lang.String> r0 = sWhitelistedFrameworkClasses     // Catch:{ RemoteException -> 0x0027 }
            boolean r3 = r0.contains(r5)     // Catch:{ RemoteException -> 0x0027 }
            if (r3 == 0) goto L_0x0033
            monitor-exit(r2)
            return r1
        L_0x0027:
            r5 = move-exception
            r5.printStackTrace()     // Catch:{ all -> 0x0040 }
            java.lang.Class r3 = r3.getClass()     // Catch:{ all -> 0x0040 }
            java.lang.String r5 = r3.getSimpleName()     // Catch:{ all -> 0x0040 }
        L_0x0033:
            java.util.function.BiConsumer<java.lang.String, java.lang.Integer> r3 = r2.mUnexpectedTransactionCallback     // Catch:{ all -> 0x0040 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0040 }
            r3.accept(r5, r4)     // Catch:{ all -> 0x0040 }
            monitor-exit(r2)
            return r1
        L_0x003e:
            monitor-exit(r2)
            return r1
        L_0x0040:
            r3 = move-exception
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.uioverrides.DejankBinderTracker.onTransactStarted(android.os.IBinder, int, int):java.lang.Object");
    }

    public static boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }
}
