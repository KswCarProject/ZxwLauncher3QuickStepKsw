package com.android.launcher3.taskbar.allapps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.appprediction.PredictionRowView;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.taskbar.TaskbarActivityContext;
import com.android.launcher3.taskbar.TaskbarControllers;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.TaskStackChangeListeners;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class TaskbarAllAppsController {
    private static final String WINDOW_TITLE = "Taskbar All Apps";
    /* access modifiers changed from: private */
    public TaskbarAllAppsContext mAllAppsContext;
    private AppInfo[] mApps;
    private int mAppsModelFlags;
    private TaskbarControllers mControllers;
    private DeviceProfile mDeviceProfile;
    private final WindowManager.LayoutParams mLayoutParams;
    private List<ItemInfo> mPredictedApps;
    /* access modifiers changed from: private */
    public final TaskbarAllAppsProxyView mProxyView;
    private final TaskStackChangeListener mTaskStackListener = new TaskStackChangeListener() {
        public void onTaskStackChanged() {
            TaskbarAllAppsController.this.mProxyView.close(false);
        }
    };
    /* access modifiers changed from: private */
    public final TaskbarActivityContext mTaskbarContext;

    public TaskbarAllAppsController(TaskbarActivityContext taskbarActivityContext, DeviceProfile deviceProfile) {
        this.mDeviceProfile = deviceProfile;
        this.mTaskbarContext = taskbarActivityContext;
        this.mProxyView = new TaskbarAllAppsProxyView(taskbarActivityContext);
        this.mLayoutParams = createLayoutParams();
    }

    public void init(TaskbarControllers taskbarControllers, boolean z) {
        if (FeatureFlags.ENABLE_ALL_APPS_IN_TASKBAR.get()) {
            this.mControllers = taskbarControllers;
            if (z) {
                show(false);
            }
        }
    }

    public void setApps(AppInfo[] appInfoArr, int i) {
        if (FeatureFlags.ENABLE_ALL_APPS_IN_TASKBAR.get()) {
            this.mApps = appInfoArr;
            this.mAppsModelFlags = i;
            TaskbarAllAppsContext taskbarAllAppsContext = this.mAllAppsContext;
            if (taskbarAllAppsContext != null) {
                taskbarAllAppsContext.getAppsView().getAppsStore().setApps(this.mApps, this.mAppsModelFlags);
            }
        }
    }

    public void setPredictedApps(List<ItemInfo> list) {
        if (FeatureFlags.ENABLE_ALL_APPS_IN_TASKBAR.get()) {
            this.mPredictedApps = list;
            TaskbarAllAppsContext taskbarAllAppsContext = this.mAllAppsContext;
            if (taskbarAllAppsContext != null) {
                ((PredictionRowView) taskbarAllAppsContext.getAppsView().getFloatingHeaderView().findFixedRowByType(PredictionRowView.class)).setPredictedApps(this.mPredictedApps);
            }
        }
    }

    public void show() {
        show(true);
    }

    private void show(boolean z) {
        if (!this.mProxyView.isOpen()) {
            this.mProxyView.show();
            this.mControllers.getSharedState().allAppsVisible = true;
            TaskbarAllAppsContext taskbarAllAppsContext = new TaskbarAllAppsContext(this.mTaskbarContext, this, this.mControllers.taskbarStashController);
            this.mAllAppsContext = taskbarAllAppsContext;
            taskbarAllAppsContext.getDragController().init(this.mControllers);
            TaskStackChangeListeners.getInstance().registerTaskStackListener(this.mTaskStackListener);
            Optional.ofNullable((WindowManager) this.mAllAppsContext.getSystemService(WindowManager.class)).ifPresent(new Consumer() {
                public final void accept(Object obj) {
                    TaskbarAllAppsController.this.lambda$show$0$TaskbarAllAppsController((WindowManager) obj);
                }
            });
            this.mAllAppsContext.getAppsView().getAppsStore().setApps(this.mApps, this.mAppsModelFlags);
            ((PredictionRowView) this.mAllAppsContext.getAppsView().getFloatingHeaderView().findFixedRowByType(PredictionRowView.class)).setPredictedApps(this.mPredictedApps);
            this.mAllAppsContext.getAllAppsViewController().show(z);
        }
    }

    public /* synthetic */ void lambda$show$0$TaskbarAllAppsController(WindowManager windowManager) {
        windowManager.addView(this.mAllAppsContext.getDragLayer(), this.mLayoutParams);
    }

    public void hide() {
        this.mProxyView.close(true);
    }

    /* access modifiers changed from: package-private */
    public void maybeCloseWindow() {
        if (AbstractFloatingView.getOpenView(this.mAllAppsContext, AbstractFloatingView.TYPE_ALL) == null && !this.mAllAppsContext.getDragController().isSystemDragInProgress()) {
            this.mProxyView.close(false);
            this.mControllers.getSharedState().allAppsVisible = false;
            onDestroy();
        }
    }

    public void onDestroy() {
        TaskStackChangeListeners.getInstance().unregisterTaskStackListener(this.mTaskStackListener);
        Optional.ofNullable(this.mAllAppsContext).map($$Lambda$TaskbarAllAppsController$AqdwoYNgFMmacQrDIkCBOw01BPM.INSTANCE).ifPresent(new Consumer() {
            public final void accept(Object obj) {
                TaskbarAllAppsController.this.lambda$onDestroy$2$TaskbarAllAppsController((WindowManager) obj);
            }
        });
        this.mAllAppsContext = null;
    }

    static /* synthetic */ WindowManager lambda$onDestroy$1(TaskbarAllAppsContext taskbarAllAppsContext) {
        return (WindowManager) taskbarAllAppsContext.getSystemService(WindowManager.class);
    }

    public /* synthetic */ void lambda$onDestroy$2$TaskbarAllAppsController(WindowManager windowManager) {
        windowManager.removeView(this.mAllAppsContext.getDragLayer());
    }

    public void updateDeviceProfile(DeviceProfile deviceProfile) {
        this.mDeviceProfile = deviceProfile;
        Optional.ofNullable(this.mAllAppsContext).ifPresent($$Lambda$TaskbarAllAppsController$c5CpRbZn3H4_k2WsfK90fmdH2I.INSTANCE);
    }

    static /* synthetic */ void lambda$updateDeviceProfile$3(TaskbarAllAppsContext taskbarAllAppsContext) {
        AbstractFloatingView.closeAllOpenViewsExcept(taskbarAllAppsContext, false, AbstractFloatingView.TYPE_REBIND_SAFE);
        taskbarAllAppsContext.dispatchDeviceProfileChanged();
    }

    /* access modifiers changed from: package-private */
    public DeviceProfile getDeviceProfile() {
        return this.mDeviceProfile;
    }

    private WindowManager.LayoutParams createLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(2038, 0, -3);
        layoutParams.setTitle(WINDOW_TITLE);
        layoutParams.gravity = 80;
        layoutParams.packageName = this.mTaskbarContext.getPackageName();
        layoutParams.setFitInsetsTypes(0);
        layoutParams.layoutInDisplayCutoutMode = 3;
        layoutParams.setSystemApplicationOverlay(true);
        return layoutParams;
    }

    private class TaskbarAllAppsProxyView extends AbstractFloatingView {
        /* access modifiers changed from: protected */
        public boolean isOfType(int i) {
            return (i & 131072) != 0;
        }

        public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        private TaskbarAllAppsProxyView(Context context) {
            super(context, (AttributeSet) null);
        }

        /* access modifiers changed from: private */
        public void show() {
            this.mIsOpen = true;
            TaskbarAllAppsController.this.mTaskbarContext.getDragLayer().addView(this);
        }

        /* access modifiers changed from: protected */
        public void handleClose(boolean z) {
            TaskbarAllAppsController.this.mTaskbarContext.getDragLayer().removeView(this);
            Optional.ofNullable(TaskbarAllAppsController.this.mAllAppsContext).map($$Lambda$as6uDznpGwMMvuvDEj3AJCAUs2c.INSTANCE).ifPresent(new Consumer(z) {
                public final /* synthetic */ boolean f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    ((TaskbarAllAppsViewController) obj).close(this.f$0);
                }
            });
        }
    }
}
