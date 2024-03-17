package com.android.launcher3.taskbar;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.launcher3.taskbar.TaskbarControllers;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.SystemUiProxy;
import java.io.PrintWriter;

public class TaskbarScrimViewController implements TaskbarControllers.LoggableTaskbarController {
    private static final float SCRIM_ALPHA = 0.6f;
    private static final Interpolator SCRIM_ALPHA_IN = new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
    private static final Interpolator SCRIM_ALPHA_OUT = new PathInterpolator(0.0f, 0.0f, 0.8f, 1.0f);
    private final TaskbarActivityContext mActivity;
    private TaskbarControllers mControllers;
    private final AnimatedFloat mScrimAlpha = new AnimatedFloat(new Runnable() {
        public final void run() {
            TaskbarScrimViewController.this.updateScrimAlpha();
        }
    });
    private final TaskbarScrimView mScrimView;

    public TaskbarScrimViewController(TaskbarActivityContext taskbarActivityContext, TaskbarScrimView taskbarScrimView) {
        this.mActivity = taskbarActivityContext;
        this.mScrimView = taskbarScrimView;
    }

    public void init(TaskbarControllers taskbarControllers) {
        this.mControllers = taskbarControllers;
    }

    public void updateStateForSysuiFlags(int i, boolean z) {
        boolean z2 = true;
        boolean z3 = (i & 16384) != 0;
        boolean z4 = (i & 8388608) != 0;
        if (this.mControllers.navbarButtonsViewController.isImeVisible() || !z3 || !this.mControllers.taskbarStashController.isInAppAndNotStashed()) {
            z2 = false;
        }
        showScrim(z2, z4 ? 0.84000003f : z2 ? 0.6f : 0.0f, z);
    }

    private void showScrim(boolean z, float f, boolean z2) {
        this.mScrimView.setOnClickListener(z ? new View.OnClickListener() {
            public final void onClick(View view) {
                TaskbarScrimViewController.this.lambda$showScrim$0$TaskbarScrimViewController(view);
            }
        } : null);
        this.mScrimView.setClickable(z);
        AnimatedFloat animatedFloat = this.mScrimAlpha;
        if (!z) {
            f = 0.0f;
        }
        ObjectAnimator animateToValue = animatedFloat.animateToValue(f);
        animateToValue.setInterpolator(z ? SCRIM_ALPHA_IN : SCRIM_ALPHA_OUT);
        animateToValue.start();
        if (z2) {
            animateToValue.end();
        }
    }

    public /* synthetic */ void lambda$showScrim$0$TaskbarScrimViewController(View view) {
        onClick();
    }

    /* access modifiers changed from: private */
    public void updateScrimAlpha() {
        this.mScrimView.setScrimAlpha(this.mScrimAlpha.value);
    }

    private void onClick() {
        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mActivity).onBackPressed();
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "TaskbarScrimViewController:");
        printWriter.println(String.format("%s\tmScrimAlpha.value=%.2f", new Object[]{str, Float.valueOf(this.mScrimAlpha.value)}));
    }
}
