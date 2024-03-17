package com.android.systemui.animation;

import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.testing.TestProtocol;
import com.android.systemui.animation.LaunchAnimator;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000/\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0003*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\b\u0010\t\u001a\u00020\nH\u0016J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016J \u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\n2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0012H\u0016J\u0010\u0010\u0014\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016R$\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0002\u001a\u00020\u00038V@VX\u000e¢\u0006\f\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\b¨\u0006\u0015"}, d2 = {"com/android/systemui/animation/AnimatedDialog$startAnimation$controller$1", "Lcom/android/systemui/animation/LaunchAnimator$Controller;", "value", "Landroid/view/ViewGroup;", "launchContainer", "getLaunchContainer", "()Landroid/view/ViewGroup;", "setLaunchContainer", "(Landroid/view/ViewGroup;)V", "createAnimatorState", "Lcom/android/systemui/animation/LaunchAnimator$State;", "onLaunchAnimationEnd", "", "isExpandingFullyAbove", "", "onLaunchAnimationProgress", "state", "progress", "", "linearProgress", "onLaunchAnimationStart", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: DialogLaunchAnimator.kt */
public final class AnimatedDialog$startAnimation$controller$1 implements LaunchAnimator.Controller {
    final /* synthetic */ LaunchAnimator.State $endState;
    final /* synthetic */ GhostedViewLaunchAnimatorController $endViewController;
    final /* synthetic */ Function0<Unit> $onLaunchAnimationEnd;
    final /* synthetic */ Function0<Unit> $onLaunchAnimationStart;
    final /* synthetic */ GhostedViewLaunchAnimatorController $startViewController;

    AnimatedDialog$startAnimation$controller$1(GhostedViewLaunchAnimatorController ghostedViewLaunchAnimatorController, GhostedViewLaunchAnimatorController ghostedViewLaunchAnimatorController2, Function0<Unit> function0, Function0<Unit> function02, LaunchAnimator.State state) {
        this.$startViewController = ghostedViewLaunchAnimatorController;
        this.$endViewController = ghostedViewLaunchAnimatorController2;
        this.$onLaunchAnimationStart = function0;
        this.$onLaunchAnimationEnd = function02;
        this.$endState = state;
    }

    public View getOpeningWindowSyncView() {
        return LaunchAnimator.Controller.DefaultImpls.getOpeningWindowSyncView(this);
    }

    public ViewGroup getLaunchContainer() {
        return this.$startViewController.getLaunchContainer();
    }

    public void setLaunchContainer(ViewGroup viewGroup) {
        Intrinsics.checkNotNullParameter(viewGroup, "value");
        this.$startViewController.setLaunchContainer(viewGroup);
        this.$endViewController.setLaunchContainer(viewGroup);
    }

    public LaunchAnimator.State createAnimatorState() {
        return this.$startViewController.createAnimatorState();
    }

    public void onLaunchAnimationStart(boolean z) {
        this.$onLaunchAnimationStart.invoke();
        this.$startViewController.onLaunchAnimationStart(z);
        this.$endViewController.onLaunchAnimationStart(z);
    }

    public void onLaunchAnimationEnd(boolean z) {
        this.$startViewController.onLaunchAnimationEnd(z);
        this.$endViewController.onLaunchAnimationEnd(z);
        this.$onLaunchAnimationEnd.invoke();
    }

    public void onLaunchAnimationProgress(LaunchAnimator.State state, float f, float f2) {
        Intrinsics.checkNotNullParameter(state, TestProtocol.STATE_FIELD);
        this.$startViewController.onLaunchAnimationProgress(state, f, f2);
        state.setVisible(!state.getVisible());
        this.$endViewController.onLaunchAnimationProgress(state, f, f2);
        this.$endViewController.fillGhostedViewState(this.$endState);
    }
}
