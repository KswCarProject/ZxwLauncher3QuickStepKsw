package com.android.launcher3.icons;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import com.android.launcher3.icons.cache.CachingLogic;

public interface ComponentWithLabel {
    ComponentName getComponent();

    CharSequence getLabel(PackageManager packageManager);

    UserHandle getUser();

    public static class ComponentCachingLogic<T extends ComponentWithLabel> implements CachingLogic<T> {
        private final boolean mAddToMemCache;
        private final PackageManager mPackageManager;

        public ComponentCachingLogic(Context context, boolean z) {
            this.mPackageManager = context.getPackageManager();
            this.mAddToMemCache = z;
        }

        public ComponentName getComponent(T t) {
            return t.getComponent();
        }

        public UserHandle getUser(T t) {
            return t.getUser();
        }

        public CharSequence getLabel(T t) {
            return t.getLabel(this.mPackageManager);
        }

        public BitmapInfo loadIcon(Context context, T t) {
            return BitmapInfo.LOW_RES_INFO;
        }

        public boolean addToMemCache() {
            return this.mAddToMemCache;
        }
    }
}
