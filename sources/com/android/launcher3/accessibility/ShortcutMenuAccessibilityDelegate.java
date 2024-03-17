package com.android.launcher3.accessibility;

import android.view.View;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.accessibility.BaseAccessibilityDelegate;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.notification.NotificationMainView;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.android.launcher3.views.ActivityContext;
import java.util.Collections;
import java.util.List;

public class ShortcutMenuAccessibilityDelegate extends LauncherAccessibilityDelegate {
    private static final int DISMISS_NOTIFICATION = 2131296320;

    public ShortcutMenuAccessibilityDelegate(Launcher launcher) {
        super(launcher);
        this.mActions.put(R.id.action_dismiss_notification, new BaseAccessibilityDelegate.LauncherAction(R.id.action_dismiss_notification, R.string.action_dismiss_notification, 52));
    }

    /* access modifiers changed from: protected */
    public void getSupportedActions(View view, ItemInfo itemInfo, List<BaseAccessibilityDelegate<Launcher>.LauncherAction> list) {
        if (view.getParent() instanceof DeepShortcutView) {
            list.add((BaseAccessibilityDelegate.LauncherAction) this.mActions.get(R.id.action_add_to_workspace));
        } else if ((view instanceof NotificationMainView) && ((NotificationMainView) view).canChildBeDismissed()) {
            list.add((BaseAccessibilityDelegate.LauncherAction) this.mActions.get(R.id.action_dismiss_notification));
        }
    }

    /* access modifiers changed from: protected */
    public boolean performAction(View view, ItemInfo itemInfo, int i, boolean z) {
        if (i == R.id.action_add_to_workspace) {
            if (!(view.getParent() instanceof DeepShortcutView)) {
                return false;
            }
            WorkspaceItemInfo finalInfo = ((DeepShortcutView) view.getParent()).getFinalInfo();
            int[] iArr = new int[2];
            int findSpaceOnWorkspace = findSpaceOnWorkspace(itemInfo, iArr);
            if (findSpaceOnWorkspace == -1) {
                return false;
            }
            ((Launcher) this.mContext).getStateManager().goToState(LauncherState.NORMAL, true, AnimatorListeners.forSuccessCallback(new Runnable(finalInfo, findSpaceOnWorkspace, iArr) {
                public final /* synthetic */ WorkspaceItemInfo f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ int[] f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    ShortcutMenuAccessibilityDelegate.this.lambda$performAction$0$ShortcutMenuAccessibilityDelegate(this.f$1, this.f$2, this.f$3);
                }
            }));
            return true;
        } else if (i != R.id.action_dismiss_notification || !(view instanceof NotificationMainView)) {
            return false;
        } else {
            ((NotificationMainView) view).onChildDismissed();
            announceConfirmation(R.string.notification_dismissed);
            return true;
        }
    }

    public /* synthetic */ void lambda$performAction$0$ShortcutMenuAccessibilityDelegate(WorkspaceItemInfo workspaceItemInfo, int i, int[] iArr) {
        ((Launcher) this.mContext).getModelWriter().addItemToDatabase(workspaceItemInfo, -100, i, iArr[0], iArr[1]);
        ((Launcher) this.mContext).bindItems(Collections.singletonList(workspaceItemInfo), true);
        AbstractFloatingView.closeAllOpenViews((ActivityContext) this.mContext);
        announceConfirmation(R.string.item_added_to_workspace);
    }
}
