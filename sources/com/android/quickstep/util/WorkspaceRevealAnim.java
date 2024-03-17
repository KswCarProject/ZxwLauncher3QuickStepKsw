package com.android.quickstep.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.FloatProperty;
import android.view.View;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.Hotseat;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.util.DynamicResource;
import com.android.quickstep.views.RecentsView;

public class WorkspaceRevealAnim {
    public static final int DURATION_MS = 350;
    private static final FloatProperty<Hotseat> HOTSEAT_SCALE_PROPERTY = LauncherAnimUtils.HOTSEAT_SCALE_PROPERTY_FACTORY.get(4);
    private static final FloatProperty<Workspace<?>> WORKSPACE_SCALE_PROPERTY = LauncherAnimUtils.WORKSPACE_SCALE_PROPERTY_FACTORY.get(4);
    private final AnimatorSet mAnimators;
    private final float mScaleStart;

    public WorkspaceRevealAnim(Launcher launcher, boolean z) {
        AnimatorSet animatorSet = new AnimatorSet();
        this.mAnimators = animatorSet;
        prepareToAnimate(launcher, z);
        this.mScaleStart = DynamicResource.provider(launcher).getFloat(R.dimen.swipe_up_scale_start);
        Workspace<?> workspace = launcher.getWorkspace();
        workspace.setPivotToScaleWithSelf(launcher.getHotseat());
        addRevealAnimatorsForView(workspace, WORKSPACE_SCALE_PROPERTY);
        addRevealAnimatorsForView(launcher.getHotseat(), HOTSEAT_SCALE_PROPERTY);
        if (z) {
            PendingAnimation pendingAnimation = new PendingAnimation(350);
            launcher.getWorkspace().getStateTransitionAnimation().setScrim(pendingAnimation, LauncherState.NORMAL, new StateAnimationConfig());
            animatorSet.play(pendingAnimation.buildAnim());
        }
        if (launcher instanceof BaseQuickstepLauncher) {
            PendingAnimation pendingAnimation2 = new PendingAnimation(350);
            ((BaseQuickstepLauncher) launcher).getDepthController().setStateWithAnimation(LauncherState.NORMAL, new StateAnimationConfig(), pendingAnimation2);
            animatorSet.play(pendingAnimation2.buildAnim());
        }
        animatorSet.play(launcher.getRootView().getSysUiScrim().createSysuiMultiplierAnim(0.0f, 1.0f));
        animatorSet.setDuration(350);
        animatorSet.setInterpolator(Interpolators.DECELERATED_EASE);
    }

    private <T extends View> void addRevealAnimatorsForView(final T t, final FloatProperty<T> floatProperty) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(t, floatProperty, new float[]{this.mScaleStart, 1.0f});
        ofFloat.setDuration(350);
        ofFloat.setInterpolator(Interpolators.DECELERATED_EASE);
        this.mAnimators.play(ofFloat);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(t, View.ALPHA, new float[]{0.0f, 1.0f});
        ofFloat2.setDuration(350);
        ofFloat2.setInterpolator(Interpolators.DECELERATED_EASE);
        this.mAnimators.play(ofFloat2);
        this.mAnimators.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                floatProperty.set(t, Float.valueOf(1.0f));
                t.setAlpha(1.0f);
            }
        });
    }

    private void prepareToAnimate(Launcher launcher, boolean z) {
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        stateAnimationConfig.animFlags = 14;
        stateAnimationConfig.duration = 0;
        launcher.getStateManager().createAtomicAnimation(LauncherState.BACKGROUND_APP, LauncherState.NORMAL, stateAnimationConfig).start();
        ((RecentsView) launcher.getOverviewPanel()).forceFinishScroller();
        if (z) {
            launcher.getWorkspace().getStateTransitionAnimation().setScrim(PropertySetter.NO_ANIM_PROPERTY_SETTER, LauncherState.BACKGROUND_APP, stateAnimationConfig);
        }
    }

    public AnimatorSet getAnimators() {
        return this.mAnimators;
    }

    public WorkspaceRevealAnim addAnimatorListener(Animator.AnimatorListener animatorListener) {
        this.mAnimators.addListener(animatorListener);
        return this;
    }

    public void start() {
        this.mAnimators.start();
    }
}
