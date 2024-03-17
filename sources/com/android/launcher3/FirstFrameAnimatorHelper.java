package com.android.launcher3;

import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import com.android.launcher3.FirstFrameAnimatorHelper;
import com.android.launcher3.util.window.RefreshRateTracker;

public class FirstFrameAnimatorHelper implements ViewTreeObserver.OnDrawListener, View.OnAttachStateChangeListener {
    private static final boolean DEBUG = false;
    private static final int MAX_DELAY = 1000;
    private static final String TAG = "FirstFrameAnimatorHlpr";
    /* access modifiers changed from: private */
    public long mGlobalFrameCount;
    /* access modifiers changed from: private */
    public View mRootView;

    public FirstFrameAnimatorHelper(View view) {
        view.addOnAttachStateChangeListener(this);
        if (view.isAttachedToWindow()) {
            onViewAttachedToWindow(view);
        }
    }

    public <T extends ValueAnimator> T addTo(T t) {
        t.addUpdateListener(new MyListener());
        return t;
    }

    public void onDraw() {
        this.mGlobalFrameCount++;
    }

    public void onViewAttachedToWindow(View view) {
        View rootView = view.getRootView();
        this.mRootView = rootView;
        rootView.getViewTreeObserver().addOnDrawListener(this);
    }

    public void onViewDetachedFromWindow(View view) {
        View view2 = this.mRootView;
        if (view2 != null) {
            view2.getViewTreeObserver().removeOnDrawListener(this);
            this.mRootView = null;
        }
    }

    private class MyListener implements ValueAnimator.AnimatorUpdateListener {
        private boolean mAdjustedSecondFrameTime;
        private boolean mHandlingOnAnimationUpdate;
        private long mStartFrame;
        private long mStartTime;

        private MyListener() {
            this.mStartTime = -1;
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            ValueAnimator valueAnimator2 = valueAnimator;
            long currentTimeMillis = System.currentTimeMillis();
            if (this.mStartTime == -1) {
                this.mStartFrame = FirstFrameAnimatorHelper.this.mGlobalFrameCount;
                this.mStartTime = currentTimeMillis;
            }
            long currentPlayTime = valueAnimator.getCurrentPlayTime();
            boolean z = Float.compare(1.0f, valueAnimator.getAnimatedFraction()) == 0;
            if (!this.mHandlingOnAnimationUpdate && FirstFrameAnimatorHelper.this.mRootView != null && FirstFrameAnimatorHelper.this.mRootView.getWindowVisibility() == 0 && currentPlayTime < valueAnimator.getDuration() && !z) {
                this.mHandlingOnAnimationUpdate = true;
                long access$100 = FirstFrameAnimatorHelper.this.mGlobalFrameCount - this.mStartFrame;
                if (access$100 != 0 || currentTimeMillis >= this.mStartTime + 1000 || currentPlayTime <= 0) {
                    int singleFrameMs = RefreshRateTracker.getSingleFrameMs(FirstFrameAnimatorHelper.this.mRootView.getContext());
                    int i = (access$100 > 1 ? 1 : (access$100 == 1 ? 0 : -1));
                    if (i == 0) {
                        long j = this.mStartTime;
                        if (currentTimeMillis < 1000 + j && !this.mAdjustedSecondFrameTime) {
                            long j2 = (long) singleFrameMs;
                            if (currentTimeMillis > j + j2 && currentPlayTime > j2) {
                                valueAnimator2.setCurrentPlayTime(j2);
                                this.mAdjustedSecondFrameTime = true;
                            }
                        }
                    }
                    if (i > 0) {
                        FirstFrameAnimatorHelper.this.mRootView.post(new Runnable(valueAnimator2) {
                            public final /* synthetic */ ValueAnimator f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                FirstFrameAnimatorHelper.MyListener.this.lambda$onAnimationUpdate$0$FirstFrameAnimatorHelper$MyListener(this.f$1);
                            }
                        });
                    }
                } else {
                    FirstFrameAnimatorHelper.this.mRootView.invalidate();
                    valueAnimator2.setCurrentPlayTime(0);
                }
                this.mHandlingOnAnimationUpdate = false;
            }
        }

        public /* synthetic */ void lambda$onAnimationUpdate$0$FirstFrameAnimatorHelper$MyListener(ValueAnimator valueAnimator) {
            valueAnimator.removeUpdateListener(this);
        }

        public void print(ValueAnimator valueAnimator) {
            Log.d(FirstFrameAnimatorHelper.TAG, FirstFrameAnimatorHelper.this.mGlobalFrameCount + "(" + (FirstFrameAnimatorHelper.this.mGlobalFrameCount - this.mStartFrame) + ") " + FirstFrameAnimatorHelper.this.mRootView + " dirty? " + FirstFrameAnimatorHelper.this.mRootView.isDirty() + " " + (((float) valueAnimator.getCurrentPlayTime()) / ((float) valueAnimator.getDuration())) + " " + this + " " + valueAnimator);
        }
    }
}
