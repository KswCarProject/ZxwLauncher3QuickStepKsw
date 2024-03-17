package com.android.systemui.animation;

import android.view.View;
import android.view.ViewGroup;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

@Metadata(d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, d2 = {"<anonymous>", "", "invoke"}, k = 3, mv = {1, 6, 0}, xi = 48)
/* compiled from: DialogLaunchAnimator.kt */
final class AnimatedDialog$hideDialogIntoView$2 extends Lambda implements Function0<Unit> {
    final /* synthetic */ Function1<Boolean, Unit> $onAnimationFinished;
    final /* synthetic */ AnimatedDialog this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    AnimatedDialog$hideDialogIntoView$2(AnimatedDialog animatedDialog, Function1<? super Boolean, Unit> function1) {
        super(0);
        this.this$0 = animatedDialog;
        this.$onAnimationFinished = function1;
    }

    public final void invoke() {
        View touchSurface = this.this$0.getTouchSurface();
        LaunchableView launchableView = touchSurface instanceof LaunchableView ? (LaunchableView) touchSurface : null;
        if (launchableView != null) {
            launchableView.setShouldBlockVisibilityChanges(false);
        }
        this.this$0.getTouchSurface().setVisibility(0);
        ViewGroup dialogContentWithBackground = this.this$0.getDialogContentWithBackground();
        Intrinsics.checkNotNull(dialogContentWithBackground);
        dialogContentWithBackground.setVisibility(4);
        if (this.this$0.backgroundLayoutListener != null) {
            dialogContentWithBackground.removeOnLayoutChangeListener(this.this$0.backgroundLayoutListener);
        }
        AnimatedDialog animatedDialog = this.this$0;
        final Function1<Boolean, Unit> function1 = this.$onAnimationFinished;
        final AnimatedDialog animatedDialog2 = this.this$0;
        animatedDialog.synchronizeNextDraw(new Function0<Unit>() {
            public final void invoke() {
                function1.invoke(true);
                animatedDialog2.onDialogDismissed.invoke(animatedDialog2);
            }
        });
    }
}
