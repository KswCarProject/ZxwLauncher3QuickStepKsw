package com.android.systemui.unfold.progress;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.util.FloatProperty;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import com.android.systemui.unfold.updates.FoldStateProvider;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0007\b\u0000\u0018\u0000 \u001f2\u00020\u00012\u00020\u0002:\u0003\u001d\u001e\u001fB\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\rH\u0016J\b\u0010\u0016\u001a\u00020\u0014H\u0016J\u0010\u0010\u0017\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\u0010\u0010\u001a\u001a\u00020\u00142\u0006\u0010\u001b\u001a\u00020\u000fH\u0016J\u0010\u0010\u001c\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\rH\u0016R\u0016\u0010\u0006\u001a\n \b*\u0004\u0018\u00010\u00070\u0007X\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\t\u001a\u00060\nR\u00020\u0000X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u000e\u001a\u00020\u000f@BX\u000e¢\u0006\b\n\u0000\"\u0004\b\u0011\u0010\u0012¨\u0006 "}, d2 = {"Lcom/android/systemui/unfold/progress/FixedTimingTransitionProgressProvider;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;", "Lcom/android/systemui/unfold/updates/FoldStateProvider$FoldUpdatesListener;", "foldStateProvider", "Lcom/android/systemui/unfold/updates/FoldStateProvider;", "(Lcom/android/systemui/unfold/updates/FoldStateProvider;)V", "animator", "Landroid/animation/ObjectAnimator;", "kotlin.jvm.PlatformType", "animatorListener", "Lcom/android/systemui/unfold/progress/FixedTimingTransitionProgressProvider$AnimatorListener;", "listeners", "", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider$TransitionProgressListener;", "value", "", "transitionProgress", "setTransitionProgress", "(F)V", "addCallback", "", "listener", "destroy", "onFoldUpdate", "update", "", "onHingeAngleUpdate", "angle", "removeCallback", "AnimationProgressProperty", "AnimatorListener", "Companion", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: FixedTimingTransitionProgressProvider.kt */
public final class FixedTimingTransitionProgressProvider implements UnfoldTransitionProgressProvider, FoldStateProvider.FoldUpdatesListener {
    private static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @Deprecated
    private static final long TRANSITION_TIME_MILLIS = 400;
    private final ObjectAnimator animator;
    private final AnimatorListener animatorListener;
    private final FoldStateProvider foldStateProvider;
    /* access modifiers changed from: private */
    public final List<UnfoldTransitionProgressProvider.TransitionProgressListener> listeners = new ArrayList();
    /* access modifiers changed from: private */
    public float transitionProgress;

    public void onHingeAngleUpdate(float f) {
    }

    public FixedTimingTransitionProgressProvider(FoldStateProvider foldStateProvider2) {
        Intrinsics.checkNotNullParameter(foldStateProvider2, "foldStateProvider");
        this.foldStateProvider = foldStateProvider2;
        AnimatorListener animatorListener2 = new AnimatorListener(this);
        this.animatorListener = animatorListener2;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, AnimationProgressProperty.INSTANCE, new float[]{0.0f, 1.0f});
        ofFloat.setDuration(TRANSITION_TIME_MILLIS);
        ofFloat.addListener(animatorListener2);
        this.animator = ofFloat;
        foldStateProvider2.addCallback(this);
        foldStateProvider2.start();
    }

    /* access modifiers changed from: private */
    public final void setTransitionProgress(float f) {
        for (UnfoldTransitionProgressProvider.TransitionProgressListener onTransitionProgress : this.listeners) {
            onTransitionProgress.onTransitionProgress(f);
        }
        this.transitionProgress = f;
    }

    public void destroy() {
        this.animator.cancel();
        this.foldStateProvider.removeCallback(this);
        this.foldStateProvider.stop();
    }

    public void onFoldUpdate(int i) {
        if (i == 2) {
            this.animator.start();
        } else if (i == 5) {
            this.animator.cancel();
        }
    }

    public void addCallback(UnfoldTransitionProgressProvider.TransitionProgressListener transitionProgressListener) {
        Intrinsics.checkNotNullParameter(transitionProgressListener, "listener");
        this.listeners.add(transitionProgressListener);
    }

    public void removeCallback(UnfoldTransitionProgressProvider.TransitionProgressListener transitionProgressListener) {
        Intrinsics.checkNotNullParameter(transitionProgressListener, "listener");
        this.listeners.remove(transitionProgressListener);
    }

    @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\bÂ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0002H\u0002¢\u0006\u0002\u0010\u0007J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\u0005H\u0016¨\u0006\u000b"}, d2 = {"Lcom/android/systemui/unfold/progress/FixedTimingTransitionProgressProvider$AnimationProgressProperty;", "Landroid/util/FloatProperty;", "Lcom/android/systemui/unfold/progress/FixedTimingTransitionProgressProvider;", "()V", "get", "", "provider", "(Lcom/android/systemui/unfold/progress/FixedTimingTransitionProgressProvider;)Ljava/lang/Float;", "setValue", "", "value", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: FixedTimingTransitionProgressProvider.kt */
    private static final class AnimationProgressProperty extends FloatProperty<FixedTimingTransitionProgressProvider> {
        public static final AnimationProgressProperty INSTANCE = new AnimationProgressProperty();

        private AnimationProgressProperty() {
            super("animation_progress");
        }

        public void setValue(FixedTimingTransitionProgressProvider fixedTimingTransitionProgressProvider, float f) {
            Intrinsics.checkNotNullParameter(fixedTimingTransitionProgressProvider, "provider");
            fixedTimingTransitionProgressProvider.setTransitionProgress(f);
        }

        public Float get(FixedTimingTransitionProgressProvider fixedTimingTransitionProgressProvider) {
            Intrinsics.checkNotNullParameter(fixedTimingTransitionProgressProvider, "provider");
            return Float.valueOf(fixedTimingTransitionProgressProvider.transitionProgress);
        }
    }

    @Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\t\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016¨\u0006\n"}, d2 = {"Lcom/android/systemui/unfold/progress/FixedTimingTransitionProgressProvider$AnimatorListener;", "Landroid/animation/Animator$AnimatorListener;", "(Lcom/android/systemui/unfold/progress/FixedTimingTransitionProgressProvider;)V", "onAnimationCancel", "", "animator", "Landroid/animation/Animator;", "onAnimationEnd", "onAnimationRepeat", "onAnimationStart", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: FixedTimingTransitionProgressProvider.kt */
    private final class AnimatorListener implements Animator.AnimatorListener {
        final /* synthetic */ FixedTimingTransitionProgressProvider this$0;

        public void onAnimationCancel(Animator animator) {
            Intrinsics.checkNotNullParameter(animator, "animator");
        }

        public void onAnimationRepeat(Animator animator) {
            Intrinsics.checkNotNullParameter(animator, "animator");
        }

        public AnimatorListener(FixedTimingTransitionProgressProvider fixedTimingTransitionProgressProvider) {
            Intrinsics.checkNotNullParameter(fixedTimingTransitionProgressProvider, "this$0");
            this.this$0 = fixedTimingTransitionProgressProvider;
        }

        public void onAnimationStart(Animator animator) {
            Intrinsics.checkNotNullParameter(animator, "animator");
            for (UnfoldTransitionProgressProvider.TransitionProgressListener onTransitionStarted : this.this$0.listeners) {
                onTransitionStarted.onTransitionStarted();
            }
        }

        public void onAnimationEnd(Animator animator) {
            Intrinsics.checkNotNullParameter(animator, "animator");
            for (UnfoldTransitionProgressProvider.TransitionProgressListener onTransitionFinished : this.this$0.listeners) {
                onTransitionFinished.onTransitionFinished();
            }
        }
    }

    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006\u0005"}, d2 = {"Lcom/android/systemui/unfold/progress/FixedTimingTransitionProgressProvider$Companion;", "", "()V", "TRANSITION_TIME_MILLIS", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: FixedTimingTransitionProgressProvider.kt */
    private static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
