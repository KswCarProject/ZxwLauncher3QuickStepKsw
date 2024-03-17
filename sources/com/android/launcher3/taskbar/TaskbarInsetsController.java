package com.android.launcher3.taskbar;

import android.graphics.Insets;
import android.graphics.Region;
import android.view.WindowManager;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.taskbar.TaskbarControllers;
import com.android.quickstep.KtR;
import com.android.systemui.shared.system.ViewTreeObserverWrapper;
import com.android.systemui.shared.system.WindowManagerWrapper;
import java.io.PrintWriter;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0018\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0010H\u0002J\u000e\u0010\u001d\u001a\u00020\u000e2\u0006\u0010\t\u001a\u00020\nJ\u0006\u0010\u001e\u001a\u00020\u000eJ\u0006\u0010\u001f\u001a\u00020\u000eJ\u000e\u0010 \u001a\u00020\u000e2\u0006\u0010!\u001a\u00020\"R\u000e\u0010\u0005\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX.¢\u0006\u0002\n\u0000R\u001a\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000e0\fX\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u000f\u001a\u00020\u0010¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u000e\u0010\u0013\u001a\u00020\u0014X.¢\u0006\u0002\n\u0000¨\u0006#"}, d2 = {"Lcom/android/launcher3/taskbar/TaskbarInsetsController;", "Lcom/android/launcher3/taskbar/TaskbarControllers$LoggableTaskbarController;", "context", "Lcom/android/launcher3/taskbar/TaskbarActivityContext;", "(Lcom/android/launcher3/taskbar/TaskbarActivityContext;)V", "contentRegion", "Landroid/graphics/Region;", "getContext", "()Lcom/android/launcher3/taskbar/TaskbarActivityContext;", "controllers", "Lcom/android/launcher3/taskbar/TaskbarControllers;", "deviceProfileChangeListener", "Lkotlin/Function1;", "Lcom/android/launcher3/DeviceProfile;", "", "taskbarHeightForIme", "", "getTaskbarHeightForIme", "()I", "windowLayoutParams", "Landroid/view/WindowManager$LayoutParams;", "dumpLogs", "prefix", "", "pw", "Ljava/io/PrintWriter;", "getReducingInsetsForTaskbarInsetsHeight", "Landroid/graphics/Insets;", "height", "init", "onDestroy", "onTaskbarWindowHeightOrInsetsChanged", "updateInsetsTouchability", "insetsInfo", "Lcom/android/systemui/shared/system/ViewTreeObserverWrapper$InsetsInfo;", "Launcher3_Android13_aospWithQuickstepRelease"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: TaskbarInsetsController.kt */
public final class TaskbarInsetsController implements TaskbarControllers.LoggableTaskbarController {
    private final Region contentRegion = new Region();
    private final TaskbarActivityContext context;
    private TaskbarControllers controllers;
    private final Function1<DeviceProfile, Unit> deviceProfileChangeListener = new TaskbarInsetsController$deviceProfileChangeListener$1(this);
    private final int taskbarHeightForIme;
    private WindowManager.LayoutParams windowLayoutParams;

    public TaskbarInsetsController(TaskbarActivityContext taskbarActivityContext) {
        Intrinsics.checkNotNullParameter(taskbarActivityContext, "context");
        this.context = taskbarActivityContext;
        this.taskbarHeightForIme = taskbarActivityContext.getResources().getDimensionPixelSize(KtR.dimen.taskbar_ime_size);
    }

    public final TaskbarActivityContext getContext() {
        return this.context;
    }

    public final int getTaskbarHeightForIme() {
        return this.taskbarHeightForIme;
    }

    public final void init(TaskbarControllers taskbarControllers) {
        Intrinsics.checkNotNullParameter(taskbarControllers, "controllers");
        this.controllers = taskbarControllers;
        WindowManager.LayoutParams windowLayoutParams2 = this.context.getWindowLayoutParams();
        Intrinsics.checkNotNullExpressionValue(windowLayoutParams2, "context.windowLayoutParams");
        this.windowLayoutParams = windowLayoutParams2;
        WindowManagerWrapper instance = WindowManagerWrapper.getInstance();
        Intrinsics.checkNotNullExpressionValue(instance, "getInstance()");
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
        WindowManager.LayoutParams layoutParams2 = null;
        if (layoutParams == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
            layoutParams = null;
        }
        instance.setProvidesInsetsTypes(layoutParams, new int[]{21, 18, 8});
        WindowManager.LayoutParams layoutParams3 = this.windowLayoutParams;
        if (layoutParams3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
            layoutParams3 = null;
        }
        layoutParams3.providedInternalInsets = new Insets[24];
        WindowManager.LayoutParams layoutParams4 = this.windowLayoutParams;
        if (layoutParams4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
            layoutParams4 = null;
        }
        layoutParams4.providedInternalImeInsets = new Insets[24];
        onTaskbarWindowHeightOrInsetsChanged();
        WindowManager.LayoutParams layoutParams5 = this.windowLayoutParams;
        if (layoutParams5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
        } else {
            layoutParams2 = layoutParams5;
        }
        layoutParams2.insetsRoundedCornerFrame = true;
        this.context.addOnDeviceProfileChangeListener(new DeviceProfile.OnDeviceProfileChangeListener() {
            public final void onDeviceProfileChanged(DeviceProfile deviceProfile) {
                TaskbarInsetsController.m60init$lambda0(Function1.this, deviceProfile);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: init$lambda-0  reason: not valid java name */
    public static final void m60init$lambda0(Function1 function1, DeviceProfile deviceProfile) {
        Intrinsics.checkNotNullParameter(function1, "$tmp0");
        function1.invoke(deviceProfile);
    }

    /* access modifiers changed from: private */
    /* renamed from: onDestroy$lambda-1  reason: not valid java name */
    public static final void m61onDestroy$lambda1(Function1 function1, DeviceProfile deviceProfile) {
        Intrinsics.checkNotNullParameter(function1, "$tmp0");
        function1.invoke(deviceProfile);
    }

    public final void onDestroy() {
        this.context.removeOnDeviceProfileChangeListener(new DeviceProfile.OnDeviceProfileChangeListener() {
            public final void onDeviceProfileChanged(DeviceProfile deviceProfile) {
                TaskbarInsetsController.m61onDestroy$lambda1(Function1.this, deviceProfile);
            }
        });
    }

    public final void onTaskbarWindowHeightOrInsetsChanged() {
        TaskbarControllers taskbarControllers = this.controllers;
        WindowManager.LayoutParams layoutParams = null;
        if (taskbarControllers == null) {
            Intrinsics.throwUninitializedPropertyAccessException("controllers");
            taskbarControllers = null;
        }
        Insets reducingInsetsForTaskbarInsetsHeight = getReducingInsetsForTaskbarInsetsHeight(taskbarControllers.taskbarStashController.getContentHeightToReportToApps());
        Region region = this.contentRegion;
        int i = reducingInsetsForTaskbarInsetsHeight.top;
        int i2 = this.context.getDeviceProfile().widthPx;
        WindowManager.LayoutParams layoutParams2 = this.windowLayoutParams;
        if (layoutParams2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
            layoutParams2 = null;
        }
        region.set(0, i, i2, layoutParams2.height);
        WindowManager.LayoutParams layoutParams3 = this.windowLayoutParams;
        if (layoutParams3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
            layoutParams3 = null;
        }
        layoutParams3.providedInternalInsets[21] = reducingInsetsForTaskbarInsetsHeight;
        WindowManager.LayoutParams layoutParams4 = this.windowLayoutParams;
        if (layoutParams4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
            layoutParams4 = null;
        }
        layoutParams4.providedInternalInsets[8] = reducingInsetsForTaskbarInsetsHeight;
        TaskbarControllers taskbarControllers2 = this.controllers;
        if (taskbarControllers2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("controllers");
            taskbarControllers2 = null;
        }
        Insets reducingInsetsForTaskbarInsetsHeight2 = getReducingInsetsForTaskbarInsetsHeight(taskbarControllers2.taskbarStashController.getTappableHeightToReportToApps());
        WindowManager.LayoutParams layoutParams5 = this.windowLayoutParams;
        if (layoutParams5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
            layoutParams5 = null;
        }
        layoutParams5.providedInternalInsets[18] = reducingInsetsForTaskbarInsetsHeight2;
        WindowManager.LayoutParams layoutParams6 = this.windowLayoutParams;
        if (layoutParams6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
            layoutParams6 = null;
        }
        layoutParams6.providedInternalInsets[8] = reducingInsetsForTaskbarInsetsHeight2;
        Insets reducingInsetsForTaskbarInsetsHeight3 = getReducingInsetsForTaskbarInsetsHeight(this.taskbarHeightForIme);
        WindowManager.LayoutParams layoutParams7 = this.windowLayoutParams;
        if (layoutParams7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
            layoutParams7 = null;
        }
        layoutParams7.providedInternalImeInsets[21] = reducingInsetsForTaskbarInsetsHeight3;
        WindowManager.LayoutParams layoutParams8 = this.windowLayoutParams;
        if (layoutParams8 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
            layoutParams8 = null;
        }
        layoutParams8.providedInternalImeInsets[18] = reducingInsetsForTaskbarInsetsHeight3;
        WindowManager.LayoutParams layoutParams9 = this.windowLayoutParams;
        if (layoutParams9 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
        } else {
            layoutParams = layoutParams9;
        }
        layoutParams.providedInternalImeInsets[8] = reducingInsetsForTaskbarInsetsHeight3;
    }

    private final Insets getReducingInsetsForTaskbarInsetsHeight(int i) {
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
        if (layoutParams == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
            layoutParams = null;
        }
        Insets of = Insets.of(0, layoutParams.height - i, 0, 0);
        Intrinsics.checkNotNullExpressionValue(of, "of(0, windowLayoutParams.height - height, 0, 0)");
        return of;
    }

    public final void updateInsetsTouchability(ViewTreeObserverWrapper.InsetsInfo insetsInfo) {
        Intrinsics.checkNotNullParameter(insetsInfo, "insetsInfo");
        insetsInfo.touchableRegion.setEmpty();
        TaskbarControllers taskbarControllers = this.controllers;
        TaskbarControllers taskbarControllers2 = null;
        if (taskbarControllers == null) {
            Intrinsics.throwUninitializedPropertyAccessException("controllers");
            taskbarControllers = null;
        }
        taskbarControllers.navbarButtonsViewController.addVisibleButtonsRegion(this.context.getDragLayer(), insetsInfo.touchableRegion);
        boolean z = true;
        int i = 3;
        if (this.context.getDragLayer().getAlpha() < 0.01f) {
            insetsInfo.setTouchableInsets(3);
        } else {
            TaskbarControllers taskbarControllers3 = this.controllers;
            if (taskbarControllers3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("controllers");
                taskbarControllers3 = null;
            }
            if (taskbarControllers3.navbarButtonsViewController.isImeVisible()) {
                insetsInfo.setTouchableInsets(3);
            } else {
                TaskbarControllers taskbarControllers4 = this.controllers;
                if (taskbarControllers4 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("controllers");
                    taskbarControllers4 = null;
                }
                if (!taskbarControllers4.uiController.isTaskbarTouchable()) {
                    insetsInfo.setTouchableInsets(3);
                } else {
                    TaskbarControllers taskbarControllers5 = this.controllers;
                    if (taskbarControllers5 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("controllers");
                        taskbarControllers5 = null;
                    }
                    if (taskbarControllers5.taskbarDragController.isSystemDragInProgress()) {
                        insetsInfo.setTouchableInsets(3);
                    } else if (AbstractFloatingView.hasOpenView(this.context, 131072)) {
                        insetsInfo.setTouchableInsets(3);
                    } else {
                        TaskbarControllers taskbarControllers6 = this.controllers;
                        if (taskbarControllers6 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("controllers");
                        } else {
                            taskbarControllers2 = taskbarControllers6;
                        }
                        if (taskbarControllers2.taskbarViewController.areIconsVisible() || AbstractFloatingView.hasOpenView(this.context, AbstractFloatingView.TYPE_ALL) || this.context.isNavBarKidsModeActive()) {
                            if (this.context.isTaskbarWindowFullscreen()) {
                                i = 0;
                            } else {
                                insetsInfo.touchableRegion.set(this.contentRegion);
                            }
                            insetsInfo.setTouchableInsets(i);
                            z = false;
                        } else {
                            insetsInfo.setTouchableInsets(3);
                        }
                    }
                }
            }
        }
        this.context.excludeFromMagnificationRegion(z);
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        Intrinsics.checkNotNullParameter(str, "prefix");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        printWriter.println(Intrinsics.stringPlus(str, "TaskbarInsetsController:"));
        StringBuilder append = new StringBuilder().append(str).append("\twindowHeight=");
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
        WindowManager.LayoutParams layoutParams2 = null;
        if (layoutParams == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
            layoutParams = null;
        }
        printWriter.println(append.append(layoutParams.height).toString());
        StringBuilder append2 = new StringBuilder().append(str).append("\tprovidedInternalInsets[ITYPE_EXTRA_NAVIGATION_BAR]=");
        WindowManager.LayoutParams layoutParams3 = this.windowLayoutParams;
        if (layoutParams3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
            layoutParams3 = null;
        }
        printWriter.println(append2.append(layoutParams3.providedInternalInsets[21]).toString());
        StringBuilder append3 = new StringBuilder().append(str).append("\tprovidedInternalInsets[ITYPE_BOTTOM_TAPPABLE_ELEMENT]=");
        WindowManager.LayoutParams layoutParams4 = this.windowLayoutParams;
        if (layoutParams4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
            layoutParams4 = null;
        }
        printWriter.println(append3.append(layoutParams4.providedInternalInsets[18]).toString());
        StringBuilder append4 = new StringBuilder().append(str).append("\tprovidedInternalImeInsets[ITYPE_EXTRA_NAVIGATION_BAR]=");
        WindowManager.LayoutParams layoutParams5 = this.windowLayoutParams;
        if (layoutParams5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
            layoutParams5 = null;
        }
        printWriter.println(append4.append(layoutParams5.providedInternalImeInsets[21]).toString());
        StringBuilder append5 = new StringBuilder().append(str).append("\tprovidedInternalImeInsets[ITYPE_BOTTOM_TAPPABLE_ELEMENT]=");
        WindowManager.LayoutParams layoutParams6 = this.windowLayoutParams;
        if (layoutParams6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("windowLayoutParams");
        } else {
            layoutParams2 = layoutParams6;
        }
        printWriter.println(append5.append(layoutParams2.providedInternalImeInsets[18]).toString());
    }
}
