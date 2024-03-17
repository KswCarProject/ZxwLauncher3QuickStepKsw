package com.android.quickstep;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.R;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.BaseState;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.taskbar.TaskbarUIController;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.WindowBounds;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.GestureState;
import com.android.quickstep.util.ActivityInitListener;
import com.android.quickstep.util.AnimatorControllerWithResistance;
import com.android.quickstep.util.SplitScreenBounds;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class BaseActivityInterface<STATE_TYPE extends BaseState<STATE_TYPE>, ACTIVITY_TYPE extends StatefulActivity<STATE_TYPE>> {
    /* access modifiers changed from: private */
    public final STATE_TYPE mBackgroundState;
    private Runnable mOnInitBackgroundStateUICallback = null;
    /* access modifiers changed from: private */
    public STATE_TYPE mTargetState;
    public final boolean rotationSupportedByActivity;

    public interface AnimationFactory {
        void createActivityInterface(long j);

        boolean hasRecentsEverAttachedToAppWindow() {
            return false;
        }

        boolean isRecentsAttachedToAppWindow() {
            return false;
        }

        void setEndTarget(GestureState.GestureEndTarget gestureEndTarget) {
        }

        void setRecentsAttachedToAppWindow(boolean z, boolean z2) {
        }
    }

    public abstract boolean allowMinimizeSplitScreen();

    public abstract ActivityInitListener createActivityInitListener(Predicate<Boolean> predicate);

    public abstract ACTIVITY_TYPE getCreatedActivity();

    public DepthController getDepthController() {
        return null;
    }

    /* access modifiers changed from: protected */
    public abstract int getOverviewScrimColorForState(ACTIVITY_TYPE activity_type, STATE_TYPE state_type);

    public abstract Rect getOverviewWindowBounds(Rect rect, RemoteAnimationTargetCompat remoteAnimationTargetCompat);

    public abstract int getSwipeUpDestinationAndLength(DeviceProfile deviceProfile, Context context, Rect rect, PagedOrientationHandler pagedOrientationHandler);

    public abstract TaskbarUIController getTaskbarController();

    public abstract <T extends RecentsView> T getVisibleRecentsView();

    public abstract boolean isInLiveTileMode();

    public abstract void onAssistantVisibilityChanged(float f);

    public abstract void onExitOverview(RotationTouchHelper rotationTouchHelper, Runnable runnable);

    public abstract void onLaunchTaskFailed();

    public abstract void onOneHandedModeStateChanged(boolean z);

    public void onSwipeUpToHomeComplete(RecentsAnimationDeviceState recentsAnimationDeviceState) {
    }

    public abstract AnimationFactory prepareRecentsUI(RecentsAnimationDeviceState recentsAnimationDeviceState, boolean z, Consumer<AnimatorControllerWithResistance> consumer);

    public void setOnDeferredActivityLaunchCallback(Runnable runnable) {
    }

    public boolean shouldCancelCurrentGesture() {
        return false;
    }

    public abstract STATE_TYPE stateFromGestureEndTarget(GestureState.GestureEndTarget gestureEndTarget);

    public abstract boolean switchToRecentsIfVisible(Runnable runnable);

    protected BaseActivityInterface(boolean z, STATE_TYPE state_type, STATE_TYPE state_type2) {
        this.rotationSupportedByActivity = z;
        this.mTargetState = state_type;
        this.mBackgroundState = state_type2;
    }

    public void onTransitionCancelled(boolean z, GestureState.GestureEndTarget gestureEndTarget) {
        StatefulActivity createdActivity = getCreatedActivity();
        if (createdActivity != null) {
            BaseState restState = createdActivity.getStateManager().getRestState();
            if (gestureEndTarget != null) {
                restState = stateFromGestureEndTarget(gestureEndTarget);
            }
            createdActivity.getStateManager().goToState(restState, z);
        }
    }

    public final boolean isResumed() {
        StatefulActivity createdActivity = getCreatedActivity();
        return createdActivity != null && createdActivity.hasBeenResumed();
    }

    public final boolean isStarted() {
        StatefulActivity createdActivity = getCreatedActivity();
        return createdActivity != null && createdActivity.isStarted();
    }

    public boolean deferStartingActivity(RecentsAnimationDeviceState recentsAnimationDeviceState, MotionEvent motionEvent) {
        return recentsAnimationDeviceState.isInDeferredGestureRegion(motionEvent) || recentsAnimationDeviceState.isImeRenderingNavButtons();
    }

    public void onLaunchTaskSuccess() {
        StatefulActivity createdActivity = getCreatedActivity();
        if (createdActivity != null) {
            createdActivity.getStateManager().moveToRestState();
        }
    }

    public void closeOverlay() {
        Optional.ofNullable(getTaskbarController()).ifPresent($$Lambda$O9TiOjZcqjlsoFstiq6baeldrAU.INSTANCE);
    }

    public void switchRunningTaskViewToScreenshot(HashMap<Integer, ThumbnailData> hashMap, Runnable runnable) {
        StatefulActivity createdActivity = getCreatedActivity();
        if (createdActivity != null) {
            RecentsView recentsView = (RecentsView) createdActivity.getOverviewPanel();
            if (recentsView != null) {
                recentsView.switchToScreenshot(hashMap, runnable);
            } else if (runnable != null) {
                runnable.run();
            }
        }
    }

    public final void calculateTaskSize(Context context, DeviceProfile deviceProfile, Rect rect) {
        Resources resources = context.getResources();
        float f = resources.getFloat(R.dimen.overview_max_scale);
        if (deviceProfile.isTablet) {
            Rect rect2 = new Rect();
            calculateGridSize(deviceProfile, rect2);
            calculateTaskSizeInternal(context, deviceProfile, rect2, f, 17, rect);
            return;
        }
        Context context2 = context;
        DeviceProfile deviceProfile2 = deviceProfile;
        calculateTaskSizeInternal(context2, deviceProfile2, deviceProfile.overviewTaskThumbnailTopMarginPx, deviceProfile.getOverviewActionsClaimedSpace(), resources.getDimensionPixelSize(R.dimen.overview_minimum_next_prev_size) + deviceProfile.overviewTaskMarginPx, f, 17, rect);
    }

    private void calculateTaskSizeInternal(Context context, DeviceProfile deviceProfile, int i, int i2, int i3, float f, int i4, Rect rect) {
        Rect insets = deviceProfile.getInsets();
        Rect rect2 = new Rect(0, 0, deviceProfile.widthPx, deviceProfile.heightPx);
        rect2.inset(insets.left, insets.top, insets.right, insets.bottom);
        rect2.inset(i3, i, i3, i2);
        calculateTaskSizeInternal(context, deviceProfile, rect2, f, i4, rect);
    }

    private void calculateTaskSizeInternal(Context context, DeviceProfile deviceProfile, Rect rect, float f, int i, Rect rect2) {
        PointF taskDimension = getTaskDimension(context, deviceProfile);
        float min = Math.min(Math.min(((float) rect.width()) / taskDimension.x, ((float) rect.height()) / taskDimension.y), f);
        Gravity.apply(i, Math.round(taskDimension.x * min), Math.round(min * taskDimension.y), rect, rect2);
    }

    private static PointF getTaskDimension(Context context, DeviceProfile deviceProfile) {
        PointF pointF = new PointF();
        getTaskDimension(context, deviceProfile, pointF);
        return pointF;
    }

    public static void getTaskDimension(Context context, DeviceProfile deviceProfile, PointF pointF) {
        if (deviceProfile.isMultiWindowMode) {
            WindowBounds secondaryWindowBounds = SplitScreenBounds.INSTANCE.getSecondaryWindowBounds(context);
            pointF.x = (float) secondaryWindowBounds.availableSize.x;
            pointF.y = (float) secondaryWindowBounds.availableSize.y;
            if (!TaskView.clipLeft(deviceProfile)) {
                pointF.x += (float) secondaryWindowBounds.insets.left;
            }
            if (!TaskView.clipRight(deviceProfile)) {
                pointF.x += (float) secondaryWindowBounds.insets.right;
            }
            if (!TaskView.clipTop(deviceProfile)) {
                pointF.y += (float) secondaryWindowBounds.insets.top;
            }
            if (!TaskView.clipBottom(deviceProfile)) {
                pointF.y += (float) secondaryWindowBounds.insets.bottom;
                return;
            }
            return;
        }
        pointF.x = (float) deviceProfile.widthPx;
        pointF.y = (float) deviceProfile.heightPx;
        if (TaskView.clipLeft(deviceProfile)) {
            pointF.x -= (float) deviceProfile.getInsets().left;
        }
        if (TaskView.clipRight(deviceProfile)) {
            pointF.x -= (float) deviceProfile.getInsets().right;
        }
        if (TaskView.clipTop(deviceProfile)) {
            pointF.y -= (float) deviceProfile.getInsets().top;
        }
        if (TaskView.clipBottom(deviceProfile)) {
            pointF.y -= (float) Math.max(deviceProfile.getInsets().bottom, deviceProfile.taskbarSize);
        }
    }

    public final void calculateGridSize(DeviceProfile deviceProfile, Rect rect) {
        Rect insets = deviceProfile.getInsets();
        int i = deviceProfile.overviewTaskThumbnailTopMarginPx;
        int overviewActionsClaimedSpace = deviceProfile.getOverviewActionsClaimedSpace();
        int i2 = deviceProfile.overviewGridSideMargin;
        rect.set(0, 0, deviceProfile.widthPx, deviceProfile.heightPx);
        rect.inset(Math.max(insets.left, i2), insets.top + i, Math.max(insets.right, i2), Math.max(insets.bottom, overviewActionsClaimedSpace));
    }

    public final void calculateGridTaskSize(Context context, DeviceProfile deviceProfile, Rect rect, PagedOrientationHandler pagedOrientationHandler) {
        Resources resources = context.getResources();
        Rect rect2 = new Rect();
        calculateTaskSize(context, deviceProfile, rect2);
        PointF taskDimension = getTaskDimension(context, deviceProfile);
        float height = ((((float) ((rect2.height() + deviceProfile.overviewTaskThumbnailTopMarginPx) - deviceProfile.overviewRowSpacing)) / 2.0f) - ((float) deviceProfile.overviewTaskThumbnailTopMarginPx)) / taskDimension.y;
        Gravity.apply((pagedOrientationHandler.getRecentsRtlSetting(resources) ? 5 : 3) | 48, Math.round(taskDimension.x * height), Math.round(height * taskDimension.y), rect2, rect);
    }

    public final void calculateModalTaskSize(Context context, DeviceProfile deviceProfile, Rect rect) {
        calculateTaskSize(context, deviceProfile, rect);
        float f = context.getResources().getFloat(R.dimen.overview_modal_max_scale);
        calculateTaskSizeInternal(context, deviceProfile, deviceProfile.overviewTaskMarginPx, (deviceProfile.heightPx - rect.bottom) - deviceProfile.getInsets().bottom, Math.round((((float) deviceProfile.availableWidthPx) - (((float) rect.width()) * f)) / 2.0f), 1.0f, 81, rect);
    }

    public Animator getParallelAnimationToLauncher(GestureState.GestureEndTarget gestureEndTarget, long j, RecentsAnimationCallbacks recentsAnimationCallbacks) {
        StatefulActivity createdActivity;
        if (gestureEndTarget != GestureState.GestureEndTarget.RECENTS || (createdActivity = getCreatedActivity()) == null) {
            return null;
        }
        BaseState stateFromGestureEndTarget = stateFromGestureEndTarget(gestureEndTarget);
        ObjectAnimator ofArgb = ObjectAnimator.ofArgb(createdActivity.getScrimView(), LauncherAnimUtils.VIEW_BACKGROUND_COLOR, new int[]{getOverviewScrimColorForState(createdActivity, stateFromGestureEndTarget)});
        ofArgb.setDuration(j);
        return ofArgb;
    }

    public View onSettledOnEndTarget(GestureState.GestureEndTarget gestureEndTarget) {
        TaskbarUIController taskbarController = getTaskbarController();
        if (taskbarController == null) {
            return null;
        }
        taskbarController.setSystemGestureInProgress(false);
        return taskbarController.getRootView();
    }

    /* access modifiers changed from: protected */
    public void runOnInitBackgroundStateUI(Runnable runnable) {
        this.mOnInitBackgroundStateUICallback = runnable;
        StatefulActivity createdActivity = getCreatedActivity();
        if (createdActivity != null && createdActivity.getStateManager().getState() == this.mBackgroundState) {
            onInitBackgroundStateUI();
        }
    }

    /* access modifiers changed from: private */
    public void onInitBackgroundStateUI() {
        Runnable runnable = this.mOnInitBackgroundStateUICallback;
        if (runnable != null) {
            runnable.run();
            this.mOnInitBackgroundStateUICallback = null;
        }
    }

    class DefaultAnimationFactory implements AnimationFactory {
        protected final ACTIVITY_TYPE mActivity;
        private final Consumer<AnimatorControllerWithResistance> mCallback;
        private boolean mHasEverAttachedToWindow;
        private boolean mIsAttachedToWindow;
        private final STATE_TYPE mStartState;

        DefaultAnimationFactory(Consumer<AnimatorControllerWithResistance> consumer) {
            this.mCallback = consumer;
            ACTIVITY_TYPE createdActivity = BaseActivityInterface.this.getCreatedActivity();
            this.mActivity = createdActivity;
            this.mStartState = createdActivity.getStateManager().getState();
        }

        /* access modifiers changed from: protected */
        public ACTIVITY_TYPE initBackgroundStateUI() {
            STATE_TYPE state_type = this.mStartState;
            if (state_type.shouldDisableRestore()) {
                state_type = this.mActivity.getStateManager().getRestState();
            }
            this.mActivity.getStateManager().setRestState(state_type);
            this.mActivity.getStateManager().goToState(BaseActivityInterface.this.mBackgroundState, false);
            BaseActivityInterface.this.onInitBackgroundStateUI();
            return this.mActivity;
        }

        public void createActivityInterface(long j) {
            PendingAnimation pendingAnimation = new PendingAnimation(j * 2);
            createBackgroundToOverviewAnim(this.mActivity, pendingAnimation);
            AnimatorPlaybackController createPlaybackController = pendingAnimation.createPlaybackController();
            this.mActivity.getStateManager().setCurrentUserControlledAnimation(createPlaybackController);
            createPlaybackController.setEndAction(new Runnable(createPlaybackController) {
                public final /* synthetic */ AnimatorPlaybackController f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    BaseActivityInterface.DefaultAnimationFactory.this.lambda$createActivityInterface$0$BaseActivityInterface$DefaultAnimationFactory(this.f$1);
                }
            });
            RecentsView recentsView = (RecentsView) this.mActivity.getOverviewPanel();
            this.mCallback.accept(AnimatorControllerWithResistance.createForRecents(createPlaybackController, this.mActivity, recentsView.getPagedViewOrientedState(), this.mActivity.getDeviceProfile(), recentsView, RecentsView.RECENTS_SCALE_PROPERTY, recentsView, RecentsView.TASK_SECONDARY_TRANSLATION));
            if (DisplayController.getNavigationMode(this.mActivity) == DisplayController.NavigationMode.NO_BUTTON) {
                setRecentsAttachedToAppWindow(this.mIsAttachedToWindow, false);
            }
        }

        public /* synthetic */ void lambda$createActivityInterface$0$BaseActivityInterface$DefaultAnimationFactory(AnimatorPlaybackController animatorPlaybackController) {
            this.mActivity.getStateManager().goToState(((double) animatorPlaybackController.getInterpolatedProgress()) > 0.5d ? BaseActivityInterface.this.mTargetState : BaseActivityInterface.this.mBackgroundState, false);
        }

        public void setRecentsAttachedToAppWindow(boolean z, boolean z2) {
            if (this.mIsAttachedToWindow != z || !z2) {
                this.mIsAttachedToWindow = z;
                RecentsView recentsView = (RecentsView) this.mActivity.getOverviewPanel();
                if (z) {
                    this.mHasEverAttachedToWindow = true;
                }
                StateManager stateManager = this.mActivity.getStateManager();
                float[] fArr = new float[1];
                float f = 1.0f;
                fArr[0] = z ? 1.0f : 0.0f;
                Animator createStateElementAnimation = stateManager.createStateElementAnimation(0, fArr);
                float f2 = z ? 1.0f : 0.0f;
                if (z) {
                    f = 0.0f;
                }
                this.mActivity.getStateManager().cancelStateElementAnimation(1);
                if (recentsView.isShown() || !z2) {
                    f2 = ((Float) RecentsView.ADJACENT_PAGE_HORIZONTAL_OFFSET.get(recentsView)).floatValue();
                } else {
                    RecentsView.ADJACENT_PAGE_HORIZONTAL_OFFSET.set(recentsView, Float.valueOf(f2));
                }
                if (!z2) {
                    RecentsView.ADJACENT_PAGE_HORIZONTAL_OFFSET.set(recentsView, Float.valueOf(f));
                } else {
                    this.mActivity.getStateManager().createStateElementAnimation(1, f2, f).start();
                }
                createStateElementAnimation.setInterpolator(z ? Interpolators.INSTANT : Interpolators.ACCEL_2);
                createStateElementAnimation.setDuration(z2 ? 300 : 0).start();
            }
        }

        public boolean isRecentsAttachedToAppWindow() {
            return this.mIsAttachedToWindow;
        }

        public boolean hasRecentsEverAttachedToAppWindow() {
            return this.mHasEverAttachedToWindow;
        }

        public void setEndTarget(GestureState.GestureEndTarget gestureEndTarget) {
            BaseActivityInterface baseActivityInterface = BaseActivityInterface.this;
            BaseState unused = baseActivityInterface.mTargetState = baseActivityInterface.stateFromGestureEndTarget(gestureEndTarget);
        }

        /* access modifiers changed from: protected */
        public void createBackgroundToOverviewAnim(ACTIVITY_TYPE activity_type, PendingAnimation pendingAnimation) {
            RecentsView recentsView = (RecentsView) activity_type.getOverviewPanel();
            PendingAnimation pendingAnimation2 = pendingAnimation;
            RecentsView recentsView2 = recentsView;
            pendingAnimation2.addFloat(recentsView2, RecentsView.RECENTS_SCALE_PROPERTY, recentsView.getMaxScaleForFullScreen(), 1.0f, Interpolators.LINEAR);
            pendingAnimation2.addFloat(recentsView2, RecentsView.FULLSCREEN_PROGRESS, 1.0f, 0.0f, Interpolators.LINEAR);
            pendingAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    TaskbarUIController taskbarController = BaseActivityInterface.this.getTaskbarController();
                    if (taskbarController != null) {
                        taskbarController.setSystemGestureInProgress(true);
                    }
                }
            });
        }
    }
}
