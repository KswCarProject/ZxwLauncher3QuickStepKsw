package com.android.systemui.animation;

import android.view.IRemoteAnimationFinishedCallback;
import android.view.RemoteAnimationTarget;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.testing.TestProtocol;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.animation.LaunchAnimator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u00007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0003*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\t\u0010\f\u001a\u00020\rH\u0001J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J \u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\r2\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0015H\u0016J\u0010\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016R\u0018\u0010\u0002\u001a\u00020\u0003X\u000f¢\u0006\f\u001a\u0004\b\u0004\u0010\u0005\"\u0004\b\u0006\u0010\u0007R\u0016\u0010\b\u001a\u0004\u0018\u00010\t8VX\u0005¢\u0006\u0006\u001a\u0004\b\n\u0010\u000b¨\u0006\u0018"}, d2 = {"com/android/systemui/animation/ActivityLaunchAnimator$Runner$startAnimation$controller$1", "Lcom/android/systemui/animation/LaunchAnimator$Controller;", "launchContainer", "Landroid/view/ViewGroup;", "getLaunchContainer", "()Landroid/view/ViewGroup;", "setLaunchContainer", "(Landroid/view/ViewGroup;)V", "openingWindowSyncView", "Landroid/view/View;", "getOpeningWindowSyncView", "()Landroid/view/View;", "createAnimatorState", "Lcom/android/systemui/animation/LaunchAnimator$State;", "onLaunchAnimationEnd", "", "isExpandingFullyAbove", "", "onLaunchAnimationProgress", "state", "progress", "", "linearProgress", "onLaunchAnimationStart", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: ActivityLaunchAnimator.kt */
public final class ActivityLaunchAnimator$Runner$startAnimation$controller$1 implements LaunchAnimator.Controller {
    private final /* synthetic */ ActivityLaunchAnimator.Controller $$delegate_0;
    final /* synthetic */ ActivityLaunchAnimator.Controller $delegate;
    final /* synthetic */ IRemoteAnimationFinishedCallback $iCallback;
    final /* synthetic */ RemoteAnimationTarget $navigationBar;
    final /* synthetic */ RemoteAnimationTarget $window;
    final /* synthetic */ ActivityLaunchAnimator this$0;
    final /* synthetic */ ActivityLaunchAnimator.Runner this$1;

    public LaunchAnimator.State createAnimatorState() {
        return this.$$delegate_0.createAnimatorState();
    }

    public ViewGroup getLaunchContainer() {
        return this.$$delegate_0.getLaunchContainer();
    }

    public View getOpeningWindowSyncView() {
        return this.$$delegate_0.getOpeningWindowSyncView();
    }

    public void setLaunchContainer(ViewGroup viewGroup) {
        Intrinsics.checkNotNullParameter(viewGroup, "<set-?>");
        this.$$delegate_0.setLaunchContainer(viewGroup);
    }

    ActivityLaunchAnimator$Runner$startAnimation$controller$1(ActivityLaunchAnimator.Controller controller, ActivityLaunchAnimator activityLaunchAnimator, IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback, ActivityLaunchAnimator.Runner runner, RemoteAnimationTarget remoteAnimationTarget, RemoteAnimationTarget remoteAnimationTarget2) {
        this.$delegate = controller;
        this.this$0 = activityLaunchAnimator;
        this.$iCallback = iRemoteAnimationFinishedCallback;
        this.this$1 = runner;
        this.$window = remoteAnimationTarget;
        this.$navigationBar = remoteAnimationTarget2;
        this.$$delegate_0 = controller;
    }

    public void onLaunchAnimationStart(boolean z) {
        for (ActivityLaunchAnimator.Listener onLaunchAnimationStart : this.this$0.listeners) {
            onLaunchAnimationStart.onLaunchAnimationStart();
        }
        this.$delegate.onLaunchAnimationStart(z);
    }

    public void onLaunchAnimationEnd(boolean z) {
        for (ActivityLaunchAnimator.Listener onLaunchAnimationEnd : this.this$0.listeners) {
            onLaunchAnimationEnd.onLaunchAnimationEnd();
        }
        IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback = this.$iCallback;
        if (iRemoteAnimationFinishedCallback != null) {
            this.this$1.invoke(iRemoteAnimationFinishedCallback);
        }
        this.$delegate.onLaunchAnimationEnd(z);
    }

    public void onLaunchAnimationProgress(LaunchAnimator.State state, float f, float f2) {
        Intrinsics.checkNotNullParameter(state, TestProtocol.STATE_FIELD);
        if (!state.getVisible()) {
            this.this$1.applyStateToWindow(this.$window, state);
        }
        RemoteAnimationTarget remoteAnimationTarget = this.$navigationBar;
        if (remoteAnimationTarget != null) {
            this.this$1.applyStateToNavigationBar(remoteAnimationTarget, state, f2);
        }
        for (ActivityLaunchAnimator.Listener onLaunchAnimationProgress : this.this$0.listeners) {
            onLaunchAnimationProgress.onLaunchAnimationProgress(f2);
        }
        this.$delegate.onLaunchAnimationProgress(state, f, f2);
    }
}
