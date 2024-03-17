package com.android.launcher3.allapps;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.util.FloatProperty;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.util.MultiAdditivePropertyFactory;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.launcher3.util.UiThreadHelper;
import com.android.launcher3.views.ScrimView;
import java.util.function.Consumer;

public class AllAppsTransitionController implements StateManager.StateHandler<LauncherState>, DeviceProfile.OnDeviceProfileChangeListener {
    public static final FloatProperty<AllAppsTransitionController> ALL_APPS_PROGRESS = new FloatProperty<AllAppsTransitionController>("allAppsProgress") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public Float get(AllAppsTransitionController allAppsTransitionController) {
            return Float.valueOf(allAppsTransitionController.mProgress);
        }

        public void setValue(AllAppsTransitionController allAppsTransitionController, float f) {
            allAppsTransitionController.setProgress(f);
        }
    };
    public static final FloatProperty<AllAppsTransitionController> ALL_APPS_PULL_BACK_ALPHA = new FloatProperty<AllAppsTransitionController>("allAppsPullBackAlpha") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public Float get(AllAppsTransitionController allAppsTransitionController) {
            if (allAppsTransitionController.mIsTablet) {
                return Float.valueOf(allAppsTransitionController.mAppsView.getActiveRecyclerView().getAlpha());
            }
            return Float.valueOf(allAppsTransitionController.getAppsViewPullbackAlpha().getValue());
        }

        public void setValue(AllAppsTransitionController allAppsTransitionController, float f) {
            if (allAppsTransitionController.mIsTablet) {
                allAppsTransitionController.mAppsView.getActiveRecyclerView().setAlpha(f);
            } else {
                allAppsTransitionController.getAppsViewPullbackAlpha().setValue(f);
            }
        }
    };
    public static final FloatProperty<AllAppsTransitionController> ALL_APPS_PULL_BACK_TRANSLATION = new FloatProperty<AllAppsTransitionController>("allAppsPullBackTranslation") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public Float get(AllAppsTransitionController allAppsTransitionController) {
            if (allAppsTransitionController.mIsTablet) {
                return Float.valueOf(allAppsTransitionController.mAppsView.getActiveRecyclerView().getTranslationY());
            }
            return (Float) allAppsTransitionController.getAppsViewPullbackTranslationY().get(allAppsTransitionController.mAppsView);
        }

        public void setValue(AllAppsTransitionController allAppsTransitionController, float f) {
            if (allAppsTransitionController.mIsTablet) {
                allAppsTransitionController.mAppsView.getActiveRecyclerView().setTranslationY(f);
            } else {
                allAppsTransitionController.getAppsViewPullbackTranslationY().set(allAppsTransitionController.mAppsView, Float.valueOf(f));
            }
        }
    };
    private static final int APPS_VIEW_INDEX_COUNT = 2;
    private static final int INDEX_APPS_VIEW_PROGRESS = 0;
    private static final int INDEX_APPS_VIEW_PULLBACK = 1;
    public static final float INTERP_COEFF = 1.7f;
    /* access modifiers changed from: private */
    public ActivityAllAppsContainerView<Launcher> mAppsView;
    private MultiValueAlpha mAppsViewAlpha;
    private final MultiAdditivePropertyFactory<View> mAppsViewTranslationYPropertyFactory = new MultiAdditivePropertyFactory<>("appsViewTranslationY", View.TRANSLATION_Y);
    /* access modifiers changed from: private */
    public boolean mIsTablet;
    private boolean mIsVerticalLayout;
    private final Launcher mLauncher;
    /* access modifiers changed from: private */
    public float mProgress;
    private ScrimView mScrimView;
    private float mShiftRange;

    public AllAppsTransitionController(Launcher launcher) {
        this.mLauncher = launcher;
        DeviceProfile deviceProfile = launcher.getDeviceProfile();
        setShiftRange((float) deviceProfile.allAppsShiftRange);
        this.mProgress = 1.0f;
        this.mIsVerticalLayout = deviceProfile.isVerticalBarLayout();
        this.mIsTablet = deviceProfile.isTablet;
        launcher.addOnDeviceProfileChangeListener(this);
    }

    public float getShiftRange() {
        return this.mShiftRange;
    }

    public void onDeviceProfileChanged(DeviceProfile deviceProfile) {
        this.mIsVerticalLayout = deviceProfile.isVerticalBarLayout();
        setShiftRange((float) deviceProfile.allAppsShiftRange);
        if (this.mIsVerticalLayout) {
            this.mLauncher.getHotseat().setTranslationY(0.0f);
            this.mLauncher.getWorkspace().getPageIndicator().setTranslationY(0.0f);
        }
        this.mIsTablet = deviceProfile.isTablet;
    }

    public void setProgress(float f) {
        this.mProgress = f;
        getAppsViewProgressTranslationY().set(this.mAppsView, Float.valueOf(this.mProgress * this.mShiftRange));
        this.mLauncher.onAllAppsTransition(1.0f - f);
    }

    public float getProgress() {
        return this.mProgress;
    }

    private FloatProperty<View> getAppsViewProgressTranslationY() {
        return this.mAppsViewTranslationYPropertyFactory.get(0);
    }

    /* access modifiers changed from: private */
    public FloatProperty<View> getAppsViewPullbackTranslationY() {
        return this.mAppsViewTranslationYPropertyFactory.get(1);
    }

    private MultiValueAlpha.AlphaProperty getAppsViewProgressAlpha() {
        return this.mAppsViewAlpha.getProperty(0);
    }

    /* access modifiers changed from: private */
    public MultiValueAlpha.AlphaProperty getAppsViewPullbackAlpha() {
        return this.mAppsViewAlpha.getProperty(1);
    }

    public void setState(LauncherState launcherState) {
        setProgress(launcherState.getVerticalProgress(this.mLauncher));
        setAlphas(launcherState, new StateAnimationConfig(), PropertySetter.NO_ANIM_PROPERTY_SETTER);
        onProgressAnimationEnd();
    }

    public void setStateWithAnimation(LauncherState launcherState, StateAnimationConfig stateAnimationConfig, PendingAnimation pendingAnimation) {
        if (LauncherState.NORMAL.equals(launcherState) && this.mLauncher.isInState(LauncherState.ALL_APPS)) {
            Launcher launcher = this.mLauncher;
            UiThreadHelper.hideKeyboardAsync(launcher, launcher.getAppsView().getWindowToken());
            pendingAnimation.addEndListener(new Consumer() {
                public final void accept(Object obj) {
                    AllAppsTransitionController.this.lambda$setStateWithAnimation$0$AllAppsTransitionController((Boolean) obj);
                }
            });
        }
        float verticalProgress = launcherState.getVerticalProgress(this.mLauncher);
        if (Float.compare(this.mProgress, verticalProgress) == 0) {
            setAlphas(launcherState, stateAnimationConfig, pendingAnimation);
            onProgressAnimationEnd();
            return;
        }
        Interpolator interpolator = stateAnimationConfig.getInterpolator(0, stateAnimationConfig.userControlled ? Interpolators.LINEAR : Interpolators.DEACCEL_1_7);
        Animator createSpringAnimation = createSpringAnimation(this.mProgress, verticalProgress);
        createSpringAnimation.setInterpolator(interpolator);
        createSpringAnimation.addListener(getProgressAnimatorListener());
        pendingAnimation.add(createSpringAnimation);
        setAlphas(launcherState, stateAnimationConfig, pendingAnimation);
        if (LauncherState.ALL_APPS.equals(launcherState) && this.mLauncher.isInState(LauncherState.NORMAL)) {
            this.mLauncher.getAppsView().performHapticFeedback(1, 1);
        }
    }

    public /* synthetic */ void lambda$setStateWithAnimation$0$AllAppsTransitionController(Boolean bool) {
        ALL_APPS_PULL_BACK_TRANSLATION.set(this, Float.valueOf(0.0f));
        ALL_APPS_PULL_BACK_ALPHA.set(this, Float.valueOf(1.0f));
    }

    public Animator createSpringAnimation(float... fArr) {
        return ObjectAnimator.ofFloat(this, ALL_APPS_PROGRESS, fArr);
    }

    public void setAlphas(LauncherState launcherState, StateAnimationConfig stateAnimationConfig, PropertySetter propertySetter) {
        boolean z = true;
        propertySetter.setFloat(getAppsViewProgressAlpha(), MultiValueAlpha.VALUE, (launcherState.getVisibleElements(this.mLauncher) & 2) != 0 ? 1.0f : 0.0f, stateAnimationConfig.getInterpolator(10, Interpolators.LINEAR));
        if (!(LauncherState.ALL_APPS == launcherState || this.mLauncher.getStateManager().getState() == LauncherState.ALL_APPS)) {
            z = false;
        }
        this.mScrimView.setDrawingController(z ? this.mAppsView : null);
    }

    public Animator.AnimatorListener getProgressAnimatorListener() {
        return AnimatorListeners.forSuccessCallback(new Runnable() {
            public final void run() {
                AllAppsTransitionController.this.onProgressAnimationEnd();
            }
        });
    }

    public void setupViews(ScrimView scrimView, ActivityAllAppsContainerView<Launcher> activityAllAppsContainerView) {
        this.mScrimView = scrimView;
        this.mAppsView = activityAllAppsContainerView;
        if (FeatureFlags.ENABLE_DEVICE_SEARCH.get() && Utilities.ATLEAST_R) {
            this.mLauncher.getSystemUiController().updateUiState(4, 1280);
        }
        this.mAppsView.setScrimView(scrimView);
        MultiValueAlpha multiValueAlpha = new MultiValueAlpha(this.mAppsView, 2);
        this.mAppsViewAlpha = multiValueAlpha;
        multiValueAlpha.setUpdateVisibility(true);
    }

    public void setShiftRange(float f) {
        this.mShiftRange = f;
    }

    /* access modifiers changed from: private */
    public void onProgressAnimationEnd() {
        if (!FeatureFlags.ENABLE_DEVICE_SEARCH.get() && Float.compare(this.mProgress, 1.0f) == 0) {
            this.mAppsView.reset(false);
        }
    }
}
