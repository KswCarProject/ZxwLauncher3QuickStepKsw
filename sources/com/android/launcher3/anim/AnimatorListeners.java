package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import java.util.function.Consumer;

public class AnimatorListeners {
    public static Animator.AnimatorListener forSuccessCallback(Runnable runnable) {
        return new RunnableSuccessListener(runnable);
    }

    public static Animator.AnimatorListener forEndCallback(Consumer<Boolean> consumer) {
        return new EndStateCallbackWrapper(consumer);
    }

    public static Animator.AnimatorListener forEndCallback(final Runnable runnable) {
        return new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                runnable.run();
            }
        };
    }

    private static class EndStateCallbackWrapper extends AnimatorListenerAdapter {
        private final Consumer<Boolean> mListener;
        private boolean mListenerCalled = false;

        EndStateCallbackWrapper(Consumer<Boolean> consumer) {
            this.mListener = consumer;
        }

        public void onAnimationCancel(Animator animator) {
            if (!this.mListenerCalled) {
                this.mListenerCalled = true;
                this.mListener.accept(false);
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (!this.mListenerCalled) {
                boolean z = true;
                this.mListenerCalled = true;
                Consumer<Boolean> consumer = this.mListener;
                if ((animator instanceof ValueAnimator) && ((ValueAnimator) animator).getAnimatedFraction() <= 0.5f) {
                    z = false;
                }
                consumer.accept(Boolean.valueOf(z));
            }
        }
    }

    private static class RunnableSuccessListener extends AnimationSuccessListener {
        private final Runnable mRunnable;

        private RunnableSuccessListener(Runnable runnable) {
            this.mRunnable = runnable;
        }

        public void onAnimationSuccess(Animator animator) {
            this.mRunnable.run();
        }
    }
}
