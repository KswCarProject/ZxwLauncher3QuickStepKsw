package com.android.launcher3.model.data;

import android.app.Person;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.text.TextUtils;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.icons.cache.BaseIconCache;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.uioverrides.ApiWrapper;
import com.android.launcher3.util.ContentWriter;
import java.util.Arrays;

public class WorkspaceItemInfo extends ItemInfoWithIcon {
    public static final int DEFAULT = 0;
    public static final int FLAG_AUTOINSTALL_ICON = 2;
    public static final int FLAG_RESTORED_ICON = 1;
    public static final int FLAG_RESTORE_STARTED = 4;
    public static final int FLAG_START_FOR_RESULT = 16;
    public static final int FLAG_SUPPORTS_WEB_UI = 8;
    public CharSequence disabledMessage;
    public Intent.ShortcutIconResource iconResource;
    public Intent intent;
    public int options;
    private String[] personKeys = Utilities.EMPTY_STRING_ARRAY;
    public int status;

    public WorkspaceItemInfo() {
        this.itemType = 1;
    }

    public WorkspaceItemInfo(WorkspaceItemInfo workspaceItemInfo) {
        super(workspaceItemInfo);
        this.title = workspaceItemInfo.title;
        this.intent = new Intent(workspaceItemInfo.intent);
        this.iconResource = workspaceItemInfo.iconResource;
        this.status = workspaceItemInfo.status;
        this.personKeys = (String[]) workspaceItemInfo.personKeys.clone();
    }

    public WorkspaceItemInfo(AppInfo appInfo) {
        super(appInfo);
        this.title = Utilities.trim(appInfo.title);
        this.intent = new Intent(appInfo.getIntent());
    }

    public WorkspaceItemInfo(ShortcutInfo shortcutInfo, Context context) {
        this.user = shortcutInfo.getUserHandle();
        this.itemType = 6;
        updateFromDeepShortcutInfo(shortcutInfo, context);
    }

    public void onAddToDatabase(ContentWriter contentWriter) {
        super.onAddToDatabase(contentWriter);
        contentWriter.put(LauncherSettings.Favorites.TITLE, this.title).put(LauncherSettings.Favorites.INTENT, getIntent()).put(LauncherSettings.Favorites.OPTIONS, Integer.valueOf(this.options)).put(LauncherSettings.Favorites.RESTORED, Integer.valueOf(this.status));
        if (!usingLowResIcon()) {
            contentWriter.putIcon(this.bitmap, this.user);
        }
        Intent.ShortcutIconResource shortcutIconResource = this.iconResource;
        if (shortcutIconResource != null) {
            contentWriter.put(LauncherSettings.Favorites.ICON_PACKAGE, shortcutIconResource.packageName).put(LauncherSettings.Favorites.ICON_RESOURCE, this.iconResource.resourceName);
        }
    }

    public Intent getIntent() {
        return this.intent;
    }

    public boolean hasStatusFlag(int i) {
        return (i & this.status) != 0;
    }

    public final boolean isPromise() {
        return hasStatusFlag(3);
    }

    public boolean hasPromiseIconUi() {
        return isPromise() && !hasStatusFlag(8);
    }

    public void updateFromDeepShortcutInfo(ShortcutInfo shortcutInfo, Context context) {
        String[] strArr;
        this.intent = ShortcutKey.makeIntent(shortcutInfo);
        this.title = shortcutInfo.getShortLabel();
        CharSequence longLabel = shortcutInfo.getLongLabel();
        if (TextUtils.isEmpty(longLabel)) {
            longLabel = shortcutInfo.getShortLabel();
        }
        this.contentDescription = context.getPackageManager().getUserBadgedLabel(longLabel, this.user);
        if (shortcutInfo.isEnabled()) {
            this.runtimeStatusFlags &= -17;
        } else {
            this.runtimeStatusFlags |= 16;
        }
        this.disabledMessage = shortcutInfo.getDisabledMessage();
        if (!Utilities.ATLEAST_P || shortcutInfo.getDisabledReason() != 100) {
            this.runtimeStatusFlags &= -4097;
        } else {
            this.runtimeStatusFlags |= 4096;
        }
        Person[] persons = ApiWrapper.getPersons(shortcutInfo);
        if (persons.length == 0) {
            strArr = Utilities.EMPTY_STRING_ARRAY;
        } else {
            strArr = (String[]) Arrays.stream(persons).map($$Lambda$WorkspaceItemInfo$NLQcSJEFKBLqD04iK_mFvrZ6l0.INSTANCE).sorted().toArray($$Lambda$WorkspaceItemInfo$eh4QKIvhsXJbkoPvNfKYpNPADI.INSTANCE);
        }
        this.personKeys = strArr;
    }

    static /* synthetic */ String[] lambda$updateFromDeepShortcutInfo$0(int i) {
        return new String[i];
    }

    public boolean isDisabledVersionLower() {
        return (this.runtimeStatusFlags & 4096) != 0;
    }

    public String getDeepShortcutId() {
        if (this.itemType == 6) {
            return getIntent().getStringExtra("shortcut_id");
        }
        return null;
    }

    public String[] getPersonKeys() {
        return this.personKeys;
    }

    public ComponentName getTargetComponent() {
        ComponentName targetComponent = super.getTargetComponent();
        if (targetComponent != null) {
            return targetComponent;
        }
        if (this.itemType != 1 && !hasStatusFlag(11)) {
            return targetComponent;
        }
        String str = this.intent.getPackage();
        if (str == null) {
            return null;
        }
        return new ComponentName(str, BaseIconCache.EMPTY_CLASS_NAME);
    }

    public WorkspaceItemInfo clone() {
        return new WorkspaceItemInfo(this);
    }
}
