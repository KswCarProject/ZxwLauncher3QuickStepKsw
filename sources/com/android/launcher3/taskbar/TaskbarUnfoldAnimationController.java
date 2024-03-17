package com.android.launcher3.taskbar;

import android.view.IWindowManager;
import android.view.View;
import android.view.WindowManager;
import com.android.launcher3.taskbar.TaskbarControllers;
import com.android.quickstep.util.LauncherViewsMoveFromCenterTranslationApplier;
import com.android.systemui.shared.animation.UnfoldMoveFromCenterAnimator;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import com.android.systemui.unfold.util.NaturalRotationUnfoldProgressProvider;
import com.android.systemui.unfold.util.ScopedUnfoldTransitionProgressProvider;
import java.io.PrintWriter;

public class TaskbarUnfoldAnimationController implements TaskbarControllers.LoggableTaskbarController {
    /* access modifiers changed from: private */
    public final UnfoldMoveFromCenterAnimator mMoveFromCenterAnimator;
    private final NaturalRotationUnfoldProgressProvider mNaturalUnfoldTransitionProgressProvider;
    private final ScopedUnfoldTransitionProgressProvider mScopedUnfoldTransitionProgressProvider;
    /* access modifiers changed from: private */
    public TaskbarViewController mTaskbarViewController;
    private final TransitionListener mTransitionListener = new TransitionListener();

    public TaskbarUnfoldAnimationController(BaseTaskbarContext baseTaskbarContext, ScopedUnfoldTransitionProgressProvider scopedUnfoldTransitionProgressProvider, WindowManager windowManager, IWindowManager iWindowManager) {
        this.mScopedUnfoldTransitionProgressProvider = scopedUnfoldTransitionProgressProvider;
        this.mNaturalUnfoldTransitionProgressProvider = new NaturalRotationUnfoldProgressProvider(baseTaskbarContext, iWindowManager, scopedUnfoldTransitionProgressProvider);
        this.mMoveFromCenterAnimator = new UnfoldMoveFromCenterAnimator(windowManager, new LauncherViewsMoveFromCenterTranslationApplier());
    }

    public void init(TaskbarControllers taskbarControllers) {
        this.mNaturalUnfoldTransitionProgressProvider.init();
        TaskbarViewController taskbarViewController = taskbarControllers.taskbarViewController;
        this.mTaskbarViewController = taskbarViewController;
        taskbarViewController.addOneTimePreDrawListener(new Runnable() {
            public final void run() {
                TaskbarUnfoldAnimationController.this.lambda$init$0$TaskbarUnfoldAnimationController();
            }
        });
        this.mNaturalUnfoldTransitionProgressProvider.addCallback((UnfoldTransitionProgressProvider.TransitionProgressListener) this.mTransitionListener);
    }

    public /* synthetic */ void lambda$init$0$TaskbarUnfoldAnimationController() {
        this.mScopedUnfoldTransitionProgressProvider.setReadyToHandleTransition(true);
    }

    public void onDestroy() {
        this.mScopedUnfoldTransitionProgressProvider.setReadyToHandleTransition(false);
        this.mNaturalUnfoldTransitionProgressProvider.removeCallback((UnfoldTransitionProgressProvider.TransitionProgressListener) this.mTransitionListener);
        this.mNaturalUnfoldTransitionProgressProvider.destroy();
        this.mTaskbarViewController = null;
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "TaskbarUnfoldAnimationController:");
    }

    private class TransitionListener implements UnfoldTransitionProgressProvider.TransitionProgressListener {
        private TransitionListener() {
        }

        public void onTransitionStarted() {
            TaskbarUnfoldAnimationController.this.mMoveFromCenterAnimator.updateDisplayProperties();
            for (View registerViewForAnimation : TaskbarUnfoldAnimationController.this.mTaskbarViewController.getIconViews()) {
                TaskbarUnfoldAnimationController.this.mMoveFromCenterAnimator.registerViewForAnimation(registerViewForAnimation);
            }
            TaskbarUnfoldAnimationController.this.mMoveFromCenterAnimator.onTransitionStarted();
        }

        public void onTransitionFinished() {
            TaskbarUnfoldAnimationController.this.mMoveFromCenterAnimator.onTransitionFinished();
            TaskbarUnfoldAnimationController.this.mMoveFromCenterAnimator.clearRegisteredViews();
        }

        public void onTransitionProgress(float f) {
            TaskbarUnfoldAnimationController.this.mMoveFromCenterAnimator.onTransitionProgress(f);
        }
    }
}
