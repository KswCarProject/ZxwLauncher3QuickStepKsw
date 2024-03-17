package com.android.systemui;

import android.util.EventLog;

public class EventLogTags {
    public static final int SYSUI_FULLSCREEN_NOTIFICATION = 36002;
    public static final int SYSUI_HEADS_UP_ESCALATION = 36003;
    public static final int SYSUI_HEADS_UP_STATUS = 36001;
    public static final int SYSUI_LOCKSCREEN_GESTURE = 36021;
    public static final int SYSUI_NOTIFICATIONPANEL_TOUCH = 36020;
    public static final int SYSUI_PANELBAR_TOUCH = 36010;
    public static final int SYSUI_PANELHOLDER_TOUCH = 36040;
    public static final int SYSUI_QUICKPANEL_TOUCH = 36030;
    public static final int SYSUI_RECENTS_CONNECTION = 36060;
    public static final int SYSUI_SEARCHPANEL_TOUCH = 36050;
    public static final int SYSUI_STATUSBAR_TOUCH = 36000;
    public static final int SYSUI_STATUS_BAR_STATE = 36004;

    private EventLogTags() {
    }

    public static void writeSysuiStatusbarTouch(int i, int i2, int i3, int i4, int i5) {
        EventLog.writeEvent(SYSUI_STATUSBAR_TOUCH, new Object[]{Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5)});
    }

    public static void writeSysuiHeadsUpStatus(String str, int i) {
        EventLog.writeEvent(SYSUI_HEADS_UP_STATUS, new Object[]{str, Integer.valueOf(i)});
    }

    public static void writeSysuiFullscreenNotification(String str) {
        EventLog.writeEvent(SYSUI_FULLSCREEN_NOTIFICATION, str);
    }

    public static void writeSysuiHeadsUpEscalation(String str) {
        EventLog.writeEvent(SYSUI_HEADS_UP_ESCALATION, str);
    }

    public static void writeSysuiStatusBarState(int i, int i2, int i3, int i4, int i5, int i6) {
        EventLog.writeEvent(SYSUI_STATUS_BAR_STATE, new Object[]{Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6)});
    }

    public static void writeSysuiPanelbarTouch(int i, int i2, int i3, int i4) {
        EventLog.writeEvent(SYSUI_PANELBAR_TOUCH, new Object[]{Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)});
    }

    public static void writeSysuiNotificationpanelTouch(int i, int i2, int i3) {
        EventLog.writeEvent(SYSUI_NOTIFICATIONPANEL_TOUCH, new Object[]{Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3)});
    }

    public static void writeSysuiLockscreenGesture(int i, int i2, int i3) {
        EventLog.writeEvent(SYSUI_LOCKSCREEN_GESTURE, new Object[]{Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3)});
    }

    public static void writeSysuiQuickpanelTouch(int i, int i2, int i3) {
        EventLog.writeEvent(SYSUI_QUICKPANEL_TOUCH, new Object[]{Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3)});
    }

    public static void writeSysuiPanelholderTouch(int i, int i2, int i3) {
        EventLog.writeEvent(SYSUI_PANELHOLDER_TOUCH, new Object[]{Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3)});
    }

    public static void writeSysuiSearchpanelTouch(int i, int i2, int i3) {
        EventLog.writeEvent(SYSUI_SEARCHPANEL_TOUCH, new Object[]{Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3)});
    }

    public static void writeSysuiRecentsConnection(int i, int i2) {
        EventLog.writeEvent(SYSUI_RECENTS_CONNECTION, new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
    }
}
