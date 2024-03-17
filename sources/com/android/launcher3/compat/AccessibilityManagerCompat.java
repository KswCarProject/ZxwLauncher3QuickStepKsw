package com.android.launcher3.compat;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import com.android.launcher3.Utilities;
import com.android.launcher3.testing.TestProtocol;

public class AccessibilityManagerCompat {
    public static boolean isAccessibilityEnabled(Context context) {
        return getManager(context).isEnabled();
    }

    public static boolean isObservedEventType(Context context, int i) {
        return isAccessibilityEnabled(context);
    }

    public static void sendCustomAccessibilityEvent(View view, int i, String str) {
        if (view != null && isObservedEventType(view.getContext(), i)) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain(i);
            view.onInitializeAccessibilityEvent(obtain);
            if (!TextUtils.isEmpty(str)) {
                obtain.getText().add(str);
            }
            getManager(view.getContext()).sendAccessibilityEvent(obtain);
        }
    }

    private static AccessibilityManager getManager(Context context) {
        return (AccessibilityManager) context.getSystemService("accessibility");
    }

    public static void sendStateEventToTest(Context context, int i) {
        AccessibilityManager accessibilityManagerForTest = getAccessibilityManagerForTest(context);
        if (accessibilityManagerForTest != null) {
            Bundle bundle = new Bundle();
            bundle.putInt(TestProtocol.STATE_FIELD, i);
            sendEventToTest(accessibilityManagerForTest, context, TestProtocol.SWITCHED_TO_STATE_MESSAGE, bundle);
            Log.d(TestProtocol.PERMANENT_DIAG_TAG, "sendStateEventToTest: " + i);
        }
    }

    public static void sendScrollFinishedEventToTest(Context context) {
        AccessibilityManager accessibilityManagerForTest = getAccessibilityManagerForTest(context);
        if (accessibilityManagerForTest != null) {
            sendEventToTest(accessibilityManagerForTest, context, TestProtocol.SCROLL_FINISHED_MESSAGE, (Bundle) null);
        }
    }

    public static void sendPauseDetectedEventToTest(Context context) {
        AccessibilityManager accessibilityManagerForTest = getAccessibilityManagerForTest(context);
        if (accessibilityManagerForTest != null) {
            sendEventToTest(accessibilityManagerForTest, context, TestProtocol.PAUSE_DETECTED_MESSAGE, (Bundle) null);
        }
    }

    public static void sendDismissAnimationEndsEventToTest(Context context) {
        AccessibilityManager accessibilityManagerForTest = getAccessibilityManagerForTest(context);
        if (accessibilityManagerForTest != null) {
            sendEventToTest(accessibilityManagerForTest, context, TestProtocol.DISMISS_ANIMATION_ENDS_MESSAGE, (Bundle) null);
        }
    }

    public static void sendFolderOpenedEventToTest(Context context) {
        AccessibilityManager accessibilityManagerForTest = getAccessibilityManagerForTest(context);
        if (accessibilityManagerForTest != null) {
            sendEventToTest(accessibilityManagerForTest, context, TestProtocol.FOLDER_OPENED_MESSAGE, (Bundle) null);
        }
    }

    private static void sendEventToTest(AccessibilityManager accessibilityManager, Context context, String str, Bundle bundle) {
        AccessibilityEvent obtain = AccessibilityEvent.obtain(16384);
        obtain.setClassName(str);
        obtain.setParcelableData(bundle);
        obtain.setPackageName(context.getApplicationContext().getPackageName());
        accessibilityManager.sendAccessibilityEvent(obtain);
    }

    private static AccessibilityManager getAccessibilityManagerForTest(Context context) {
        if (!Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            return null;
        }
        AccessibilityManager manager = getManager(context);
        if (!manager.isEnabled()) {
            return null;
        }
        return manager;
    }

    public static int getRecommendedTimeoutMillis(Context context, int i, int i2) {
        return Utilities.ATLEAST_Q ? getManager(context).getRecommendedTimeoutMillis(i, i2) : i;
    }
}
