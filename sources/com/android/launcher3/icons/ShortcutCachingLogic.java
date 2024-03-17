package com.android.launcher3.icons;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.icons.cache.CachingLogic;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.Themes;

public class ShortcutCachingLogic implements CachingLogic<ShortcutInfo> {
    private static final String TAG = "ShortcutCachingLogic";

    public boolean addToMemCache() {
        return false;
    }

    public ComponentName getComponent(ShortcutInfo shortcutInfo) {
        return ShortcutKey.fromInfo(shortcutInfo).componentName;
    }

    public UserHandle getUser(ShortcutInfo shortcutInfo) {
        return shortcutInfo.getUserHandle();
    }

    public CharSequence getLabel(ShortcutInfo shortcutInfo) {
        return shortcutInfo.getShortLabel();
    }

    public CharSequence getDescription(ShortcutInfo shortcutInfo, CharSequence charSequence) {
        CharSequence longLabel = shortcutInfo.getLongLabel();
        return TextUtils.isEmpty(longLabel) ? charSequence : longLabel;
    }

    public BitmapInfo loadIcon(Context context, ShortcutInfo shortcutInfo) {
        LauncherIcons obtain = LauncherIcons.obtain(context);
        try {
            Drawable icon = getIcon(context, shortcutInfo, LauncherAppState.getIDP(context).fillResIconDpi);
            if (icon == null) {
                BitmapInfo bitmapInfo = BitmapInfo.LOW_RES_INFO;
                if (obtain != null) {
                    obtain.close();
                }
                return bitmapInfo;
            }
            BitmapInfo bitmapInfo2 = new BitmapInfo(obtain.createScaledBitmapWithoutShadow(icon), Themes.getColorAccent(context));
            if (obtain != null) {
                obtain.close();
            }
            return bitmapInfo2;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public long getLastUpdatedTime(ShortcutInfo shortcutInfo, PackageInfo packageInfo) {
        if (shortcutInfo == null || !FeatureFlags.ENABLE_DEEP_SHORTCUT_ICON_CACHE.get()) {
            return packageInfo.lastUpdateTime;
        }
        return Math.max(shortcutInfo.getLastChangedTimestamp(), packageInfo.lastUpdateTime);
    }

    public static Drawable getIcon(Context context, ShortcutInfo shortcutInfo, int i) {
        try {
            return ((LauncherApps) context.getSystemService(LauncherApps.class)).getShortcutIconDrawable(shortcutInfo, i);
        } catch (IllegalStateException | SecurityException e) {
            Log.e(TAG, "Failed to get shortcut icon", e);
            return null;
        }
    }
}
