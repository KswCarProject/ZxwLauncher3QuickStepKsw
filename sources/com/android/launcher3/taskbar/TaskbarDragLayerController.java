package com.android.launcher3.taskbar;

import android.graphics.Rect;
import com.android.launcher3.R;
import com.android.launcher3.taskbar.TaskbarControllers;
import com.android.launcher3.util.TouchController;
import com.android.quickstep.AnimatedFloat;
import com.android.systemui.shared.system.ViewTreeObserverWrapper;
import java.io.PrintWriter;

public class TaskbarDragLayerController implements TaskbarControllers.LoggableTaskbarController {
    /* access modifiers changed from: private */
    public final TaskbarActivityContext mActivity;
    private final AnimatedFloat mBgNavbar = new AnimatedFloat(new Runnable() {
        public final void run() {
            TaskbarDragLayerController.this.updateBackgroundAlpha();
        }
    });
    private final AnimatedFloat mBgOffset = new AnimatedFloat(new Runnable() {
        public final void run() {
            TaskbarDragLayerController.this.updateBackgroundOffset();
        }
    });
    private final AnimatedFloat mBgOverride = new AnimatedFloat(new Runnable() {
        public final void run() {
            TaskbarDragLayerController.this.updateBackgroundAlpha();
        }
    });
    private final AnimatedFloat mBgTaskbar = new AnimatedFloat(new Runnable() {
        public final void run() {
            TaskbarDragLayerController.this.updateBackgroundAlpha();
        }
    });
    /* access modifiers changed from: private */
    public TaskbarControllers mControllers;
    private final int mFolderMargin;
    private final AnimatedFloat mImeBgTaskbar = new AnimatedFloat(new Runnable() {
        public final void run() {
            TaskbarDragLayerController.this.updateBackgroundAlpha();
        }
    });
    private final AnimatedFloat mKeyguardBgTaskbar = new AnimatedFloat(new Runnable() {
        public final void run() {
            TaskbarDragLayerController.this.updateBackgroundAlpha();
        }
    });
    private float mLastSetBackgroundAlpha;
    private AnimatedFloat mNavButtonDarkIntensityMultiplier;
    private final AnimatedFloat mNotificationShadeBgTaskbar = new AnimatedFloat(new Runnable() {
        public final void run() {
            TaskbarDragLayerController.this.updateBackgroundAlpha();
        }
    });
    private final TaskbarDragLayer mTaskbarDragLayer;

    public TaskbarDragLayerController(TaskbarActivityContext taskbarActivityContext, TaskbarDragLayer taskbarDragLayer) {
        this.mActivity = taskbarActivityContext;
        this.mTaskbarDragLayer = taskbarDragLayer;
        this.mFolderMargin = taskbarDragLayer.getResources().getDimensionPixelSize(R.dimen.taskbar_folder_margin);
    }

    public void init(TaskbarControllers taskbarControllers) {
        this.mControllers = taskbarControllers;
        this.mTaskbarDragLayer.init(new TaskbarDragLayerCallbacks());
        this.mNavButtonDarkIntensityMultiplier = this.mControllers.navbarButtonsViewController.getNavButtonDarkIntensityMultiplier();
        this.mBgTaskbar.value = 1.0f;
        this.mKeyguardBgTaskbar.value = 1.0f;
        this.mNotificationShadeBgTaskbar.value = 1.0f;
        this.mImeBgTaskbar.value = 1.0f;
        this.mBgOverride.value = 1.0f;
        updateBackgroundAlpha();
    }

    public void onDestroy() {
        this.mTaskbarDragLayer.onDestroy();
    }

    public Rect getFolderBoundingBox() {
        Rect rect = new Rect(0, 0, this.mTaskbarDragLayer.getWidth(), this.mTaskbarDragLayer.getHeight() - this.mActivity.getDeviceProfile().taskbarSize);
        int i = this.mFolderMargin;
        rect.inset(i, i);
        return rect;
    }

    public AnimatedFloat getTaskbarBackgroundAlpha() {
        return this.mBgTaskbar;
    }

    public AnimatedFloat getNavbarBackgroundAlpha() {
        return this.mBgNavbar;
    }

    public AnimatedFloat getKeyguardBgTaskbar() {
        return this.mKeyguardBgTaskbar;
    }

    public AnimatedFloat getNotificationShadeBgTaskbar() {
        return this.mNotificationShadeBgTaskbar;
    }

    public AnimatedFloat getImeBgTaskbar() {
        return this.mImeBgTaskbar;
    }

    public AnimatedFloat getOverrideBackgroundAlpha() {
        return this.mBgOverride;
    }

    public AnimatedFloat getTaskbarBackgroundOffset() {
        return this.mBgOffset;
    }

    /* access modifiers changed from: private */
    public void updateBackgroundAlpha() {
        float max = this.mBgOverride.value * Math.max(this.mBgNavbar.value, this.mBgTaskbar.value * this.mKeyguardBgTaskbar.value * this.mNotificationShadeBgTaskbar.value * this.mImeBgTaskbar.value);
        this.mLastSetBackgroundAlpha = max;
        this.mTaskbarDragLayer.setTaskbarBackgroundAlpha(max);
        updateNavBarDarkIntensityMultiplier();
    }

    /* access modifiers changed from: private */
    public void updateBackgroundOffset() {
        this.mTaskbarDragLayer.setTaskbarBackgroundOffset(this.mBgOffset.value);
        updateNavBarDarkIntensityMultiplier();
    }

    private void updateNavBarDarkIntensityMultiplier() {
        this.mNavButtonDarkIntensityMultiplier.updateValue(1.0f - (this.mLastSetBackgroundAlpha * (1.0f - this.mBgOffset.value)));
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "TaskbarDragLayerController:");
        printWriter.println(String.format("%s\tmBgOffset=%.2f", new Object[]{str, Float.valueOf(this.mBgOffset.value)}));
        printWriter.println(String.format("%s\tmFolderMargin=%dpx", new Object[]{str, Integer.valueOf(this.mFolderMargin)}));
        printWriter.println(String.format("%s\tmLastSetBackgroundAlpha=%.2f", new Object[]{str, Float.valueOf(this.mLastSetBackgroundAlpha)}));
    }

    public class TaskbarDragLayerCallbacks {
        public TaskbarDragLayerCallbacks() {
        }

        public void updateInsetsTouchability(ViewTreeObserverWrapper.InsetsInfo insetsInfo) {
            TaskbarDragLayerController.this.mControllers.taskbarInsetsController.updateInsetsTouchability(insetsInfo);
        }

        public void onDragLayerViewRemoved() {
            TaskbarDragLayerController.this.mActivity.maybeSetTaskbarWindowNotFullscreen();
        }

        public int getTaskbarBackgroundHeight() {
            return TaskbarDragLayerController.this.mActivity.getDeviceProfile().taskbarSize;
        }

        public TouchController[] getTouchControllers() {
            return new TouchController[]{TaskbarDragLayerController.this.mActivity.getDragController(), TaskbarDragLayerController.this.mControllers.taskbarForceVisibleImmersiveController, TaskbarDragLayerController.this.mControllers.navbarButtonsViewController.getTouchController()};
        }
    }
}
