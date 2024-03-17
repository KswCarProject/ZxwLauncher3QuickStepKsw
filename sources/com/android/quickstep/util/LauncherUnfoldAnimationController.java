package com.android.quickstep.util;

import android.util.FloatProperty;
import android.util.MathUtils;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import androidx.core.view.OneShotPreDrawListener;
import com.android.launcher3.Hotseat;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.util.HorizontalInsettableView;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import com.android.systemui.unfold.util.NaturalRotationUnfoldProgressProvider;
import com.android.systemui.unfold.util.ScopedUnfoldTransitionProgressProvider;

public class LauncherUnfoldAnimationController {
    /* access modifiers changed from: private */
    public static final FloatProperty<Hotseat> HOTSEAT_SCALE_PROPERTY = LauncherAnimUtils.HOTSEAT_SCALE_PROPERTY_FACTORY.get(1);
    private static final float MAX_WIDTH_INSET_FRACTION = 0.15f;
    /* access modifiers changed from: private */
    public static final FloatProperty<Workspace<?>> WORKSPACE_SCALE_PROPERTY = LauncherAnimUtils.WORKSPACE_SCALE_PROPERTY_FACTORY.get(1);
    /* access modifiers changed from: private */
    public final Launcher mLauncher;
    private final NaturalRotationUnfoldProgressProvider mNaturalOrientationProgressProvider;
    private final ScopedUnfoldTransitionProgressProvider mProgressProvider;
    /* access modifiers changed from: private */
    public HorizontalInsettableView mQsbInsettable;
    private final UnfoldMoveFromCenterHotseatAnimator mUnfoldMoveFromCenterHotseatAnimator;
    private final UnfoldMoveFromCenterWorkspaceAnimator mUnfoldMoveFromCenterWorkspaceAnimator;

    public LauncherUnfoldAnimationController(Launcher launcher, WindowManager windowManager, UnfoldTransitionProgressProvider unfoldTransitionProgressProvider) {
        this.mLauncher = launcher;
        ScopedUnfoldTransitionProgressProvider scopedUnfoldTransitionProgressProvider = new ScopedUnfoldTransitionProgressProvider(unfoldTransitionProgressProvider);
        this.mProgressProvider = scopedUnfoldTransitionProgressProvider;
        UnfoldMoveFromCenterHotseatAnimator unfoldMoveFromCenterHotseatAnimator = new UnfoldMoveFromCenterHotseatAnimator(launcher, windowManager);
        this.mUnfoldMoveFromCenterHotseatAnimator = unfoldMoveFromCenterHotseatAnimator;
        UnfoldMoveFromCenterWorkspaceAnimator unfoldMoveFromCenterWorkspaceAnimator = new UnfoldMoveFromCenterWorkspaceAnimator(launcher, windowManager);
        this.mUnfoldMoveFromCenterWorkspaceAnimator = unfoldMoveFromCenterWorkspaceAnimator;
        NaturalRotationUnfoldProgressProvider naturalRotationUnfoldProgressProvider = new NaturalRotationUnfoldProgressProvider(launcher, WindowManagerGlobal.getWindowManagerService(), scopedUnfoldTransitionProgressProvider);
        this.mNaturalOrientationProgressProvider = naturalRotationUnfoldProgressProvider;
        naturalRotationUnfoldProgressProvider.init();
        scopedUnfoldTransitionProgressProvider.addCallback((UnfoldTransitionProgressProvider.TransitionProgressListener) unfoldMoveFromCenterWorkspaceAnimator);
        scopedUnfoldTransitionProgressProvider.addCallback((UnfoldTransitionProgressProvider.TransitionProgressListener) new LauncherScaleAnimationListener());
        naturalRotationUnfoldProgressProvider.addCallback((UnfoldTransitionProgressProvider.TransitionProgressListener) new QsbAnimationListener());
        naturalRotationUnfoldProgressProvider.addCallback((UnfoldTransitionProgressProvider.TransitionProgressListener) unfoldMoveFromCenterHotseatAnimator);
    }

    public void onResume() {
        Hotseat hotseat = this.mLauncher.getHotseat();
        if (hotseat != null && (hotseat.getQsb() instanceof HorizontalInsettableView)) {
            this.mQsbInsettable = (HorizontalInsettableView) hotseat.getQsb();
        }
        OneShotPreDrawListener.add(this.mLauncher.getWorkspace(), new Runnable() {
            public final void run() {
                LauncherUnfoldAnimationController.this.lambda$onResume$0$LauncherUnfoldAnimationController();
            }
        });
    }

    public /* synthetic */ void lambda$onResume$0$LauncherUnfoldAnimationController() {
        this.mProgressProvider.setReadyToHandleTransition(true);
    }

    public void onPause() {
        this.mProgressProvider.setReadyToHandleTransition(false);
        this.mQsbInsettable = null;
    }

    public void onDestroy() {
        this.mProgressProvider.destroy();
        this.mNaturalOrientationProgressProvider.destroy();
    }

    public void updateRegisteredViewsIfNeeded() {
        this.mUnfoldMoveFromCenterHotseatAnimator.updateRegisteredViewsIfNeeded();
        this.mUnfoldMoveFromCenterWorkspaceAnimator.updateRegisteredViewsIfNeeded();
    }

    private class QsbAnimationListener implements UnfoldTransitionProgressProvider.TransitionProgressListener {
        public void onTransitionStarted() {
        }

        private QsbAnimationListener() {
        }

        public void onTransitionFinished() {
            if (LauncherUnfoldAnimationController.this.mQsbInsettable != null) {
                LauncherUnfoldAnimationController.this.mQsbInsettable.setHorizontalInsets(0.0f);
            }
        }

        public void onTransitionProgress(float f) {
            if (LauncherUnfoldAnimationController.this.mQsbInsettable != null) {
                LauncherUnfoldAnimationController.this.mQsbInsettable.setHorizontalInsets(Utilities.comp(f) * LauncherUnfoldAnimationController.MAX_WIDTH_INSET_FRACTION);
            }
        }
    }

    private class LauncherScaleAnimationListener implements UnfoldTransitionProgressProvider.TransitionProgressListener {
        private LauncherScaleAnimationListener() {
        }

        public void onTransitionStarted() {
            LauncherUnfoldAnimationController.this.mLauncher.getWorkspace().setPivotToScaleWithSelf(LauncherUnfoldAnimationController.this.mLauncher.getHotseat());
        }

        public void onTransitionFinished() {
            setScale(1.0f);
        }

        public void onTransitionProgress(float f) {
            setScale(MathUtils.constrainedMap(0.85f, 1.0f, 0.0f, 1.0f, f));
        }

        private void setScale(float f) {
            LauncherUnfoldAnimationController.WORKSPACE_SCALE_PROPERTY.setValue(LauncherUnfoldAnimationController.this.mLauncher.getWorkspace(), f);
            LauncherUnfoldAnimationController.HOTSEAT_SCALE_PROPERTY.setValue(LauncherUnfoldAnimationController.this.mLauncher.getHotseat(), f);
        }
    }
}
