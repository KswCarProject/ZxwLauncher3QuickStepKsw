package com.android.launcher3.util.window;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.SafeCloseable;

public class RefreshRateTracker implements DisplayManager.DisplayListener, SafeCloseable {
    private static final MainThreadInitializedObject<RefreshRateTracker> INSTANCE = new MainThreadInitializedObject<>($$Lambda$RefreshRateTracker$Q1tWNsYLhYAL3xuueRPRMAas8Ak.INSTANCE);
    private final DisplayManager mDM;
    private int mSingleFrameMs = 1;

    public static /* synthetic */ RefreshRateTracker lambda$Q1tWNsYLhYAL3xuueRPRMAas8Ak(Context context) {
        return new RefreshRateTracker(context);
    }

    public final void onDisplayAdded(int i) {
    }

    public final void onDisplayRemoved(int i) {
    }

    private RefreshRateTracker(Context context) {
        DisplayManager displayManager = (DisplayManager) context.getSystemService(DisplayManager.class);
        this.mDM = displayManager;
        updateSingleFrameMs();
        displayManager.registerDisplayListener(this, Executors.UI_HELPER_EXECUTOR.getHandler());
    }

    public static int getSingleFrameMs(Context context) {
        return INSTANCE.lambda$get$1$MainThreadInitializedObject(context).mSingleFrameMs;
    }

    public final void onDisplayChanged(int i) {
        if (i == 0) {
            updateSingleFrameMs();
        }
    }

    private void updateSingleFrameMs() {
        Display display = this.mDM.getDisplay(0);
        if (display != null) {
            float refreshRate = display.getRefreshRate();
            this.mSingleFrameMs = refreshRate > 0.0f ? (int) (1000.0f / refreshRate) : 16;
        }
    }

    public void close() {
        this.mDM.unregisterDisplayListener(this);
    }
}
