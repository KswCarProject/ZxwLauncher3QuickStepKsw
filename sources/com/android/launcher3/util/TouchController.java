package com.android.launcher3.util;

import android.view.MotionEvent;
import java.io.PrintWriter;

public interface TouchController {
    void dump(String str, PrintWriter printWriter) {
    }

    boolean onControllerInterceptTouchEvent(MotionEvent motionEvent);

    boolean onControllerTouchEvent(MotionEvent motionEvent);

    void onOneHandedModeStateChanged(boolean z) {
    }
}
