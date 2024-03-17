package com.android.quickstep.util;

import android.view.View;
import android.view.WindowManager;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Workspace;
import java.util.function.Consumer;

public class UnfoldMoveFromCenterWorkspaceAnimator extends BaseUnfoldMoveFromCenterAnimator {
    private final Launcher mLauncher;

    public UnfoldMoveFromCenterWorkspaceAnimator(Launcher launcher, WindowManager windowManager) {
        super(windowManager);
        this.mLauncher = launcher;
    }

    /* access modifiers changed from: protected */
    public void onPrepareViewsForAnimation() {
        Workspace<?> workspace = this.mLauncher.getWorkspace();
        workspace.forEachVisiblePage(new Consumer() {
            public final void accept(Object obj) {
                UnfoldMoveFromCenterWorkspaceAnimator.this.lambda$onPrepareViewsForAnimation$0$UnfoldMoveFromCenterWorkspaceAnimator((View) obj);
            }
        });
        disableClipping(workspace);
    }

    public /* synthetic */ void lambda$onPrepareViewsForAnimation$0$UnfoldMoveFromCenterWorkspaceAnimator(View view) {
        CellLayout cellLayout = (CellLayout) view;
        ShortcutAndWidgetContainer shortcutsAndWidgets = cellLayout.getShortcutsAndWidgets();
        disableClipping(cellLayout);
        for (int i = 0; i < shortcutsAndWidgets.getChildCount(); i++) {
            registerViewForAnimation(shortcutsAndWidgets.getChildAt(i));
        }
    }

    public void onTransitionFinished() {
        restoreClipping(this.mLauncher.getWorkspace());
        this.mLauncher.getWorkspace().forEachVisiblePage(new Consumer() {
            public final void accept(Object obj) {
                UnfoldMoveFromCenterWorkspaceAnimator.this.lambda$onTransitionFinished$1$UnfoldMoveFromCenterWorkspaceAnimator((View) obj);
            }
        });
        super.onTransitionFinished();
    }

    public /* synthetic */ void lambda$onTransitionFinished$1$UnfoldMoveFromCenterWorkspaceAnimator(View view) {
        restoreClipping((CellLayout) view);
    }
}
