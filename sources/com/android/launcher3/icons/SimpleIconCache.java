package com.android.launcher3.icons;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.SparseLongArray;
import com.android.launcher3.icons.cache.BaseIconCache;

public class SimpleIconCache extends BaseIconCache {
    private static final Object CACHE_LOCK = new Object();
    private static SimpleIconCache sIconCache;
    private final UserManager mUserManager;
    private final SparseLongArray mUserSerialMap = new SparseLongArray(2);

    public SimpleIconCache(Context context, String str, Looper looper, int i, int i2, boolean z) {
        super(context, str, looper, i, i2, z);
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MANAGED_PROFILE_ADDED");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                SimpleIconCache.this.resetUserCache();
            }
        }, intentFilter, (String) null, new Handler(looper), 0);
    }

    /* access modifiers changed from: protected */
    public long getSerialNumberForUser(UserHandle userHandle) {
        synchronized (this.mUserSerialMap) {
            int indexOfKey = this.mUserSerialMap.indexOfKey(userHandle.getIdentifier());
            if (indexOfKey >= 0) {
                long valueAt = this.mUserSerialMap.valueAt(indexOfKey);
                return valueAt;
            }
            long serialNumberForUser = this.mUserManager.getSerialNumberForUser(userHandle);
            this.mUserSerialMap.put(userHandle.getIdentifier(), serialNumberForUser);
            return serialNumberForUser;
        }
    }

    /* access modifiers changed from: private */
    public void resetUserCache() {
        synchronized (this.mUserSerialMap) {
            this.mUserSerialMap.clear();
        }
    }

    /* access modifiers changed from: protected */
    public boolean isInstantApp(ApplicationInfo applicationInfo) {
        return applicationInfo.isInstantApp();
    }

    public BaseIconFactory getIconFactory() {
        return IconFactory.obtain(this.mContext);
    }

    public static SimpleIconCache getIconCache(Context context) {
        synchronized (CACHE_LOCK) {
            SimpleIconCache simpleIconCache = sIconCache;
            if (simpleIconCache != null) {
                return simpleIconCache;
            }
            boolean z = context.getResources().getBoolean(R.bool.simple_cache_enable_im_memory);
            String string = context.getString(R.string.cache_db_name);
            HandlerThread handlerThread = new HandlerThread("simple-icon-cache");
            handlerThread.start();
            SimpleIconCache simpleIconCache2 = new SimpleIconCache(context.getApplicationContext(), string, handlerThread.getLooper(), context.getResources().getConfiguration().densityDpi, context.getResources().getDimensionPixelSize(R.dimen.default_icon_bitmap_size), z);
            sIconCache = simpleIconCache2;
            return simpleIconCache2;
        }
    }
}
