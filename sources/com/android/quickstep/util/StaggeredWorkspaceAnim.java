package com.android.quickstep.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Hotseat;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.anim.SpringAnimationBuilder;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.util.DynamicResource;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.plugins.ResourceProvider;
import java.util.Objects;
import java.util.function.Consumer;

public class StaggeredWorkspaceAnim {
    private static final int ALPHA_DURATION_MS = 250;
    private static final int APP_CLOSE_ROW_START_DELAY_MS = 10;
    public static final int DURATION_MS = 250;
    private static final float MAX_VELOCITY_PX_PER_S = 22.0f;
    private final AnimatorSet mAnimators;
    private final View mIgnoredView;
    private final float mSpringTransY;
    private final float mVelocity;

    public StaggeredWorkspaceAnim(Launcher launcher, float f, boolean z, View view) {
        this(launcher, f, z, view, true);
    }

    public StaggeredWorkspaceAnim(Launcher launcher, float f, boolean z, View view, boolean z2) {
        int i;
        int i2;
        Launcher launcher2 = launcher;
        boolean z3 = z;
        this.mAnimators = new AnimatorSet();
        prepareToAnimate(launcher2, z3);
        this.mIgnoredView = view;
        this.mVelocity = f;
        this.mSpringTransY = (((Math.abs(f) * 0.9f) / MAX_VELOCITY_PX_PER_S) + 0.2f) * ((float) launcher.getResources().getDimensionPixelSize(R.dimen.swipe_up_max_workspace_trans_y));
        if (z2) {
            DeviceProfile deviceProfile = launcher.getDeviceProfile();
            final Workspace<?> workspace = launcher.getWorkspace();
            final Hotseat hotseat = launcher.getHotseat();
            int i3 = deviceProfile.inv.numRows + (deviceProfile.isVerticalBarLayout() ? 0 : 2);
            workspace.forEachVisiblePage(new Consumer(i3) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    StaggeredWorkspaceAnim.this.lambda$new$0$StaggeredWorkspaceAnim(this.f$1, (View) obj);
                }
            });
            final boolean clipChildren = workspace.getClipChildren();
            boolean clipToPadding = workspace.getClipToPadding();
            boolean clipChildren2 = hotseat.getClipChildren();
            boolean clipToPadding2 = hotseat.getClipToPadding();
            workspace.setClipChildren(false);
            workspace.setClipToPadding(false);
            hotseat.setClipChildren(false);
            hotseat.setClipToPadding(false);
            ShortcutAndWidgetContainer shortcutsAndWidgets = hotseat.getShortcutsAndWidgets();
            if (deviceProfile.isVerticalBarLayout()) {
                for (int childCount = shortcutsAndWidgets.getChildCount() - 1; childCount >= 0; childCount--) {
                    View childAt = shortcutsAndWidgets.getChildAt(childCount);
                    addStaggeredAnimationForView(childAt, ((CellLayout.LayoutParams) childAt.getLayoutParams()).cellY + 1, i3);
                }
            } else {
                if (deviceProfile.isTaskbarPresent) {
                    i = deviceProfile.inv.numRows + 1;
                    i2 = deviceProfile.inv.numRows + 2;
                } else {
                    int i4 = deviceProfile.inv.numRows + 1;
                    i = deviceProfile.inv.numRows + 2;
                    i2 = i4;
                }
                for (int childCount2 = shortcutsAndWidgets.getChildCount() - 1; childCount2 >= 0; childCount2--) {
                    addStaggeredAnimationForView(shortcutsAndWidgets.getChildAt(childCount2), i2, i3);
                }
                addStaggeredAnimationForView(hotseat.getQsb(), i, i3);
            }
            final boolean z4 = clipToPadding;
            final boolean z5 = clipChildren2;
            final boolean z6 = clipToPadding2;
            this.mAnimators.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    workspace.setClipChildren(clipChildren);
                    workspace.setClipToPadding(z4);
                    hotseat.setClipChildren(z5);
                    hotseat.setClipToPadding(z6);
                }
            });
        }
        launcher.pauseExpensiveViewUpdates();
        AnimatorSet animatorSet = this.mAnimators;
        Objects.requireNonNull(launcher);
        animatorSet.addListener(AnimatorListeners.forEndCallback((Runnable) new Runnable() {
            public final void run() {
                Launcher.this.resumeExpensiveViewUpdates();
            }
        }));
        if (z3) {
            PendingAnimation pendingAnimation = new PendingAnimation(250);
            launcher.getWorkspace().getStateTransitionAnimation().setScrim(pendingAnimation, LauncherState.NORMAL, new StateAnimationConfig());
            this.mAnimators.play(pendingAnimation.buildAnim());
        }
        addDepthAnimationForState(launcher2, LauncherState.NORMAL, 250);
        this.mAnimators.play(launcher.getRootView().getSysUiScrim().createSysuiMultiplierAnim(0.0f, 1.0f).setDuration(250));
    }

    /* access modifiers changed from: private */
    /* renamed from: addAnimationForPage */
    public void lambda$new$0$StaggeredWorkspaceAnim(final CellLayout cellLayout, int i) {
        ShortcutAndWidgetContainer shortcutsAndWidgets = cellLayout.getShortcutsAndWidgets();
        final boolean clipChildren = cellLayout.getClipChildren();
        final boolean clipToPadding = cellLayout.getClipToPadding();
        cellLayout.setClipChildren(false);
        cellLayout.setClipToPadding(false);
        for (int childCount = shortcutsAndWidgets.getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = shortcutsAndWidgets.getChildAt(childCount);
            CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) childAt.getLayoutParams();
            addStaggeredAnimationForView(childAt, layoutParams.cellY + layoutParams.cellVSpan, i);
        }
        this.mAnimators.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                cellLayout.setClipChildren(clipChildren);
                cellLayout.setClipToPadding(clipToPadding);
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

    public StaggeredWorkspaceAnim addAnimatorListener(Animator.AnimatorListener animatorListener) {
        this.mAnimators.addListener(animatorListener);
        return this;
    }

    public void start() {
        this.mAnimators.start();
    }

    private void addStaggeredAnimationForView(final View view, int i, int i2) {
        View view2 = this.mIgnoredView;
        if (view2 == null || view2 != view) {
            long j = (long) (((i2 - i) + 1) * 10);
            view.setTranslationY(this.mSpringTransY);
            ResourceProvider provider = DynamicResource.provider(view.getContext());
            float f = provider.getFloat(R.dimen.staggered_stiffness);
            float f2 = provider.getFloat(R.dimen.staggered_damping_ratio);
            ValueAnimator build = new SpringAnimationBuilder(view.getContext()).setStiffness(f).setDampingRatio(f2).setMinimumVisibleChange(1.0f).setStartValue(this.mSpringTransY).setEndValue(0.0f).setStartVelocity(Math.abs(this.mVelocity) * Math.signum(0.0f - this.mSpringTransY)).build(view, LauncherAnimUtils.VIEW_TRANSLATE_Y);
            build.setStartDelay(j);
            build.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    view.setTranslationY(0.0f);
                }
            });
            this.mAnimators.play(build);
            view.setAlpha(0.0f);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f, 1.0f});
            ofFloat.setInterpolator(Interpolators.LINEAR);
            ofFloat.setDuration(250);
            ofFloat.setStartDelay(j);
            ofFloat.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    view.setAlpha(1.0f);
                }
            });
            this.mAnimators.play(ofFloat);
        }
    }

    private void addDepthAnimationForState(Launcher launcher, LauncherState launcherState, long j) {
        if (launcher instanceof BaseQuickstepLauncher) {
            PendingAnimation pendingAnimation = new PendingAnimation(j);
            ((BaseQuickstepLauncher) launcher).getDepthController().setStateWithAnimation(launcherState, new StateAnimationConfig(), pendingAnimation);
            this.mAnimators.play(pendingAnimation.buildAnim());
        }
    }
}
