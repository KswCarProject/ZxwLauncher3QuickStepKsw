package com.android.launcher3.taskbar;

import android.content.Intent;
import android.content.pm.LauncherApps;
import android.os.Bundle;
import android.view.View;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.R;
import com.android.launcher3.accessibility.BaseAccessibilityDelegate;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.util.ShortcutUtil;
import com.android.quickstep.SystemUiProxy;
import java.util.List;

public class TaskbarShortcutMenuAccessibilityDelegate extends BaseAccessibilityDelegate<TaskbarActivityContext> {
    public static final int MOVE_TO_BOTTOM_OR_RIGHT = 2131296332;
    public static final int MOVE_TO_TOP_OR_LEFT = 2131296333;
    private final LauncherApps mLauncherApps;

    /* access modifiers changed from: protected */
    public boolean beginAccessibleDrag(View view, ItemInfo itemInfo, boolean z) {
        return false;
    }

    public TaskbarShortcutMenuAccessibilityDelegate(TaskbarActivityContext taskbarActivityContext) {
        super(taskbarActivityContext);
        this.mLauncherApps = (LauncherApps) taskbarActivityContext.getSystemService(LauncherApps.class);
        this.mActions.put(R.id.action_deep_shortcuts, new BaseAccessibilityDelegate.LauncherAction(R.id.action_deep_shortcuts, R.string.action_deep_shortcut, 47));
        this.mActions.put(R.id.action_shortcuts_and_notifications, new BaseAccessibilityDelegate.LauncherAction(R.id.action_deep_shortcuts, R.string.shortcuts_menu_with_notifications_description, 47));
        this.mActions.put(R.id.action_move_to_top_or_left, new BaseAccessibilityDelegate.LauncherAction(R.id.action_move_to_top_or_left, R.string.move_drop_target_top_or_left, 40));
        this.mActions.put(R.id.action_move_to_bottom_or_right, new BaseAccessibilityDelegate.LauncherAction(R.id.action_move_to_bottom_or_right, R.string.move_drop_target_bottom_or_right, 46));
    }

    /* access modifiers changed from: protected */
    public void getSupportedActions(View view, ItemInfo itemInfo, List<BaseAccessibilityDelegate<TaskbarActivityContext>.LauncherAction> list) {
        if (ShortcutUtil.supportsShortcuts(itemInfo) && FeatureFlags.ENABLE_TASKBAR_POPUP_MENU.get()) {
            list.add((BaseAccessibilityDelegate.LauncherAction) this.mActions.get(NotificationListener.getInstanceIfConnected() != null ? R.id.action_shortcuts_and_notifications : R.id.action_deep_shortcuts));
        }
        list.add((BaseAccessibilityDelegate.LauncherAction) this.mActions.get(R.id.action_move_to_top_or_left));
        list.add((BaseAccessibilityDelegate.LauncherAction) this.mActions.get(R.id.action_move_to_bottom_or_right));
    }

    /* access modifiers changed from: protected */
    public boolean performAction(View view, ItemInfo itemInfo, int i, boolean z) {
        if ((itemInfo instanceof WorkspaceItemInfo) && (i == R.id.action_move_to_top_or_left || i == R.id.action_move_to_bottom_or_right)) {
            WorkspaceItemInfo workspaceItemInfo = (WorkspaceItemInfo) itemInfo;
            int i2 = i == R.id.action_move_to_top_or_left ? 0 : 1;
            if (workspaceItemInfo.itemType == 6) {
                SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).startShortcut(workspaceItemInfo.getIntent().getPackage(), workspaceItemInfo.getDeepShortcutId(), i2, (Bundle) null, workspaceItemInfo.user);
            } else {
                SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).startIntent(this.mLauncherApps.getMainActivityLaunchIntent(itemInfo.getIntent().getComponent(), (Bundle) null, itemInfo.user), new Intent(), i2, (Bundle) null);
            }
            return true;
        } else if (i != R.id.action_deep_shortcuts && i != R.id.action_shortcuts_and_notifications) {
            return false;
        } else {
            ((TaskbarActivityContext) this.mContext).showPopupMenuForIcon((BubbleTextView) view);
            return true;
        }
    }
}
