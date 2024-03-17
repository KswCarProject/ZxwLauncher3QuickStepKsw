package com.android.launcher3.dragndrop;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.os.Process;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.pm.PinRequestHelper;
import com.android.launcher3.pm.ShortcutConfigActivityInfo;

class PinShortcutRequestActivityInfo extends ShortcutConfigActivityInfo {
    private static final String STUB_COMPONENT_CLASS = "pinned-shortcut";
    private final Context mContext;
    private final ShortcutInfo mInfo;
    private final LauncherApps.PinItemRequest mRequest;

    public int getItemType() {
        return 6;
    }

    public boolean isPersistable() {
        return false;
    }

    public boolean startConfigActivity(Activity activity, int i) {
        return false;
    }

    public PinShortcutRequestActivityInfo(LauncherApps.PinItemRequest pinItemRequest, Context context) {
        super(new ComponentName(pinItemRequest.getShortcutInfo().getPackage(), STUB_COMPONENT_CLASS), pinItemRequest.getShortcutInfo().getUserHandle());
        this.mRequest = pinItemRequest;
        this.mInfo = pinItemRequest.getShortcutInfo();
        this.mContext = context;
    }

    public CharSequence getLabel(PackageManager packageManager) {
        return this.mInfo.getShortLabel();
    }

    public Drawable getFullResIcon(IconCache iconCache) {
        Drawable shortcutIconDrawable = ((LauncherApps) this.mContext.getSystemService(LauncherApps.class)).getShortcutIconDrawable(this.mInfo, LauncherAppState.getIDP(this.mContext).fillResIconDpi);
        return shortcutIconDrawable == null ? iconCache.getDefaultIcon(Process.myUserHandle()).newIcon(this.mContext) : shortcutIconDrawable;
    }

    public WorkspaceItemInfo createWorkspaceItemInfo() {
        return PinRequestHelper.createWorkspaceItemFromPinItemRequest(this.mContext, this.mRequest, (long) (this.mContext.getResources().getInteger(R.integer.config_dropAnimMaxDuration) + 500 + LauncherState.SPRING_LOADED.getTransitionDuration(Launcher.getLauncher(this.mContext), true)));
    }
}
