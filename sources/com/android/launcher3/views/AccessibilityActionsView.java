package com.android.launcher3.views;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.views.OptionsPopupView;
import java.util.Iterator;

public class AccessibilityActionsView extends View implements StateManager.StateListener<LauncherState> {
    public AccessibilityActionsView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AccessibilityActionsView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AccessibilityActionsView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Launcher.getLauncher(context).getStateManager().addStateListener(this);
        setWillNotDraw(true);
    }

    public void onStateTransitionComplete(LauncherState launcherState) {
        setImportantForAccessibility(launcherState == LauncherState.NORMAL ? 1 : 2);
    }

    public AccessibilityNodeInfo createAccessibilityNodeInfo() {
        AccessibilityNodeInfo createAccessibilityNodeInfo = super.createAccessibilityNodeInfo();
        Launcher launcher = Launcher.getLauncher(getContext());
        createAccessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.string.all_apps_button_label, launcher.getText(R.string.all_apps_button_label)));
        Iterator<OptionsPopupView.OptionItem> it = OptionsPopupView.getOptions(launcher).iterator();
        while (it.hasNext()) {
            OptionsPopupView.OptionItem next = it.next();
            createAccessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(next.labelRes, next.label));
        }
        return createAccessibilityNodeInfo;
    }

    public boolean performAccessibilityAction(int i, Bundle bundle) {
        if (super.performAccessibilityAction(i, bundle)) {
            return true;
        }
        Launcher launcher = Launcher.getLauncher(getContext());
        if (i == R.string.all_apps_button_label) {
            launcher.getStateManager().goToState(LauncherState.ALL_APPS);
            return true;
        }
        Iterator<OptionsPopupView.OptionItem> it = OptionsPopupView.getOptions(launcher).iterator();
        while (it.hasNext()) {
            OptionsPopupView.OptionItem next = it.next();
            if (next.labelRes == i) {
                if (next.eventId.getId() > 0) {
                    launcher.getStatsLogManager().logger().log(next.eventId);
                }
                if (next.clickListener.onLongClick(this)) {
                    return true;
                }
            }
        }
        return false;
    }
}
