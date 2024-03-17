package com.android.launcher3.shortcuts;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.os.UserHandle;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.ComponentKey;

public class ShortcutKey extends ComponentKey {
    public static final String EXTRA_SHORTCUT_ID = "shortcut_id";
    private static final String INTENT_CATEGORY = "com.android.launcher3.DEEP_SHORTCUT";

    public ShortcutKey(String str, UserHandle userHandle, String str2) {
        super(new ComponentName(str, str2), userHandle);
    }

    public ShortcutKey(ComponentName componentName, UserHandle userHandle) {
        super(componentName, userHandle);
    }

    public String getId() {
        return this.componentName.getClassName();
    }

    public String getPackageName() {
        return this.componentName.getPackageName();
    }

    public ShortcutRequest buildRequest(Context context) {
        return new ShortcutRequest(context, this.user).forPackage(this.componentName.getPackageName(), getId());
    }

    public static ShortcutKey fromInfo(ShortcutInfo shortcutInfo) {
        return new ShortcutKey(shortcutInfo.getPackage(), shortcutInfo.getUserHandle(), shortcutInfo.getId());
    }

    public static ShortcutKey fromIntent(Intent intent, UserHandle userHandle) {
        return new ShortcutKey(intent.getPackage(), userHandle, intent.getStringExtra("shortcut_id"));
    }

    public static ShortcutKey fromItemInfo(ItemInfo itemInfo) {
        return fromIntent(itemInfo.getIntent(), itemInfo.user);
    }

    public static Intent makeIntent(ShortcutInfo shortcutInfo) {
        return makeIntent(shortcutInfo.getId(), shortcutInfo.getPackage()).setComponent(shortcutInfo.getActivity());
    }

    public static Intent makeIntent(String str, String str2) {
        return new Intent("android.intent.action.MAIN").addCategory(INTENT_CATEGORY).setPackage(str2).setFlags(270532608).putExtra("shortcut_id", str);
    }
}
