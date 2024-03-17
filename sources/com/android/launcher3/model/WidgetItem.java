package com.android.launcher3.model;

import android.content.pm.PackageManager;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Utilities;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.pm.ShortcutConfigActivityInfo;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.widget.LauncherAppWidgetProviderInfo;

public class WidgetItem extends ComponentKey {
    public final ShortcutConfigActivityInfo activityInfo;
    public final String label;
    public final int spanX;
    public final int spanY;
    public final LauncherAppWidgetProviderInfo widgetInfo;

    public WidgetItem(LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo, InvariantDeviceProfile invariantDeviceProfile, IconCache iconCache) {
        super(launcherAppWidgetProviderInfo.provider, launcherAppWidgetProviderInfo.getProfile());
        this.label = iconCache.getTitleNoCache(launcherAppWidgetProviderInfo);
        this.widgetInfo = launcherAppWidgetProviderInfo;
        this.activityInfo = null;
        this.spanX = Math.min(launcherAppWidgetProviderInfo.spanX, invariantDeviceProfile.numColumns);
        this.spanY = Math.min(launcherAppWidgetProviderInfo.spanY, invariantDeviceProfile.numRows);
    }

    public WidgetItem(ShortcutConfigActivityInfo shortcutConfigActivityInfo, IconCache iconCache, PackageManager packageManager) {
        super(shortcutConfigActivityInfo.getComponent(), shortcutConfigActivityInfo.getUser());
        String str;
        if (shortcutConfigActivityInfo.isPersistable()) {
            str = iconCache.getTitleNoCache(shortcutConfigActivityInfo);
        } else {
            str = Utilities.trim(shortcutConfigActivityInfo.getLabel(packageManager));
        }
        this.label = str;
        this.widgetInfo = null;
        this.activityInfo = shortcutConfigActivityInfo;
        this.spanY = 1;
        this.spanX = 1;
    }

    public boolean hasSameType(WidgetItem widgetItem) {
        if (this.widgetInfo != null && widgetItem.widgetInfo != null) {
            return true;
        }
        if (this.activityInfo == null || widgetItem.activityInfo == null) {
            return false;
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r1.widgetInfo;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean hasPreviewLayout() {
        /*
            r1 = this;
            boolean r0 = com.android.launcher3.Utilities.ATLEAST_S
            if (r0 == 0) goto L_0x000e
            com.android.launcher3.widget.LauncherAppWidgetProviderInfo r0 = r1.widgetInfo
            if (r0 == 0) goto L_0x000e
            int r0 = r0.previewLayout
            if (r0 == 0) goto L_0x000e
            r0 = 1
            goto L_0x000f
        L_0x000e:
            r0 = 0
        L_0x000f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.WidgetItem.hasPreviewLayout():boolean");
    }

    public boolean isShortcut() {
        return this.activityInfo != null;
    }
}
