package com.android.quickstep.util;

import android.view.View;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.widget.NavigableAppWidgetHostView;
import com.android.systemui.shared.animation.UnfoldMoveFromCenterAnimator;

public class LauncherViewsMoveFromCenterTranslationApplier implements UnfoldMoveFromCenterAnimator.TranslationApplier {
    public void apply(View view, float f, float f2) {
        if (view instanceof NavigableAppWidgetHostView) {
            ((NavigableAppWidgetHostView) view).setTranslationForMoveFromCenterAnimation(f, f2);
        } else if (view instanceof BubbleTextView) {
            ((BubbleTextView) view).setTranslationForMoveFromCenterAnimation(f, f2);
        } else if (view instanceof FolderIcon) {
            ((FolderIcon) view).setTranslationForMoveFromCenterAnimation(f, f2);
        } else {
            view.setTranslationX(f);
            view.setTranslationY(f2);
        }
    }
}
