package com.android.quickstep.util;

import android.view.WindowManager;
import com.android.launcher3.Hotseat;
import com.android.launcher3.Launcher;
import com.android.launcher3.ShortcutAndWidgetContainer;

public class UnfoldMoveFromCenterHotseatAnimator extends BaseUnfoldMoveFromCenterAnimator {
    private final Launcher mLauncher;

    public UnfoldMoveFromCenterHotseatAnimator(Launcher launcher, WindowManager windowManager) {
        super(windowManager);
        this.mLauncher = launcher;
    }

    /* access modifiers changed from: protected */
    public void onPrepareViewsForAnimation() {
        Hotseat hotseat = this.mLauncher.getHotseat();
        ShortcutAndWidgetContainer shortcutsAndWidgets = hotseat.getShortcutsAndWidgets();
        disableClipping(hotseat);
        for (int i = 0; i < shortcutsAndWidgets.getChildCount(); i++) {
            registerViewForAnimation(shortcutsAndWidgets.getChildAt(i));
        }
    }

    public void onTransitionFinished() {
        restoreClipping(this.mLauncher.getHotseat());
        super.onTransitionFinished();
    }
}
