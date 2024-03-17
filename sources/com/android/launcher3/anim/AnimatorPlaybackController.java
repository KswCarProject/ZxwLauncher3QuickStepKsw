package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.window.RefreshRateTracker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AnimatorPlaybackController implements ValueAnimator.AnimatorUpdateListener {
    private static final float ANIMATION_COMPLETE_THRESHOLD = 0.95f;
    private final AnimatorSet mAnim;
    private final ValueAnimator mAnimationPlayer;
    private final Holder[] mChildAnimations;
    protected float mCurrentFraction;
    private final long mDuration;
    /* access modifiers changed from: private */
    public Runnable mEndAction;
    protected boolean mTargetCancelled = false;

    private interface ProgressMapper {
        public static final ProgressMapper DEFAULT = $$Lambda$AnimatorPlaybackController$ProgressMapper$HV5dcaCySg2ZC6m3BVIyMUNjbfs.INSTANCE;

        static /* synthetic */ float lambda$static$0(float f, float f2) {
            if (f > f2) {
                return 1.0f;
            }
            return f / f2;
        }

        float getProgress(float f, float f2);
    }

    public static AnimatorPlaybackController wrap(AnimatorSet animatorSet, long j) {
        ArrayList arrayList = new ArrayList();
        addAnimationHoldersRecur(animatorSet, j, SpringProperty.DEFAULT, arrayList);
        return new AnimatorPlaybackController(animatorSet, j, arrayList);
    }

    AnimatorPlaybackController(AnimatorSet animatorSet, long j, ArrayList<Holder> arrayList) {
        this.mAnim = animatorSet;
        this.mDuration = j;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.mAnimationPlayer = ofFloat;
        ofFloat.setInterpolator(Interpolators.LINEAR);
        ofFloat.addListener(new OnAnimationEndDispatcher());
        ofFloat.addUpdateListener(this);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationCancel(Animator animator) {
                AnimatorPlaybackController.this.mTargetCancelled = true;
            }

            public void onAnimationEnd(Animator animator) {
                AnimatorPlaybackController.this.mTargetCancelled = false;
            }

            public void onAnimationStart(Animator animator) {
                AnimatorPlaybackController.this.mTargetCancelled = false;
            }
        });
        this.mChildAnimations = (Holder[]) arrayList.toArray(new Holder[arrayList.size()]);
    }

    public AnimatorSet getTarget() {
        return this.mAnim;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public TimeInterpolator getInterpolator() {
        return this.mAnim.getInterpolator() != null ? this.mAnim.getInterpolator() : Interpolators.LINEAR;
    }

    public void start() {
        this.mAnimationPlayer.setFloatValues(new float[]{this.mCurrentFraction, 1.0f});
        this.mAnimationPlayer.setDuration(clampDuration(1.0f - this.mCurrentFraction));
        this.mAnimationPlayer.start();
    }

    public void reverse() {
        this.mAnimationPlayer.setFloatValues(new float[]{this.mCurrentFraction, 0.0f});
        this.mAnimationPlayer.setDuration(clampDuration(this.mCurrentFraction));
        this.mAnimationPlayer.start();
    }

    public void startWithVelocity(Context context, boolean z, float f, float f2, long j) {
        Holder[] holderArr;
        int i;
        long j2 = j;
        float abs = 1.0f / Math.abs(f2);
        float f3 = f * abs;
        float singleFrameMs = ((float) RefreshRateTracker.getSingleFrameMs(context)) * f3;
        float boundToRange = Utilities.boundToRange(getProgressFraction() + singleFrameMs, 0.0f, 1.0f);
        int i2 = z ? 1 : 2;
        Holder[] holderArr2 = this.mChildAnimations;
        int length = holderArr2.length;
        long j3 = j2;
        int i3 = 0;
        while (i3 < length) {
            Holder holder = holderArr2[i3];
            if ((holder.springProperty.flags & i2) != 0) {
                i = i2;
                SpringAnimationBuilder computeParams = new SpringAnimationBuilder(context).setStartValue(this.mCurrentFraction).setEndValue(z ? 1.0f : 0.0f).setStartVelocity(f3).setMinimumVisibleChange(abs).setDampingRatio(holder.springProperty.mDampingRatio).setStiffness(holder.springProperty.mStiffness).computeParams();
                holderArr = holderArr2;
                long duration = computeParams.getDuration();
                j3 = Math.max(duration, j3);
                holder.mapper = new ProgressMapper((float) duration, singleFrameMs) {
                    public final /* synthetic */ float f$1;
                    public final /* synthetic */ float f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final float getProgress(float f, float f2) {
                        return AnimatorPlaybackController.this.lambda$startWithVelocity$0$AnimatorPlaybackController(this.f$1, this.f$2, f, f2);
                    }
                };
                ValueAnimator valueAnimator = holder.anim;
                Objects.requireNonNull(computeParams);
                valueAnimator.setInterpolator(new TimeInterpolator() {
                    public final float getInterpolation(float f) {
                        return SpringAnimationBuilder.this.getInterpolatedValue(f);
                    }
                });
            } else {
                Context context2 = context;
                i = i2;
                holderArr = holderArr2;
            }
            i3++;
            i2 = i;
            holderArr2 = holderArr;
        }
        ValueAnimator valueAnimator2 = this.mAnimationPlayer;
        float[] fArr = new float[2];
        fArr[0] = boundToRange;
        fArr[1] = z ? 1.0f : 0.0f;
        valueAnimator2.setFloatValues(fArr);
        if (j3 <= j2) {
            this.mAnimationPlayer.setDuration(j2);
            this.mAnimationPlayer.setInterpolator(Interpolators.scrollInterpolatorForVelocity(f));
        } else {
            this.mAnimationPlayer.setDuration(j3);
            this.mAnimationPlayer.setInterpolator(Interpolators.clampToProgress(Interpolators.scrollInterpolatorForVelocity(f), 0.0f, ((float) j2) / ((float) j3)));
        }
        this.mAnimationPlayer.start();
    }

    public /* synthetic */ float lambda$startWithVelocity$0$AnimatorPlaybackController(float f, float f2, float f3, float f4) {
        if (f <= 0.0f || f2 >= 1.0f) {
            return 1.0f;
        }
        return Utilities.mapToRange(((float) this.mAnimationPlayer.getCurrentPlayTime()) / f, 0.0f, 1.0f, Math.abs(f2), 1.0f, Interpolators.LINEAR);
    }

    public void forceFinishIfCloseToEnd() {
        if (this.mAnimationPlayer.isRunning() && this.mAnimationPlayer.getAnimatedFraction() > ANIMATION_COMPLETE_THRESHOLD) {
            this.mAnimationPlayer.end();
        }
    }

    public void pause() {
        for (Holder reset : this.mChildAnimations) {
            reset.reset();
        }
        this.mAnimationPlayer.cancel();
    }

    public ValueAnimator getAnimationPlayer() {
        return this.mAnimationPlayer;
    }

    public void setPlayFraction(float f) {
        this.mCurrentFraction = f;
        if (!this.mTargetCancelled) {
            float boundToRange = Utilities.boundToRange(f, 0.0f, 1.0f);
            for (Holder progress : this.mChildAnimations) {
                progress.setProgress(boundToRange);
            }
        }
    }

    public float getProgressFraction() {
        return this.mCurrentFraction;
    }

    public float getInterpolatedProgress() {
        return getInterpolator().getInterpolation(this.mCurrentFraction);
    }

    public void setEndAction(Runnable runnable) {
        this.mEndAction = runnable;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        setPlayFraction(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: protected */
    public long clampDuration(float f) {
        long j = this.mDuration;
        float f2 = ((float) j) * f;
        if (f2 <= 0.0f) {
            return 0;
        }
        return Math.min((long) f2, j);
    }

    public AnimatorPlaybackController dispatchOnStart() {
        callListenerCommandRecursively(this.mAnim, $$Lambda$hzTHhXlShDIGy0XROKsCGCgAjaY.INSTANCE);
        return this;
    }

    public AnimatorPlaybackController dispatchOnCancel() {
        callListenerCommandRecursively(this.mAnim, $$Lambda$ERUW54S9iwj8TJ01RKFFfhN9pl0.INSTANCE);
        return this;
    }

    public AnimatorPlaybackController dispatchOnEnd() {
        callListenerCommandRecursively(this.mAnim, $$Lambda$G9BCccKCnF7M65F4KmuNOitA8.INSTANCE);
        return this;
    }

    public void dispatchSetInterpolator(TimeInterpolator timeInterpolator) {
        callAnimatorCommandRecursively(this.mAnim, new Consumer(timeInterpolator) {
            public final /* synthetic */ TimeInterpolator f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                ((Animator) obj).setInterpolator(this.f$0);
            }
        });
    }

    public static void callListenerCommandRecursively(Animator animator, BiConsumer<Animator.AnimatorListener, Animator> biConsumer) {
        callAnimatorCommandRecursively(animator, new Consumer(biConsumer) {
            public final /* synthetic */ BiConsumer f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                AnimatorPlaybackController.lambda$callListenerCommandRecursively$2(this.f$0, (Animator) obj);
            }
        });
    }

    static /* synthetic */ void lambda$callListenerCommandRecursively$2(BiConsumer biConsumer, Animator animator) {
        for (T accept : nonNullList(animator.getListeners())) {
            biConsumer.accept(accept, animator);
        }
    }

    private static void callAnimatorCommandRecursively(Animator animator, Consumer<Animator> consumer) {
        consumer.accept(animator);
        if (animator instanceof AnimatorSet) {
            for (T callAnimatorCommandRecursively : nonNullList(((AnimatorSet) animator).getChildAnimations())) {
                callAnimatorCommandRecursively(callAnimatorCommandRecursively, consumer);
            }
        }
    }

    private class OnAnimationEndDispatcher extends AnimationSuccessListener {
        boolean mDispatched;

        private OnAnimationEndDispatcher() {
            this.mDispatched = false;
        }

        public void onAnimationStart(Animator animator) {
            this.mCancelled = false;
            this.mDispatched = false;
        }

        public void onAnimationSuccess(Animator animator) {
            if (!this.mDispatched) {
                AnimatorPlaybackController.this.dispatchOnEnd();
                if (AnimatorPlaybackController.this.mEndAction != null) {
                    AnimatorPlaybackController.this.mEndAction.run();
                }
                this.mDispatched = true;
            }
        }
    }

    private static <T> List<T> nonNullList(ArrayList<T> arrayList) {
        return arrayList == null ? Collections.emptyList() : arrayList;
    }

    static class Holder {
        public final ValueAnimator anim;
        public final float globalEndProgress;
        public final TimeInterpolator interpolator;
        public ProgressMapper mapper = ProgressMapper.DEFAULT;
        public final SpringProperty springProperty;

        Holder(Animator animator, float f, SpringProperty springProperty2) {
            ValueAnimator valueAnimator = (ValueAnimator) animator;
            this.anim = valueAnimator;
            this.springProperty = springProperty2;
            this.interpolator = valueAnimator.getInterpolator();
            this.globalEndProgress = ((float) animator.getDuration()) / f;
        }

        public void setProgress(float f) {
            this.anim.setCurrentFraction(this.mapper.getProgress(f, this.globalEndProgress));
        }

        public void reset() {
            this.anim.setInterpolator(this.interpolator);
            this.mapper = ProgressMapper.DEFAULT;
        }
    }

    static void addAnimationHoldersRecur(Animator animator, long j, SpringProperty springProperty, ArrayList<Holder> arrayList) {
        long duration = animator.getDuration();
        TimeInterpolator interpolator = animator.getInterpolator();
        if (animator instanceof ValueAnimator) {
            arrayList.add(new Holder(animator, (float) j, springProperty));
        } else if (animator instanceof AnimatorSet) {
            Iterator<Animator> it = ((AnimatorSet) animator).getChildAnimations().iterator();
            while (it.hasNext()) {
                Animator next = it.next();
                if (duration > 0) {
                    next.setDuration(duration);
                }
                if (interpolator != null) {
                    next.setInterpolator(interpolator);
                }
                addAnimationHoldersRecur(next, j, springProperty, arrayList);
            }
        } else {
            throw new RuntimeException("Unknown animation type " + animator);
        }
    }
}
