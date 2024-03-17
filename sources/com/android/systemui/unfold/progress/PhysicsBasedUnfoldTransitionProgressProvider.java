package com.android.systemui.unfold.progress;

import android.util.Log;
import android.util.MathUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import com.android.systemui.unfold.updates.FoldStateProvider;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\b\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003:\u0001)B\r\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\fH\u0016J\u0018\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u0019\u001a\u00020\bH\u0002J\b\u0010\u001a\u001a\u00020\u0015H\u0016J4\u0010\u001b\u001a\u00020\u00152\u0012\u0010\u001c\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\u001d0\u001d2\u0006\u0010\u001e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u001f\u001a\u00020\u0010H\u0016J\u0010\u0010 \u001a\u00020\u00152\u0006\u0010!\u001a\u00020\"H\u0016J\u0010\u0010#\u001a\u00020\u00152\u0006\u0010$\u001a\u00020\u0010H\u0016J\b\u0010%\u001a\u00020\u0015H\u0002J\u0010\u0010&\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\fH\u0016J\u0010\u0010'\u001a\u00020\u00152\u0006\u0010(\u001a\u00020\u0010H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u0010@BX\u000e¢\u0006\b\n\u0000\"\u0004\b\u0012\u0010\u0013¨\u0006*"}, d2 = {"Lcom/android/systemui/unfold/progress/PhysicsBasedUnfoldTransitionProgressProvider;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;", "Lcom/android/systemui/unfold/updates/FoldStateProvider$FoldUpdatesListener;", "Landroidx/dynamicanimation/animation/DynamicAnimation$OnAnimationEndListener;", "foldStateProvider", "Lcom/android/systemui/unfold/updates/FoldStateProvider;", "(Lcom/android/systemui/unfold/updates/FoldStateProvider;)V", "isAnimatedCancelRunning", "", "isTransitionRunning", "listeners", "", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider$TransitionProgressListener;", "springAnimation", "Landroidx/dynamicanimation/animation/SpringAnimation;", "value", "", "transitionProgress", "setTransitionProgress", "(F)V", "addCallback", "", "listener", "cancelTransition", "endValue", "animate", "destroy", "onAnimationEnd", "animation", "Landroidx/dynamicanimation/animation/DynamicAnimation;", "canceled", "velocity", "onFoldUpdate", "update", "", "onHingeAngleUpdate", "angle", "onStartTransition", "removeCallback", "startTransition", "startValue", "AnimationProgressProperty", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: PhysicsBasedUnfoldTransitionProgressProvider.kt */
public final class PhysicsBasedUnfoldTransitionProgressProvider implements UnfoldTransitionProgressProvider, FoldStateProvider.FoldUpdatesListener, DynamicAnimation.OnAnimationEndListener {
    private final FoldStateProvider foldStateProvider;
    private boolean isAnimatedCancelRunning;
    private boolean isTransitionRunning;
    private final List<UnfoldTransitionProgressProvider.TransitionProgressListener> listeners = new ArrayList();
    private final SpringAnimation springAnimation;
    /* access modifiers changed from: private */
    public float transitionProgress;

    public PhysicsBasedUnfoldTransitionProgressProvider(FoldStateProvider foldStateProvider2) {
        Intrinsics.checkNotNullParameter(foldStateProvider2, "foldStateProvider");
        this.foldStateProvider = foldStateProvider2;
        SpringAnimation springAnimation2 = new SpringAnimation(this, AnimationProgressProperty.INSTANCE);
        springAnimation2.addEndListener(this);
        this.springAnimation = springAnimation2;
        foldStateProvider2.addCallback(this);
        foldStateProvider2.start();
    }

    /* access modifiers changed from: private */
    public final void setTransitionProgress(float f) {
        if (this.isTransitionRunning) {
            for (UnfoldTransitionProgressProvider.TransitionProgressListener onTransitionProgress : this.listeners) {
                onTransitionProgress.onTransitionProgress(f);
            }
        }
        this.transitionProgress = f;
    }

    public void destroy() {
        this.foldStateProvider.stop();
    }

    public void onHingeAngleUpdate(float f) {
        if (this.isTransitionRunning && !this.isAnimatedCancelRunning) {
            this.springAnimation.animateToFinalPosition(MathUtils.saturate(f / 165.0f));
        }
    }

    public void onFoldUpdate(int i) {
        if (i != 1) {
            if (i == 2) {
                startTransition(0.0f);
                if (this.foldStateProvider.isFinishedOpening()) {
                    cancelTransition(1.0f, true);
                }
            } else if (i == 3 || i == 4) {
                if (this.isTransitionRunning) {
                    cancelTransition(1.0f, true);
                }
            } else if (i == 5) {
                cancelTransition(0.0f, false);
            }
        } else if (!this.isTransitionRunning) {
            startTransition(1.0f);
        } else if (this.isAnimatedCancelRunning) {
            this.isAnimatedCancelRunning = false;
        }
        Log.d("PhysicsBasedUnfoldTransitionProgressProvider", Intrinsics.stringPlus("onFoldUpdate = ", Integer.valueOf(i)));
    }

    private final void cancelTransition(float f, boolean z) {
        if (!this.isTransitionRunning || !z) {
            setTransitionProgress(f);
            this.isAnimatedCancelRunning = false;
            this.isTransitionRunning = false;
            this.springAnimation.cancel();
            for (UnfoldTransitionProgressProvider.TransitionProgressListener onTransitionFinished : this.listeners) {
                onTransitionFinished.onTransitionFinished();
            }
            Log.d("PhysicsBasedUnfoldTransitionProgressProvider", "onTransitionFinished");
            return;
        }
        this.isAnimatedCancelRunning = true;
        this.springAnimation.animateToFinalPosition(f);
    }

    public void onAnimationEnd(DynamicAnimation<? extends DynamicAnimation<?>> dynamicAnimation, boolean z, float f, float f2) {
        Intrinsics.checkNotNullParameter(dynamicAnimation, "animation");
        if (this.isAnimatedCancelRunning) {
            cancelTransition(f, false);
        }
    }

    private final void onStartTransition() {
        for (UnfoldTransitionProgressProvider.TransitionProgressListener onTransitionStarted : this.listeners) {
            onTransitionStarted.onTransitionStarted();
        }
        this.isTransitionRunning = true;
        Log.d("PhysicsBasedUnfoldTransitionProgressProvider", "onTransitionStarted");
    }

    private final void startTransition(float f) {
        if (!this.isTransitionRunning) {
            onStartTransition();
        }
        SpringAnimation springAnimation2 = this.springAnimation;
        SpringForce springForce = new SpringForce();
        springForce.setFinalPosition(f);
        springForce.setDampingRatio(1.0f);
        springForce.setStiffness(200.0f);
        springAnimation2.setSpring(springForce);
        springAnimation2.setMinimumVisibleChange(0.001f);
        springAnimation2.setStartValue(f);
        springAnimation2.setMinValue(0.0f);
        springAnimation2.setMaxValue(1.0f);
        this.springAnimation.start();
    }

    public void addCallback(UnfoldTransitionProgressProvider.TransitionProgressListener transitionProgressListener) {
        Intrinsics.checkNotNullParameter(transitionProgressListener, "listener");
        this.listeners.add(transitionProgressListener);
    }

    public void removeCallback(UnfoldTransitionProgressProvider.TransitionProgressListener transitionProgressListener) {
        Intrinsics.checkNotNullParameter(transitionProgressListener, "listener");
        this.listeners.remove(transitionProgressListener);
    }

    @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\bÂ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0002H\u0016J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0006\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\u0005H\u0016¨\u0006\n"}, d2 = {"Lcom/android/systemui/unfold/progress/PhysicsBasedUnfoldTransitionProgressProvider$AnimationProgressProperty;", "Landroidx/dynamicanimation/animation/FloatPropertyCompat;", "Lcom/android/systemui/unfold/progress/PhysicsBasedUnfoldTransitionProgressProvider;", "()V", "getValue", "", "provider", "setValue", "", "value", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: PhysicsBasedUnfoldTransitionProgressProvider.kt */
    private static final class AnimationProgressProperty extends FloatPropertyCompat<PhysicsBasedUnfoldTransitionProgressProvider> {
        public static final AnimationProgressProperty INSTANCE = new AnimationProgressProperty();

        private AnimationProgressProperty() {
            super("animation_progress");
        }

        public void setValue(PhysicsBasedUnfoldTransitionProgressProvider physicsBasedUnfoldTransitionProgressProvider, float f) {
            Intrinsics.checkNotNullParameter(physicsBasedUnfoldTransitionProgressProvider, "provider");
            physicsBasedUnfoldTransitionProgressProvider.setTransitionProgress(f);
        }

        public float getValue(PhysicsBasedUnfoldTransitionProgressProvider physicsBasedUnfoldTransitionProgressProvider) {
            Intrinsics.checkNotNullParameter(physicsBasedUnfoldTransitionProgressProvider, "provider");
            return physicsBasedUnfoldTransitionProgressProvider.transitionProgress;
        }
    }
}
