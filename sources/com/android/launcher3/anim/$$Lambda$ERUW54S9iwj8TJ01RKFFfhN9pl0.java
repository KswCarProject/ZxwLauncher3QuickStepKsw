package com.android.launcher3.anim;

import android.animation.Animator;
import java.util.function.BiConsumer;

/* renamed from: com.android.launcher3.anim.-$$Lambda$ERUW54S9iwj8TJ01RKFFfhN9pl0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ERUW54S9iwj8TJ01RKFFfhN9pl0 implements BiConsumer {
    public static final /* synthetic */ $$Lambda$ERUW54S9iwj8TJ01RKFFfhN9pl0 INSTANCE = new $$Lambda$ERUW54S9iwj8TJ01RKFFfhN9pl0();

    private /* synthetic */ $$Lambda$ERUW54S9iwj8TJ01RKFFfhN9pl0() {
    }

    public final void accept(Object obj, Object obj2) {
        ((Animator.AnimatorListener) obj).onAnimationCancel((Animator) obj2);
    }
}
