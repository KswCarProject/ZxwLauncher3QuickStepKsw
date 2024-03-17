package com.android.quickstep;

import android.app.Activity;
import android.app.ActivityOptions;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import androidx.core.view.ViewCompat;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.util.InstantAppResolver;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.quickstep.TaskShortcutFactory;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskThumbnailView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.view.AppTransitionAnimationSpecCompat;
import com.android.systemui.shared.recents.view.AppTransitionAnimationSpecsFuture;
import com.android.systemui.shared.recents.view.RecentsTransition;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.ActivityOptionsCompat;
import com.android.systemui.shared.system.WindowManagerWrapper;
import java.util.Collections;
import java.util.List;

public interface TaskShortcutFactory {
    public static final TaskShortcutFactory APP_INFO = new TaskShortcutFactory() {
        public boolean showForSplitscreen() {
            return true;
        }

        public SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, TaskView.TaskIdAttributeContainer taskIdAttributeContainer) {
            TaskView taskView = taskIdAttributeContainer.getTaskView();
            return new SystemShortcut.AppInfo(baseDraggingActivity, taskIdAttributeContainer.getItemInfo(), taskView, new SystemShortcut.AppInfo.SplitAccessibilityInfo(taskView.containsMultipleTasks(), TaskUtils.getTitle(taskView.getContext(), taskIdAttributeContainer.getTask()), taskIdAttributeContainer.getA11yNodeId()));
        }
    };
    public static final TaskShortcutFactory FREE_FORM = new MultiWindowFactory(R.drawable.ic_split_screen, R.string.recent_task_option_freeform, StatsLogManager.LauncherEvent.LAUNCHER_SYSTEM_SHORTCUT_FREE_FORM_TAP) {
        /* access modifiers changed from: protected */
        public boolean isAvailable(BaseDraggingActivity baseDraggingActivity, int i) {
            return ActivityManagerWrapper.getInstance().supportsFreeformMultiWindow(baseDraggingActivity);
        }

        /* access modifiers changed from: protected */
        public ActivityOptions makeLaunchOptions(Activity activity) {
            ActivityOptions makeFreeformOptions = ActivityOptionsCompat.makeFreeformOptions();
            makeFreeformOptions.setLaunchBounds(new Rect(50, 50, 200, 200));
            return makeFreeformOptions;
        }

        /* access modifiers changed from: protected */
        public boolean onActivityStarted(BaseDraggingActivity baseDraggingActivity) {
            baseDraggingActivity.returnToHomescreen();
            return true;
        }
    };
    public static final TaskShortcutFactory INSTALL = $$Lambda$TaskShortcutFactory$Idn2tuX77bHIhGUSBZFWluBDFA.INSTANCE;
    public static final TaskShortcutFactory MODAL = $$Lambda$TaskShortcutFactory$LrGw8HYYuYXXs1XTyk3Aj8Spck.INSTANCE;
    public static final TaskShortcutFactory PIN = $$Lambda$TaskShortcutFactory$DZCof3Igf5fhqop6TKso9AvzZsY.INSTANCE;
    public static final TaskShortcutFactory SCREENSHOT = $$Lambda$TaskShortcutFactory$EwdGxY7Z0fgSMQMNBnTp9yEoZWI.INSTANCE;
    public static final TaskShortcutFactory SPLIT_SCREEN = new MultiWindowFactory(R.drawable.ic_split_screen, R.string.recent_task_option_split_screen, StatsLogManager.LauncherEvent.LAUNCHER_SYSTEM_SHORTCUT_SPLIT_SCREEN_TAP) {
        /* access modifiers changed from: protected */
        public boolean onActivityStarted(BaseDraggingActivity baseDraggingActivity) {
            return true;
        }

        /* access modifiers changed from: protected */
        public boolean isAvailable(BaseDraggingActivity baseDraggingActivity, int i) {
            return !baseDraggingActivity.getDeviceProfile().isMultiWindowMode && (i == -1 || i == 0);
        }

        /* access modifiers changed from: protected */
        public ActivityOptions makeLaunchOptions(Activity activity) {
            int navBarPosition = WindowManagerWrapper.getInstance().getNavBarPosition(activity.getDisplayId());
            if (navBarPosition == -1) {
                return null;
            }
            boolean z = true;
            if (navBarPosition == 1) {
                z = false;
            }
            return ActivityOptionsCompat.makeSplitScreenOptions(z);
        }
    };
    public static final TaskShortcutFactory WELLBEING = $$Lambda$TaskShortcutFactory$dbnp3P629cNDOg0riBlvYCie_Yk.INSTANCE;

    SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, TaskView.TaskIdAttributeContainer taskIdAttributeContainer);

    boolean showForSplitscreen() {
        return false;
    }

    public static abstract class MultiWindowFactory implements TaskShortcutFactory {
        private final int mIconRes;
        private final StatsLogManager.LauncherEvent mLauncherEvent;
        private final int mTextRes;

        /* access modifiers changed from: protected */
        public abstract boolean isAvailable(BaseDraggingActivity baseDraggingActivity, int i);

        /* access modifiers changed from: protected */
        public abstract ActivityOptions makeLaunchOptions(Activity activity);

        /* access modifiers changed from: protected */
        public abstract boolean onActivityStarted(BaseDraggingActivity baseDraggingActivity);

        MultiWindowFactory(int i, int i2, StatsLogManager.LauncherEvent launcherEvent) {
            this.mIconRes = i;
            this.mTextRes = i2;
            this.mLauncherEvent = launcherEvent;
        }

        public SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, TaskView.TaskIdAttributeContainer taskIdAttributeContainer) {
            Task task = taskIdAttributeContainer.getTask();
            if (!task.isDockable || !isAvailable(baseDraggingActivity, task.key.displayId)) {
                return null;
            }
            return new MultiWindowSystemShortcut(this.mIconRes, this.mTextRes, baseDraggingActivity, taskIdAttributeContainer, this, this.mLauncherEvent);
        }
    }

    public static class SplitSelectSystemShortcut extends SystemShortcut {
        private final SplitConfigurationOptions.SplitPositionOption mSplitPositionOption;
        private final TaskView mTaskView;

        public SplitSelectSystemShortcut(BaseDraggingActivity baseDraggingActivity, TaskView taskView, SplitConfigurationOptions.SplitPositionOption splitPositionOption) {
            super(splitPositionOption.iconResId, splitPositionOption.textResId, baseDraggingActivity, taskView.getItemInfo(), taskView);
            this.mTaskView = taskView;
            this.mSplitPositionOption = splitPositionOption;
        }

        public void onClick(View view) {
            Log.d("TaskShortcutFactory", "onClick");
            this.mTaskView.initiateSplitSelect(this.mSplitPositionOption);
        }
    }

    public static class MultiWindowSystemShortcut extends SystemShortcut<BaseDraggingActivity> {
        private final MultiWindowFactory mFactory;
        private Handler mHandler = new Handler(Looper.getMainLooper());
        private final StatsLogManager.LauncherEvent mLauncherEvent;
        /* access modifiers changed from: private */
        public final RecentsView mRecentsView;
        /* access modifiers changed from: private */
        public final TaskView mTaskView;
        private final TaskThumbnailView mThumbnailView;

        public MultiWindowSystemShortcut(int i, int i2, BaseDraggingActivity baseDraggingActivity, TaskView.TaskIdAttributeContainer taskIdAttributeContainer, MultiWindowFactory multiWindowFactory, StatsLogManager.LauncherEvent launcherEvent) {
            super(i, i2, baseDraggingActivity, taskIdAttributeContainer.getItemInfo(), taskIdAttributeContainer.getTaskView());
            this.mLauncherEvent = launcherEvent;
            this.mTaskView = taskIdAttributeContainer.getTaskView();
            this.mRecentsView = (RecentsView) baseDraggingActivity.getOverviewPanel();
            this.mThumbnailView = taskIdAttributeContainer.getThumbnailView();
            this.mFactory = multiWindowFactory;
        }

        public void onClick(View view) {
            Task.TaskKey taskKey = this.mTaskView.getTask().key;
            final int i = taskKey.id;
            final AnonymousClass1 r0 = new View.OnLayoutChangeListener() {
                public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    MultiWindowSystemShortcut.this.mTaskView.getRootView().removeOnLayoutChangeListener(this);
                    MultiWindowSystemShortcut.this.mRecentsView.clearIgnoreResetTask(i);
                    MultiWindowSystemShortcut.this.mRecentsView.dismissTask(MultiWindowSystemShortcut.this.mTaskView, false, false);
                }
            };
            AnonymousClass2 r1 = new DeviceProfile.OnDeviceProfileChangeListener() {
                public void onDeviceProfileChanged(DeviceProfile deviceProfile) {
                    ((BaseDraggingActivity) MultiWindowSystemShortcut.this.mTarget).removeOnDeviceProfileChangeListener(this);
                    if (deviceProfile.isMultiWindowMode) {
                        MultiWindowSystemShortcut.this.mTaskView.getRootView().addOnLayoutChangeListener(r0);
                    }
                }
            };
            dismissTaskMenuView((BaseDraggingActivity) this.mTarget);
            ActivityOptions makeLaunchOptions = this.mFactory.makeLaunchOptions((Activity) this.mTarget);
            if (makeLaunchOptions != null) {
                makeLaunchOptions.setSplashScreenStyle(1);
            }
            if (makeLaunchOptions != null && ActivityManagerWrapper.getInstance().startActivityFromRecents(i, makeLaunchOptions) && this.mFactory.onActivityStarted((BaseDraggingActivity) this.mTarget)) {
                ((BaseDraggingActivity) this.mTarget).addOnDeviceProfileChangeListener(r1);
                $$Lambda$TaskShortcutFactory$MultiWindowSystemShortcut$7RninhuHNl_DkR1gORIEO3fX4rk r6 = new Runnable(i) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        TaskShortcutFactory.MultiWindowSystemShortcut.this.lambda$onClick$0$TaskShortcutFactory$MultiWindowSystemShortcut(this.f$1);
                    }
                };
                int[] iArr = new int[2];
                this.mThumbnailView.getLocationOnScreen(iArr);
                final Rect rect = new Rect(iArr[0], iArr[1], iArr[0] + ((int) (((float) this.mThumbnailView.getWidth()) * this.mTaskView.getScaleX())), iArr[1] + ((int) (((float) this.mThumbnailView.getHeight()) * this.mTaskView.getScaleY())));
                float dimAlpha = this.mThumbnailView.getDimAlpha();
                this.mThumbnailView.setDimAlpha(0.0f);
                final Bitmap drawViewIntoHardwareBitmap = RecentsTransition.drawViewIntoHardwareBitmap(rect.width(), rect.height(), this.mThumbnailView, 1.0f, ViewCompat.MEASURED_STATE_MASK);
                this.mThumbnailView.setDimAlpha(dimAlpha);
                AnonymousClass3 r02 = new AppTransitionAnimationSpecsFuture(this.mHandler) {
                    public List<AppTransitionAnimationSpecCompat> composeSpecs() {
                        return Collections.singletonList(new AppTransitionAnimationSpecCompat(i, drawViewIntoHardwareBitmap, rect));
                    }
                };
                WindowManagerWrapper.getInstance().overridePendingAppTransitionMultiThumbFuture(r02, r6, this.mHandler, true, taskKey.displayId);
                ((BaseDraggingActivity) this.mTarget).getStatsLogManager().logger().withItemInfo(this.mTaskView.getItemInfo()).log(this.mLauncherEvent);
            }
        }

        public /* synthetic */ void lambda$onClick$0$TaskShortcutFactory$MultiWindowSystemShortcut(int i) {
            this.mRecentsView.setIgnoreResetTask(i);
            this.mTaskView.setAlpha(0.0f);
        }
    }

    static /* synthetic */ SystemShortcut lambda$static$0(BaseDraggingActivity baseDraggingActivity, TaskView.TaskIdAttributeContainer taskIdAttributeContainer) {
        if (SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(baseDraggingActivity).isActive() && ActivityManagerWrapper.getInstance().isScreenPinningEnabled() && !ActivityManagerWrapper.getInstance().isLockToAppActive()) {
            return new PinSystemShortcut(baseDraggingActivity, taskIdAttributeContainer);
        }
        return null;
    }

    public static class PinSystemShortcut extends SystemShortcut<BaseDraggingActivity> {
        private static final String TAG = "PinSystemShortcut";
        private final TaskView mTaskView;

        public PinSystemShortcut(BaseDraggingActivity baseDraggingActivity, TaskView.TaskIdAttributeContainer taskIdAttributeContainer) {
            super(R.drawable.ic_pin, R.string.recent_task_option_pin, baseDraggingActivity, taskIdAttributeContainer.getItemInfo(), taskIdAttributeContainer.getTaskView());
            this.mTaskView = taskIdAttributeContainer.getTaskView();
        }

        public void onClick(View view) {
            if (this.mTaskView.launchTaskAnimated() != null) {
                SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mTarget).startScreenPinning(this.mTaskView.getTask().key.id);
            }
            dismissTaskMenuView((BaseDraggingActivity) this.mTarget);
            ((BaseDraggingActivity) this.mTarget).getStatsLogManager().logger().withItemInfo(this.mTaskView.getItemInfo()).log(StatsLogManager.LauncherEvent.LAUNCHER_SYSTEM_SHORTCUT_PIN_TAP);
        }
    }

    static /* synthetic */ SystemShortcut lambda$static$1(BaseDraggingActivity baseDraggingActivity, TaskView.TaskIdAttributeContainer taskIdAttributeContainer) {
        if (InstantAppResolver.newInstance(baseDraggingActivity).isInstantApp(baseDraggingActivity, taskIdAttributeContainer.getTask().getTopComponent().getPackageName())) {
            return new SystemShortcut.Install(baseDraggingActivity, taskIdAttributeContainer.getItemInfo(), taskIdAttributeContainer.getTaskView());
        }
        return null;
    }

    static /* synthetic */ SystemShortcut lambda$static$4(BaseDraggingActivity baseDraggingActivity, TaskView.TaskIdAttributeContainer taskIdAttributeContainer) {
        if (FeatureFlags.ENABLE_OVERVIEW_SELECTIONS.get()) {
            return taskIdAttributeContainer.getThumbnailView().getTaskOverlay().getModalStateSystemShortcut(taskIdAttributeContainer.getItemInfo(), taskIdAttributeContainer.getTaskView());
        }
        return null;
    }
}
