package com.android.quickstep.util;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.android.systemui.shared.animation.UnfoldMoveFromCenterAnimator;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseUnfoldMoveFromCenterAnimator implements UnfoldTransitionProgressProvider.TransitionProgressListener {
    private boolean mAnimationInProgress = false;
    private final UnfoldMoveFromCenterAnimator mMoveFromCenterAnimation;
    private final Map<ViewGroup, Boolean> mOriginalClipChildren = new HashMap();
    private final Map<ViewGroup, Boolean> mOriginalClipToPadding = new HashMap();

    /* access modifiers changed from: protected */
    public void onPrepareViewsForAnimation() {
    }

    public BaseUnfoldMoveFromCenterAnimator(WindowManager windowManager) {
        this.mMoveFromCenterAnimation = new UnfoldMoveFromCenterAnimator(windowManager, new LauncherViewsMoveFromCenterTranslationApplier());
    }

    public void onTransitionStarted() {
        this.mAnimationInProgress = true;
        this.mMoveFromCenterAnimation.updateDisplayProperties();
        onPrepareViewsForAnimation();
        onTransitionProgress(0.0f);
    }

    public void onTransitionProgress(float f) {
        this.mMoveFromCenterAnimation.onTransitionProgress(f);
    }

    public void onTransitionFinished() {
        this.mAnimationInProgress = false;
        this.mMoveFromCenterAnimation.onTransitionFinished();
        clearRegisteredViews();
    }

    public void updateRegisteredViewsIfNeeded() {
        if (this.mAnimationInProgress) {
            clearRegisteredViews();
            onPrepareViewsForAnimation();
        }
    }

    private void clearRegisteredViews() {
        this.mMoveFromCenterAnimation.clearRegisteredViews();
        this.mOriginalClipChildren.clear();
        this.mOriginalClipToPadding.clear();
    }

    /* access modifiers changed from: protected */
    public void registerViewForAnimation(View view) {
        this.mMoveFromCenterAnimation.registerViewForAnimation(view);
    }

    /* access modifiers changed from: protected */
    public void disableClipping(ViewGroup viewGroup) {
        this.mOriginalClipToPadding.put(viewGroup, Boolean.valueOf(viewGroup.getClipToPadding()));
        this.mOriginalClipChildren.put(viewGroup, Boolean.valueOf(viewGroup.getClipChildren()));
        viewGroup.setClipToPadding(false);
        viewGroup.setClipChildren(false);
    }

    /* access modifiers changed from: protected */
    public void restoreClipping(ViewGroup viewGroup) {
        Boolean bool = this.mOriginalClipToPadding.get(viewGroup);
        if (bool != null) {
            viewGroup.setClipToPadding(bool.booleanValue());
        }
        Boolean bool2 = this.mOriginalClipChildren.get(viewGroup);
        if (bool2 != null) {
            viewGroup.setClipChildren(bool2.booleanValue());
        }
    }
}
