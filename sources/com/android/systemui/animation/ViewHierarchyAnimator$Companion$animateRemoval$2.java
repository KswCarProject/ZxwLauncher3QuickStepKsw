package com.android.systemui.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewGroup;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000\u0017\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016¨\u0006\u0006"}, d2 = {"com/android/systemui/animation/ViewHierarchyAnimator$Companion$animateRemoval$2", "Landroid/animation/AnimatorListenerAdapter;", "onAnimationEnd", "", "animation", "Landroid/animation/Animator;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: ViewHierarchyAnimator.kt */
public final class ViewHierarchyAnimator$Companion$animateRemoval$2 extends AnimatorListenerAdapter {
    final /* synthetic */ long $duration;
    final /* synthetic */ ViewGroup $parent;
    final /* synthetic */ View $rootView;

    ViewHierarchyAnimator$Companion$animateRemoval$2(View view, long j, ViewGroup viewGroup) {
        this.$rootView = view;
        this.$duration = j;
        this.$parent = viewGroup;
    }

    public void onAnimationEnd(Animator animator) {
        Intrinsics.checkNotNullParameter(animator, "animation");
        this.$rootView.animate().alpha(0.0f).setInterpolator(Interpolators.ALPHA_OUT).setDuration(this.$duration / ((long) 2)).withEndAction(new Runnable(this.$parent, this.$rootView) {
            public final /* synthetic */ ViewGroup f$0;
            public final /* synthetic */ View f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                ViewHierarchyAnimator$Companion$animateRemoval$2.m115onAnimationEnd$lambda0(this.f$0, this.f$1);
            }
        }).start();
    }

    /* access modifiers changed from: private */
    /* renamed from: onAnimationEnd$lambda-0  reason: not valid java name */
    public static final void m115onAnimationEnd$lambda0(ViewGroup viewGroup, View view) {
        Intrinsics.checkNotNullParameter(viewGroup, "$parent");
        Intrinsics.checkNotNullParameter(view, "$rootView");
        viewGroup.getOverlay().remove(view);
    }
}
