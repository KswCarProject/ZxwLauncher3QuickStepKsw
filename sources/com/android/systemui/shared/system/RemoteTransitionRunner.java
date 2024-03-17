package com.android.systemui.shared.system;

import android.os.IBinder;
import android.view.SurfaceControl;
import android.window.TransitionInfo;

public interface RemoteTransitionRunner {
    void mergeAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, IBinder iBinder2, Runnable runnable) {
    }

    void startAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, Runnable runnable);
}
