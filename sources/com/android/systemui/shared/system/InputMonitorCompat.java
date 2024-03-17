package com.android.systemui.shared.system;

import android.hardware.input.InputManager;
import android.os.Looper;
import android.view.Choreographer;
import android.view.InputMonitor;
import com.android.systemui.shared.system.InputChannelCompat;

public class InputMonitorCompat {
    private final InputMonitor mInputMonitor;

    public InputMonitorCompat(String str, int i) {
        this.mInputMonitor = InputManager.getInstance().monitorGestureInput(str, i);
    }

    public void pilferPointers() {
        this.mInputMonitor.pilferPointers();
    }

    public void dispose() {
        this.mInputMonitor.dispose();
    }

    public InputChannelCompat.InputEventReceiver getInputReceiver(Looper looper, Choreographer choreographer, InputChannelCompat.InputEventListener inputEventListener) {
        return new InputChannelCompat.InputEventReceiver(this.mInputMonitor.getInputChannel(), looper, choreographer, inputEventListener);
    }
}
