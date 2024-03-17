package com.android.systemui.animation;

import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.testing.TestProtocol;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.animation.LaunchAnimator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u00009\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0010\u0007\n\u0002\b\u0003*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u0001J\b\u0010\u0011\u001a\u00020\u0012H\u0002J\b\u0010\u0013\u001a\u00020\u0012H\u0002J\u0010\u0010\u0014\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u0003H\u0016J\b\u0010\u0016\u001a\u00020\u0012H\u0016J\u0010\u0010\u0017\u001a\u00020\u00122\u0006\u0010\u0018\u001a\u00020\u0003H\u0016J!\u0010\u0019\u001a\u00020\u00122\u0006\u0010\u001a\u001a\u00020\u00102\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001cH\u0001J\u0010\u0010\u001e\u001a\u00020\u00122\u0006\u0010\u0018\u001a\u00020\u0003H\u0016R\u0014\u0010\u0002\u001a\u00020\u0003XD¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0004R\u0018\u0010\u0005\u001a\u00020\u0006X\u000f¢\u0006\f\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u0016\u0010\u000b\u001a\u0004\u0018\u00010\f8VX\u0005¢\u0006\u0006\u001a\u0004\b\r\u0010\u000e¨\u0006\u001f"}, d2 = {"com/android/systemui/animation/DialogLaunchAnimator$createActivityLaunchController$1", "Lcom/android/systemui/animation/ActivityLaunchAnimator$Controller;", "isDialogLaunch", "", "()Z", "launchContainer", "Landroid/view/ViewGroup;", "getLaunchContainer", "()Landroid/view/ViewGroup;", "setLaunchContainer", "(Landroid/view/ViewGroup;)V", "openingWindowSyncView", "Landroid/view/View;", "getOpeningWindowSyncView", "()Landroid/view/View;", "createAnimatorState", "Lcom/android/systemui/animation/LaunchAnimator$State;", "disableDialogDismiss", "", "enableDialogDismiss", "onIntentStarted", "willAnimate", "onLaunchAnimationCancelled", "onLaunchAnimationEnd", "isExpandingFullyAbove", "onLaunchAnimationProgress", "state", "progress", "", "linearProgress", "onLaunchAnimationStart", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: DialogLaunchAnimator.kt */
public final class DialogLaunchAnimator$createActivityLaunchController$1 implements ActivityLaunchAnimator.Controller {
    private final /* synthetic */ ActivityLaunchAnimator.Controller $$delegate_0;
    final /* synthetic */ AnimatedDialog $animatedDialog;
    final /* synthetic */ ActivityLaunchAnimator.Controller $controller;
    final /* synthetic */ Dialog $dialog;
    private final boolean isDialogLaunch = true;

    /* access modifiers changed from: private */
    /* renamed from: disableDialogDismiss$lambda-0  reason: not valid java name */
    public static final void m110disableDialogDismiss$lambda0() {
    }

    public LaunchAnimator.State createAnimatorState() {
        return this.$$delegate_0.createAnimatorState();
    }

    public ViewGroup getLaunchContainer() {
        return this.$$delegate_0.getLaunchContainer();
    }

    public View getOpeningWindowSyncView() {
        return this.$$delegate_0.getOpeningWindowSyncView();
    }

    public void onLaunchAnimationProgress(LaunchAnimator.State state, float f, float f2) {
        Intrinsics.checkNotNullParameter(state, TestProtocol.STATE_FIELD);
        this.$$delegate_0.onLaunchAnimationProgress(state, f, f2);
    }

    public void setLaunchContainer(ViewGroup viewGroup) {
        Intrinsics.checkNotNullParameter(viewGroup, "<set-?>");
        this.$$delegate_0.setLaunchContainer(viewGroup);
    }

    DialogLaunchAnimator$createActivityLaunchController$1(ActivityLaunchAnimator.Controller controller, Dialog dialog, AnimatedDialog animatedDialog) {
        this.$controller = controller;
        this.$dialog = dialog;
        this.$animatedDialog = animatedDialog;
        this.$$delegate_0 = controller;
    }

    public boolean isDialogLaunch() {
        return this.isDialogLaunch;
    }

    public void onIntentStarted(boolean z) {
        this.$controller.onIntentStarted(z);
        if (!z) {
            this.$dialog.dismiss();
        }
    }

    public void onLaunchAnimationCancelled() {
        this.$controller.onLaunchAnimationCancelled();
        enableDialogDismiss();
        this.$dialog.dismiss();
    }

    public void onLaunchAnimationStart(boolean z) {
        this.$controller.onLaunchAnimationStart(z);
        disableDialogDismiss();
        AnimatedDialog animatedDialog = this.$animatedDialog;
        animatedDialog.setTouchSurface(animatedDialog.prepareForStackDismiss());
        this.$dialog.getWindow().clearFlags(2);
    }

    public void onLaunchAnimationEnd(boolean z) {
        this.$controller.onLaunchAnimationEnd(z);
        this.$dialog.hide();
        enableDialogDismiss();
        this.$dialog.dismiss();
    }

    private final void disableDialogDismiss() {
        this.$dialog.setDismissOverride($$Lambda$DialogLaunchAnimator$createActivityLaunchController$1$qBn9LpruPjcugkbLcAstxCczC98.INSTANCE);
    }

    private final void enableDialogDismiss() {
        this.$dialog.setDismissOverride(new Runnable() {
            public final void run() {
                AnimatedDialog.this.onDialogDismissed();
            }
        });
    }
}
