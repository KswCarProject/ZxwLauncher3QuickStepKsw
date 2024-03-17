package com.android.quickstep.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.util.Log;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.StateAnimationConfig;

public class OverviewToHomeAnim {
    private static final String TAG = "OverviewToHomeAnim";
    /* access modifiers changed from: private */
    public boolean mIsHomeStaggeredAnimFinished;
    /* access modifiers changed from: private */
    public boolean mIsOverviewHidden;
    private final Launcher mLauncher;
    private final Runnable mOnReachedHome;

    public OverviewToHomeAnim(Launcher launcher, Runnable runnable) {
        this.mLauncher = launcher;
        this.mOnReachedHome = runnable;
    }

    public void animateWithVelocity(float f) {
        StateManager<LauncherState> stateManager = this.mLauncher.getStateManager();
        LauncherState state = stateManager.getState();
        if (state != LauncherState.OVERVIEW) {
            Log.e(TAG, "animateFromOverviewToHome: unexpected start state " + state);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        boolean z = f < 0.0f;
        if (z) {
            WorkspaceRevealAnim workspaceRevealAnim = new WorkspaceRevealAnim(this.mLauncher, false);
            workspaceRevealAnim.addAnimatorListener(new AnimationSuccessListener() {
                public void onAnimationSuccess(Animator animator) {
                    boolean unused = OverviewToHomeAnim.this.mIsHomeStaggeredAnimFinished = true;
                    OverviewToHomeAnim.this.maybeOverviewToHomeAnimComplete();
                }
            });
            animatorSet.play(workspaceRevealAnim.getAnimators());
        } else {
            this.mIsHomeStaggeredAnimFinished = true;
        }
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        if (z) {
            stateAnimationConfig.animFlags |= 4;
        }
        stateAnimationConfig.duration = (long) state.getTransitionDuration(this.mLauncher, false);
        AnimatorSet createAtomicAnimation = stateManager.createAtomicAnimation(state, LauncherState.NORMAL, stateAnimationConfig);
        createAtomicAnimation.addListener(new AnimationSuccessListener() {
            public void onAnimationSuccess(Animator animator) {
                boolean unused = OverviewToHomeAnim.this.mIsOverviewHidden = true;
                OverviewToHomeAnim.this.maybeOverviewToHomeAnimComplete();
            }
        });
        animatorSet.play(createAtomicAnimation);
        stateManager.setCurrentAnimation(animatorSet, LauncherState.NORMAL);
        animatorSet.start();
    }

    /* access modifiers changed from: private */
    public void maybeOverviewToHomeAnimComplete() {
        if (this.mIsHomeStaggeredAnimFinished && this.mIsOverviewHidden) {
            this.mOnReachedHome.run();
        }
    }
}
