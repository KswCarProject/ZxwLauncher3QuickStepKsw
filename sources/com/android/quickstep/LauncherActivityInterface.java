package com.android.quickstep;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherInitListener;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.taskbar.LauncherTaskbarUIController;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.Executors;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.GestureState;
import com.android.quickstep.util.ActivityInitListener;
import com.android.quickstep.util.AnimatorControllerWithResistance;
import com.android.quickstep.util.LayoutUtils;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.plugins.shared.LauncherOverlayManager;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class LauncherActivityInterface extends BaseActivityInterface<LauncherState, BaseQuickstepLauncher> {
    public static final LauncherActivityInterface INSTANCE = new LauncherActivityInterface();

    public boolean allowMinimizeSplitScreen() {
        return true;
    }

    public Rect getOverviewWindowBounds(Rect rect, RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        return rect;
    }

    private LauncherActivityInterface() {
        super(true, LauncherState.OVERVIEW, LauncherState.BACKGROUND_APP);
    }

    public int getSwipeUpDestinationAndLength(DeviceProfile deviceProfile, Context context, Rect rect, PagedOrientationHandler pagedOrientationHandler) {
        calculateTaskSize(context, deviceProfile, rect);
        if (!deviceProfile.isVerticalBarLayout() || DisplayController.getNavigationMode(context) == DisplayController.NavigationMode.NO_BUTTON) {
            return LayoutUtils.getShelfTrackingDistance(context, deviceProfile, pagedOrientationHandler);
        }
        return deviceProfile.isSeascape() ? rect.left : deviceProfile.widthPx - rect.right;
    }

    public void onSwipeUpToHomeComplete(RecentsAnimationDeviceState recentsAnimationDeviceState) {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity != null) {
            Handler handler = Executors.MAIN_EXECUTOR.getHandler();
            StateManager<LauncherState> stateManager = createdActivity.getStateManager();
            Objects.requireNonNull(stateManager);
            handler.post(new Runnable() {
                public final void run() {
                    StateManager.this.reapplyState();
                }
            });
            createdActivity.getRootView().setForceHideBackArrow(false);
            notifyRecentsOfOrientation(recentsAnimationDeviceState.getRotationTouchHelper());
        }
    }

    public void onAssistantVisibilityChanged(float f) {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity != null) {
            createdActivity.onAssistantVisibilityChanged(f);
        }
    }

    public void onOneHandedModeStateChanged(boolean z) {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity != null) {
            createdActivity.onOneHandedStateChanged(z);
        }
    }

    public BaseActivityInterface.AnimationFactory prepareRecentsUI(RecentsAnimationDeviceState recentsAnimationDeviceState, boolean z, Consumer<AnimatorControllerWithResistance> consumer) {
        notifyRecentsOfOrientation(recentsAnimationDeviceState.getRotationTouchHelper());
        AnonymousClass1 r1 = new BaseActivityInterface<LauncherState, BaseQuickstepLauncher>.DefaultAnimationFactory(consumer) {
            /* access modifiers changed from: protected */
            public void createBackgroundToOverviewAnim(BaseQuickstepLauncher baseQuickstepLauncher, PendingAnimation pendingAnimation) {
                super.createBackgroundToOverviewAnim(baseQuickstepLauncher, pendingAnimation);
                float depth = LauncherState.BACKGROUND_APP.getDepth(baseQuickstepLauncher);
                float depth2 = LauncherState.OVERVIEW.getDepth(baseQuickstepLauncher);
                pendingAnimation.addFloat(LauncherActivityInterface.this.getDepthController(), new DepthController.ClampedDepthProperty(depth, depth2), depth, depth2, Interpolators.LINEAR);
            }
        };
        ((BaseQuickstepLauncher) r1.initBackgroundStateUI()).getAppsView().reset(false);
        return r1;
    }

    public ActivityInitListener createActivityInitListener(Predicate<Boolean> predicate) {
        return new LauncherInitListener(new BiPredicate(predicate) {
            public final /* synthetic */ Predicate f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj, Object obj2) {
                return this.f$0.test((Boolean) obj2);
            }
        });
    }

    public void setOnDeferredActivityLaunchCallback(Runnable runnable) {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity != null) {
            createdActivity.setOnDeferredActivityLaunchCallback(runnable);
        }
    }

    public BaseQuickstepLauncher getCreatedActivity() {
        return (BaseQuickstepLauncher) BaseQuickstepLauncher.ACTIVITY_TRACKER.getCreatedActivity();
    }

    public DepthController getDepthController() {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity == null) {
            return null;
        }
        return createdActivity.getDepthController();
    }

    public LauncherTaskbarUIController getTaskbarController() {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity == null) {
            return null;
        }
        return createdActivity.getTaskbarUIController();
    }

    public RecentsView getVisibleRecentsView() {
        Launcher visibleLauncher = getVisibleLauncher();
        RecentsView recentsView = (visibleLauncher == null || !visibleLauncher.getStateManager().getState().overviewUi) ? null : (RecentsView) visibleLauncher.getOverviewPanel();
        if (recentsView == null || (!visibleLauncher.hasBeenResumed() && recentsView.getRunningTaskViewId() == -1)) {
            return null;
        }
        return recentsView;
    }

    private Launcher getVisibleLauncher() {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity == null || !createdActivity.isStarted() || ((!FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() || !isInLiveTileMode()) && !createdActivity.hasBeenResumed())) {
            return null;
        }
        return createdActivity;
    }

    public boolean switchToRecentsIfVisible(Runnable runnable) {
        Animator.AnimatorListener animatorListener;
        Launcher visibleLauncher = getVisibleLauncher();
        if (visibleLauncher == null) {
            return false;
        }
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && isInLiveTileMode() && getVisibleRecentsView() == null) {
            return false;
        }
        closeOverlay();
        StateManager<LauncherState> stateManager = visibleLauncher.getStateManager();
        LauncherState launcherState = LauncherState.OVERVIEW;
        boolean shouldAnimateStateChange = visibleLauncher.getStateManager().shouldAnimateStateChange();
        if (runnable == null) {
            animatorListener = null;
        } else {
            animatorListener = AnimatorListeners.forEndCallback(runnable);
        }
        stateManager.goToState(launcherState, shouldAnimateStateChange, animatorListener);
        return true;
    }

    public void onExitOverview(final RotationTouchHelper rotationTouchHelper, final Runnable runnable) {
        final StateManager<LauncherState> stateManager = getCreatedActivity().getStateManager();
        stateManager.addStateListener(new StateManager.StateListener<LauncherState>() {
            public void onStateTransitionComplete(LauncherState launcherState) {
                if (launcherState == LauncherState.NORMAL) {
                    runnable.run();
                    LauncherActivityInterface.this.notifyRecentsOfOrientation(rotationTouchHelper);
                    stateManager.removeStateListener(this);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void notifyRecentsOfOrientation(RotationTouchHelper rotationTouchHelper) {
        ((RecentsView) getCreatedActivity().getOverviewPanel()).setLayoutRotation(rotationTouchHelper.getCurrentActiveRotation(), rotationTouchHelper.getDisplayRotation());
    }

    public boolean isInLiveTileMode() {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        return createdActivity != null && createdActivity.getStateManager().getState() == LauncherState.OVERVIEW && createdActivity.isStarted();
    }

    public void onLaunchTaskFailed() {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity != null) {
            createdActivity.getStateManager().goToState(LauncherState.OVERVIEW);
        }
    }

    public void closeOverlay() {
        super.closeOverlay();
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity != null) {
            LauncherOverlayManager overlayManager = createdActivity.getOverlayManager();
            if (!createdActivity.isStarted() || createdActivity.isForceInvisible()) {
                overlayManager.hideOverlay(false);
            } else {
                overlayManager.hideOverlay((int) DragView.VIEW_ZOOM_DURATION);
            }
            LauncherTaskbarUIController taskbarController = getTaskbarController();
            if (taskbarController != null) {
                taskbarController.hideEdu();
            }
        }
    }

    public Animator getParallelAnimationToLauncher(GestureState.GestureEndTarget gestureEndTarget, long j, RecentsAnimationCallbacks recentsAnimationCallbacks) {
        LauncherTaskbarUIController taskbarController = getTaskbarController();
        Animator parallelAnimationToLauncher = super.getParallelAnimationToLauncher(gestureEndTarget, j, recentsAnimationCallbacks);
        if (taskbarController == null || recentsAnimationCallbacks == null) {
            return parallelAnimationToLauncher;
        }
        Animator createAnimToLauncher = taskbarController.createAnimToLauncher(stateFromGestureEndTarget(gestureEndTarget), recentsAnimationCallbacks, j);
        if (parallelAnimationToLauncher == null) {
            return createAnimToLauncher;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{parallelAnimationToLauncher, createAnimToLauncher});
        return animatorSet;
    }

    /* access modifiers changed from: protected */
    public int getOverviewScrimColorForState(BaseQuickstepLauncher baseQuickstepLauncher, LauncherState launcherState) {
        return launcherState.getWorkspaceScrimColor(baseQuickstepLauncher);
    }

    public boolean deferStartingActivity(RecentsAnimationDeviceState recentsAnimationDeviceState, MotionEvent motionEvent) {
        LauncherTaskbarUIController taskbarController = getTaskbarController();
        if (taskbarController == null) {
            return super.deferStartingActivity(recentsAnimationDeviceState, motionEvent);
        }
        return taskbarController.isEventOverAnyTaskbarItem(motionEvent) || super.deferStartingActivity(recentsAnimationDeviceState, motionEvent);
    }

    public boolean shouldCancelCurrentGesture() {
        LauncherTaskbarUIController taskbarController = getTaskbarController();
        if (taskbarController == null) {
            return super.shouldCancelCurrentGesture();
        }
        return taskbarController.isDraggingItem();
    }

    /* renamed from: com.android.quickstep.LauncherActivityInterface$3  reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$GestureState$GestureEndTarget;

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        static {
            /*
                com.android.quickstep.GestureState$GestureEndTarget[] r0 = com.android.quickstep.GestureState.GestureEndTarget.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$quickstep$GestureState$GestureEndTarget = r0
                com.android.quickstep.GestureState$GestureEndTarget r1 = com.android.quickstep.GestureState.GestureEndTarget.RECENTS     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$android$quickstep$GestureState$GestureEndTarget     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.quickstep.GestureState$GestureEndTarget r1 = com.android.quickstep.GestureState.GestureEndTarget.NEW_TASK     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$com$android$quickstep$GestureState$GestureEndTarget     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.quickstep.GestureState$GestureEndTarget r1 = com.android.quickstep.GestureState.GestureEndTarget.LAST_TASK     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$com$android$quickstep$GestureState$GestureEndTarget     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.quickstep.GestureState$GestureEndTarget r1 = com.android.quickstep.GestureState.GestureEndTarget.HOME     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.LauncherActivityInterface.AnonymousClass3.<clinit>():void");
        }
    }

    public LauncherState stateFromGestureEndTarget(GestureState.GestureEndTarget gestureEndTarget) {
        int i = AnonymousClass3.$SwitchMap$com$android$quickstep$GestureState$GestureEndTarget[gestureEndTarget.ordinal()];
        if (i == 1) {
            return LauncherState.OVERVIEW;
        }
        if (i == 2 || i == 3) {
            return LauncherState.QUICK_SWITCH;
        }
        return LauncherState.NORMAL;
    }
}
