package com.android.launcher3.taskbar;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.taskbar.TaskbarControllers;
import com.android.systemui.shared.system.QuickStepContract;
import java.io.PrintWriter;

public class TaskbarKeyguardController implements TaskbarControllers.LoggableTaskbarController {
    private static final int KEYGUARD_SYSUI_FLAGS = 6292424;
    private boolean mBouncerShowing;
    /* access modifiers changed from: private */
    public final TaskbarActivityContext mContext;
    /* access modifiers changed from: private */
    public boolean mIsScreenOff;
    private final KeyguardManager mKeyguardManager;
    private int mKeyguardSysuiFlags;
    private NavbarButtonsViewController mNavbarButtonsViewController;
    private final BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean unused = TaskbarKeyguardController.this.mIsScreenOff = true;
            AbstractFloatingView.closeOpenViews(TaskbarKeyguardController.this.mContext, false, AbstractFloatingView.TYPE_ALL);
        }
    };

    public TaskbarKeyguardController(TaskbarActivityContext taskbarActivityContext) {
        this.mContext = taskbarActivityContext;
        this.mKeyguardManager = (KeyguardManager) taskbarActivityContext.getSystemService(KeyguardManager.class);
    }

    public void init(NavbarButtonsViewController navbarButtonsViewController) {
        this.mNavbarButtonsViewController = navbarButtonsViewController;
        this.mContext.registerReceiver(this.mScreenOffReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
    }

    public void updateStateForSysuiFlags(int i) {
        boolean z = false;
        boolean z2 = (i & 8) != 0;
        boolean z3 = (i & 64) != 0;
        boolean z4 = (i & 512) != 0;
        boolean z5 = (2097152 & i) != 0;
        int i2 = i & KEYGUARD_SYSUI_FLAGS;
        if (i2 != this.mKeyguardSysuiFlags) {
            this.mKeyguardSysuiFlags = i2;
            this.mBouncerShowing = z2;
            NavbarButtonsViewController navbarButtonsViewController = this.mNavbarButtonsViewController;
            if (z3 || z5) {
                z = true;
            }
            navbarButtonsViewController.setKeyguardVisible(z, z4);
            updateIconsForBouncer();
            if (z3) {
                AbstractFloatingView.closeOpenViews(this.mContext, true, AbstractFloatingView.TYPE_ALL);
            }
        }
    }

    public boolean isScreenOff() {
        return this.mIsScreenOff;
    }

    public void setScreenOn() {
        this.mIsScreenOff = false;
    }

    private void updateIconsForBouncer() {
        int i = this.mKeyguardSysuiFlags;
        boolean z = true;
        if (!(!((4194304 & i) != 0) && ((i & 128) != 0) && ((i & 256) != 0)) || !this.mKeyguardManager.isDeviceSecure() || !this.mBouncerShowing) {
            z = false;
        }
        this.mNavbarButtonsViewController.setBackForBouncer(z);
    }

    public void onDestroy() {
        this.mContext.unregisterReceiver(this.mScreenOffReceiver);
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "TaskbarKeyguardController:");
        printWriter.println(String.format("%s\tmKeyguardSysuiFlags=%s", new Object[]{str, QuickStepContract.getSystemUiStateString(this.mKeyguardSysuiFlags)}));
        printWriter.println(String.format("%s\tmBouncerShowing=%b", new Object[]{str, Boolean.valueOf(this.mBouncerShowing)}));
        printWriter.println(String.format("%s\tmIsScreenOff=%b", new Object[]{str, Boolean.valueOf(this.mIsScreenOff)}));
    }
}
