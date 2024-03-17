package com.android.quickstep.util;

import com.android.launcher3.logging.EventLogArray;

public class ActiveGestureLog extends EventLogArray {
    public static final ActiveGestureLog INSTANCE = new ActiveGestureLog();
    public static final String INTENT_EXTRA_LOG_TRACE_ID = "INTENT_EXTRA_LOG_TRACE_ID";

    private ActiveGestureLog() {
        super("touch_interaction_log", 40);
    }
}
