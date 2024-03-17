package com.android.systemui.animation;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\n¢\u0006\u0002\b\u0004"}, d2 = {"<anonymous>", "", "it", "Lcom/android/systemui/animation/AnimatedDialog;", "invoke"}, k = 3, mv = {1, 6, 0}, xi = 48)
/* compiled from: DialogLaunchAnimator.kt */
final class DialogLaunchAnimator$showFromView$animatedDialog$1 extends Lambda implements Function1<AnimatedDialog, Unit> {
    final /* synthetic */ DialogLaunchAnimator this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    DialogLaunchAnimator$showFromView$animatedDialog$1(DialogLaunchAnimator dialogLaunchAnimator) {
        super(1);
        this.this$0 = dialogLaunchAnimator;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((AnimatedDialog) obj);
        return Unit.INSTANCE;
    }

    public final void invoke(AnimatedDialog animatedDialog) {
        Intrinsics.checkNotNullParameter(animatedDialog, "it");
        this.this$0.openedDialogs.remove(animatedDialog);
    }
}
