package com.android.launcher3.taskbar;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.android.launcher3.R;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.taskbar.TaskbarControllers;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.TaskUtils;
import com.android.quickstep.TouchInteractionService;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TaskbarNavButtonController implements TaskbarControllers.LoggableTaskbarController {
    static final int BUTTON_A11Y = 16;
    static final int BUTTON_BACK = 1;
    static final int BUTTON_HOME = 2;
    static final int BUTTON_IME_SWITCH = 8;
    static final int BUTTON_NOTIFICATIONS = 64;
    static final int BUTTON_QUICK_SETTINGS = 32;
    static final int BUTTON_RECENTS = 4;
    static final int SCREEN_PIN_LONG_PRESS_RESET = 300;
    static final int SCREEN_PIN_LONG_PRESS_THRESHOLD = 200;
    private static final int SCREEN_UNPIN_COMBO = 5;
    private static final String TAG = "TaskbarNavButtonController";
    private final Handler mHandler;
    private long mLastScreenPinLongPress;
    private int mLongPressedButtons = 0;
    private final Runnable mResetLongPress = new Runnable() {
        public final void run() {
            TaskbarNavButtonController.this.resetScreenUnpin();
        }
    };
    private boolean mScreenPinned;
    private final TouchInteractionService mService;
    private StatsLogManager mStatsLogManager;
    private final SystemUiProxy mSystemUiProxy;

    @Retention(RetentionPolicy.SOURCE)
    public @interface TaskbarButton {
    }

    public int getButtonContentDescription(int i) {
        if (i == 1) {
            return R.string.taskbar_button_back;
        }
        if (i == 2) {
            return R.string.taskbar_button_home;
        }
        if (i == 4) {
            return R.string.taskbar_button_recents;
        }
        if (i == 8) {
            return R.string.taskbar_button_ime_switcher;
        }
        if (i == 16) {
            return R.string.taskbar_button_a11y;
        }
        if (i == 32) {
            return R.string.taskbar_button_quick_settings;
        }
        if (i != 64) {
            return 0;
        }
        return R.string.taskbar_button_notifications;
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "TaskbarNavButtonController:");
        printWriter.println(String.format("%s\tmLastScreenPinLongPress=%dms", new Object[]{str, Long.valueOf(this.mLastScreenPinLongPress)}));
        printWriter.println(String.format("%s\tmScreenPinned=%b", new Object[]{str, Boolean.valueOf(this.mScreenPinned)}));
    }

    public TaskbarNavButtonController(TouchInteractionService touchInteractionService, SystemUiProxy systemUiProxy, Handler handler) {
        this.mService = touchInteractionService;
        this.mSystemUiProxy = systemUiProxy;
        this.mHandler = handler;
    }

    public void onButtonClick(int i) {
        if (i == 1) {
            logEvent(StatsLogManager.LauncherEvent.LAUNCHER_TASKBAR_BACK_BUTTON_TAP);
            executeBack();
        } else if (i == 2) {
            logEvent(StatsLogManager.LauncherEvent.LAUNCHER_TASKBAR_HOME_BUTTON_TAP);
            navigateHome();
        } else if (i == 4) {
            logEvent(StatsLogManager.LauncherEvent.LAUNCHER_TASKBAR_OVERVIEW_BUTTON_TAP);
            navigateToOverview();
        } else if (i == 8) {
            logEvent(StatsLogManager.LauncherEvent.LAUNCHER_TASKBAR_IME_SWITCHER_BUTTON_TAP);
            showIMESwitcher();
        } else if (i == 16) {
            logEvent(StatsLogManager.LauncherEvent.LAUNCHER_TASKBAR_A11Y_BUTTON_TAP);
            notifyA11yClick(false);
        } else if (i == 32) {
            showQuickSettings();
        } else if (i == 64) {
            showNotifications();
        }
    }

    public boolean onButtonLongClick(int i) {
        if (i == 1) {
            logEvent(StatsLogManager.LauncherEvent.LAUNCHER_TASKBAR_BACK_BUTTON_LONGPRESS);
            return backRecentsLongpress(i);
        } else if (i == 2) {
            logEvent(StatsLogManager.LauncherEvent.LAUNCHER_TASKBAR_HOME_BUTTON_LONGPRESS);
            startAssistant();
            return true;
        } else if (i == 4) {
            logEvent(StatsLogManager.LauncherEvent.LAUNCHER_TASKBAR_OVERVIEW_BUTTON_LONGPRESS);
            return backRecentsLongpress(i);
        } else if (i != 16) {
            return false;
        } else {
            logEvent(StatsLogManager.LauncherEvent.LAUNCHER_TASKBAR_A11Y_BUTTON_LONGPRESS);
            notifyA11yClick(true);
            return true;
        }
    }

    private boolean backRecentsLongpress(int i) {
        this.mLongPressedButtons = i | this.mLongPressedButtons;
        return determineScreenUnpin();
    }

    private boolean determineScreenUnpin() {
        long currentTimeMillis = System.currentTimeMillis();
        if (!this.mScreenPinned) {
            return false;
        }
        long j = this.mLastScreenPinLongPress;
        if (j == 0) {
            this.mLastScreenPinLongPress = System.currentTimeMillis();
            this.mHandler.postDelayed(this.mResetLongPress, 300);
            return true;
        } else if (currentTimeMillis - j > 200) {
            resetScreenUnpin();
            return false;
        } else {
            if ((this.mLongPressedButtons & 5) == 5) {
                this.mSystemUiProxy.stopScreenPinning();
                this.mHandler.removeCallbacks(this.mResetLongPress);
                resetScreenUnpin();
            }
            return true;
        }
    }

    /* access modifiers changed from: private */
    public void resetScreenUnpin() {
        this.mLongPressedButtons = 0;
        this.mLastScreenPinLongPress = 0;
    }

    public void updateSysuiFlags(int i) {
        boolean z = true;
        if ((i & 1) == 0) {
            z = false;
        }
        this.mScreenPinned = z;
    }

    public void init(TaskbarControllers taskbarControllers) {
        this.mStatsLogManager = taskbarControllers.getTaskbarActivityContext().getStatsLogManager();
    }

    public void onDestroy() {
        this.mStatsLogManager = null;
    }

    private void logEvent(StatsLogManager.LauncherEvent launcherEvent) {
        StatsLogManager statsLogManager = this.mStatsLogManager;
        if (statsLogManager == null) {
            Log.w(TAG, "No stats log manager to log taskbar button event");
        } else {
            statsLogManager.logger().log(launcherEvent);
        }
    }

    private void navigateHome() {
        this.mService.getOverviewCommandHelper().addCommand(5);
    }

    private void navigateToOverview() {
        if (!this.mScreenPinned) {
            TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "onOverviewToggle");
            TaskUtils.closeSystemWindowsAsync(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_RECENTS);
            this.mService.getOverviewCommandHelper().addCommand(4);
        }
    }

    private void executeBack() {
        this.mSystemUiProxy.onBackPressed();
    }

    private void showIMESwitcher() {
        this.mSystemUiProxy.onImeSwitcherPressed();
    }

    private void notifyA11yClick(boolean z) {
        if (z) {
            this.mSystemUiProxy.notifyAccessibilityButtonLongClicked();
        } else {
            this.mSystemUiProxy.notifyAccessibilityButtonClicked(this.mService.getDisplayId());
        }
    }

    private void startAssistant() {
        if (!this.mScreenPinned) {
            Bundle bundle = new Bundle();
            bundle.putInt("invocation_type", 5);
            this.mSystemUiProxy.startAssistant(bundle);
        }
    }

    private void showQuickSettings() {
        this.mSystemUiProxy.toggleNotificationPanel();
    }

    private void showNotifications() {
        this.mSystemUiProxy.toggleNotificationPanel();
    }
}
