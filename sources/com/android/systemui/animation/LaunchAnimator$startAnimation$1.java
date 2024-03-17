package com.android.systemui.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroupOverlay;
import android.view.ViewOverlay;
import com.android.systemui.animation.LaunchAnimator;
import kotlin.Metadata;

@Metadata(d1 = {"\u0000\u001f\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0012\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u0016J\u001a\u0010\u0006\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0007\u001a\u00020\bH\u0016¨\u0006\t"}, d2 = {"com/android/systemui/animation/LaunchAnimator$startAnimation$1", "Landroid/animation/AnimatorListenerAdapter;", "onAnimationEnd", "", "animation", "Landroid/animation/Animator;", "onAnimationStart", "isReverse", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: LaunchAnimator.kt */
public final class LaunchAnimator$startAnimation$1 extends AnimatorListenerAdapter {
    final /* synthetic */ LaunchAnimator.Controller $controller;
    final /* synthetic */ boolean $isExpandingFullyAbove;
    final /* synthetic */ ViewGroupOverlay $launchContainerOverlay;
    final /* synthetic */ boolean $moveBackgroundLayerWhenAppIsVisible;
    final /* synthetic */ ViewOverlay $openingWindowSyncViewOverlay;
    final /* synthetic */ GradientDrawable $windowBackgroundLayer;

    LaunchAnimator$startAnimation$1(LaunchAnimator.Controller controller, boolean z, ViewGroupOverlay viewGroupOverlay, GradientDrawable gradientDrawable, boolean z2, ViewOverlay viewOverlay) {
        this.$controller = controller;
        this.$isExpandingFullyAbove = z;
        this.$launchContainerOverlay = viewGroupOverlay;
        this.$windowBackgroundLayer = gradientDrawable;
        this.$moveBackgroundLayerWhenAppIsVisible = z2;
        this.$openingWindowSyncViewOverlay = viewOverlay;
    }

    public void onAnimationStart(Animator animator, boolean z) {
        this.$controller.onLaunchAnimationStart(this.$isExpandingFullyAbove);
        this.$launchContainerOverlay.add(this.$windowBackgroundLayer);
    }

    public void onAnimationEnd(Animator animator) {
        ViewOverlay viewOverlay;
        this.$controller.onLaunchAnimationEnd(this.$isExpandingFullyAbove);
        this.$launchContainerOverlay.remove(this.$windowBackgroundLayer);
        if (this.$moveBackgroundLayerWhenAppIsVisible && (viewOverlay = this.$openingWindowSyncViewOverlay) != null) {
            viewOverlay.remove(this.$windowBackgroundLayer);
        }
    }
}
