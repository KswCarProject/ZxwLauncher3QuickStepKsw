package com.android.launcher3.util;

import com.android.launcher3.Utilities;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.shortcuts.ShortcutKey;

public class ShortcutUtil {
    public static boolean supportsShortcuts(ItemInfo itemInfo) {
        return isActive(itemInfo) && (isApp(itemInfo) || isPinnedShortcut(itemInfo));
    }

    public static boolean supportsDeepShortcuts(ItemInfo itemInfo) {
        return isActive(itemInfo) && isApp(itemInfo);
    }

    public static String getShortcutIdIfPinnedShortcut(ItemInfo itemInfo) {
        if (!isActive(itemInfo) || !isPinnedShortcut(itemInfo)) {
            return null;
        }
        return ShortcutKey.fromItemInfo(itemInfo).getId();
    }

    public static String[] getPersonKeysIfPinnedShortcut(ItemInfo itemInfo) {
        return (!isActive(itemInfo) || !isPinnedShortcut(itemInfo)) ? Utilities.EMPTY_STRING_ARRAY : ((WorkspaceItemInfo) itemInfo).getPersonKeys();
    }

    public static boolean isDeepShortcut(ItemInfo itemInfo) {
        return itemInfo.itemType == 6 && (itemInfo instanceof WorkspaceItemInfo);
    }

    private static boolean isActive(ItemInfo itemInfo) {
        if (((itemInfo instanceof WorkspaceItemInfo) && ((WorkspaceItemInfo) itemInfo).hasPromiseIconUi()) || itemInfo.isDisabled()) {
            return false;
        }
        return true;
    }

    private static boolean isApp(ItemInfo itemInfo) {
        return itemInfo.itemType == 0;
    }

    private static boolean isPinnedShortcut(ItemInfo itemInfo) {
        return itemInfo.itemType == 6 && itemInfo.container != -1 && (itemInfo instanceof WorkspaceItemInfo);
    }
}
