package com.android.systemui.animation;

import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.testing.TestProtocol;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.animation.LaunchAnimator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010\u0007\n\u0002\b\u0003\b\u0016\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0001¢\u0006\u0002\u0010\u0003J\t\u0010\u0013\u001a\u00020\u0014H\u0001J\u0011\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0007H\u0001J\t\u0010\u0018\u001a\u00020\u0016H\u0001J\u0011\u0010\u0019\u001a\u00020\u00162\u0006\u0010\u001a\u001a\u00020\u0007H\u0001J!\u0010\u001b\u001a\u00020\u00162\u0006\u0010\u001c\u001a\u00020\u00142\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u001eH\u0001J\u0011\u0010 \u001a\u00020\u00162\u0006\u0010\u001a\u001a\u00020\u0007H\u0001R\u0014\u0010\u0002\u001a\u00020\u0001X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0005R\u0014\u0010\u0006\u001a\u00020\u00078VX\u0005¢\u0006\u0006\u001a\u0004\b\u0006\u0010\bR\u0018\u0010\t\u001a\u00020\nX\u000f¢\u0006\f\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u0016\u0010\u000f\u001a\u0004\u0018\u00010\u00108VX\u0005¢\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012¨\u0006!"}, d2 = {"Lcom/android/systemui/animation/DelegateLaunchAnimatorController;", "Lcom/android/systemui/animation/ActivityLaunchAnimator$Controller;", "delegate", "(Lcom/android/systemui/animation/ActivityLaunchAnimator$Controller;)V", "getDelegate", "()Lcom/android/systemui/animation/ActivityLaunchAnimator$Controller;", "isDialogLaunch", "", "()Z", "launchContainer", "Landroid/view/ViewGroup;", "getLaunchContainer", "()Landroid/view/ViewGroup;", "setLaunchContainer", "(Landroid/view/ViewGroup;)V", "openingWindowSyncView", "Landroid/view/View;", "getOpeningWindowSyncView", "()Landroid/view/View;", "createAnimatorState", "Lcom/android/systemui/animation/LaunchAnimator$State;", "onIntentStarted", "", "willAnimate", "onLaunchAnimationCancelled", "onLaunchAnimationEnd", "isExpandingFullyAbove", "onLaunchAnimationProgress", "state", "progress", "", "linearProgress", "onLaunchAnimationStart", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: DelegateLaunchAnimatorController.kt */
public class DelegateLaunchAnimatorController implements ActivityLaunchAnimator.Controller {
    private final ActivityLaunchAnimator.Controller delegate;

    public LaunchAnimator.State createAnimatorState() {
        return this.delegate.createAnimatorState();
    }

    public ViewGroup getLaunchContainer() {
        return this.delegate.getLaunchContainer();
    }

    public View getOpeningWindowSyncView() {
        return this.delegate.getOpeningWindowSyncView();
    }

    public boolean isDialogLaunch() {
        return this.delegate.isDialogLaunch();
    }

    public void onIntentStarted(boolean z) {
        this.delegate.onIntentStarted(z);
    }

    public void onLaunchAnimationCancelled() {
        this.delegate.onLaunchAnimationCancelled();
    }

    public void onLaunchAnimationEnd(boolean z) {
        this.delegate.onLaunchAnimationEnd(z);
    }

    public void onLaunchAnimationProgress(LaunchAnimator.State state, float f, float f2) {
        Intrinsics.checkNotNullParameter(state, TestProtocol.STATE_FIELD);
        this.delegate.onLaunchAnimationProgress(state, f, f2);
    }

    public void onLaunchAnimationStart(boolean z) {
        this.delegate.onLaunchAnimationStart(z);
    }

    public void setLaunchContainer(ViewGroup viewGroup) {
        Intrinsics.checkNotNullParameter(viewGroup, "<set-?>");
        this.delegate.setLaunchContainer(viewGroup);
    }

    public DelegateLaunchAnimatorController(ActivityLaunchAnimator.Controller controller) {
        Intrinsics.checkNotNullParameter(controller, "delegate");
        this.delegate = controller;
    }

    /* access modifiers changed from: protected */
    public final ActivityLaunchAnimator.Controller getDelegate() {
        return this.delegate;
    }
}
