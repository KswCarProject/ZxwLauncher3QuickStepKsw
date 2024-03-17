package com.android.launcher3.testing;

import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.android.launcher3.Utilities;
import java.util.function.BiConsumer;

public final class TestLogging {
    private static BiConsumer<String, String> sEventConsumer;
    public static boolean sHadEventsNotFromTest;

    private static void recordEventSlow(String str, String str2) {
        Log.d(TestProtocol.TAPL_EVENTS_TAG, str + " / " + str2);
        BiConsumer<String, String> biConsumer = sEventConsumer;
        if (biConsumer != null) {
            biConsumer.accept(str, str2);
        }
    }

    public static void recordEvent(String str, String str2) {
        if (Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            recordEventSlow(str, str2);
        }
    }

    public static void recordEvent(String str, String str2, Object obj) {
        if (Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            recordEventSlow(str, str2 + ": " + obj);
        }
    }

    private static void registerEventNotFromTest(InputEvent inputEvent) {
        if (!sHadEventsNotFromTest && inputEvent.getDeviceId() != -1) {
            sHadEventsNotFromTest = true;
            Log.d(TestProtocol.PERMANENT_DIAG_TAG, "First event not from test: " + inputEvent);
        }
    }

    public static void recordKeyEvent(String str, String str2, KeyEvent keyEvent) {
        if (Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            recordEventSlow(str, str2 + ": " + keyEvent);
            registerEventNotFromTest(keyEvent);
        }
    }

    public static void recordMotionEvent(String str, String str2, MotionEvent motionEvent) {
        if (Utilities.IS_RUNNING_IN_TEST_HARNESS && motionEvent.getAction() != 2) {
            recordEventSlow(str, str2 + ": " + motionEvent);
            registerEventNotFromTest(motionEvent);
        }
    }

    static void setEventConsumer(BiConsumer<String, String> biConsumer) {
        sEventConsumer = biConsumer;
    }
}
