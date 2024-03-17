package com.android.launcher3.widget;

import com.android.launcher3.LauncherSettings;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.pm.ShortcutConfigActivityInfo;

public class PendingAddShortcutInfo extends PendingAddItemInfo {
    public ShortcutConfigActivityInfo activityInfo;

    public PendingAddShortcutInfo(ShortcutConfigActivityInfo shortcutConfigActivityInfo) {
        this.activityInfo = shortcutConfigActivityInfo;
        this.componentName = shortcutConfigActivityInfo.getComponent();
        this.user = shortcutConfigActivityInfo.getUser();
        this.itemType = shortcutConfigActivityInfo.getItemType();
        this.container = LauncherSettings.Favorites.CONTAINER_WIDGETS_TRAY;
    }
}
