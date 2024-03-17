package com.android.systemui.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import com.android.systemui.animation.AnimatedDialog;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000\u0017\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016¨\u0006\u0006"}, d2 = {"com/android/systemui/animation/AnimatedDialog$AnimatedBoundsLayoutListener$onLayoutChange$animator$1$1", "Landroid/animation/AnimatorListenerAdapter;", "onAnimationEnd", "", "animation", "Landroid/animation/Animator;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: DialogLaunchAnimator.kt */
public final class AnimatedDialog$AnimatedBoundsLayoutListener$onLayoutChange$animator$1$1 extends AnimatorListenerAdapter {
    final /* synthetic */ AnimatedDialog.AnimatedBoundsLayoutListener this$0;

    AnimatedDialog$AnimatedBoundsLayoutListener$onLayoutChange$animator$1$1(AnimatedDialog.AnimatedBoundsLayoutListener animatedBoundsLayoutListener) {
        this.this$0 = animatedBoundsLayoutListener;
    }

    public void onAnimationEnd(Animator animator) {
        Intrinsics.checkNotNullParameter(animator, "animation");
        this.this$0.currentAnimator = null;
    }
}
