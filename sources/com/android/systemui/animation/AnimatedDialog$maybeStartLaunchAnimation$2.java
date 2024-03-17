package com.android.systemui.animation;

import android.view.ViewGroup;
import com.android.launcher3.systemui.R;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

@Metadata(d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, d2 = {"<anonymous>", "", "invoke"}, k = 3, mv = {1, 6, 0}, xi = 48)
/* compiled from: DialogLaunchAnimator.kt */
final class AnimatedDialog$maybeStartLaunchAnimation$2 extends Lambda implements Function0<Unit> {
    final /* synthetic */ AnimatedDialog this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    AnimatedDialog$maybeStartLaunchAnimation$2(AnimatedDialog animatedDialog) {
        super(0);
        this.this$0 = animatedDialog;
    }

    public final void invoke() {
        this.this$0.getTouchSurface().setTag(R.id.tag_launch_animation_running, (Object) null);
        this.this$0.getTouchSurface().setVisibility(4);
        this.this$0.isLaunching = false;
        if (this.this$0.dismissRequested) {
            this.this$0.getDialog().dismiss();
        }
        if (this.this$0.backgroundLayoutListener != null) {
            ViewGroup dialogContentWithBackground = this.this$0.getDialogContentWithBackground();
            Intrinsics.checkNotNull(dialogContentWithBackground);
            dialogContentWithBackground.addOnLayoutChangeListener(this.this$0.backgroundLayoutListener);
        }
    }
}
