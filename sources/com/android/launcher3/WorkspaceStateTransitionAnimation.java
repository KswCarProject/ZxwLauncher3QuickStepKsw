package com.android.launcher3;

import android.animation.ValueAnimator;
import android.util.FloatProperty;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.anim.SpringAnimationBuilder;
import com.android.launcher3.graphics.Scrim;
import com.android.launcher3.graphics.SysUiScrim;
import com.android.launcher3.states.SpringLoadedState;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.util.DynamicResource;
import com.android.systemui.plugins.ResourceProvider;

public class WorkspaceStateTransitionAnimation {
    private static final FloatProperty<Hotseat> HOTSEAT_SCALE_PROPERTY = LauncherAnimUtils.HOTSEAT_SCALE_PROPERTY_FACTORY.get(3);
    private static final FloatProperty<Workspace<?>> WORKSPACE_SCALE_PROPERTY = LauncherAnimUtils.WORKSPACE_SCALE_PROPERTY_FACTORY.get(3);
    private final Launcher mLauncher;
    private float mNewScale;
    private final Workspace<?> mWorkspace;

    public WorkspaceStateTransitionAnimation(Launcher launcher, Workspace<?> workspace) {
        this.mLauncher = launcher;
        this.mWorkspace = workspace;
    }

    public void setState(LauncherState launcherState) {
        setWorkspaceProperty(launcherState, PropertySetter.NO_ANIM_PROPERTY_SETTER, new StateAnimationConfig());
    }

    public void setStateWithAnimation(LauncherState launcherState, StateAnimationConfig stateAnimationConfig, PendingAnimation pendingAnimation) {
        setWorkspaceProperty(launcherState, pendingAnimation, stateAnimationConfig);
    }

    public float getFinalScale() {
        return this.mNewScale;
    }

    private void setWorkspaceProperty(LauncherState launcherState, PropertySetter propertySetter, StateAnimationConfig stateAnimationConfig) {
        int i;
        LauncherState launcherState2 = launcherState;
        PropertySetter propertySetter2 = propertySetter;
        StateAnimationConfig stateAnimationConfig2 = stateAnimationConfig;
        LauncherState.ScaleAndTranslation workspaceScaleAndTranslation = launcherState2.getWorkspaceScaleAndTranslation(this.mLauncher);
        LauncherState.ScaleAndTranslation hotseatScaleAndTranslation = launcherState2.getHotseatScaleAndTranslation(this.mLauncher);
        this.mNewScale = workspaceScaleAndTranslation.scale;
        LauncherState.PageAlphaProvider workspacePageAlphaProvider = launcherState2.getWorkspacePageAlphaProvider(this.mLauncher);
        int childCount = this.mWorkspace.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            applyChildState(launcherState, (CellLayout) this.mWorkspace.getChildAt(i2), i2, workspacePageAlphaProvider, propertySetter, stateAnimationConfig);
        }
        int visibleElements = launcherState2.getVisibleElements(this.mLauncher);
        Hotseat hotseat = this.mWorkspace.getHotseat();
        Interpolator interpolator = stateAnimationConfig2.getInterpolator(1, Interpolators.ZOOM_OUT);
        boolean z = (propertySetter2 instanceof PendingAnimation) && this.mLauncher.getStateManager().getState() == LauncherState.HINT_STATE && launcherState2 == LauncherState.NORMAL;
        if (z) {
            i = childCount;
            ((PendingAnimation) propertySetter2).add(getSpringScaleAnimator(this.mLauncher, this.mWorkspace, this.mNewScale, WORKSPACE_SCALE_PROPERTY));
        } else {
            i = childCount;
            propertySetter2.setFloat(this.mWorkspace, WORKSPACE_SCALE_PROPERTY, this.mNewScale, interpolator);
        }
        this.mWorkspace.setPivotToScaleWithSelf(hotseat);
        float f = hotseatScaleAndTranslation.scale;
        int i3 = 4;
        if (z) {
            ((PendingAnimation) propertySetter2).add(getSpringScaleAnimator(this.mLauncher, hotseat, f, HOTSEAT_SCALE_PROPERTY));
        } else {
            propertySetter2.setFloat(hotseat, HOTSEAT_SCALE_PROPERTY, f, stateAnimationConfig2.getInterpolator(4, interpolator));
        }
        Interpolator interpolator2 = stateAnimationConfig2.getInterpolator(3, workspacePageAlphaProvider.interpolator);
        float f2 = 1.0f;
        propertySetter2.setViewAlpha(this.mLauncher.getWorkspace().getPageIndicator(), (visibleElements & 32) != 0 ? 1.0f : 0.0f, interpolator2);
        Interpolator interpolator3 = stateAnimationConfig2.getInterpolator(16, interpolator2);
        if ((visibleElements & 1) == 0) {
            f2 = 0.0f;
        }
        propertySetter2.setViewAlpha(hotseat, f2, interpolator3);
        if (!launcherState2.hasFlag(LauncherState.FLAG_HOTSEAT_INACCESSIBLE)) {
            i3 = 0;
        }
        hotseat.setImportantForAccessibility(i3);
        Interpolator interpolator4 = stateAnimationConfig2.getInterpolator(2, Interpolators.ZOOM_OUT);
        propertySetter2.setFloat(this.mWorkspace, LauncherAnimUtils.VIEW_TRANSLATE_X, workspaceScaleAndTranslation.translationX, interpolator4);
        propertySetter2.setFloat(this.mWorkspace, LauncherAnimUtils.VIEW_TRANSLATE_Y, workspaceScaleAndTranslation.translationY, interpolator4);
        LauncherState.PageTranslationProvider workspacePageTranslationProvider = launcherState2.getWorkspacePageTranslationProvider(this.mLauncher);
        int i4 = i;
        for (int i5 = 0; i5 < i4; i5++) {
            applyPageTranslation((CellLayout) this.mWorkspace.getChildAt(i5), i5, workspacePageTranslationProvider, propertySetter, stateAnimationConfig);
        }
        Interpolator interpolator5 = stateAnimationConfig2.getInterpolator(5, interpolator4);
        propertySetter2.setFloat(hotseat, LauncherAnimUtils.VIEW_TRANSLATE_Y, hotseatScaleAndTranslation.translationY, interpolator5);
        propertySetter2.setFloat(this.mWorkspace.getPageIndicator(), LauncherAnimUtils.VIEW_TRANSLATE_Y, hotseatScaleAndTranslation.translationY, interpolator5);
        if (!stateAnimationConfig2.hasAnimationFlag(8)) {
            setScrim(propertySetter2, launcherState2, stateAnimationConfig2);
        }
    }

    public void setScrim(PropertySetter propertySetter, LauncherState launcherState, StateAnimationConfig stateAnimationConfig) {
        propertySetter.setFloat(this.mLauncher.getDragLayer().getWorkspaceDragScrim(), Scrim.SCRIM_PROGRESS, launcherState.getWorkspaceBackgroundAlpha(this.mLauncher), Interpolators.LINEAR);
        propertySetter.setFloat(this.mLauncher.getRootView().getSysUiScrim(), SysUiScrim.SYSUI_PROGRESS, launcherState.hasFlag(LauncherState.FLAG_HAS_SYS_UI_SCRIM) ? 1.0f : 0.0f, Interpolators.LINEAR);
        propertySetter.setViewBackgroundColor(this.mLauncher.getScrimView(), launcherState.getWorkspaceScrimColor(this.mLauncher), stateAnimationConfig.getInterpolator(11, Interpolators.ACCEL_2));
    }

    public void applyChildState(LauncherState launcherState, CellLayout cellLayout, int i) {
        applyChildState(launcherState, cellLayout, i, launcherState.getWorkspacePageAlphaProvider(this.mLauncher), PropertySetter.NO_ANIM_PROPERTY_SETTER, new StateAnimationConfig());
    }

    private void applyChildState(LauncherState launcherState, CellLayout cellLayout, int i, LauncherState.PageAlphaProvider pageAlphaProvider, PropertySetter propertySetter, StateAnimationConfig stateAnimationConfig) {
        float pageAlpha = pageAlphaProvider.getPageAlpha(i);
        propertySetter.setFloat(cellLayout, CellLayout.SPRING_LOADED_PROGRESS, launcherState instanceof SpringLoadedState ? 1.0f : 0.0f, Interpolators.ZOOM_OUT);
        propertySetter.setFloat(cellLayout.getShortcutsAndWidgets(), LauncherAnimUtils.VIEW_ALPHA, pageAlpha, stateAnimationConfig.getInterpolator(3, pageAlphaProvider.interpolator));
    }

    private void applyPageTranslation(CellLayout cellLayout, int i, LauncherState.PageTranslationProvider pageTranslationProvider, PropertySetter propertySetter, StateAnimationConfig stateAnimationConfig) {
        propertySetter.setFloat(cellLayout, LauncherAnimUtils.VIEW_TRANSLATE_X, pageTranslationProvider.getPageTranslation(i), stateAnimationConfig.getInterpolator(15, pageTranslationProvider.interpolator));
    }

    public static ValueAnimator getWorkspaceSpringScaleAnimator(Launcher launcher, Workspace<?> workspace, float f) {
        return getSpringScaleAnimator(launcher, workspace, f, WORKSPACE_SCALE_PROPERTY);
    }

    public static <T extends View> ValueAnimator getSpringScaleAnimator(Launcher launcher, T t, float f, FloatProperty<T> floatProperty) {
        ResourceProvider provider = DynamicResource.provider(launcher);
        float f2 = provider.getFloat(R.dimen.hint_scale_damping_ratio);
        float f3 = provider.getFloat(R.dimen.hint_scale_stiffness);
        return new SpringAnimationBuilder(t.getContext()).setStiffness(f3).setDampingRatio(f2).setMinimumVisibleChange(0.002f).setEndValue(f).setStartValue(((Float) floatProperty.get(t)).floatValue()).setStartVelocity(provider.getDimension(R.dimen.hint_scale_velocity_dp_per_s)).build(t, floatProperty);
    }
}
