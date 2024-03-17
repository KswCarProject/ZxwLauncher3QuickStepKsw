package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Handler;
import com.android.launcher3.LauncherAnimationRunner;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.window.RefreshRateTracker;
import com.android.systemui.shared.recents.utilities.Utilities;
import com.android.systemui.shared.system.RemoteAnimationRunnerCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.lang.ref.WeakReference;

public class LauncherAnimationRunner implements RemoteAnimationRunnerCompat {
    private static final RemoteAnimationFactory DEFAULT_FACTORY = $$Lambda$LauncherAnimationRunner$dFfFyjS74inydCsXDGK5ftFoKtQ.INSTANCE;
    private AnimationResult mAnimationResult;
    private final WeakReference<RemoteAnimationFactory> mFactory;
    private final Handler mHandler;
    private final boolean mStartAtFrontOfQueue;

    @FunctionalInterface
    public interface RemoteAnimationFactory {
        void onAnimationCancelled() {
        }

        void onCreateAnimation(int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, AnimationResult animationResult);
    }

    public LauncherAnimationRunner(Handler handler, RemoteAnimationFactory remoteAnimationFactory, boolean z) {
        this.mHandler = handler;
        this.mFactory = new WeakReference<>(remoteAnimationFactory);
        this.mStartAtFrontOfQueue = z;
    }

    public void onAnimationStart(int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, Runnable runnable) {
        $$Lambda$LauncherAnimationRunner$DvViukAuJ6Gp76ICvQgYTlagihE r0 = new Runnable(runnable, i, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3) {
            public final /* synthetic */ Runnable f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ RemoteAnimationTargetCompat[] f$3;
            public final /* synthetic */ RemoteAnimationTargetCompat[] f$4;
            public final /* synthetic */ RemoteAnimationTargetCompat[] f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                LauncherAnimationRunner.this.lambda$onAnimationStart$2$LauncherAnimationRunner(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        };
        if (this.mStartAtFrontOfQueue) {
            Utilities.postAtFrontOfQueueAsynchronously(this.mHandler, r0);
        } else {
            Utilities.postAsyncCallback(this.mHandler, r0);
        }
    }

    public /* synthetic */ void lambda$onAnimationStart$2$LauncherAnimationRunner(Runnable runnable, int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3) {
        finishExistingAnimation();
        this.mAnimationResult = new AnimationResult(new Runnable() {
            public final void run() {
                LauncherAnimationRunner.this.lambda$onAnimationStart$1$LauncherAnimationRunner();
            }
        }, runnable);
        getFactory().onCreateAnimation(i, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, this.mAnimationResult);
    }

    public /* synthetic */ void lambda$onAnimationStart$1$LauncherAnimationRunner() {
        this.mAnimationResult = null;
    }

    public void onAnimationStart(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, Runnable runnable) {
        onAnimationStart(0, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, new RemoteAnimationTargetCompat[0], runnable);
    }

    @Deprecated
    public void onAnimationStart(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, Runnable runnable) {
        onAnimationStart(remoteAnimationTargetCompatArr, new RemoteAnimationTargetCompat[0], runnable);
    }

    private RemoteAnimationFactory getFactory() {
        RemoteAnimationFactory remoteAnimationFactory = (RemoteAnimationFactory) this.mFactory.get();
        return remoteAnimationFactory != null ? remoteAnimationFactory : DEFAULT_FACTORY;
    }

    private void finishExistingAnimation() {
        AnimationResult animationResult = this.mAnimationResult;
        if (animationResult != null) {
            animationResult.finish();
            this.mAnimationResult = null;
        }
    }

    public void onAnimationCancelled() {
        Utilities.postAsyncCallback(this.mHandler, new Runnable() {
            public final void run() {
                LauncherAnimationRunner.this.lambda$onAnimationCancelled$3$LauncherAnimationRunner();
            }
        });
    }

    public /* synthetic */ void lambda$onAnimationCancelled$3$LauncherAnimationRunner() {
        finishExistingAnimation();
        getFactory().onAnimationCancelled();
    }

    public static final class AnimationResult {
        private final Runnable mASyncFinishRunnable;
        private AnimatorSet mAnimator;
        private boolean mFinished;
        private boolean mInitialized;
        private Runnable mOnCompleteCallback;
        private final Runnable mSyncFinishRunnable;

        private AnimationResult(Runnable runnable, Runnable runnable2) {
            this.mFinished = false;
            this.mInitialized = false;
            this.mSyncFinishRunnable = runnable;
            this.mASyncFinishRunnable = runnable2;
        }

        /* access modifiers changed from: private */
        public void finish() {
            if (!this.mFinished) {
                this.mSyncFinishRunnable.run();
                Executors.UI_HELPER_EXECUTOR.execute(new Runnable() {
                    public final void run() {
                        LauncherAnimationRunner.AnimationResult.this.lambda$finish$0$LauncherAnimationRunner$AnimationResult();
                    }
                });
                this.mFinished = true;
            }
        }

        public /* synthetic */ void lambda$finish$0$LauncherAnimationRunner$AnimationResult() {
            this.mASyncFinishRunnable.run();
            if (this.mOnCompleteCallback != null) {
                Executors.MAIN_EXECUTOR.execute(this.mOnCompleteCallback);
            }
        }

        public void setAnimation(AnimatorSet animatorSet, Context context) {
            setAnimation(animatorSet, context, (Runnable) null, true);
        }

        public void setAnimation(AnimatorSet animatorSet, Context context, Runnable runnable, boolean z) {
            if (!this.mInitialized) {
                this.mInitialized = true;
                this.mAnimator = animatorSet;
                this.mOnCompleteCallback = runnable;
                if (animatorSet == null) {
                    finish();
                } else if (this.mFinished) {
                    animatorSet.start();
                    this.mAnimator.end();
                    Runnable runnable2 = this.mOnCompleteCallback;
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                } else {
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            AnimationResult.this.finish();
                        }
                    });
                    this.mAnimator.start();
                    if (z) {
                        this.mAnimator.setCurrentPlayTime(Math.min((long) RefreshRateTracker.getSingleFrameMs(context), this.mAnimator.getTotalDuration()));
                    }
                }
            } else {
                throw new IllegalStateException("Animation already initialized");
            }
        }
    }
}
