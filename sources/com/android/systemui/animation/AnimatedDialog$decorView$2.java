package com.android.systemui.animation;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import java.util.Objects;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

@Metadata(d1 = {"\u0000\b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, d2 = {"<anonymous>", "Landroid/view/ViewGroup;", "invoke"}, k = 3, mv = {1, 6, 0}, xi = 48)
/* compiled from: DialogLaunchAnimator.kt */
final class AnimatedDialog$decorView$2 extends Lambda implements Function0<ViewGroup> {
    final /* synthetic */ AnimatedDialog this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    AnimatedDialog$decorView$2(AnimatedDialog animatedDialog) {
        super(0);
        this.this$0 = animatedDialog;
    }

    public final ViewGroup invoke() {
        Window window = this.this$0.getDialog().getWindow();
        Intrinsics.checkNotNull(window);
        View decorView = window.getDecorView();
        Objects.requireNonNull(decorView, "null cannot be cast to non-null type android.view.ViewGroup");
        return (ViewGroup) decorView;
    }
}
