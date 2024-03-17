package com.android.systemui.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import com.android.launcher3.systemui.R;
import com.android.systemui.animation.ViewHierarchyAnimator;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000!\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0012\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0016J\u0010\u0010\f\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016R\u001a\u0010\u0002\u001a\u00020\u0003X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0004\u0010\u0005\"\u0004\b\u0006\u0010\u0007¨\u0006\r"}, d2 = {"com/android/systemui/animation/ViewHierarchyAnimator$Companion$startAnimation$1", "Landroid/animation/AnimatorListenerAdapter;", "cancelled", "", "getCancelled", "()Z", "setCancelled", "(Z)V", "onAnimationCancel", "", "animation", "Landroid/animation/Animator;", "onAnimationEnd", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: ViewHierarchyAnimator.kt */
public final class ViewHierarchyAnimator$Companion$startAnimation$1 extends AnimatorListenerAdapter {
    final /* synthetic */ Set<ViewHierarchyAnimator.Bound> $bounds;
    final /* synthetic */ boolean $ephemeral;
    final /* synthetic */ View $view;
    private boolean cancelled;

    ViewHierarchyAnimator$Companion$startAnimation$1(View view, Set<? extends ViewHierarchyAnimator.Bound> set, boolean z) {
        this.$view = view;
        this.$bounds = set;
        this.$ephemeral = z;
    }

    public final boolean getCancelled() {
        return this.cancelled;
    }

    public final void setCancelled(boolean z) {
        this.cancelled = z;
    }

    public void onAnimationEnd(Animator animator) {
        Intrinsics.checkNotNullParameter(animator, "animation");
        this.$view.setTag(R.id.tag_animator, (Object) null);
        View view = this.$view;
        for (ViewHierarchyAnimator.Bound overrideTag : this.$bounds) {
            view.setTag(overrideTag.getOverrideTag(), (Object) null);
        }
        if (this.$ephemeral && !this.cancelled) {
            ViewHierarchyAnimator.Companion.recursivelyRemoveListener(this.$view);
        }
    }

    public void onAnimationCancel(Animator animator) {
        this.cancelled = true;
    }
}
