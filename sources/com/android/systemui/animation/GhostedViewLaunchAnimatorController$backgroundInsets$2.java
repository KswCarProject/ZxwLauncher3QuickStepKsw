package com.android.systemui.animation;

import android.graphics.Insets;
import android.graphics.drawable.Drawable;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\n \u0002*\u0004\u0018\u00010\u00010\u0001H\n¢\u0006\u0002\b\u0003"}, d2 = {"<anonymous>", "Landroid/graphics/Insets;", "kotlin.jvm.PlatformType", "invoke"}, k = 3, mv = {1, 6, 0}, xi = 48)
/* compiled from: GhostedViewLaunchAnimatorController.kt */
final class GhostedViewLaunchAnimatorController$backgroundInsets$2 extends Lambda implements Function0<Insets> {
    final /* synthetic */ GhostedViewLaunchAnimatorController this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    GhostedViewLaunchAnimatorController$backgroundInsets$2(GhostedViewLaunchAnimatorController ghostedViewLaunchAnimatorController) {
        super(0);
        this.this$0 = ghostedViewLaunchAnimatorController;
    }

    public final Insets invoke() {
        Drawable access$getBackground$p = this.this$0.background;
        Insets opticalInsets = access$getBackground$p == null ? null : access$getBackground$p.getOpticalInsets();
        return opticalInsets == null ? Insets.NONE : opticalInsets;
    }
}
