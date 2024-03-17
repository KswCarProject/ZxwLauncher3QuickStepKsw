package com.android.launcher3.taskbar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.TaskTransitionSpec;
import android.view.WindowManagerGlobal;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherState;
import com.android.launcher3.QuickstepTransitionManager;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.InstanceIdSequence;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.util.OnboardingPrefs;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.RecentsAnimationCallbacks;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.stream.Stream;

public class LauncherTaskbarUIController extends TaskbarUIController {
    public static final int ALL_APPS_PAGE_PROGRESS_INDEX = 1;
    public static final int MINUS_ONE_PAGE_PROGRESS_INDEX = 0;
    public static final int SYSUI_SURFACE_PROGRESS_INDEX = 3;
    private static final String TAG = "TaskbarUIController";
    public static final int WIDGETS_PAGE_PROGRESS_INDEX = 2;
    private TaskbarKeyguardController mKeyguardController;
    private final BaseQuickstepLauncher mLauncher;
    private final DeviceProfile.OnDeviceProfileChangeListener mOnDeviceProfileChangeListener = new DeviceProfile.OnDeviceProfileChangeListener() {
        public final void onDeviceProfileChanged(DeviceProfile deviceProfile) {
            LauncherTaskbarUIController.this.lambda$new$0$LauncherTaskbarUIController(deviceProfile);
        }
    };
    private final SparseArray<Float> mTaskbarInAppDisplayProgress = new SparseArray<>(4);
    private final TaskbarLauncherStateController mTaskbarLauncherStateController = new TaskbarLauncherStateController();
    private AnimatedFloat mTaskbarOverrideBackgroundAlpha;

    public /* synthetic */ void lambda$new$0$LauncherTaskbarUIController(DeviceProfile deviceProfile) {
        onStashedInAppChanged(deviceProfile);
        if (this.mControllers != null && this.mControllers.taskbarViewController != null) {
            this.mControllers.taskbarViewController.onRotationChanged(deviceProfile);
        }
    }

    public LauncherTaskbarUIController(BaseQuickstepLauncher baseQuickstepLauncher) {
        this.mLauncher = baseQuickstepLauncher;
    }

    /* access modifiers changed from: protected */
    public void init(TaskbarControllers taskbarControllers) {
        super.init(taskbarControllers);
        this.mTaskbarLauncherStateController.init(this.mControllers, this.mLauncher);
        this.mTaskbarOverrideBackgroundAlpha = this.mControllers.taskbarDragLayerController.getOverrideBackgroundAlpha();
        this.mLauncher.setTaskbarUIController(this);
        this.mKeyguardController = taskbarControllers.taskbarKeyguardController;
        onLauncherResumedOrPaused(this.mLauncher.hasBeenResumed(), true);
        onStashedInAppChanged(this.mLauncher.getDeviceProfile());
        this.mLauncher.addOnDeviceProfileChangeListener(this.mOnDeviceProfileChangeListener);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        onLauncherResumedOrPaused(false);
        this.mTaskbarLauncherStateController.onDestroy();
        this.mLauncher.setTaskbarUIController((LauncherTaskbarUIController) null);
        this.mLauncher.removeOnDeviceProfileChangeListener(this.mOnDeviceProfileChangeListener);
        updateTaskTransitionSpec(true);
    }

    /* access modifiers changed from: protected */
    public boolean isTaskbarTouchable() {
        return !this.mTaskbarLauncherStateController.isAnimatingToLauncher();
    }

    public void setShouldDelayLauncherStateAnim(boolean z) {
        this.mTaskbarLauncherStateController.setShouldDelayLauncherStateAnim(z);
    }

    public void enableManualStashingForTests(boolean z) {
        this.mControllers.taskbarStashController.enableManualStashingForTests(z);
    }

    public void unstashTaskbarIfStashed() {
        this.mControllers.taskbarStashController.onLongPressToUnstashTaskbar();
    }

    /* access modifiers changed from: protected */
    public void addLauncherResumeAnimation(AnimatorSet animatorSet, int i) {
        animatorSet.play(onLauncherResumedOrPaused(true, false, false, i));
    }

    public void onLauncherResumedOrPaused(boolean z) {
        onLauncherResumedOrPaused(z, false);
    }

    private void onLauncherResumedOrPaused(boolean z, boolean z2) {
        onLauncherResumedOrPaused(z, z2, true, QuickstepTransitionManager.CONTENT_ALPHA_DURATION);
    }

    private Animator onLauncherResumedOrPaused(boolean z, boolean z2, boolean z3, int i) {
        if (this.mKeyguardController.isScreenOff()) {
            if (!z) {
                return null;
            }
            this.mKeyguardController.setScreenOn();
        }
        this.mTaskbarLauncherStateController.updateStateForFlag(1, z);
        return this.mTaskbarLauncherStateController.applyState(z2 ? 0 : (long) i, z3);
    }

    public Animator createAnimToLauncher(LauncherState launcherState, RecentsAnimationCallbacks recentsAnimationCallbacks, long j) {
        return this.mTaskbarLauncherStateController.createAnimToLauncher(launcherState, recentsAnimationCallbacks, j);
    }

    public boolean isEventOverAnyTaskbarItem(MotionEvent motionEvent) {
        return this.mControllers.taskbarViewController.isEventOverAnyItem(motionEvent) || this.mControllers.navbarButtonsViewController.isEventOverAnyItem(motionEvent);
    }

    public boolean isDraggingItem() {
        return this.mControllers.taskbarDragController.isDragging();
    }

    /* access modifiers changed from: protected */
    public void onStashedInAppChanged() {
        onStashedInAppChanged(this.mLauncher.getDeviceProfile());
    }

    private void onStashedInAppChanged(DeviceProfile deviceProfile) {
        boolean isStashedInApp = this.mControllers.taskbarStashController.isStashedInApp();
        deviceProfile.isTaskbarPresentInApps = !isStashedInApp;
        updateTaskTransitionSpec(isStashedInApp);
    }

    private void updateTaskTransitionSpec(boolean z) {
        if (z) {
            try {
                WindowManagerGlobal.getWindowManagerService().clearTaskTransitionSpec();
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to update task transition spec to account for new taskbar state", e);
            }
        } else {
            WindowManagerGlobal.getWindowManagerService().setTaskTransitionSpec(new TaskTransitionSpec(this.mLauncher.getColor(R.color.taskbar_background), LauncherTaskbarUIController$$ExternalSynthetic1.m0(new Object[]{21})));
        }
    }

    public void forceHideBackground(boolean z) {
        this.mTaskbarOverrideBackgroundAlpha.updateValue(z ? 0.0f : 1.0f);
    }

    public Stream<ItemInfoWithIcon> getAppIconsForEdu() {
        return Arrays.stream(this.mLauncher.getAppsView().getAppsStore().getApps());
    }

    public void showEdu() {
        if (shouldShowEdu()) {
            this.mLauncher.getOnboardingPrefs().markChecked(OnboardingPrefs.TASKBAR_EDU_SEEN);
            this.mControllers.taskbarEduController.showEdu();
        }
    }

    public boolean shouldShowEdu() {
        return !Utilities.IS_RUNNING_IN_TEST_HARNESS && !this.mLauncher.getOnboardingPrefs().getBoolean(OnboardingPrefs.TASKBAR_EDU_SEEN);
    }

    public void hideEdu() {
        this.mControllers.taskbarEduController.hideEdu();
    }

    public void onTaskbarIconLaunched(ItemInfo itemInfo) {
        this.mLauncher.logAppLaunch(this.mControllers.taskbarActivityContext.getStatsLogManager(), itemInfo, new InstanceIdSequence().newInstanceId());
    }

    public void setSystemGestureInProgress(boolean z) {
        super.setSystemGestureInProgress(z);
        forceHideBackground(z);
    }

    public void onTaskbarInAppDisplayProgressUpdate(float f, int i) {
        if (this.mControllers != null) {
            this.mTaskbarInAppDisplayProgress.put(i, Float.valueOf(f));
            if (!this.mControllers.taskbarStashController.isInApp() && !this.mTaskbarLauncherStateController.isAnimatingToLauncher()) {
                this.mControllers.navbarButtonsViewController.getTaskbarNavButtonTranslationYForInAppDisplay().updateValue(((float) this.mLauncher.getDeviceProfile().getTaskbarOffsetY()) * getInAppDisplayProgress());
            }
        }
    }

    public boolean shouldUseInAppLayout() {
        return getInAppDisplayProgress() > 0.0f;
    }

    private float getInAppDisplayProgress(int i) {
        if (!this.mTaskbarInAppDisplayProgress.contains(i)) {
            this.mTaskbarInAppDisplayProgress.put(i, Float.valueOf(0.0f));
        }
        return this.mTaskbarInAppDisplayProgress.get(i).floatValue();
    }

    private float getInAppDisplayProgress() {
        return ((Float) Stream.of(new Float[]{Float.valueOf(getInAppDisplayProgress(0)), Float.valueOf(getInAppDisplayProgress(1)), Float.valueOf(getInAppDisplayProgress(2)), Float.valueOf(getInAppDisplayProgress(3))}).max($$Lambda$LauncherTaskbarUIController$ko5D2GpG44diu0arR8u3EeKc9ik.INSTANCE).get()).floatValue();
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        super.dumpLogs(str, printWriter);
        printWriter.println(String.format("%s\tmTaskbarOverrideBackgroundAlpha=%.2f", new Object[]{str, Float.valueOf(this.mTaskbarOverrideBackgroundAlpha.value)}));
        printWriter.println(String.format("%s\tTaskbar in-app display progress:", new Object[]{str}));
        if (this.mControllers == null) {
            printWriter.println(String.format("%s\t\tMissing mControllers", new Object[]{str}));
        } else {
            printWriter.println(String.format("%s\t\tprogress at MINUS_ONE_PAGE_PROGRESS_INDEX=%.2f", new Object[]{str, Float.valueOf(getInAppDisplayProgress(0))}));
            printWriter.println(String.format("%s\t\tprogress at ALL_APPS_PAGE_PROGRESS_INDEX=%.2f", new Object[]{str, Float.valueOf(getInAppDisplayProgress(1))}));
            printWriter.println(String.format("%s\t\tprogress at WIDGETS_PAGE_PROGRESS_INDEX=%.2f", new Object[]{str, Float.valueOf(getInAppDisplayProgress(2))}));
            printWriter.println(String.format("%s\t\tprogress at SYSUI_SURFACE_PROGRESS_INDEX=%.2f", new Object[]{str, Float.valueOf(getInAppDisplayProgress(3))}));
        }
        this.mTaskbarLauncherStateController.dumpLogs(str + "\t", printWriter);
    }
}
