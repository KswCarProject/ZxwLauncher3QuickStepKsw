package com.android.quickstep;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.taskbar.FallbackTaskbarUIController;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.DisplayController;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.GestureState;
import com.android.quickstep.fallback.RecentsState;
import com.android.quickstep.util.ActivityInitListener;
import com.android.quickstep.util.AnimatorControllerWithResistance;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class FallbackActivityInterface extends BaseActivityInterface<RecentsState, RecentsActivity> {
    public static final FallbackActivityInterface INSTANCE = new FallbackActivityInterface();

    public boolean allowMinimizeSplitScreen() {
        return false;
    }

    public void onAssistantVisibilityChanged(float f) {
    }

    public void onOneHandedModeStateChanged(boolean z) {
    }

    public boolean switchToRecentsIfVisible(Runnable runnable) {
        return false;
    }

    private FallbackActivityInterface() {
        super(false, RecentsState.DEFAULT, RecentsState.BACKGROUND_APP);
    }

    public int getSwipeUpDestinationAndLength(DeviceProfile deviceProfile, Context context, Rect rect, PagedOrientationHandler pagedOrientationHandler) {
        calculateTaskSize(context, deviceProfile, rect);
        if (!deviceProfile.isVerticalBarLayout() || DisplayController.getNavigationMode(context) == DisplayController.NavigationMode.NO_BUTTON) {
            return deviceProfile.heightPx - rect.bottom;
        }
        return deviceProfile.isSeascape() ? rect.left : deviceProfile.widthPx - rect.right;
    }

    public BaseActivityInterface.AnimationFactory prepareRecentsUI(RecentsAnimationDeviceState recentsAnimationDeviceState, boolean z, Consumer<AnimatorControllerWithResistance> consumer) {
        notifyRecentsOfOrientation(recentsAnimationDeviceState.getRotationTouchHelper());
        BaseActivityInterface.DefaultAnimationFactory defaultAnimationFactory = new BaseActivityInterface.DefaultAnimationFactory(consumer);
        defaultAnimationFactory.initBackgroundStateUI();
        return defaultAnimationFactory;
    }

    public ActivityInitListener createActivityInitListener(Predicate<Boolean> predicate) {
        return new ActivityInitListener(new BiPredicate(predicate) {
            public final /* synthetic */ Predicate f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj, Object obj2) {
                return this.f$0.test((Boolean) obj2);
            }
        }, RecentsActivity.ACTIVITY_TRACKER);
    }

    public RecentsActivity getCreatedActivity() {
        return (RecentsActivity) RecentsActivity.ACTIVITY_TRACKER.getCreatedActivity();
    }

    public FallbackTaskbarUIController getTaskbarController() {
        RecentsActivity createdActivity = getCreatedActivity();
        if (createdActivity == null) {
            return null;
        }
        return createdActivity.getTaskbarUIController();
    }

    public RecentsView getVisibleRecentsView() {
        RecentsActivity createdActivity = getCreatedActivity();
        if (createdActivity == null) {
            return null;
        }
        if (createdActivity.hasBeenResumed() || (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && isInLiveTileMode())) {
            return (RecentsView) createdActivity.getOverviewPanel();
        }
        return null;
    }

    public Rect getOverviewWindowBounds(Rect rect, RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        return remoteAnimationTargetCompat.screenSpaceBounds;
    }

    public boolean deferStartingActivity(RecentsAnimationDeviceState recentsAnimationDeviceState, MotionEvent motionEvent) {
        return !recentsAnimationDeviceState.isFullyGesturalNavMode() || super.deferStartingActivity(recentsAnimationDeviceState, motionEvent);
    }

    public void onExitOverview(final RotationTouchHelper rotationTouchHelper, final Runnable runnable) {
        final StateManager<RecentsState> stateManager = getCreatedActivity().getStateManager();
        if (stateManager.getState() == RecentsState.HOME) {
            runnable.run();
            notifyRecentsOfOrientation(rotationTouchHelper);
            return;
        }
        stateManager.addStateListener(new StateManager.StateListener<RecentsState>() {
            public void onStateTransitionComplete(RecentsState recentsState) {
                if (recentsState == RecentsState.HOME) {
                    runnable.run();
                    FallbackActivityInterface.this.notifyRecentsOfOrientation(rotationTouchHelper);
                    stateManager.removeStateListener(this);
                }
            }
        });
    }

    public boolean isInLiveTileMode() {
        RecentsActivity createdActivity = getCreatedActivity();
        return createdActivity != null && createdActivity.getStateManager().getState() == RecentsState.DEFAULT && createdActivity.isStarted();
    }

    public void onLaunchTaskFailed() {
        RecentsActivity createdActivity = getCreatedActivity();
        if (createdActivity != null) {
            ((RecentsView) createdActivity.getOverviewPanel()).startHome();
        }
    }

    /* renamed from: com.android.quickstep.FallbackActivityInterface$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.FallbackActivityInterface.AnonymousClass2.<clinit>():void");
        }
    }

    public RecentsState stateFromGestureEndTarget(GestureState.GestureEndTarget gestureEndTarget) {
        int i = AnonymousClass2.$SwitchMap$com$android$quickstep$GestureState$GestureEndTarget[gestureEndTarget.ordinal()];
        if (i == 1) {
            return RecentsState.DEFAULT;
        }
        if (i == 2 || i == 3) {
            return RecentsState.BACKGROUND_APP;
        }
        return RecentsState.HOME;
    }

    /* access modifiers changed from: private */
    public void notifyRecentsOfOrientation(RotationTouchHelper rotationTouchHelper) {
        ((RecentsView) getCreatedActivity().getOverviewPanel()).setLayoutRotation(rotationTouchHelper.getCurrentActiveRotation(), rotationTouchHelper.getDisplayRotation());
    }

    public Animator getParallelAnimationToLauncher(GestureState.GestureEndTarget gestureEndTarget, long j, RecentsAnimationCallbacks recentsAnimationCallbacks) {
        Animator createAnimToRecentsState;
        FallbackTaskbarUIController taskbarController = getTaskbarController();
        Animator parallelAnimationToLauncher = super.getParallelAnimationToLauncher(gestureEndTarget, j, recentsAnimationCallbacks);
        if (taskbarController == null || (createAnimToRecentsState = taskbarController.createAnimToRecentsState(stateFromGestureEndTarget(gestureEndTarget), j)) == null) {
            return parallelAnimationToLauncher;
        }
        if (parallelAnimationToLauncher == null) {
            return createAnimToRecentsState;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{parallelAnimationToLauncher, createAnimToRecentsState});
        return animatorSet;
    }

    /* access modifiers changed from: protected */
    public int getOverviewScrimColorForState(RecentsActivity recentsActivity, RecentsState recentsState) {
        return recentsState.getScrimColor(recentsActivity);
    }
}
