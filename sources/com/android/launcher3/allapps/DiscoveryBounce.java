package com.android.launcher3.allapps;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Handler;
import android.os.UserManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.util.OnboardingPrefs;
import java.util.function.Consumer;

public class DiscoveryBounce extends AbstractFloatingView {
    private static final long DELAY_MS = 450;
    private final Animator mDiscoBounceAnimation;
    private final Launcher mLauncher;
    private final StateManager.StateListener<LauncherState> mStateListener;

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 64) != 0;
    }

    public DiscoveryBounce(Launcher launcher) {
        super(launcher, (AttributeSet) null);
        AnonymousClass1 r1 = new StateManager.StateListener<LauncherState>() {
            public void onStateTransitionComplete(LauncherState launcherState) {
            }

            public void onStateTransitionStart(LauncherState launcherState) {
                DiscoveryBounce.this.handleClose(false);
            }
        };
        this.mStateListener = r1;
        this.mLauncher = launcher;
        Animator loadAnimator = AnimatorInflater.loadAnimator(launcher, R.animator.discovery_bounce);
        this.mDiscoBounceAnimation = loadAnimator;
        loadAnimator.setTarget(new VerticalProgressWrapper(launcher.getHotseat(), (float) launcher.getDragLayer().getHeight()));
        loadAnimator.addListener(AnimatorListeners.forEndCallback((Consumer<Boolean>) new Consumer() {
            public final void accept(Object obj) {
                DiscoveryBounce.this.handleClose(((Boolean) obj).booleanValue());
            }
        }));
        launcher.getStateManager().addStateListener(r1);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mDiscoBounceAnimation.start();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mDiscoBounceAnimation.isRunning()) {
            this.mDiscoBounceAnimation.end();
        }
    }

    public boolean onBackPressed() {
        super.onBackPressed();
        return false;
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        handleClose(false);
        return false;
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        if (this.mIsOpen) {
            this.mIsOpen = false;
            this.mLauncher.getDragLayer().removeView(this);
            this.mLauncher.getHotseat().setTranslationY(this.mLauncher.getStateManager().getState().getHotseatScaleAndTranslation(this.mLauncher).translationY);
            this.mLauncher.getStateManager().removeStateListener(this.mStateListener);
        }
    }

    private void show() {
        this.mIsOpen = true;
        this.mLauncher.getDragLayer().addView(this);
    }

    public static void showForHomeIfNeeded(Launcher launcher) {
        showForHomeIfNeeded(launcher, true);
    }

    /* access modifiers changed from: private */
    public static void showForHomeIfNeeded(Launcher launcher, boolean z) {
        OnboardingPrefs<? extends Launcher> onboardingPrefs = launcher.getOnboardingPrefs();
        if (launcher.isInState(LauncherState.NORMAL) && !onboardingPrefs.getBoolean(OnboardingPrefs.HOME_BOUNCE_SEEN) && AbstractFloatingView.getTopOpenView(launcher) == null && !((UserManager) launcher.getSystemService(UserManager.class)).isDemoUser() && !Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            if (z) {
                new Handler().postDelayed(new Runnable() {
                    public final void run() {
                        DiscoveryBounce.showForHomeIfNeeded(Launcher.this, false);
                    }
                }, DELAY_MS);
                return;
            }
            onboardingPrefs.incrementEventCount(OnboardingPrefs.HOME_BOUNCE_COUNT);
            new DiscoveryBounce(launcher).show();
        }
    }

    public static class VerticalProgressWrapper {
        private final float mLimit;
        private final View mView;

        private VerticalProgressWrapper(View view, float f) {
            this.mView = view;
            this.mLimit = f;
        }

        public float getProgress() {
            return (this.mView.getTranslationY() / this.mLimit) + 1.0f;
        }

        public void setProgress(float f) {
            this.mView.setTranslationY(this.mLimit * (f - 1.0f));
        }
    }
}
