package com.android.launcher3.taskbar;

import com.android.launcher3.taskbar.allapps.TaskbarAllAppsController;
import com.android.systemui.shared.rotation.RotationButtonController;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TaskbarControllers {
    private boolean mAreAllControllersInitialized;
    private LoggableTaskbarController[] mControllersToLog = null;
    private final List<Runnable> mPostInitCallbacks = new ArrayList();
    private TaskbarSharedState mSharedState = null;
    public final TaskbarNavButtonController navButtonController;
    public final NavbarButtonsViewController navbarButtonsViewController;
    public final RotationButtonController rotationButtonController;
    public final StashedHandleViewController stashedHandleViewController;
    public final TaskbarActivityContext taskbarActivityContext;
    public final TaskbarAllAppsController taskbarAllAppsController;
    public final TaskbarAutohideSuspendController taskbarAutohideSuspendController;
    public final TaskbarDragController taskbarDragController;
    public final TaskbarDragLayerController taskbarDragLayerController;
    public final TaskbarEduController taskbarEduController;
    public final TaskbarForceVisibleImmersiveController taskbarForceVisibleImmersiveController;
    public final TaskbarInsetsController taskbarInsetsController;
    public final TaskbarKeyguardController taskbarKeyguardController;
    public final TaskbarPopupController taskbarPopupController;
    public final TaskbarScrimViewController taskbarScrimViewController;
    public final TaskbarStashController taskbarStashController;
    public final TaskbarUnfoldAnimationController taskbarUnfoldAnimationController;
    public final TaskbarViewController taskbarViewController;
    public TaskbarUIController uiController = TaskbarUIController.DEFAULT;

    protected interface LoggableTaskbarController {
        void dumpLogs(String str, PrintWriter printWriter);
    }

    public TaskbarControllers(TaskbarActivityContext taskbarActivityContext2, TaskbarDragController taskbarDragController2, TaskbarNavButtonController taskbarNavButtonController, NavbarButtonsViewController navbarButtonsViewController2, RotationButtonController rotationButtonController2, TaskbarDragLayerController taskbarDragLayerController2, TaskbarViewController taskbarViewController2, TaskbarScrimViewController taskbarScrimViewController2, TaskbarUnfoldAnimationController taskbarUnfoldAnimationController2, TaskbarKeyguardController taskbarKeyguardController2, StashedHandleViewController stashedHandleViewController2, TaskbarStashController taskbarStashController2, TaskbarEduController taskbarEduController2, TaskbarAutohideSuspendController taskbarAutohideSuspendController2, TaskbarPopupController taskbarPopupController2, TaskbarForceVisibleImmersiveController taskbarForceVisibleImmersiveController2, TaskbarAllAppsController taskbarAllAppsController2, TaskbarInsetsController taskbarInsetsController2) {
        this.taskbarActivityContext = taskbarActivityContext2;
        this.taskbarDragController = taskbarDragController2;
        this.navButtonController = taskbarNavButtonController;
        this.navbarButtonsViewController = navbarButtonsViewController2;
        this.rotationButtonController = rotationButtonController2;
        this.taskbarDragLayerController = taskbarDragLayerController2;
        this.taskbarViewController = taskbarViewController2;
        this.taskbarScrimViewController = taskbarScrimViewController2;
        this.taskbarUnfoldAnimationController = taskbarUnfoldAnimationController2;
        this.taskbarKeyguardController = taskbarKeyguardController2;
        this.stashedHandleViewController = stashedHandleViewController2;
        this.taskbarStashController = taskbarStashController2;
        this.taskbarEduController = taskbarEduController2;
        this.taskbarAutohideSuspendController = taskbarAutohideSuspendController2;
        this.taskbarPopupController = taskbarPopupController2;
        this.taskbarForceVisibleImmersiveController = taskbarForceVisibleImmersiveController2;
        this.taskbarAllAppsController = taskbarAllAppsController2;
        this.taskbarInsetsController = taskbarInsetsController2;
    }

    public void init(TaskbarSharedState taskbarSharedState) {
        this.mAreAllControllersInitialized = false;
        this.mSharedState = taskbarSharedState;
        this.taskbarDragController.init(this);
        this.navbarButtonsViewController.init(this);
        this.rotationButtonController.init();
        this.taskbarDragLayerController.init(this);
        this.taskbarViewController.init(this);
        this.taskbarScrimViewController.init(this);
        this.taskbarUnfoldAnimationController.init(this);
        this.taskbarKeyguardController.init(this.navbarButtonsViewController);
        this.stashedHandleViewController.init(this);
        this.taskbarStashController.init(this, taskbarSharedState.setupUIVisible);
        this.taskbarEduController.init(this);
        this.taskbarPopupController.init(this);
        this.taskbarForceVisibleImmersiveController.init(this);
        this.taskbarAllAppsController.init(this, taskbarSharedState.allAppsVisible);
        this.navButtonController.init(this);
        this.taskbarInsetsController.init(this);
        this.mControllersToLog = new LoggableTaskbarController[]{this.taskbarDragController, this.navButtonController, this.navbarButtonsViewController, this.taskbarDragLayerController, this.taskbarScrimViewController, this.taskbarViewController, this.taskbarUnfoldAnimationController, this.taskbarKeyguardController, this.stashedHandleViewController, this.taskbarStashController, this.taskbarEduController, this.taskbarAutohideSuspendController, this.taskbarPopupController, this.taskbarInsetsController};
        this.mAreAllControllersInitialized = true;
        for (Runnable run : this.mPostInitCallbacks) {
            run.run();
        }
        this.mPostInitCallbacks.clear();
    }

    public TaskbarSharedState getSharedState() {
        return this.mSharedState;
    }

    public void onConfigurationChanged(int i) {
        this.navbarButtonsViewController.onConfigurationChanged(i);
    }

    public void onDestroy() {
        this.mSharedState = null;
        this.navbarButtonsViewController.onDestroy();
        this.uiController.onDestroy();
        this.rotationButtonController.onDestroy();
        this.taskbarDragLayerController.onDestroy();
        this.taskbarKeyguardController.onDestroy();
        this.taskbarUnfoldAnimationController.onDestroy();
        this.taskbarViewController.onDestroy();
        this.stashedHandleViewController.onDestroy();
        this.taskbarAutohideSuspendController.onDestroy();
        this.taskbarPopupController.onDestroy();
        this.taskbarForceVisibleImmersiveController.onDestroy();
        this.taskbarAllAppsController.onDestroy();
        this.navButtonController.onDestroy();
        this.taskbarInsetsController.onDestroy();
        this.mControllersToLog = null;
    }

    public void runAfterInit(Runnable runnable) {
        if (this.mAreAllControllersInitialized) {
            runnable.run();
        } else {
            this.mPostInitCallbacks.add(runnable);
        }
    }

    /* access modifiers changed from: protected */
    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "TaskbarControllers:");
        if (this.mControllersToLog == null) {
            printWriter.println(String.format("%s\t%s", new Object[]{str, "All taskbar controllers have already been destroyed."}));
            return;
        }
        printWriter.println(String.format("%s\tmAreAllControllersInitialized=%b", new Object[]{str, Boolean.valueOf(this.mAreAllControllersInitialized)}));
        LoggableTaskbarController[] loggableTaskbarControllerArr = this.mControllersToLog;
        int length = loggableTaskbarControllerArr.length;
        for (int i = 0; i < length; i++) {
            loggableTaskbarControllerArr[i].dumpLogs(str + "\t", printWriter);
        }
        this.uiController.dumpLogs(str + "\t", printWriter);
        this.rotationButtonController.dumpLogs(str + "\t", printWriter);
    }

    /* access modifiers changed from: package-private */
    public TaskbarActivityContext getTaskbarActivityContext() {
        return this.taskbarActivityContext;
    }
}
